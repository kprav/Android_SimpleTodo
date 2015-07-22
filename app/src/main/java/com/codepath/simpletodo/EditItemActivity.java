package com.codepath.simpletodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;


public class EditItemActivity extends Activity {

    EditText etUpdateItem;

    int itemPosition;
    String itemValue;
    String itemNewValue;

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
        editItem(itemPosition, itemValue);
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
        // Invoked on clicking SAVE. Pass changes back to parent activity.
        Intent result = new Intent();
        itemNewValue = etUpdateItem.getText().toString();
        if ((itemNewValue.length() == 0) || (itemNewValue.length() > 0  && itemNewValue.trim().length() == 0)) {
            Toast.makeText(getApplicationContext(), "Please enter a valid item", Toast.LENGTH_SHORT).show();
        }
        else if (itemNewValue.length() > 0) {
            result.putExtra("itemPosition", itemPosition);
            result.putExtra("itemNewValue", itemNewValue.trim());
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
