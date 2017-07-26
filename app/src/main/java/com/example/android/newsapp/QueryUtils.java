package com.example.android.newsapp;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    static List<News> fetchNewsData(String requestedUrl) {
        URL url = createUrl(requestedUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        return extractFeatureFromJson(jsonResponse);
    }

    private static URL createUrl(String requestedUrl) {
        URL url = null;

        try {
            url = new URL(requestedUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }

        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = reader.readLine();
            }
        }
        return stringBuilder.toString();
    }

    private static List<News> extractFeatureFromJson(String newsJSON) {
        if (newsJSON.isEmpty()) {
            return null;
        }

        List<News> news = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(newsJSON);
            JSONObject newsObject;
            JSONArray newsArray;


            if (jsonObject.has("response")) {
                newsObject = jsonObject.getJSONObject("response");
            } else {
                return null;
            }
            if (newsObject.has("results")) {
                newsArray = newsObject.getJSONArray("results");
            } else {
                return null;
            }


            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject currentNews = newsArray.getJSONObject(i);
                String title, section, author, date;
                Uri thumbnailUri, newsUri;

                if (currentNews.has("webTitle")) {
                    title = currentNews.getString("webTitle");
                } else {
                    title = "Title";
                }

                if (currentNews.has("sectionName")) {
                    section = currentNews.getString("sectionName");
                } else {
                    section = "Section";
                }

                String uri1 = "https://i.embed.ly/1/display/resize?key=1e6a1a1efdb011df84894040444cdc60&url=http%3A%2F%2Fstatic.guim.co.uk%2Ficons%2Fsocial%2Fog%2Fgu-logo-fallback.png";
                if (currentNews.has("fields")) {
                    JSONObject fields = currentNews.getJSONObject("fields");
                    if (fields.has("thumbnail")) {
                        thumbnailUri = Uri.parse(fields.getString("thumbnail"));
                    } else {
                        thumbnailUri = Uri.parse(uri1);
                    }

                    if (fields.has("byline")) {
                        author = fields.getString("byline");
                    } else {
                        author = "";
                    }
                } else {
                    thumbnailUri = Uri.parse(uri1);
                    author = "";
                }

                String uri2 = "https://www.theguardian.com";
                if (currentNews.has("webUrl")) {
                    newsUri = Uri.parse(currentNews.getString("webUrl"));
                } else {
                    newsUri = Uri.parse(uri2);
                }


                if (currentNews.has("webPublicationDate")) {
                    date = currentNews.getString("webPublicationDate");
                } else date = "";


                News news_item = new News(title, section, thumbnailUri, newsUri, date, author);
                news.add(news_item);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return news;
    }
}
