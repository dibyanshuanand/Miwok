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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FamilyFragment extends Fragment {

    private MediaPlayer wordPlayer = null;
    private AudioManager wordAudioManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_view, container, false);

        //Creating an ArrayList of Strings
        final ArrayList<Word> family = new ArrayList<Word>();

        //Adding words to the ArrayList
        family.add(new Word("father", "әpә", R.drawable.family_father, R.raw.family_father));
        family.add(new Word("mother", "әṭa", R.drawable.family_mother, R.raw.family_mother));
        family.add(new Word("son", "angsi", R.drawable.family_son, R.raw.family_son));
        family.add(new Word("daughter", "tune", R.drawable.family_daughter, R.raw.family_daughter));
        family.add(new Word("older brother", "taachi", R.drawable.family_older_brother, R.raw.family_older_brother));
        family.add(new Word("younger brother", "chalitti", R.drawable.family_younger_brother, R.raw.family_younger_brother));
        family.add(new Word("older sister", "teṭe", R.drawable.family_older_sister, R.raw.family_older_sister));
        family.add(new Word("younger sister", "kolliti", R.drawable.family_younger_sister, R.raw.family_younger_sister));
        family.add(new Word("grandmother", "ama", R.drawable.family_grandmother, R.raw.family_grandmother));
        family.add(new Word("grandfather", "paapa", R.drawable.family_grandfather, R.raw.family_grandfather));

        /** Creating ArrayAdapter of type String with three arguments - context, XML item layout(resource) and array of data
         * Ref : https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
         */
        WordAdapter familyAdapter = new WordAdapter(getActivity(), family, R.color.category_family);

        /** Creating a ListVIew (changed activity_numbers.xml from LinearLayout to ListView)
         * ListView is powered by the ArrayAdapter
         */
        ListView familyView = (ListView) rootView.findViewById(R.id.list);

        /** Linking ArrayAdapter and ListView
         */
        familyView.setAdapter(familyAdapter);

        /** Create AudioManager object     */
        wordAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        /** Create an OnItemClickListener on the ListView
         *  Creates and plays the audio file when any item in the ListView is clicked
         */
        familyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                Word currentWord = family.get(position);

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
