package com.codepath.simpletodo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class EditItemActivity extends Activity {

    private EditText etUpdateItem;
    private EditText etUpdateDatePicker;
    private EditText etUpdateTimePicker;

    private int itemPosition;
    private String itemValue;
    private String itemNewValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Get values from intent
        itemPosition = getIntent().getIntExtra("itemPosition", 0);
        itemValue = getIntent().getStringExtra("itemValue");

        // Set the editItem Field
        etUpdateItem = (EditText) findViewById(R.id.etUpdateItem);
        etUpdateDatePicker = (EditText) findViewById(R.id.etUpdateDatePicker);
        etUpdateTimePicker = (EditText) findViewById(R.id.etUpdateTimePicker);

        editItem(itemPosition, itemValue);

        setupDatePickerListener();
        setupTimePickerListener();
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
                                etUpdateDatePicker.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });
    }

    // Pick a time
    private void setupTimePickerListener() {
        final Calendar c = Calendar.getInstance();
        final int mHour = c.get(Calendar.HOUR);
        final int mMinute = c.get(Calendar.MINUTE);
        etUpdateTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = new TimePickerDialog(EditItemActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                etUpdateTimePicker.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                tpd.show();
            }
        });
    }

    private void editItem(int itemPosition, String itemValue) {
        // Set the editItem Field
        etUpdateItem.setFocusable(true);
        etUpdateItem.setFocusableInTouchMode(true);
        etUpdateItem.requestFocus();
        etUpdateItem.setText(itemValue);
        etUpdateItem.setSelection(etUpdateItem.length());
    }

    public void onUpdateItem(View v) {
        // Invoked on clicking UPDATE. Pass changes back to parent activity.
        Intent result = new Intent();
        itemNewValue = etUpdateItem.getText().toString();
        String date = etUpdateDatePicker.getText().toString().trim();
        String time = etUpdateTimePicker.getText().toString().trim();
        String updatedItemDueTime = date + " " + time;
        if (updatedItemDueTime.trim().equals("")) {
            updatedItemDueTime = null;
        }
        if ((itemNewValue.length() == 0) || (itemNewValue.length() > 0 && itemNewValue.trim().length() == 0)) {
            Toast.makeText(getApplicationContext(), "Please enter a valid item", Toast.LENGTH_SHORT).show();
        } else if (itemNewValue.length() > 0) {
            if (updatedItemDueTime != null) {
                if ((date.equals("") && !time.equals("")) || (!date.equals("") && time.equals(""))) {
                    Toast.makeText(getApplicationContext(), "You must pick both Date & Time or nothing", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            result.putExtra("itemPosition", itemPosition);
            result.putExtra("itemNewValue", itemNewValue.trim());
            result.putExtra("updatedItemDueTime", updatedItemDueTime);
            setResult(RESULT_OK, result);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
