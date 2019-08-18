package com.example.android.miwok;

import  android.content.Context;
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

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class NumbersFragment extends Fragment {

    private MediaPlayer wordPlayer = null;
    private AudioManager wordAudioManager;

    public NumbersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view, container, false);

        /** Create AudioManager object     */
        wordAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        //Creating an ArrayList of Strings
        final ArrayList<Word> numbers = new ArrayList<Word>();

        /** Adding words to the ArrayList in typical way
         Word w = new Word("one", "lutti");
         words.add(w);
         */

        //Adding words to the ArrayList
        numbers.add(new Word("one", "lutti", R.drawable.number_one, R.raw.number_one));
        numbers.add(new Word("two", "ottiko", R.drawable.number_two, R.raw.number_two));
        numbers.add(new Word("three", "tolookosu", R.drawable.number_three, R.raw.number_three));
        numbers.add(new Word("four", "oyyisa", R.drawable.number_four, R.raw.number_four));
        numbers.add(new Word("five", "massokka", R.drawable.number_five, R.raw.number_five));
        numbers.add(new Word("six", "temmokka", R.drawable.number_six, R.raw.number_six));
        numbers.add(new Word("seven", "kenekaku", R.drawable.number_seven, R.raw.number_seven));
        numbers.add(new Word("eight", "kawinta", R.drawable.number_eight, R.raw.number_eight));
        numbers.add(new Word("nine", "wo'e", R.drawable.number_nine, R.raw.number_nine));
        numbers.add(new Word("ten", "na'aacha", R.drawable.number_ten, R.raw.number_ten));

        /** Creating ArrayAdapter of type String with three arguments - context, XML item layout(resource) and array of data
         * Ref : https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
         */
        WordAdapter numbersAdapter = new WordAdapter(getActivity(), numbers, R.color.category_numbers);

        /** Creating a ListVIew (changed activity_numbers.xml from LinearLayout to ListView)
         * ListView is powered by the ArrayAdapter
         */
        ListView wordsView = (ListView) rootView.findViewById(R.id.list);

        // Using GridView (must change activity_numbers.xml to GridView)
        // GridView wordsViewG = (GridView) findViewById(R.id.list);
        // wordsViewG.setNumColumns(2);

        /** Linking ArrayAdapter and ListView  */
        wordsView.setAdapter(numbersAdapter);

        /** Create an OnItemClickListener on the ListView
         *  Creates and plays the audio file when any item in the ListView is clicked
         */
        wordsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                Word currentWord = numbers.get(position);

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
                } else if (audioFocusState == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
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
                        // Here we seek to the beginning of the audio file because our audio files
                        // are pretty short and it would be good to play the whole of it at once
                        wordPlayer.seekTo(0);
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
