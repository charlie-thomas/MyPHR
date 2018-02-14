package com.csbgroup.myphr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class SimpleAdapter extends ArrayAdapter<Map.Entry<Integer, String>> {

    private List<Map.Entry<Integer, String>> items;

    public SimpleAdapter(@NonNull Context context, List<Map.Entry<Integer, String>> items) {
        super(context, 0, items);
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final Map.Entry<Integer, String> item = items.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item, parent, false);
        }

        if (item != null) {
            TextView title = convertView.findViewById(R.id.simple_text);
            title.setText(item.getValue());
        }

        convertView.setTag(item.getKey());

        return convertView;
    }

}


