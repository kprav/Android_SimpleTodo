package com.codepath.simpletodo;

import android.graphics.Color;

import java.util.TreeSet;

public class ListItem implements Comparable<ListItem> {

    private static TreeSet<String> itemValueList = null;

    private String itemValue;
    private String itemDueTime;
    private ItemPriority itemPriority;
    private int itemColor;

    static {
        if (itemValueList == null)
            itemValueList = new TreeSet<String>();
    }

    @Override
    public int compareTo(ListItem another) {
        if (this.itemPriority.ordinal() < another.itemPriority.ordinal())
            return -1;
        else if (this.itemPriority.ordinal() > another.itemPriority.ordinal())
            return 1;
        else
            return 0;
    }

    public enum ItemPriority {
        LOW, MEDIUM, HIGH;

        @Override
        public String toString() {
            String returnValue = "MEDIUM";
            switch (this) {
                case LOW:
                    returnValue = "LOW";
                    break;
                case MEDIUM:
                    returnValue = "MEDIUM";
                    break;
                case HIGH:
                    returnValue = "HIGH";
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            return returnValue;
        }
    }

    // Constructor
    public ListItem(String itemValue, String itemDueTime, ItemPriority itemPriority) {
        this.itemValue = itemValue;
        this.itemDueTime = itemDueTime;
        this.itemPriority = itemPriority;
        this.itemColor = setItemColor(itemPriority);
        itemValueList.add(this.itemValue);
    }

    // Constructor
    public ListItem(String itemValue, String itemDueTime) {
        this.itemValue = itemValue;
        this.itemDueTime = itemDueTime;
        this.itemPriority = ItemPriority.MEDIUM;
        this.itemColor = setItemColor(itemPriority);
        itemValueList.add(this.itemValue);
    }

    public static boolean checkIfItemAlreadyExists(String itemValue) {
        return itemValueList.contains(itemValue);
    }

    public static void removeFromItemValueList(String itemValue) {
        itemValueList.remove(itemValue);
    }

    public static ItemPriority setItemPriority(String string) {
        ItemPriority itemPriority = ItemPriority.MEDIUM;
        if (string != null) {
            switch (string) {
                case "LOW":
                    itemPriority = ItemPriority.LOW;
                    break;
                case "MEDIUM":
                    itemPriority = ItemPriority.MEDIUM;
                    break;
                case "HIGH":
                    itemPriority = ItemPriority.HIGH;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return itemPriority;
    }

    public int setItemColor(ItemPriority itemPriority) {
        int itemColor = Color.BLUE;
        switch (itemPriority) {
            case LOW:
                itemColor = Color.GREEN;
                break;
            case MEDIUM:
                itemColor = Color.BLUE;
                break;
            case HIGH:
                itemColor = Color.RED;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return itemColor;
    }

    public String getItemValue() {
        return this.itemValue;
    }

    public String getItemDueTime() {
        return this.itemDueTime;
    }

    public ItemPriority getItemPriority() {
        return this.itemPriority;
    }

    public int getItemColor() {
        return this.itemColor;
    }

}
