package com.codepath.simpletodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends Activity {

    private ArrayList<ListItem> listItems;
    private CustomListAdapter customListAdapter;

    private ListView lvItems;
    private EditText etNewItem;
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
        setupDatePickerListener();
        setupTimePickerListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        // Process the changes from EditItemActivity and Update the list
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            int itemPosition = result.getIntExtra("itemPosition", 0);
            String itemValue = customListAdapter.getItemValue(itemPosition);
            String itemNewValue = result.getStringExtra("itemNewValue");
            String updatedItemDueTime = result.getStringExtra("updatedItemDueTime");
            if (updatedItemDueTime == null)
                updatedItemDueTime = customListAdapter.getItemDueTime(itemPosition);

            if (updateItemInDb(itemValue, itemNewValue, updatedItemDueTime, null)) {
                // ListItem newListItem = new ListItem(itemNewValue, updatedItemDueTime, itemPriority);
                ListItem.removeFromItemValueList(itemValue);
                ListItem newListItem = new ListItem(itemNewValue, updatedItemDueTime);
                listItems.remove(itemPosition);
                listItems.add(itemPosition, newListItem);
                customListAdapter.notifyDataSetChanged();
            }

            etNewItem.setText("");
            etDatePicker.setText("");
            etTimePicker.setText("");
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
                startActivityForResult(intent, REQUEST_CODE);
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
                                etDatePicker.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
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
                                etTimePicker.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                tpd.show();
            }
        });
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
    private boolean updateItemInDb(String itemValue, String itemNewValue, String itemDueTime, ListItem.ItemPriority itemPriority) {
        if (listItems != null && ListItem.checkIfItemAlreadyExists(itemNewValue)) {
            Toast.makeText(getApplicationContext(), "Item already exists. Original item preserved!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            dbHelper.updateItem(itemValue, itemNewValue, itemDueTime, itemPriority == null ? ListItem.ItemPriority.MEDIUM.toString() : itemPriority.toString());
            return true;
        }
    }

    // Delete an existing item from the SQLite DB
    private void deleteItemFromDb(String itemValue) {
        dbHelper.deleteItem(itemValue);
        etNewItem.setText("");
        etDatePicker.setText("");
        etTimePicker.setText("");
    }

    // Add new item
    public void onAddItem(View v) {
        String itemValue = etNewItem.getText().toString();
        if ((itemValue.length() == 0) || (itemValue.length() > 0 && itemValue.trim().length() == 0)) {
            Toast.makeText(getApplicationContext(), "Please enter a valid item", Toast.LENGTH_SHORT).show();
        } else if (itemValue.length() > 0) {
            String itemDueTime = etDatePicker.getText().toString().trim() + " " + etTimePicker.getText().toString().trim();
            if (itemDueTime.trim().equals("")) {
                itemDueTime = "No Due Date";
            } else if ((etDatePicker.getText().toString().trim().equals("") && !etTimePicker.getText().toString().trim().equals("")) ||
                    (!etDatePicker.getText().toString().trim().equals("") && etTimePicker.getText().toString().trim().equals(""))) {
                Toast.makeText(getApplicationContext(), "You must pick both Date & Time or nothing", Toast.LENGTH_SHORT).show();
                return;
            }
            if (writeItemToDb(itemValue.trim(), itemDueTime, null)) {
                // ListItem newListItem = new ListItem(itemValue, itemDueTime, itemPriority);
                ListItem newListItem = new ListItem(itemValue, itemDueTime);
                customListAdapter.add(newListItem);
                customListAdapter.notifyDataSetChanged();
            }
        }
        etNewItem.setText("");
        etDatePicker.setText("");
        etTimePicker.setText("");
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
