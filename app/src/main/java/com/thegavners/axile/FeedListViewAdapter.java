package com.thegavners.axile;

//Created By: Carlos Mbendera
// Copyright Carlos Mbenderea

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

// The Custom List View for the Stream Feed

class FeedListViewAdapter extends BaseAdapter {

    private final Context context;
    private final List<FeedRow> feedRowList;

    FeedListViewAdapter(Context context, List<FeedRow> feedRowList) {

        this.context = context;
        this.feedRowList = feedRowList;
    }

    @Override
    public int getCount() {
        return feedRowList.size();
    }

    @Override
    public Object getItem(int position) {
        return feedRowList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        FeedListViewAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.game_feed_item, null);

            viewHolder = new FeedListViewAdapter.ViewHolder();

            viewHolder.viewerIcon = convertView.findViewById(R.id.imageView8);

            viewHolder.profilePhoto = convertView.findViewById(R.id.profilePhoto);

            viewHolder.username = convertView.findViewById(R.id.gameTitle);

            viewHolder.thumbnail = convertView.findViewById(R.id.thumbnail);

            viewHolder.views = convertView.findViewById(R.id.viewers);


            convertView.setTag(viewHolder);

        } else {
            viewHolder = (FeedListViewAdapter.ViewHolder) convertView.getTag();

        }
        FeedRow feedRow = feedRowList.get(position);


        viewHolder.username.setText(feedRow.getUsername());

        viewHolder.viewerIcon.setImageDrawable(GameFeed.eye);

        Picasso.get()
                .load(feedRow.getThumbnailURL())
                .placeholder(R.color.colorAccent)
                .into(viewHolder.thumbnail);

        Picasso.get()
                .load(feedRow.getUserImageURL())
                .placeholder(R.color.colorAccent)
                .into(viewHolder.profilePhoto);

        viewHolder.views.setText(feedRow.getViews());

        return convertView;
    }

    class ViewHolder {

        ImageView viewerIcon;
        ImageView profilePhoto;
        TextView username;
        ImageView thumbnail;
        TextView  views;


    }
}
