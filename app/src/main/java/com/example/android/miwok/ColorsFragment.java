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

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ColorsFragment extends Fragment {

    private MediaPlayer wordPlayer = null;
    private AudioManager wordAudioManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view, container, false);

        //Creating an ArrayList of Strings
        final ArrayList<Word> colors = new ArrayList<Word>();

        //Adding words to the ArrayList
        colors.add(new Word("red", "weṭeṭṭi", R.drawable.color_red, R.raw.color_red));
        colors.add(new Word("green", "chokokki", R.drawable.color_green, R.raw.color_green));
        colors.add(new Word("brown", "ṭakaakki", R.drawable.color_brown, R.raw.color_brown));
        colors.add(new Word("gray", "ṭopoppi", R.drawable.color_gray, R.raw.color_gray));
        colors.add(new Word("black", "kululli", R.drawable.color_black, R.raw.color_black));
        colors.add(new Word("white", "kelelli", R.drawable.color_white, R.raw.color_white));
        colors.add(new Word("dusty yellow", "ṭopiisә", R.drawable.color_dusty_yellow, R.raw.color_dusty_yellow));
        colors.add(new Word("mustard yellow", "chiwiiṭә", R.drawable.color_mustard_yellow, R.raw.color_mustard_yellow));

        /** Creating ArrayAdapter of type String with three arguments - context, XML item layout(resource) and array of data
         * Ref : https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
         */
        WordAdapter colorsAdapter = new WordAdapter(getActivity(), colors, R.color.category_colors);

        /** Creating a ListVIew (changed activity_numbers.xml from LinearLayout to ListView)
         * ListView is powered by the ArrayAdapter
         */
        ListView colorsView = (ListView) rootView.findViewById(R.id.list);

        /** Linking ArrayAdapter and ListView
         */
        colorsView.setAdapter(colorsAdapter);

        /** Create AudioManager object     */
        wordAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        /** Create an OnItemClickListener on the ListView
         *  Creates and plays the audio file when any item in the ListView is clicked
         */
        colorsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                Word currentWord = colors.get(position);

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


