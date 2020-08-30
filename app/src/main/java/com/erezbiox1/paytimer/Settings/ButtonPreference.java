/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.erezbiox1.paytimer.R;

public class ButtonPreference extends Preference implements View.OnClickListener {

    private Button button;
    protected ButtonPrefCallback callback;

    public ButtonPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.pref_button);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setClickable(false);
        button = (Button) holder.findViewById(R.id.pref_button);
        button.setClickable(true);
        button.setText(getTitleFromKey());

        Activity activity = getActivity();
        if(activity instanceof ButtonPrefCallback)
            setCallback((ButtonPrefCallback) activity);

        button.setOnClickListener(this);
    }

    private String getTitleFromKey(){
        if(getTitle().toString().contains(" "))
            return getTitle().toString().split(" ")[0].toUpperCase();
        return getTitle().toString().toUpperCase();
    }

    public void setCallback(ButtonPrefCallback callback) {
        this.callback = callback;
    }

    protected Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        callback.onClick(this);
    }

    public interface ButtonPrefCallback {
        void onClick(ButtonPreference button);
    }

}
