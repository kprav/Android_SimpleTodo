package com.codepath.simpletodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
 Editing an item is now done using DialogFragment
 */
@Deprecated
public class EditItemActivity extends Activity {

    private EditText etUpdateItem;
    private EditText etUpdatePriorityPicker;
    private EditText etUpdateDatePicker;
    private EditText etUpdateTimePicker;

    private int itemPosition;
    private String itemValue;
    private String itemPriority;
    private String itemDueTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Get values from intent
        itemValue = getIntent().getStringExtra("itemValue");
        itemPosition = getIntent().getIntExtra("itemPosition", 0);
        itemPriority = getIntent().getStringExtra("itemPriority");
        itemDueTime = getIntent().getStringExtra("itemDueTime");

        // Set the editItem Field
        etUpdateItem = (EditText) findViewById(R.id.etUpdateItem);
        etUpdatePriorityPicker = (EditText) findViewById(R.id.etUpdatePriorityPicker);
        etUpdateDatePicker = (EditText) findViewById(R.id.etUpdateDatePicker);
        etUpdateTimePicker = (EditText) findViewById(R.id.etUpdateTimePicker);

        editItem(itemValue, itemPriority, itemDueTime);

        setupPriorityPicker();
        setupDatePickerListener();
        setupTimePickerListener();
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
                DatePickerDialog dpd = new DatePickerDialog(EditItemActivity.this,
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
                TimePickerDialog tpd = new TimePickerDialog(EditItemActivity.this,
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

    public void onUpdateItem(View v) {
        // Invoked on clicking UPDATE. Pass changes back to parent activity.
        Intent result = new Intent();
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

        if ((itemNewValue.length() == 0) || (itemNewValue.length() > 0 && itemNewValue.trim().length() == 0)) {
            Toast.makeText(getApplicationContext(), "Please enter a valid item", Toast.LENGTH_SHORT).show();
        } else if (itemNewValue.length() > 0) {
            if (itemNewDueTime != null) {
                if ((date.equals("") && !time.equals("")) || (!date.equals("") && time.equals(""))) {
                    Toast.makeText(getApplicationContext(), "You must pick both Date & Time or nothing", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            result.putExtra("itemPosition", itemPosition);
            result.putExtra("itemValue", itemValue);
            result.putExtra("itemNewValue", itemNewValue.trim());
            result.putExtra("itemNewPriority", itemNewPriority);
            result.putExtra("itemNewDueTime", itemNewDueTime);
            setResult(RESULT_OK, result);
            finish();
        }
    }
}
