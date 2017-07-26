package com.example.android.newsapp;

import android.net.Uri;

class News {
    private String title, sector, date, author;
    private Uri thumbnailUri, newsUri;

    private News() {
        title = "Title";
        sector = "Sector";
        author = "";
        date = "";
    }

    News(String title, String sector, Uri thumbnailUri, Uri newsUri, String date, String author) {
        this();
        if (title != null && !title.equals("")) this.title = title;
        if (sector != null && !sector.equals("")) this.sector = sector;
        if (thumbnailUri != null) this.thumbnailUri = thumbnailUri;
        if (newsUri != null) this.newsUri = newsUri;
        if (date != null && !date.equals("")) this.date = dateFormat(date);
        if (author != null) this.author = author;
    }

    private String dateFormat(String date) {
        StringBuilder formatDate = new StringBuilder();
        date = date.substring(0, 10);
        String[] dates = date.split("-");
        dates[0] = dates[0].substring(2);
        for (int i = dates.length - 1; i >= 0; i--) {
            formatDate.append(dates[i]);
            if (i != 0) formatDate.append("/");
        }
        return formatDate.toString();
    }

    String getTitle() {
        return title;
    }

    String getSector() {
        return sector;
    }

    Uri getThumbnailUri() {
        return thumbnailUri;
    }

    Uri getNewsUrl() {
        return newsUri;
    }

    String getDate() {
        return date;
    }

    String getAuthor() {
        return author;
    }
}