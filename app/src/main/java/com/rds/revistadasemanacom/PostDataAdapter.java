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
 * Created by Bruno on 15/01/16.
 */
public class PostDataAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<PostData> data;
    private static LayoutInflater inflater = null;

    public PostDataAdapter(Activity a, ArrayList<PostData> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return data.size();
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

        data.clear();

    }

    public void addAll(ArrayList<PostData> arrayList) {
        data = arrayList;
    }

    static class ViewHolder {
        private ImageView icon;
        private TextView label;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder mViewHolder = null;
        PostData postData = data.get(position);
        if (convertView == null) {

            mViewHolder = new ViewHolder();

            vi = inflater.inflate(R.layout.rowlayout, parent, false);
            mViewHolder.icon = (ImageView) vi.findViewById(R.id.listview_icon);
            mViewHolder.label = (TextView) vi.findViewById(R.id.listView_label);
            vi.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) vi.getTag();
        }

        mViewHolder.icon.setImageResource(defineImage(postData.getCategory()));
        mViewHolder.label.setText(postData.getTitle());
        return vi;
    }

    private int defineImage(String category) {
        int drawable = 0;
        switch (category) {
            case "Readed":
                drawable = R.drawable.ic_done_black_36dp;
                break;
            default:
                drawable = R.drawable.ic_news;
        }
        return drawable;
    }

}
