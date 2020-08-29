/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.ListShifts;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.erezbiox1.paytimer.EditShift.EditShiftActivity;
import com.erezbiox1.paytimer.R;
import com.erezbiox1.paytimer.Room.Shift;
import com.erezbiox1.paytimer.Room.ShiftRepository;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ShiftsAdapter extends RecyclerView.Adapter<ShiftsAdapter.ViewHolder> {

    private List<Shift> shiftsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Context context;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ShiftsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shift_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftsAdapter.ViewHolder holder, final int position) {
        holder.setShift(shiftsList.get(position));
    }

    @Override
    public int getItemCount() {
        return shiftsList != null ? shiftsList.size() : 0;
    }

    public void setShiftsList(List<Shift> list){
        shiftsList.clear();
        shiftsList.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private Shift shift;
        private CardView cardView;
        private TextView dayOfTheWeek, fromHour, toHour, date, totalPayout, totalHours;
        private ImageView optionsButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.shift_card_view);

            dayOfTheWeek = itemView.findViewById(R.id.shift_day_of_week);
            fromHour = itemView.findViewById(R.id.shift_from_hour);
            toHour = itemView.findViewById(R.id.shift_to_hour);
            date = itemView.findViewById(R.id.shift_date);
            totalPayout = itemView.findViewById(R.id.shift_total_payout);
            totalHours = itemView.findViewById(R.id.shift_total_hours);
            optionsButton = itemView.findViewById(R.id.shift_options_button);

            optionsButton.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        private void setShift(Shift shift){
            this.shift = shift;

            long startDate = shift.getStartTime();
            long endDate = shift.getEndTime();
            long diff = shift.getTotalHours();

            set(dayOfTheWeek, "E", startDate);
            set(fromHour, "HH:mm", startDate);
            set(toHour, "HH:mm", endDate);
            set(date, "MMMM dd, yyyy", startDate);
            totalPayout.setText(new DecimalFormat("#.#").format(shift.getTotalPay()) + context.getString(R.string.currency_symbol));
            set(totalHours, "HH:mm", diff, "UTC");
        }

        private void set(TextView textView, String pattern, long time){
            set(textView, pattern, time, null);
        }

        private void set(TextView textView, String pattern, long time, String timeZone){
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            dateFormat.setTimeZone(timeZone == null ? TimeZone.getDefault() : TimeZone.getTimeZone("UTC"));
            textView.setText(dateFormat.format(new Date(time)));
        }

        @Override
        public void onClick(View view) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.shift_options_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.shift_options_edit:
                    clickEditOption();
                    break;
                case R.id.shift_options_delete:
                    deleteShift();
                    break;
            }
            return false;
        }

        private void clickEditOption(){
            Intent editShiftIntent = new Intent(context, EditShiftActivity.class);

            editShiftIntent.putExtra(EditShiftActivity.SHIFT_ID_EXTRA, shift.getId());
            editShiftIntent.putExtra(EditShiftActivity.START_TIME_EXTRA, shift.getStartTime());
            editShiftIntent.putExtra(EditShiftActivity.END_TIME_EXTRA, shift.getEndTime());

            context.startActivity(editShiftIntent);
        }

        public void deleteShift(){
            new AlertDialog.Builder(context)
                    .setTitle(R.string.delete_shift)
                    .setMessage(R.string.delete_shift_text)
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(R.string.delete_shift_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final int position = getAdapterPosition();
                            notifyItemRemoved(position);
                            new ShiftRepository(context).delete(shift);
                        }
                    })
                    .setNegativeButton(R.string.delete_shift_no, null)
                    .show();
        }
    }
}
