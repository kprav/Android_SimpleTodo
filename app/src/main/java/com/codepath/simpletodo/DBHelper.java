package com.codepath.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.TreeMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TodoItems.db";
    public static final String TODO_TABLE_NAME = "TODO";
    public static final String TODO_COLUMN_ID = "id";
    public static final String TODO_COLUMN_ITEM_TEXT = "item_text";
    public static final String TODO_COLUMN_ITEM_DUE_TIME = "item_due_time";
    public static final String TODO_COLUMN_ITEM_PRIORITY = "item_priority";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "create table " + TODO_TABLE_NAME + " (" +
                TODO_COLUMN_ID + " integer primary key autoincrement, " +
                TODO_COLUMN_ITEM_TEXT + " text not null unique, " +
                TODO_COLUMN_ITEM_DUE_TIME + " text, " +
                TODO_COLUMN_ITEM_PRIORITY + " text)";
        db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlQuery = "drop table if exists " + TODO_TABLE_NAME;
        db.execSQL(sqlQuery);
        onCreate(db);
    }

    // Insert an item into the DB
    public boolean insertItem(String itemName, String itemDueTime, String itemPriority) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODO_COLUMN_ITEM_TEXT, itemName);
        contentValues.put(TODO_COLUMN_ITEM_DUE_TIME, itemDueTime);
        contentValues.put(TODO_COLUMN_ITEM_PRIORITY, itemPriority);
        try {
            db.insert(TODO_TABLE_NAME, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Update an item in the DB
    public boolean updateItem(String itemText, String itemNewText, String itemDueTime, String itemPriority) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODO_COLUMN_ITEM_TEXT, itemNewText);
        contentValues.put(TODO_COLUMN_ITEM_DUE_TIME, itemDueTime);
        contentValues.put(TODO_COLUMN_ITEM_PRIORITY, itemPriority);
        try {
            db.update(TODO_TABLE_NAME, contentValues, TODO_COLUMN_ITEM_TEXT + " = ? ", new String[]{itemText});
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Delete an item in the DB
    public int deleteItem(String itemText) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numRecsDeleted = 0;
        try {
            numRecsDeleted = db.delete(TODO_TABLE_NAME, TODO_COLUMN_ITEM_TEXT + " = ? ", new String[]{itemText});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numRecsDeleted;
    }

    // Get items in the DB as a TreeMap sorted by ID
    public TreeMap<Integer, String> getAllItemNames() {
        TreeMap<Integer, String> itemMap = new TreeMap<Integer, String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "select * from " + TODO_TABLE_NAME + " order by " + TODO_COLUMN_ID;
        Cursor res = db.rawQuery(sqlQuery, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            itemMap.put(res.getInt(res.getColumnIndex(TODO_COLUMN_ID)), res.getString(res.getColumnIndex(TODO_COLUMN_ITEM_TEXT)));
            res.moveToNext();
        }
        return itemMap;
    }

    // Get all list items
    public ArrayList<ListItem> getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "select * from " + TODO_TABLE_NAME + " order by " + TODO_COLUMN_ID;
        Cursor cur = db.rawQuery(sqlQuery, null);
        ArrayList<ListItem> listItems = new ArrayList<ListItem>();
        cur.moveToFirst();
        while (cur.isAfterLast() == false) {
            String itemName = cur.getString(cur.getColumnIndex(DBHelper.TODO_COLUMN_ITEM_TEXT));
            String itemDueTime = cur.getString(cur.getColumnIndex(DBHelper.TODO_COLUMN_ITEM_DUE_TIME));
            String itemPriority = cur.getString(cur.getColumnIndex(DBHelper.TODO_COLUMN_ITEM_PRIORITY));
            ListItem.ItemPriority itemPriorityEnum = itemPriority.equals("H") ? ListItem.ItemPriority.HIGH :
                    itemPriority.equals("M") ? ListItem.ItemPriority.MEDIUM : ListItem.ItemPriority.LOW;
            ListItem listItem = new ListItem(itemName, itemDueTime, itemPriorityEnum);
            listItems.add(listItem);
            cur.moveToNext();
        }
        return listItems;
    }

    // Get total number of items in the DB
    public int getNumRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TODO_TABLE_NAME);
        return numRows;
    }
}
