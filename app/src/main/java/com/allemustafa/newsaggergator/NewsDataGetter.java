package com.allemustafa.newsaggergator;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class NewsDataGetter implements Runnable{


    private static final String TAG = "NewsLoaderRunnable";
    private final MainActivity mainActivity;
    private static final String DATA_URL = "https://newsapi.org/v2/sources";
    private static final String DATA_URL2 = "https://newsapi.org/v2/top-headlines";
    private static final String yourAPIKey = "46889552838246108629d12f1213db5e";
    private static RequestQueue queue;
    public NewsDataGetter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        if(!mainActivity.hasNetworkConnection()){
            return;
        }
        try{
        queue = Volley.newRequestQueue(mainActivity);
        Uri.Builder buildURL = Uri.parse(DATA_URL).buildUpon();
        buildURL.appendQueryParameter("apikey", yourAPIKey);
        String urlToUse = buildURL.build().toString();
        StringBuilder sb = new StringBuilder();
            Response.Listener<JSONObject> listener =
                    response -> parseJSON(response);

            Response.ErrorListener error =
                    error1 -> mainActivity.InvalidLocationSelected(error1);

            // Request a string response from the provided URL.
            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(Request.Method.GET, urlToUse,
                            null, listener, error){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("User-Agent", "News-App");
                            return headers;
                        }
                    };

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            handleResults(null);
            return;
        }
    }
    public void run(String source) {
        if(!mainActivity.hasNetworkConnection()){
            return;
        }
        try{
            queue = Volley.newRequestQueue(mainActivity);
            Uri.Builder buildURL = Uri.parse(DATA_URL2).buildUpon();
            buildURL.appendQueryParameter("apikey", yourAPIKey);
            buildURL.appendQueryParameter("sources", source);
            String urlToUse = buildURL.build().toString();
            StringBuilder sb = new StringBuilder();
            Response.Listener<JSONObject> listener =
                    response -> {
                        try {
                            parseSourcesJSON(response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    };

            Response.ErrorListener error =
                    error1 -> mainActivity.InvalidLocationSelected(error1);

            // Request a string response from the provided URL.
            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(Request.Method.GET, urlToUse,
                            null, listener, error){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("User-Agent", "News-App");
                            return headers;
                        }
                    };

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            handleResults(null);
            return;
        }
    }
    private void handleResults(String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            //mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }

/*        final ArrayList<Country> countryList = parseJSON(s);
        if (countryList == null) {
            //mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }*/

        /*mainActivity.runOnUiThread(
                () -> mainActivity.updateData(countryList));*/
    }
    private void parseSourcesJSON(JSONObject s) throws JSONException {
        List<NewsArticles> articles = new ArrayList<NewsArticles>();
        JSONArray sources = s.getJSONArray("articles");
        for(int i=0;i<sources.length();i++){
            JSONObject source = sources.getJSONObject(i);
            JSONObject src = source.getJSONObject("source");
            String name = src.getString("name");
            String author = source.getString("author");
            String title = source.getString("title");
            String description = source.getString("description");
            String url = source.getString("url");
            String urlToImage = source.getString("urlToImage");
            String publishedAt = source.getString("publishedAt");
            articles.add(new NewsArticles(author, title, description, url, urlToImage, publishedAt,name));
        }
        mainActivity.UpdateArticles(articles);
    return;
    }
    private void parseJSON(JSONObject s) {

        Map<String, List<NewsSource>> newsCategories= new HashMap<>();
        newsCategories.put("All",new ArrayList<NewsSource>());
        try {
            JSONArray sources = s.getJSONArray("sources");
            for(int i=0;i<sources.length();i++){
                JSONObject source = sources.getJSONObject(i);
                String title = source.getString("name");
                String id = source.getString("id");
                String description = source.getString("description");
                String url = source.getString("url");
                String category = source.getString("category");
                String country = source.getString("country");
                String language  = source.getString("language");
                NewsSource ns = new NewsSource(title, id, description
                        , url, category, country, language);
                if(!newsCategories.containsKey(category)){
                    ArrayList<NewsSource> list = new ArrayList<NewsSource>();
                    list.add(ns);
                    newsCategories.put(category,list);
                }
                else{
                    ArrayList<NewsSource> list = (ArrayList<NewsSource>) newsCategories.get(category);
                    list.add(ns);
                    newsCategories.put(category,list);
                }
                ArrayList<NewsSource> list = (ArrayList<NewsSource>)newsCategories.get("All");
                list.add(ns);
                newsCategories.put("All",list);
            }
            mainActivity.UpdateData(newsCategories);
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

}