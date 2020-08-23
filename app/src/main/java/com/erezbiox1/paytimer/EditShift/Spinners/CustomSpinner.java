/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.EditShift.Spinners;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

/**
 * Abstract custom spinner class that is used to open a dialog,
 * then display the dialog outcome.
 */
public abstract class CustomSpinner extends AppCompatSpinner {

    /**
     * Default constructor required by every view in android.
     * @param context
     * @param attrs
     */
    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * get the implemented dialog so the custom spinner could display it.
     * @return implemented dialog
     */
    public abstract Dialog getDialog();

    /**
     * Click listener, will trigger the dialog.
     * @return
     */
    @Override
    public final boolean performClick() {
        // get the dialog from the abstract method that will be implemented by the CustomSpinner implementation
        Dialog dialog = getDialog();

        // Create and show that dialog.
        dialog.create();
        dialog.show();

        // Event handled successfully, return true.
        return true;
    }

    /**
     * Set's the custom spinner text.
     * @param text
     */
    public final void setText(String text){
        // Create an array adapter with a single element, the specified text.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, new String[]{text});

        // Update the adapter in the custom spinner
        setAdapter(adapter);

        // choose the first and only option, our text.
        setSelection(0);
    }
}
