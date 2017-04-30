package com.example.bringo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by huojing on 2/19/17.
 */

public class TravelCategoryAdapter extends BaseAdapter {
    public static HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>();;
    private List<HashMap<String, Object>> list = null;
    private String[] keyString = new String[] {"name", "status"};
    private String itemString;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
    private ArrayList<String> data = new ArrayList<String>();
    private LayoutInflater inflater;
    private TreeSet<Integer> set = new TreeSet<Integer>();

    public TravelCategoryAdapter(Context context, List<HashMap<String, Object>> list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
//        init();
    }

    private void init() {
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < list.size(); i++) {
            isSelected.put(i, false);
        }
    }

    public void addItem(String item) {
        data.add(item);
    }

    public void addSeparatorItem(String item) {
        data.add(item);
        set.add(data.size() - 1);
    }

    public int getItemViewType(int position) {
        return set.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case TYPE_ITEM:
                    convertView = inflater.inflate(R.layout.travel_list_contents_name, null);
                    holder.textView = (TextView) convertView
                            .findViewById(R.id.item2);
                    holder.checkBox = (CheckBox) convertView
                            .findViewById((R.id.aswitch));
                    break;
                case TYPE_SEPARATOR:
                    convertView = inflater.inflate(R.layout.travel_list_category_name, null);
                    holder.textView = (TextView) convertView
                            .findViewById(R.id.item1);
                    holder.checkBox = (CheckBox) convertView
                            .findViewById((R.id.aswitch));
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HashMap<String, Object> map = list.get(position);
        if (map != null) {
            itemString = map.get(keyString[0]).toString();
            holder.textView.setText(itemString);
        }
        holder.checkBox.setChecked(isSelected.get(position));
//        holder.textView.setText(data.get(position));
        return convertView;
    }

    public static class ViewHolder {
        public TextView textView;
        public CheckBox checkBox;
    }
}
