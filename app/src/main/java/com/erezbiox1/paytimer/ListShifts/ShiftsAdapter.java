/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.ListShifts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView dayOfTheWeek, fromHour, toHour, date, totalPayout, totalHours;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.shift_card_view);

            dayOfTheWeek = itemView.findViewById(R.id.shift_day_of_week);
            fromHour = itemView.findViewById(R.id.shift_from_hour);
            toHour = itemView.findViewById(R.id.shift_to_hour);
            date = itemView.findViewById(R.id.shift_date);
            totalPayout = itemView.findViewById(R.id.shift_total_payout);
            totalHours = itemView.findViewById(R.id.shift_total_hours);
        }

        private void setShift(Shift shift){

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
    }
}
