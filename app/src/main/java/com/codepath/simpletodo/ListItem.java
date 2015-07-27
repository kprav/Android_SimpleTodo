package com.codepath.simpletodo;

import java.util.ArrayList;

public class ListItem {

    private static ArrayList<String> itemValueList = new ArrayList<String>();

    private String itemValue;
    private String itemDueTime;
    private ItemPriority itemPriority;

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
        itemValueList.add(this.itemValue);
    }

    // Constructor
    public ListItem(String itemValue, String itemDueTime) {
        this.itemValue = itemValue;
        this.itemDueTime = itemDueTime;
        this.itemPriority = ItemPriority.MEDIUM;
        itemValueList.add(this.itemValue);
    }

    public static boolean checkIfItemAlreadyExists(String itemValue) {
        return itemValueList.contains(itemValue);
    }

    public static void removeFromItemValueList(String itemValue) {
        itemValueList.remove(itemValue);
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

    public String getItemPriorityAsString() {
        return this.itemPriority.toString();
    }
}
