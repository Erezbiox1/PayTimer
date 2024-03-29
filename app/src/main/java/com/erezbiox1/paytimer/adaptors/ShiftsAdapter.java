/*
 * Copyright (c) 2020. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.adaptors;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.erezbiox1.paytimer.R;
import com.erezbiox1.paytimer.activities.EditShiftActivity;
import com.erezbiox1.paytimer.activities.MonthlySummary;
import com.erezbiox1.paytimer.database.ShiftRepository;
import com.erezbiox1.paytimer.model.Shift;
import com.erezbiox1.paytimer.utils.Utils;
import com.erezbiox1.paytimer.utils.Utils.Month;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.erezbiox1.paytimer.utils.Utils.format;

public class ShiftsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Selected shifts live data
    public MutableLiveData<List<Integer>> selectedLiveData = new MutableLiveData<>(new ArrayList<>());

    // The local shift data store ( static and is updated through setShiftsList(). also the recyclerView ref and activity context
    private List<ListItem> entryList = new ArrayList<>();
    private List<Shift> shiftsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Context context;

    // Filter status (null for all, paid for paid, unpaid for unpaid)
    private String filterStatus = null;

    /**
     * Called when the adapter is attached to a recycler view, thus enabled us to save a reference to the recycler view.
     * @param recyclerView recycler view reference
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        return entryList.get(position).type.value;
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get the item type
        ListItem.ListItemType type = ListItem.ListItemType.getType(viewType);

        // Inflate a the item view
        View view = LayoutInflater.from(parent.getContext()).inflate(type.layout, parent, false);

        // Create a custom ViewHolder class and pass the shift item view ( so
        // the custom class could change it's children properties. )
        RecyclerView.ViewHolder viewHolder;
        switch (type){
            case SHIFT:
                viewHolder = new ShiftViewHolder(view);
                break;
            case MONTHLY_TITLE:
                viewHolder = new MonthlyTitleViewHolder(view);
                break;
            case FILTERS:
                viewHolder = new FiltersViewHolder(view);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        // Get the item type
        ListItem.ListItemType type = ListItem.ListItemType.getType(holder.getItemViewType());

        // Bind it to its content according to it's type.
        switch (type){
            case SHIFT:
                // Tell the holder to update their shift with the shift from the list
                // ( will allow the holder to update their UI to the new shift in the specified position. )
                final ShiftViewHolder shiftViewHolder = ((ShiftViewHolder) holder);
                shiftViewHolder.setShift(entryList.get(position).shift);

                int id = shiftViewHolder.shift.getId();
                List<Integer> selected = selectedLiveData.getValue();

                shiftViewHolder.cardView.setOnLongClickListener(v -> {
                    if(!selected.contains(id))
                        selected.add(id);
                    else
                        selected.remove(id);

                    selectedLiveData.setValue(selectedLiveData.getValue());
                    return true;
                });

                shiftViewHolder.cardView.setOnClickListener(v -> {
                    if(selected.size() > 0) {
                        if(!selected.contains(id))
                            selected.add(id);
                        else
                            selected.remove(Integer.valueOf(id));

                        selectedLiveData.setValue(selectedLiveData.getValue());
                    }
                });

                break;
            case MONTHLY_TITLE:
                // Tell the holder to update their month with the updated month from the list
                // ( will allow the holder to update their UI to the new month in the specified position. )
                ((MonthlyTitleViewHolder) holder).setMonth(entryList.get(position).month);
                break;
        }
    }

    /**
     * @return the shift list size
     */
    @Override
    public int getItemCount() {
        // If the shift list is null return 0, otherwise return it's size.
        return entryList != null ? entryList.size() : 0;
    }

    /**
     * Sets the shifts list and notifies the recycler view that the data was updated
     * @param list new shift's list
     */
    public void setShiftsList(List<Shift> list){
        this.shiftsList.clear();
        this.shiftsList.addAll(list);

        // Delete all previous shifts references stored in the local shift's list
        entryList.clear();

        // Map all of the shifts to their months
        Map<Month, List<Shift>> map = new TreeMap<>();
        for (Shift shift : list) {
            if(filterStatus != null && filterStatus.equals("paid") != shift.isPaid())
                continue;

            Month month = Month.getMonth(shift.getStartTime());

            map.putIfAbsent(month, new ArrayList<>());
            map.get(month).add(shift);
        }

        // Add the filters row
        entryList.add(new ListItem());

        // iterate the month's map,
        for (Map.Entry<Month, List<Shift>> entry : map.entrySet()) {
            // for each month add a monthly title item
            entryList.add(new ListItem(entry.getKey()));

            // then add all of the shifts in that month as a shift item.
            for (Shift shift : entry.getValue())
                entryList.add(new ListItem(shift));
        }

        // Notify the recycler view that the data was updated.
        notifyDataSetChanged();
    }

    public List<Shift> getShiftsList() {
        return shiftsList;
    }

    // A custom item list entry that can be either a shift or a monthly title
    public static class ListItem {
        private final ListItemType type;
        private final Shift shift;
        private final Month month;

        public ListItem(Shift shift){
            this.type = ListItemType.SHIFT;
            this.shift = shift;
            this.month = null;
        }

        public ListItem(Month month){
            this.type = ListItemType.MONTHLY_TITLE;
            this.shift = null;
            this.month = month;
        }

        public ListItem(){
            this.type = ListItemType.FILTERS;
            this.shift = null;
            this.month = null;
        }

        public enum ListItemType {
            SHIFT(0, R.layout.shift_item),
            MONTHLY_TITLE(1, R.layout.monthly_title_item),
            FILTERS(2, R.layout.filters_item);

            final int value, layout;

            ListItemType(int value, int layout){
                this.value = value;
                this.layout = layout;
            }

            public static ListItemType getType(int value){
                return value == 0 ? SHIFT : (value == 1 ? MONTHLY_TITLE : FILTERS);
            }
        }
    }

    public class MonthlyTitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView monthlyTitle;
        private Month month;

        public MonthlyTitleViewHolder(@NonNull View itemView) {
            super(itemView);

            monthlyTitle = itemView.findViewById(R.id.monthly_item_text);
            View view = itemView.findViewById(R.id.monthly_card_view);
            view.setOnClickListener(this);
        }

        public void setMonth(Month month) {
            this.month = month;
            monthlyTitle.setText(month.toString());
        }

        @Override
        public void onClick(View v) {
            Intent monthlySummary = new Intent(context, MonthlySummary.class);
            monthlySummary.putExtra(MonthlySummary.MONTH_EXTRA, month);
            context.startActivity(monthlySummary);
        }
    }

    public class FiltersViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout unpaid, all, paid;
        private TextView unpaidText, allText, paidText;
        private View unpaidLogo, allLogo, paidLogo;

        public FiltersViewHolder(@NonNull View itemView) {
            super(itemView);

            unpaid = itemView.findViewById(R.id.filter_unpaid);
            unpaidText = itemView.findViewById(R.id.filter_unpaid_text);
            unpaidLogo = itemView.findViewById(R.id.filter_unpaid_icon);

            all = itemView.findViewById(R.id.filter_all);
            allText = itemView.findViewById(R.id.filter_all_text);
            allLogo = itemView.findViewById(R.id.filter_all_icon);

            paid = itemView.findViewById(R.id.filter_paid);
            paidText = itemView.findViewById(R.id.filter_paid_text);
            paidLogo = itemView.findViewById(R.id.filter_paid_icon);

            unpaid.setOnClickListener(v -> setStatus("unpaid"));
            all.setOnClickListener(v -> setStatus(null));
            paid.setOnClickListener(v -> setStatus("paid"));

            //updateUI();
        }

        private void setStatus(String status){
            filterStatus = status;
            updateUI();
        }

        private void updateUI(){
            if(filterStatus == null){
                unpaidText.setTextColor(Color.parseColor("#AAAAAA"));
                unpaidLogo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AAAAAA")));

                allText.setTextColor(Color.WHITE);
                allLogo.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));

                paidText.setTextColor(Color.parseColor("#AAAAAA"));
                paidLogo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AAAAAA")));
            } else if(filterStatus.equals("unpaid")){
                unpaidText.setTextColor(Color.WHITE);
                unpaidLogo.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));

                allText.setTextColor(Color.parseColor("#AAAAAA"));
                allLogo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AAAAAA")));

                paidText.setTextColor(Color.parseColor("#AAAAAA"));
                paidLogo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AAAAAA")));
            } else if(filterStatus.equals("paid")){
                unpaidText.setTextColor(Color.parseColor("#AAAAAA"));
                unpaidLogo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AAAAAA")));

                allText.setTextColor(Color.parseColor("#AAAAAA"));
                allLogo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#AAAAAA")));

                paidText.setTextColor(Color.WHITE);
                paidLogo.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            }

            if(!shiftsList.isEmpty())
                setShiftsList(new ArrayList<>(shiftsList));
        }
    }

    // A custom ViewHolder class
    public class ShiftViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, Observer<List<Integer>> {

        // UI elements
        private final TextView dayOfTheWeek, fromHour, toHour, date, totalPayout, totalHours, checkmarkCircle;
        private final ImageView optionsButton;
        private final View cardView, selectionCircle, selectionCircleSelected;

        // Stored shift
        private Shift shift;
        private boolean wasSelected;

        ShiftViewHolder(@NonNull View itemView) {
            super(itemView);

            // Get the UI elements
            cardView = itemView.findViewById(R.id.shift_card_view);

            checkmarkCircle = itemView.findViewById(R.id.checkmark_circle);
            selectionCircle = itemView.findViewById(R.id.selection_circle);
            selectionCircleSelected = itemView.findViewById(R.id.selection_circle_selected);

            dayOfTheWeek = itemView.findViewById(R.id.shift_day_of_week);
            fromHour = itemView.findViewById(R.id.shift_from_hour);
            toHour = itemView.findViewById(R.id.shift_to_hour);
            date = itemView.findViewById(R.id.shift_date);
            totalPayout = itemView.findViewById(R.id.shift_total_payout);
            totalHours = itemView.findViewById(R.id.shift_total_hours);
            optionsButton = itemView.findViewById(R.id.shift_options_button);

            // Set the options button click listener to this view holder.
            optionsButton.setOnClickListener(this);

            // Observe selected shifts live data
            selectedLiveData.observeForever(this);
        }

        /**
         * Sets the custom view holder shift and updated the UI to match it's data.
         * @param shift the new shift
         */
        @SuppressLint("SetTextI18n")
        private void setShift(Shift shift){
            // Get the start, end, total hours between them ( will be used
            // in the future to deduct breaks ), and given tip from the shift.
            long shiftStartTime = shift.getStartTime();
            long shiftEndTime = shift.getEndTime();
            double shiftTotalPay = shift.getTotalPay();
            long shiftTotalHours = shift.getTotalHours();
            int shiftTip = shift.getTip();
            boolean paid = shift.isPaid();

            // Set the UI
            dayOfTheWeek    .setText(format("E", shiftStartTime, null));
            fromHour        .setText(format("HH:mm", shiftStartTime, null));
            toHour          .setText(format("HH:mm", shiftEndTime, null));
            date            .setText(format("MMMM dd, yyyy", shiftStartTime, null));
            totalPayout     .setText(Utils.getFormattedTotalPayout(context, shiftTotalPay));
            totalHours      .setText(Utils.getFormattedTotalHours(context, shiftTotalHours, shiftTip));

            checkmarkCircle.setText(Utils.getCurrencySymbol(context));
            checkmarkCircle.setVisibility(paid ? View.VISIBLE : View.INVISIBLE);

            // Saves the shift for future reference ( to get it's ID so we can
            // react to options button clicks and edit/delete the shift for example. )
            this.shift = shift;
        }

        @Override
        public void onChanged(List<Integer> selectedShifts) {
            selectionCircle.setVisibility(selectedShifts.size() > 0 ? View.VISIBLE : View.INVISIBLE);
            //selectionCircleSelected.setVisibility(shift == null || !selectedShifts.contains(shift.getId()) ? View.INVISIBLE : View.VISIBLE);
            if(shift != null && selectedShifts.contains(shift.getId())){
                if(!wasSelected)
                    animateSelectedCheckmark(true);
                else
                    selectionCircleSelected.setVisibility(View.VISIBLE);
                wasSelected = true;
            } else if(wasSelected){
                if(selectedShifts.size() == 0)
                    selectionCircleSelected.setVisibility(View.INVISIBLE);
                else
                    animateSelectedCheckmark(false);
                wasSelected = false;
            }
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
                case R.id.shift_options_paid:
                    clickPaidUnpaid();
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
                    .setPositiveButton(R.string.delete_shift_yes, (dialogInterface, i) -> {
                        // If the alert "yes" button was clicked, delete the shift:

                        // Notify the recyclerView that an item was deleted in this location
                        notifyItemRemoved(position);

                        // Tell the ShiftRepository to delete the shift asynchronously.
                        ShiftRepository.getInstance(context).delete(shift);
                    })
                    .setNegativeButton(R.string.delete_shift_no, (dialogInterface, i) -> {
                        // If the alert "no" button was clicked, revert the swipe operation by simply notifying
                        // the recyclerView that an item was changed in that position, causing the recyclerView
                        // to Re-Populate that shift's item view.
                        notifyItemChanged(position);
                    })
                    .show();
        }

        /**
         * Toggle shift paid
         */
        public void clickPaidUnpaid(){
            shift.setPaid(!shift.isPaid());
            ShiftRepository.getInstance(context).update(shift);
        }

        private void animateSelectedCheckmark(final boolean checked){
            cardView.post(() -> {
                // Animate the selected checkmark icon
                int cx = selectionCircleSelected.getWidth() / 2;
                int cy = selectionCircleSelected.getHeight() / 2;
                float finalRadius = (float) Math.hypot(cx, cy);

                Animator anim = ViewAnimationUtils.createCircularReveal(
                        selectionCircleSelected,
                        cx,
                        cy,
                        checked ? 0 : finalRadius,
                        checked ? finalRadius : 0);

                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        if(!checked)
                            selectionCircleSelected.setVisibility(View.INVISIBLE);
                    }
                });

                if(checked)
                    selectionCircleSelected.setVisibility(View.VISIBLE);

                anim.start();
            });
        }
    }
}
