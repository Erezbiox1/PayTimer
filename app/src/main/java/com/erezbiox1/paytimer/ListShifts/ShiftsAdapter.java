/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.ListShifts;

import android.content.Context;
import android.content.Intent;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.erezbiox1.paytimer.EditShift.EditShiftActivity;
import com.erezbiox1.paytimer.R;
import com.erezbiox1.paytimer.Room.Shift;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ShiftsAdapter extends RecyclerView.Adapter<ShiftsAdapter.ViewHolder> {

    private List<Shift> shiftsList = new ArrayList<>();
    private Context context;

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
        Shift shift = shiftsList.get(position);

        holder.setShift(shift);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "The position is " + position, Toast.LENGTH_SHORT).show();
            }
        });
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

        private void setShift(Shift shift){
            this.shift = shift;

            long startDate = shift.getStartTime();
            long endDate = shift.getEndTime();
            long diff = endDate - startDate;

            int pay = 23; // TODO

            set(dayOfTheWeek, "E", startDate);
            set(fromHour, "HH:mm", startDate);
            set(toHour, "HH:mm", endDate);
            set(date, "MMMM dd, yyyy", startDate);
            totalPayout.setText(String.format(context.getString(R.string.shift_payout), (diff/1000/60/60 * pay)));
            set(totalHours, "HH:mm", diff);

        }

        private void set(TextView textView, String pattern, long time){
            textView.setText(new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date(time)));
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
                    Toast.makeText(context, "NOT AN OPTION", Toast.LENGTH_SHORT).show();
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
    }
}
