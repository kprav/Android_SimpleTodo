package com.codepath.simpletodo;

import java.util.ArrayList;

public class ListItem {

    private static ArrayList<String> itemValueList = new ArrayList<String>();

    private String itemValue;
    private String itemCreationTime;
    private String itemDueTime;
    private ItemPriority itemPriority;

    public enum ItemPriority {
        LOW, MEDIUM, HIGH;

        @Override
        public String toString() {
            String returnValue = "M";
            switch (this) {
                case LOW:
                    returnValue = "L";
                    break;
                case MEDIUM:
                    returnValue = "M";
                    break;
                case HIGH:
                    returnValue = "H";
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            return returnValue;
        }
    }

    // Constructor
    public ListItem(String itemValue, String itemCreationTime, String itemDueTime, ItemPriority itemPriority) {
        this.itemValue = itemValue;
        this.itemCreationTime = itemCreationTime;
        this.itemDueTime = itemDueTime;
        this.itemPriority = itemPriority;
        itemValueList.add(this.itemValue);
    }

    // Constructor
    public ListItem(String itemValue, String itemCreationTime, String itemDueTime) {
        this.itemValue = itemValue;
        this.itemCreationTime = itemCreationTime;
        this.itemDueTime = itemDueTime;
        this.itemPriority = ItemPriority.MEDIUM;
        itemValueList.add(this.itemValue);
    }

    public static boolean checkIfItemAlreadyExists(String itemName) {
        return itemValueList.contains(itemName);
    }

    public static void removeFromItemValueList(String itemValue) {
        itemValueList.remove(itemValue);
    }

    public String getItemValue() {
        return this.itemValue;
    }

    public String getItemCreationTime() {
        return this.itemCreationTime;
    }

    public String getItemDueTime() {
        return this.itemDueTime;
    }

    public ItemPriority getItemPriority() {
        return this.itemPriority;
    }

    public String getItemPriorityAsString() {
        return this.itemPriority.toString();
    }
}
