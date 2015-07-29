package com.codepath.simpletodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends Activity {

    private ArrayList<ListItem> listItems;
    private CustomListAdapter customListAdapter;

    private ListView lvItems;
    private EditText etNewItem;
    private EditText etPriorityPicker;
    private EditText etDatePicker;
    private EditText etTimePicker;
    private DBHelper dbHelper;

    private int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvItems = (ListView) findViewById(R.id.lvItems);
        etNewItem = (EditText) findViewById(R.id.etNewItem);
        etPriorityPicker = (EditText) findViewById(R.id.etPriorityPicker);
        etDatePicker = (EditText) findViewById(R.id.etDatePicker);
        etTimePicker = (EditText) findViewById(R.id.etTimePicker);

        etNewItem.setFocusable(true);
        etNewItem.setFocusableInTouchMode(true);
        etNewItem.requestFocus();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        initializeDatabase();
        readItemsFromDb();

        View header = (View) getLayoutInflater().inflate(R.layout.list_row_header, null);
        customListAdapter = new CustomListAdapter(this, listItems);

        lvItems.addHeaderView(header);
        lvItems.setAdapter(customListAdapter);

        setupListViewListener();
        setupPriorityPicker();
        setupDatePickerListener();
        setupTimePickerListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        // Process the changes from EditItemActivity and Update the list
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            int itemPosition = result.getIntExtra("itemPosition", 0);
            String itemValue = result.getStringExtra("itemValue");
            String itemNewValue = result.getStringExtra("itemNewValue");
            String itemNewPriority = result.getStringExtra("itemNewPriority");
            String itemNewDueTime = result.getStringExtra("itemNewDueTime");

            if (itemNewDueTime == null) {
                itemNewDueTime = customListAdapter.getItemDueTime(itemPosition);
            }
            if (itemNewPriority == null) {
                itemNewPriority = customListAdapter.getItemPriority(itemPosition);
            }

            if (updateItemInDb(itemPosition, itemValue, itemNewValue, itemNewDueTime, ListItem.setItemPriority(itemNewPriority))) {
                ListItem.removeFromItemValueList(itemValue);
                ListItem newListItem = new ListItem(itemNewValue, itemNewDueTime, ListItem.setItemPriority(itemNewPriority));
                listItems.remove(itemPosition);
                listItems.add(itemPosition, newListItem);
                customListAdapter.notifyDataSetChanged();
            }

            resetEditTextFields();
        }
    }

    private void initializeDatabase() {
        dbHelper = new DBHelper(this);
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Since there is a header for the list view, reduce position by 1
                final int pos = position - 1;

                // Setup a Dialog Listener for Deletion
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                // Yes button clicked. Delete the item.
                                ListItem.removeFromItemValueList(listItems.get(pos).getItemValue());
                                deleteItemFromDb(listItems.get(pos).getItemValue());
                                listItems.remove(pos);
                                customListAdapter.notifyDataSetChanged();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                // No button clicked. Do not delete item.
                                break;

                            default:
                                // Default action is not to delete.
                                break;
                        }
                    }
                };

                // Build an alert dialog to get user confirmation before deletion.
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure you want to delete?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Since there is a header for the list view, reduce position by 1
                final int pos = position - 1;
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                intent.putExtra("itemPosition", pos);
                intent.putExtra("itemValue", customListAdapter.getItemValue(pos));
                intent.putExtra("itemPriority", customListAdapter.getItemPriority(pos));
                intent.putExtra("itemDueTime", customListAdapter.getItemDueTime(pos));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    // Pick a priority
    private void setupPriorityPicker() {
        final String LOW = ListItem.ItemPriority.LOW.toString();
        final String MEDIUM = ListItem.ItemPriority.MEDIUM.toString();
        final String HIGH = ListItem.ItemPriority.HIGH.toString();
        final CharSequence[] priorityTypes = {LOW, MEDIUM, HIGH};
        etPriorityPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int priority) {
                        switch (priority) {
                            case 0:
                                etPriorityPicker.setText(LOW);
                                break;
                            case 1:
                                etPriorityPicker.setText(MEDIUM);
                                break;
                            case 2:
                                etPriorityPicker.setText(HIGH);
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
        etDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                etDatePicker.setText(formatDate(monthOfYear, dayOfMonth, year));
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
        etTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                etTimePicker.setText(formatTime(hourOfDay, minute));
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
        cal.set(Calendar.HOUR, hour);
        cal.set(Calendar.MINUTE, minute);
        Date d = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        time = dateFormat.format(d);
        return time;
    }

    private void resetEditTextFields() {
        etNewItem.setText("");
        etPriorityPicker.setText("");
        etDatePicker.setText("");
        etTimePicker.setText("");
    }

    // Read items from SQLite DB
    private void readItemsFromDb() {
        int numItemsInDb = dbHelper.getNumRows();
        if (numItemsInDb > 0) {
            listItems = dbHelper.getAllItems();
        } else {
            listItems = new ArrayList<ListItem>();
        }
    }

    // Write items to SQLite DB
    private boolean writeItemToDb(String itemValue, String itemDueTime, ListItem.ItemPriority itemPriority) {
        if (listItems != null && ListItem.checkIfItemAlreadyExists(itemValue)) {
            Toast.makeText(getApplicationContext(), "Item already exists", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            dbHelper.insertItem(itemValue, itemDueTime, itemPriority == null ? ListItem.ItemPriority.MEDIUM.toString() : itemPriority.toString());
            return true;
        }
    }

    // Update an item in SQLite DB
    private boolean updateItemInDb(int itemPosition, String itemValue, String itemNewValue, String itemNewDueTime, ListItem.ItemPriority itemNewPriority) {
        if (listItems != null && ListItem.checkIfItemAlreadyExists(itemNewValue) && !itemNewValue.equals(listItems.get(itemPosition).getItemValue())) {
            Toast.makeText(getApplicationContext(), "Item already exists. Original item preserved!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            dbHelper.updateItem(itemValue, itemNewValue, itemNewDueTime, itemNewPriority == null ? ListItem.ItemPriority.MEDIUM.toString() : itemNewPriority.toString());
            return true;
        }
    }

    // Delete an existing item from the SQLite DB
    private void deleteItemFromDb(String itemValue) {
        dbHelper.deleteItem(itemValue);
        resetEditTextFields();
    }

    // Add new item
    public void onAddItem(View view) {
        String itemValue = etNewItem.getText().toString();
        String itemPriorityString = etPriorityPicker.getText().toString();
        ListItem.ItemPriority itemPriority = ListItem.ItemPriority.MEDIUM;
        if ((itemValue.length() == 0) || (itemValue.length() > 0 && itemValue.trim().length() == 0)) {
            Toast.makeText(getApplicationContext(), "Please enter a valid item", Toast.LENGTH_SHORT).show();
        } else if (itemValue.length() > 0) {
            if (itemPriorityString.length() != 0) {
                itemPriority = ListItem.setItemPriority(itemPriorityString);
            }
            String itemDueTime = etDatePicker.getText().toString().trim() + " " + etTimePicker.getText().toString().trim();
            if (itemDueTime.trim().equals("")) {
                itemDueTime = "No Due Date";
            } else if ((etDatePicker.getText().toString().trim().equals("") && !etTimePicker.getText().toString().trim().equals("")) ||
                    (!etDatePicker.getText().toString().trim().equals("") && etTimePicker.getText().toString().trim().equals(""))) {
                Toast.makeText(getApplicationContext(), "You must pick both Date & Time or nothing", Toast.LENGTH_SHORT).show();
                return;
            }
            if (writeItemToDb(itemValue.trim(), itemDueTime, itemPriority)) {
                ListItem newListItem = new ListItem(itemValue, itemDueTime, itemPriority);
                customListAdapter.add(newListItem);
                customListAdapter.notifyDataSetChanged();
            }
        }
        resetEditTextFields();
    }

    // Read items from file
    // Deprecated, Now using SQLite DB
    @Deprecated
    private void readItemsFromFile() {
//        File filesDir = getFilesDir();
//        File todoFile = new File(filesDir, "todo.txt");
//        try {
//            items = new ArrayList<String>(FileUtils.readLines(todoFile));
//        } catch (IOException e) {
//            items = new ArrayList<String>();
//        }
    }

    // Write items to file
    // Deprecated, Now using SQLite DB
    @Deprecated
    private void writeItemsToFile() {
//        File filesDir = getFilesDir();
//        File todoFile = new File(filesDir, "todo.txt");
//        try {
//            FileUtils.writeLines(todoFile, items);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
