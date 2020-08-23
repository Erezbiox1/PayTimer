package com.erezbiox1.paytimer.AddShift.Spinners;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

public abstract class CustomSpinner extends AppCompatSpinner {

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract Dialog getDialog();

    @Override
    public final boolean performClick() {
        Dialog dialog = getDialog();
        dialog.create();
        dialog.show();

        return true;
    }

    public final void setText(String text){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, new String[]{text});
        setAdapter(adapter);
        setSelection(0);
    }
}
