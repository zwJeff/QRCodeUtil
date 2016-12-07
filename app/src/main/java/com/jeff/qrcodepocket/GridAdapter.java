package com.jeff.qrcodepocket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 张武 on 2016/8/26.
 */
public class GridAdapter extends BaseAdapter {
    private ArrayList<String> mNameList = new ArrayList<String>();
    private ArrayList<Integer> mIconResourceIdList;
    private LayoutInflater mInflater;
    private Context mContext;

    public GridAdapter(Context context, ArrayList<String> nameList,ArrayList<Integer> mIconResourceIdList) {
        this.mIconResourceIdList=mIconResourceIdList;
        mNameList = nameList;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mNameList.size();
    }

    public Object getItem(int position) {
        return mNameList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        GridViewTag viewTag;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_view_child, null);
            // construct an item tag
            viewTag = new GridViewTag((TextView) convertView.findViewById(R.id.funtion_name_text), (ImageView) convertView.findViewById(R.id.grid_view_child_mIcon));
            convertView.setTag(viewTag);
        } else {
            viewTag = (GridViewTag) convertView.getTag();
        }

        // set name
        viewTag.mName.setText(mNameList.get(position));
        viewTag.mIcon.setImageResource(mIconResourceIdList.get(position));

        return convertView;
    }

    class GridViewTag {

        protected TextView mName;
        protected ImageView mIcon;

        public GridViewTag(TextView layout, ImageView mIcon) {
            this.mName = layout;
            this.mIcon = mIcon;
        }
    }
}
