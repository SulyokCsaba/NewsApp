package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String GUARDIAN_START_URL = "https://content.guardianapis.com/search?",
            GUARDIAN_END_URL = "&api-key=test&show-fields=thumbnail,byline";
    private static final int NEWS_LOADER_ID = 1;
    private String GUARDIAN_REQUESTED_URL = GUARDIAN_START_URL + GUARDIAN_END_URL;
    private NewsAdapter newsAdapter;
    private TextView noNews;
    private ProgressBar progressBar;
    private RelativeLayout first_news;
    private News firstNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView = (ListView) findViewById(R.id.list);
        noNews = (TextView) findViewById(R.id.no_news);
        listView.setEmptyView(noNews);
        first_news = (RelativeLayout) findViewById(R.id.first_item);
        first_news.setVisibility(View.GONE);
        final ArrayList<News> news = new ArrayList<>();
        newsAdapter = new NewsAdapter(MainActivity.this, news);
        listView.setAdapter(newsAdapter);

        progressBar = (ProgressBar) findViewById(R.id.progress);

        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            progressBar.setVisibility(View.GONE);
            first_news.setVisibility(View.GONE);
            noNews.setText(R.string.no_internet);
        } else {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, MainActivity.this);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = (News) parent.getItemAtPosition(position);
                Uri url = news.getNewsUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(url);
                startActivity(i);
            }
        });

        final EditText search = (EditText) findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ConnectivityManager cm =
                        (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (!isConnected) {
                    progressBar.setVisibility(View.GONE);
                    noNews.setVisibility(View.VISIBLE);
                    newsAdapter.clear();
                    noNews.setText(R.string.no_internet);
                } else {
                    if (!s.equals("")) {
                        s = s.toString().replace(" ", "%20");
                        GUARDIAN_REQUESTED_URL = GUARDIAN_START_URL + "q=" + s.toString().toLowerCase() + GUARDIAN_END_URL;
                    }
                    noNews.setVisibility(View.GONE);
                    getLoaderManager().restartLoader(0, null, MainActivity.this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            first_news.setVisibility(View.GONE);
            newsAdapter.insert(firstNews, 0);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            first_news.setVisibility(View.VISIBLE);
            populateFirstItem(firstNews);
            newsAdapter.remove(firstNews);
        }
    }

    private void populateFirstItem(News news) {
        firstNews = news;
        TextView title = (TextView) findViewById(R.id.news_first_title);
        TextView section = (TextView) findViewById(R.id.news_first_section);
        ImageView thumbnail = (ImageView) findViewById(R.id.news_first_image);
        TextView date = (TextView) findViewById(R.id.news_first_date);
        TextView author = (TextView) findViewById(R.id.news_first_author);

        title.setText(news.getTitle());
        section.setText(news.getSector());
        Picasso.with(getApplicationContext()).load(news.getThumbnailUri()).into(thumbnail);
        first_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri url = firstNews.getNewsUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(url);
                startActivity(i);
            }
        });
        date.setText(news.getDate());
        author.setText(news.getAuthor());
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        //return new NewsLoader(this, "https://content.guardianapis.com/search??q=&api-key=test&show-fields=thumbnail");
//        return new NewsLoader(this, "https://content.guardianapis.com/search?q=nature&api-key=test&show-fields=thumbnail,byline");
        return new NewsLoader(this, GUARDIAN_REQUESTED_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        newsAdapter.clear();
        progressBar.setVisibility(View.GONE);

        if (news != null && !news.isEmpty()) {
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                populateFirstItem(news.get(0));
                news.remove(0);
                first_news.setVisibility(View.VISIBLE);
            }
            newsAdapter.addAll(news);
        } else {
            first_news.setVisibility(View.GONE);
            noNews.setText(R.string.no_news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        newsAdapter.clear();
    }
}