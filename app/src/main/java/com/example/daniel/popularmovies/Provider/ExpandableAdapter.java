package com.example.daniel.popularmovies.Provider;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.daniel.popularmovies.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Daniel on 21/11/2015.
 */
public class ExpandableAdapter extends BaseExpandableListAdapter{

    private Context context;
    private List<String> listHeaders;
    private HashMap<String, String> listExpandableReviews;

    public ExpandableAdapter(Context context, List<String> listHeaders, HashMap<String, String> listExpandableReviews){
        this.context=context;
        this.listHeaders=listHeaders;
        this.listExpandableReviews=listExpandableReviews;
    }

    @Override
    public int getGroupCount() {
        return listHeaders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listExpandableReviews.get(this.listHeaders.get(groupPosition)).length();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listHeaders.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listExpandableReviews.get(this.listHeaders.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_review_author_item, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.authorReview);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_review_content_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.contentReview);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
