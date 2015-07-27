package com.codepath.simpletodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<ListItem> {

    public CustomListAdapter(Context context, ArrayList<ListItem> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ListItem item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row_item, parent, false);
        }

        // Lookup view for data population
        TextView tvItemValue = (TextView) convertView.findViewById(R.id.tvItemValue);
        TextView tvCreationTime = (TextView) convertView.findViewById(R.id.tvCreationTime);
        TextView tvDueTime = (TextView) convertView.findViewById(R.id.tvDueTime);
        TextView tvPriority = (TextView) convertView.findViewById(R.id.tvPriority);

        // Populate the data into the template view using the data object
        tvItemValue.setText(item.getItemValue());
        tvCreationTime.setText(item.getItemCreationTime());
        tvDueTime.setText(item.getItemDueTime());
        tvPriority.setText(item.getItemPriorityAsString());

        // Return the completed view to render on screen
        return convertView;
    }

    public String getItemValue(int position) {
        ListItem item = getItem(position);
        return item.getItemValue();
    }

}