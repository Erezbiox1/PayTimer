/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.erezbiox1.paytimer.activities.EditShiftActivity;
import com.erezbiox1.paytimer.R;
import com.erezbiox1.paytimer.model.Shift;
import com.erezbiox1.paytimer.database.ShiftRepository;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ShiftsAdapter extends RecyclerView.Adapter<ShiftsAdapter.ViewHolder> {

    // The local shift data store ( static and is updated through setShiftsList(). also the recyclerView ref and activity context
    private List<Shift> shiftsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Context context;

    /**
     * Called when the adapter is attached to a recycler view, thus enabled us to save a reference to the recycler view.
     * @param recyclerView recycler view reference
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    /**
     * Called when the recycler view asks us to inflate a new view holder
     * returns a custom ViewHolder class that takes the inflated view ( shift_item layout )
     * @param parent the view holder group
     * @param viewType the type of the view
     * @return the custom view holder
     */
    @NonNull
    @Override
    public ShiftsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a shift item view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shift_item, parent, false);

        // Create a custom ViewHolder class and pass the shift item view ( so
        // the custom class could change it's children properties. )
        ViewHolder viewHolder = new ViewHolder(view);

        // Save the context
        context = parent.getContext();

        // Return the custom viewHolder.
        return viewHolder;
    }

    /**
     * Called when the recycler view assigns a position to the view holder.
     * @param holder the view holder that will be assigned the position
     * @param position the new position assigned to the view holder
     */
    @Override
    public void onBindViewHolder(@NonNull ShiftsAdapter.ViewHolder holder, final int position) {
        // Tell the holder to update their shift with the shift from the list
        // ( will allow the holder to update their UI to the new shift in the specified position. )
        holder.setShift(shiftsList.get(position));
    }

    /**
     * @return the shift list size
     */
    @Override
    public int getItemCount() {
        // If the shift list is null return 0, otherwise return it's size.
        return shiftsList != null ? shiftsList.size() : 0;
    }

    /**
     * Sets the shifts list and notifies the recycler view that the data was updated
     * @param list new shift's list
     */
    public void setShiftsList(List<Shift> list){
        // Delete all previous shifts references stored in the local shift's list
        shiftsList.clear();

        // Add all of the shifts to our list
        shiftsList.addAll(list);

        // Notify the recycler view that the data was updated.
        notifyDataSetChanged();
    }

    // A custom ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        // UI elements
        private TextView dayOfTheWeek, fromHour, toHour, date, totalPayout, totalHours;
        private ImageView optionsButton;
        private CardView cardView;

        // Stored shift
        private Shift shift;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Get the UI elements

            cardView = itemView.findViewById(R.id.shift_card_view);

            dayOfTheWeek = itemView.findViewById(R.id.shift_day_of_week);
            fromHour = itemView.findViewById(R.id.shift_from_hour);
            toHour = itemView.findViewById(R.id.shift_to_hour);
            date = itemView.findViewById(R.id.shift_date);
            totalPayout = itemView.findViewById(R.id.shift_total_payout);
            totalHours = itemView.findViewById(R.id.shift_total_hours);
            optionsButton = itemView.findViewById(R.id.shift_options_button);

            // Set the options button click listener to this view holder.
            optionsButton.setOnClickListener(this);
        }

        /**
         * Sets the custom view holder shift and updated the UI to match it's data.
         * @param shift the new shift
         */
        @SuppressLint("SetTextI18n")
        private void setShift(Shift shift){
            // Saves the shift for future reference ( to get it's ID so we can
            // react to options button clicks and edit/delete the shift for example. )
            this.shift = shift;

            // Get the start, end, total hours between them ( will be used
            // in the future to deduct breaks ), and given tip from the shift.
            long shiftStartTime = shift.getStartTime();
            long shiftEndTime = shift.getEndTime();
            double shiftTotalPay = shift.getTotalPay();
            long shiftTotalHours = shift.getTotalHours();
            int shiftTip = shift.getTip();

            // Set the UI
            dayOfTheWeek    .setText(format("E", shiftStartTime, null));
            fromHour        .setText(format("HH:mm", shiftStartTime, null));
            toHour          .setText(format("HH:mm", shiftEndTime, null));
            date            .setText(format("MMMM dd, yyyy", shiftStartTime, null));
            totalPayout     .setText(getFormattedTotalPayout(shiftTotalPay));
            totalHours      .setText(getFormattedTotalHours(shiftTotalHours, shiftTip));
        }

        /**
         * Get the formatted total hours text
         * @param totalHours total shift hours
         * @param tip total tip given in the shift
         * @return the formatted total payout text
         */
        private String getFormattedTotalHours(long totalHours, int tip){
            // Create a string builder to avoid concatenating strings
            StringBuilder builder = new StringBuilder();

            // Get the currency symbol ( changes per language )
            String symbol = context.getString(R.string.currency_symbol);

            // If the tip isn't 0, add it to the total hours
            if(tip != 0) {
                // Append the tip
                builder.append(tip);
                builder.append(symbol);

                // Append the separator
                builder.append(" + ");
            }

            // Append the total hours
            builder.append(format("HH:mm", totalHours, "UTC"));

            // Return the result
            return builder.toString();
        }

        /**
         * Get the formatted total payout text
         * @param totalPayout total shit's payout
         * @return the formatted total hours text
         */
        private String getFormattedTotalPayout(double totalPayout){
            // Create a string builder to avoid concatenating strings
            StringBuilder builder = new StringBuilder();

            // Add the total payout
            builder.append(new DecimalFormat("#.#").format(totalPayout));

            // Add the currency symbol
            builder.append(context.getString(R.string.currency_symbol));

            // Return the result
            return builder.toString();
        }

        /**
         * Parses a long time in the chosen pattern
         * @param pattern time pattern used to update the text view
         * @param time time in epoch format to update the text view
         * @param timeZone time zone to parse the time in.
         */
        private String format(String pattern, long time, String timeZone){
            // Create a simple date format with the supplied pattern ( and default locale. )
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());

            // Set the time zone to the specified time zone ( if null use the default time zone )
            dateFormat.setTimeZone(timeZone == null ? TimeZone.getDefault() : TimeZone.getTimeZone(timeZone));

            // Format the time and return the formatted time.
            return dateFormat.format(time);
        }

        /**
         * Called when the options button is clicked.
         * @param view the view clicked ( always the options button )
         */
        @Override
        public void onClick(View view) {
            // Create a popup menu, inflate it with shift_option_menu
            PopupMenu popupMenu = new PopupMenu(context, view);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.shift_options_menu, popupMenu.getMenu());

            // Set the current viewHolder as it's on click listener
            popupMenu.setOnMenuItemClickListener(this);

            // Show the popup menu
            popupMenu.show();
        }

        /**
         * called when an item is clicked on the options menu
         * @param menuItem item clicked
         * @return was the event handled
         */
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            // Handle different buttons on the menu differently using a switch
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

        /**
         * Called when the "edit" button is clicked on the option menu popup.
         */
        private void clickEditOption(){
            // Create a EditShiftActivity intent
            Intent editShiftIntent = new Intent(context, EditShiftActivity.class);

            // Add the shift ID to the intent ( and thus passing it to the activity )
            editShiftIntent.putExtra(EditShiftActivity.SHIFT_ID_EXTRA, shift.getId());

            // TODO: TRY TO REMOVE
            editShiftIntent.putExtra(EditShiftActivity.START_TIME_EXTRA, shift.getStartTime());
            editShiftIntent.putExtra(EditShiftActivity.END_TIME_EXTRA, shift.getEndTime());

            // Tell the context to start the activity
            context.startActivity(editShiftIntent);
        }

        /**
         * Called when the "delete" button is clicked on the option menu popup.
         */
        public void deleteShift(){
            // Get the shift position
            final int position = getAdapterPosition();

            // Creates and shows a new alert dialog, set it's title, message, icon, and both positive and negative buttons
            new AlertDialog.Builder(context)
                    .setTitle(R.string.delete_shift)
                    .setMessage(R.string.delete_shift_text)
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(R.string.delete_shift_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // If the alert "yes" button was clicked, delete the shift:

                            // Notify the recyclerView that an item was deleted in this location
                            notifyItemRemoved(position);

                            // Tell the ShiftRepository to delete the shift asynchronously.
                            new ShiftRepository(context).delete(shift);
                        }
                    })
                    .setNegativeButton(R.string.delete_shift_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // If the alert "no" button was clicked, revert the swipe operation by simply notifying
                            // the recyclerView that an item was changed in that position, causing the recyclerView
                            // to Re-Populate that shift's item view.
                            notifyItemChanged(position);
                        }
                    })
                    .show();
        }
    }
}
