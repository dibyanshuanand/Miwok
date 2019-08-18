package com.example.android.miwok;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class WordAdapter extends ArrayAdapter<Word>{

    // Variable to access color resource id obtained through constructor
    private int mColorResourceId;
    MediaPlayer wordPlayer = null;
    /**
     * This is the custom constructor of the WordAdapter class
     * The context is used to inflate the layout file and, the list is the data we want to populate
     * into the lists
     *
     * @param context The current context. Used to inflate the layout file.
     * @param words   A list of Word objects to display in a list
     * @param colorResourceId color resource obtained through constructor sent by user while creating WordAdapter
     */
    public WordAdapter(Activity context, ArrayList<Word> words, int colorResourceId) {
        // Here we initialize the ArrayAdapter's internal storage for the context and the list.
        // The second argument (int resource) is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews, the adapter is not going to use this
        // second argument, so we can use any value. Here we have used 0.
        super(context, 0, words);
        mColorResourceId = colorResourceId;
    }


    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the list item view
     * @param convertView The recycled view to populate
     * @param parent      The parent ViewGroup that is used for inflation
     * @return The view for the position in the AdapterView
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View miwokListView = convertView;
        if (miwokListView == null) {
            miwokListView = LayoutInflater.from(getContext()).inflate(
                    R.layout.miwok_list, parent, false);
        }

        // Get the (@link Word) object located at this position in the list
        final Word currentWord = getItem(position);

        TextView miwokTextView = (TextView) miwokListView.findViewById(R.id.miwok_text);
        //Get the miwok word from the current Word object and set this text on the miwok TextView
        miwokTextView.setText(currentWord.getMiwokTranslation(getContext()));

        TextView defaultTextView = (TextView) miwokListView.findViewById(R.id.en_text);
        // Get the english word from the current WOrd object and set this text on the default TextView
        defaultTextView.setText(currentWord.getDefaultTranslation(getContext()));

        ImageView imageView = (ImageView) miwokListView.findViewById(R.id.imageRef);

        // Check whether there is a Image present in the current 'Word'
        if (currentWord.hasImageResource()) {
            // IT IS IMPORTANT TO SET THE VIEW TO VISIBLE BECAUSE VIEWS ARE REUSED
            imageView.setVisibility(View.VISIBLE);
            //Get the image resource id from the current Word object and det this image on the ImageView
            imageView.setImageResource(currentWord.getImageResourceId(getContext()));
        }
        else {
            imageView.setVisibility(View.GONE);
        }

        // Get the LinearLayout containing the ListView as View object
        View containerView = miwokListView.findViewById(R.id.text_layout);
        // Set background color to the LinearLayout using ContextCompat
        containerView.setBackgroundColor(ContextCompat.getColor(getContext(), mColorResourceId));

        // Return the whole miwok list layout (containing 2 TextViews) so that it can be shown in
        // the ListView
        return miwokListView;
    }
}


