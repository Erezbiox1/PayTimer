/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.views.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.erezbiox1.paytimer.R;

/**
 * A custom preference used by settings activities. ( {@link com.erezbiox1.paytimer.activities.SettingsActivity}, {@link com.erezbiox1.paytimer.activities.TimeNotificationsActivity} )
 * Stores no value by default ( can be extended to modify this behaviour, see {@link NumberPreference}
 * Activates a custom callback in that activity if found.
 */
public class ButtonPreference extends Preference implements View.OnClickListener {

    // The UI button
    private Button button;

    // The activity callback
    protected ButtonPrefCallback callback;

    public ButtonPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Inflate the preference with pref_button layout
        setWidgetLayoutResource(R.layout.pref_button);
    }

    /**
     * Called when the preference manager assigns a view holder to the preference
     * @param holder the assigned view holder
     */
    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        // Disable the click-ability of the view holder.
        holder.itemView.setClickable(false);

        // Get the button from the view holder
        button = (Button) holder.findViewById(R.id.pref_button);

        // Set the button clickable, and set it's text derived from the preference title ( getText() )
        button.setClickable(true);
        button.setText(getText());

        // Try finding the activity
        Activity activity = getActivity();

        // If the activity is not null, and is implementing ButtonPrefCallback, set the callback to the activity.
        if(activity instanceof ButtonPrefCallback)
            setCallback((ButtonPrefCallback) activity);

        // Set the button click listener to this preference.
        button.setOnClickListener(this);
    }

    /**
     * Get's the button text, derived from the title
     * @return the button text
     */
    private String getText(){
        // If the title is more that one word, return the upper case of the first word
        // otherwise, return the uppercase version of the only word.
        if(getTitle().toString().contains(" "))
            return getTitle().toString().split(" ")[0].toUpperCase();
        return getTitle().toString().toUpperCase();
    }

    /**
     * Set the callback
     * @param callback provided callback, the activity
     */
    public void setCallback(ButtonPrefCallback callback) {
        this.callback = callback;
    }

    /**
     * Will find the activity, otherwise will return null
     * @return the activity
     */
    protected Activity getActivity() {
        // Get the context, as long as the context is a context wrapper ( it has a base context ),
        // move up in the base context tree, until you find the activity ( context is instanceof activity )
        // return that activity, if no activity was found, return null.
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /**
     * Handle button onClick event
     * @param view the clicked button view
     */
    @Override
    public void onClick(View view) {
        // Call the callback ( if not null ) with this preference.
        if(callback != null)
            callback.onClick(this);
    }

    // The custom button preference onClick listener
    public interface ButtonPrefCallback {
        /**
         * Called when the preference button is clicked.
         * @param button preference button
         */
        void onClick(ButtonPreference button);
    }

}
