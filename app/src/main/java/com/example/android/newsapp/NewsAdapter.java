package com.example.android.newsapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class NewsAdapter extends ArrayAdapter<News> {

    NewsAdapter(Activity context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.items_title);
            holder.section = (TextView) convertView.findViewById(R.id.items_section);
            holder.thumbnail = (ImageView) convertView.findViewById(R.id.items_thumbnail);
            holder.date = (TextView) convertView.findViewById(R.id.items_date);
            holder.author = (TextView) convertView.findViewById(R.id.items_author);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final News currentNews = getItem(position);

        holder.title.setText(currentNews.getTitle());
        holder.section.setText(currentNews.getSector());
        Picasso.with(getContext()).load(currentNews.getThumbnailUri()).into(holder.thumbnail);
        holder.date.setText(currentNews.getDate());
        holder.author.setText(currentNews.getAuthor());

        return convertView;
    }

    private static class ViewHolder {
        private TextView title, section, date, author;
        private ImageView thumbnail;
    }

}