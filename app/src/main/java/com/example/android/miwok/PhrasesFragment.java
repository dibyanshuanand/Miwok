package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class PhrasesFragment extends Fragment {

    private MediaPlayer wordPlayer = null;
    private AudioManager wordAudioManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view, container, false);

        //Creating an ArrayList of Strings
        final ArrayList<Word> phrases = new ArrayList<Word>();

        //Adding words to the ArrayList
        phrases.add(new Word("Where are you going?", "minto wuksus", R.raw.phrase_where_are_you_going));
        phrases.add(new Word("What is your name?", "tinnә oyaase'nә", R.raw.phrase_what_is_your_name));
        phrases.add(new Word("My name is...", "oyaaset...", R.raw.phrase_my_name_is));
        phrases.add(new Word("How're you feeling?", "michәksәs?", R.raw.phrase_how_are_you_feeling));
        phrases.add(new Word("I'm feeling good.", "kuchi achit", R.raw.phrase_im_feeling_good));
        phrases.add(new Word("Are you coming?", "әәnәs'aa?", R.raw.phrase_are_you_coming));
        phrases.add(new Word("Yes, I'm coming.", "hәә’ әәnәm", R.raw.phrase_yes_im_coming));
        phrases.add(new Word("I'm coming.", "әәnәm", R.raw.phrase_im_coming));
        phrases.add(new Word("Let's go.", "yoowutis", R.raw.phrase_lets_go));
        phrases.add(new Word("Come here.", "әnni'nem", R.raw.phrase_come_here));

        /** Creating ArrayAdapter of type String with three arguments - context, XML item layout(resource) and array of data
         * Ref : https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
         */
        WordAdapter phrasesAdapter = new WordAdapter(getActivity(), phrases, R.color.category_phrases);

        /** Creating a ListVIew (changed activity_numbers.xml from LinearLayout to ListView)
         * ListView is powered by the ArrayAdapter
         */
        ListView phrasesView = (ListView) rootView.findViewById(R.id.list);

        // Using GridView (must change activity_numbers.xml to GridView)
        // GridView wordsViewG = (GridView) findViewById(R.id.list);
        // wordsViewG.setNumColumns(2);

        /** Linking ArrayAdapter and ListView
         */
        phrasesView.setAdapter(phrasesAdapter);

        /** Create AudioManager object     */
        wordAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        /** Create an OnItemClickListener on the ListView
         *  Creates and plays the audio file when any item in the ListView is clicked
         */
        phrasesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // This code part ensures that there is no MediaPlayer instance already running, and
                // if so release it
                // Particularly useful when the user clicks many words in quick succession
                if (wordPlayer != null) {
                    wordPlayer.release();
                    //Toast.makeText(getContext(), "Released", Toast.LENGTH_SHORT).show();
                    wordPlayer = null;
                }

                // Get the item clicked using get()
                Word currentWord = phrases.get(position);

                // Logs the current state of the object currentWord
                // Using + as concatenator is equivalent to "Current word:" + currentWord.toString()
                Log.v("Word Status", "Current word: " + currentWord);

                /** Request AudioFocus from AudioManager                 */
                int audioFocusState = wordAudioManager.requestAudioFocus(audioFocusListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                /** Create MediaPlayer and start playback if request is granted                 */
                if (audioFocusState == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                    wordPlayer = MediaPlayer.create(getActivity(), currentWord.getMediaResourceId(getActivity()));
                    wordPlayer.setLooping(false);
                    wordPlayer.start();
                    //Toast.makeText(getContext(), "Playing", Toast.LENGTH_SHORT).show();

                    // Callback method invoked on completion of current playing track
                    // MediaPlayer is released on callback
                    wordPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (wordPlayer != null)
                                wordPlayer.release();
                            //Toast.makeText(getContext(), "Released", Toast.LENGTH_SHORT).show();

                            wordPlayer = null;

                            wordAudioManager.abandonAudioFocus(audioFocusListener);
                        }
                    });
                }

                else if (audioFocusState == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                    Toast.makeText(getActivity(), "Cannot play audio\nPermission failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    /** Listener handling change in audio focus     */
    private AudioManager.OnAudioFocusChangeListener audioFocusListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        wordPlayer.start();
                    }
                    else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        wordPlayer.stop();
                        wordPlayer.release();
                        wordPlayer = null;

                        wordAudioManager.abandonAudioFocus(audioFocusListener);
                    }
                    else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                            focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        wordPlayer.pause();
                    }
                }
            };


    @Override
    public void onStop() {
        super.onStop();

        if (wordPlayer != null) {
            wordPlayer.release();
            wordPlayer = null;
        }

        wordAudioManager.abandonAudioFocus(audioFocusListener);
    }
}
