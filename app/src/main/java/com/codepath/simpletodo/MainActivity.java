package com.codepath.simpletodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private ArrayList<ListItem> listItems;
    private CustomListAdapter customListAdapter;

    private ListView lvItems;
    private EditText etNewItem;
    private DBHelper dbHelper;

    private int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNewItem = (EditText) findViewById(R.id.etNewItem);
        etNewItem.setFocusable(true);
        etNewItem.setFocusableInTouchMode(true);
        etNewItem.requestFocus();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        lvItems = (ListView) findViewById(R.id.lvItems);

        initializeDatabase();
        readItemsFromDb();
        // readItemsFromFile();

        customListAdapter = new CustomListAdapter(this, listItems);
        lvItems.setAdapter(customListAdapter);

        setupListViewListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        // Process the changes from EditItemActivity and Update the list
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            int itemPosition = result.getIntExtra("itemPosition", 0);
            String itemValue = customListAdapter.getItemValue(itemPosition);
            String itemNewValue = result.getStringExtra("itemNewValue");
            // TODO: Change hardcoded values
            String creationTime = "testUDate";
            String dueTime = "testUDate";
            // ListItem newListItem = new ListItem(itemNewValue, creationTime, dueTime, null);
            if (updateItemInDb(itemValue, itemNewValue, creationTime, dueTime, null)) {
                ListItem newListItem = new ListItem(itemNewValue, creationTime, dueTime);
                listItems.remove(itemPosition);
                listItems.add(itemPosition, newListItem);
                customListAdapter.notifyDataSetChanged();
            }
            // writeItemsToFile();
        }
    }

    private void initializeDatabase() {
        dbHelper = new DBHelper(this);
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
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
                                // writeItemsToFile();
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
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                intent.putExtra("itemPosition", position);
                intent.putExtra("itemValue", customListAdapter.getItemValue(position));
                startActivityForResult(intent, REQUEST_CODE);
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
    private void writeItemToDb(String itemValue, String itemCreationTime, String itemDueTime, ListItem.ItemPriority itemPriority) {
        if (listItems != null && ListItem.checkIfItemAlreadyExists(itemValue)) {
            Toast.makeText(getApplicationContext(), "Item already exists", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.insertItem(itemValue, itemCreationTime, itemDueTime, itemPriority == null ? ListItem.ItemPriority.MEDIUM.toString() : itemPriority.toString());
            // ListItem newListItem = new ListItem(itemValue, itemCreationTime, itemDueTime, itemPriority);
            ListItem newListItem = new ListItem(itemValue, itemCreationTime, itemDueTime);
            customListAdapter.add(newListItem);
            etNewItem.setText("");
        }
    }

    // Update an item in SQLite DB
    private boolean updateItemInDb(String itemValue, String itemNewValue, String itemUpdateTime, String itemDueTime, ListItem.ItemPriority itemPriority) {
        if (listItems != null && ListItem.checkIfItemAlreadyExists(itemNewValue)) {
            Toast.makeText(getApplicationContext(), "Item already exists. Original item preserved!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            dbHelper.updateItem(itemValue, itemNewValue, itemUpdateTime, itemDueTime, itemPriority == null ? ListItem.ItemPriority.MEDIUM.toString() : itemPriority.toString());
            return true;
        }
    }

    // Delete an existing item from the SQLite DB
    private void deleteItemFromDb(String itemValue) {
        dbHelper.deleteItem(itemValue);
    }

    // Add new item
    public void onAddItem(View v) {
        String itemValue = etNewItem.getText().toString();
        if ((itemValue.length() == 0) || (itemValue.length() > 0 && itemValue.trim().length() == 0)) {
            Toast.makeText(getApplicationContext(), "Please enter a valid item", Toast.LENGTH_SHORT).show();
        } else if (itemValue.length() > 0) {
            // TODO: change hardcoded values
            writeItemToDb(itemValue.trim(), "testIDate", "testIDate", null);
            // writeItemsToFile();
        }
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
