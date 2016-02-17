package com.rds.revistadasemanacom;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by brunomorais on 15/02/16.
 */
public class CategoryMenuAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<CategoryMenu> menus;
    private static LayoutInflater inflater = null;

    public CategoryMenuAdapter(Activity a, ArrayList<CategoryMenu> m) {
        activity = a;
        menus = m;
        for (CategoryMenu cm : menus) {
        }
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return menus.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clear() {

        menus.clear();

    }

    public void addAll(ArrayList<CategoryMenu> arrayList) {
        menus = arrayList;
    }

    static class ViewHolder {
        private TextView label;
        private TextView counter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder mViewHolder = null;
        CategoryMenu categoryMenu = menus.get(position);
        if (convertView == null) {

            mViewHolder = new ViewHolder();

            vi = inflater.inflate(R.layout.row_menu_layout, parent, false);
            mViewHolder.label = (TextView) vi.findViewById(R.id.listView_cat_label);
            mViewHolder.counter = (TextView) vi.findViewById(R.id.listView_counter);
            vi.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) vi.getTag();
        }


        mViewHolder.label.setText(categoryMenu.getCatName());
        if (categoryMenu.getQuantity() > 0) {
            vi.findViewById(R.id.listView_counter_parent).setAlpha(1.0f);
            mViewHolder.counter.setText(Integer.toString(categoryMenu.getQuantity()));
        } else {
            vi.findViewById(R.id.listView_counter_parent).setAlpha(0.0f);
        }

        return vi;
    }


}
