package com.codepath.simpletodo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditItemFragment extends DialogFragment {

    private EditText etUpdateItem;
    private EditText etUpdatePriorityPicker;
    private EditText etUpdateDatePicker;
    private EditText etUpdateTimePicker;
    private Button btnUpdateItem;

    private static int itemPosition;
    private static String itemValue;
    private static String itemPriority;
    private static String itemDueTime;

    public EditItemFragment() {
        // Required empty public constructor
    }

    /**
     * @param itemPosition Parameter 1.
     * @param itemValue    Parameter 2.
     * @param itemPriority Parameter 3.
     * @param itemDueTime  Parameter 4.
     * @return A new instance of fragment EditItemFragment.
     */
    public static EditItemFragment newInstance(int itemPosition, String itemValue, String itemPriority, String itemDueTime) {
        EditItemFragment fragment = new EditItemFragment();
        Bundle args = new Bundle();
        args.putString("itemPosition", Integer.toString(itemPosition));
        args.putString("itemValue", itemValue);
        args.putString("itemPriority", itemPriority);
        args.putString("itemDueTime", itemDueTime);
        fragment.setArguments(args);
        EditItemFragment.itemPosition = itemPosition;
        EditItemFragment.itemValue = itemValue;
        EditItemFragment.itemPriority = itemPriority;
        EditItemFragment.itemDueTime = itemDueTime;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fragment_edit_item_theme);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_item, container, false);

        etUpdateItem = (EditText) view.findViewById(R.id.etUpdateItem);
        etUpdatePriorityPicker = (EditText) view.findViewById(R.id.etUpdatePriorityPicker);
        etUpdateDatePicker = (EditText) view.findViewById(R.id.etUpdateDatePicker);
        etUpdateTimePicker = (EditText) view.findViewById(R.id.etUpdateTimePicker);
        btnUpdateItem = (Button) view.findViewById(R.id.btnUpdateItem);

        String title = getArguments().getString("title", "Edit Item");
        getDialog().setTitle(title);

        // Show soft keyboard automatically
        etUpdateItem.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        setupPriorityPicker();
        setupDatePickerListener();
        setupTimePickerListener();
        setupUpdateButtonListener();

        editItem(itemValue, itemPriority, itemDueTime);

        return view;
    }

    // Pick a priority
    private void setupPriorityPicker() {
        final String LOW = ListItem.ItemPriority.LOW.toString();
        final String MEDIUM = ListItem.ItemPriority.MEDIUM.toString();
        final String HIGH = ListItem.ItemPriority.HIGH.toString();
        final CharSequence[] priorityTypes = {LOW, MEDIUM, HIGH};
        etUpdatePriorityPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int priority) {
                        switch (priority) {
                            case 0:
                                etUpdatePriorityPicker.setText(LOW);
                                break;
                            case 1:
                                etUpdatePriorityPicker.setText(MEDIUM);
                                break;
                            case 2:
                                etUpdatePriorityPicker.setText(HIGH);
                                break;
                        }
                        dialog.dismiss();
                    }
                };
                builder.setTitle("Select the priority of this item");
                builder.setSingleChoiceItems(priorityTypes, -1, dialogClickListener);
                dialog = builder.create();
                dialog.show();
            }
        });
    }

    // Pick a date
    private void setupDatePickerListener() {
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        etUpdateDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                etUpdateDatePicker.setText(formatDate(monthOfYear, dayOfMonth, year));
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });
    }

    // Pick a time
    private void setupTimePickerListener() {
        final Calendar c = Calendar.getInstance();
        final int mHour = c.get(Calendar.HOUR_OF_DAY);
        final int mMinute = c.get(Calendar.MINUTE);
        etUpdateTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                etUpdateTimePicker.setText(formatTime(hourOfDay, minute));
                            }
                        }, mHour, mMinute, false);
                tpd.show();
            }
        });
    }

    // Format Date to mm/dd/yyyy format
    private String formatDate(int month, int day, int year) {
        String date = null;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        Date d = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        date = dateFormat.format(d);
        return date;
    }

    // Format time to hh24:mi format
    private String formatTime(int hour, int minute) {
        String time = null;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        Date d = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        time = dateFormat.format(d);
        return time;
    }

    // Update the item with the edits
    private void setupUpdateButtonListener() {
        btnUpdateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemNewValue = etUpdateItem.getText().toString();
                String itemNewPriority = etUpdatePriorityPicker.getText().toString().trim();
                String date = etUpdateDatePicker.getText().toString().trim();
                String time = etUpdateTimePicker.getText().toString().trim();
                String itemNewDueTime = date + " " + time;

                if (itemNewPriority.trim().equals("")) {
                    itemNewPriority = null;
                }
                if (itemNewDueTime.trim().equals("")) {
                    itemNewDueTime = null;
                }

                OnFragmentInteractionListener listener = (OnFragmentInteractionListener) getActivity();
                listener.onFinishEditItem(itemPosition, itemValue, itemNewValue, itemNewPriority, itemNewDueTime);

                dismiss();
            }
        });
    }

    // Set the current values for the item to be edited
    private void editItem(String itemValue, String itemPriority, String itemDueTime) {
        // Set the editItem Field
        etUpdateItem.setFocusable(true);
        etUpdateItem.setFocusableInTouchMode(true);
        etUpdateItem.requestFocus();
        etUpdateItem.setText(itemValue);
        etUpdateItem.setSelection(etUpdateItem.length());
        etUpdatePriorityPicker.setText(itemPriority);
        if (!itemDueTime.equals("No Due Date")) {
            String[] itemDueDateAndTime = itemDueTime.split("\\s+");
            etUpdateDatePicker.setText(itemDueDateAndTime[0]);
            etUpdateTimePicker.setText(itemDueDateAndTime[1]);
        }
    }

    // Activity must implement this method to interact with fragment
    public interface OnFragmentInteractionListener {
        public void onFinishEditItem(int itemPosition, String itemValue, String itemNewValue, String itemNewPriority, String itemNewDueTime);
    }
}
