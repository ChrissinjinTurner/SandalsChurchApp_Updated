package com.android.christophersinjinturner.sandalschurchapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            populateLastSermon();
            populateSeriesList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method connects to the API and grabs the last sermon.
     * @throws IOException
     */
    public void populateLastSermon() throws IOException {
        // sets up the request to the API and makes the request.
        OkHttpClient client = new OkHttpClient();
        String url = "https://sandalschurch.com/api/latest_sermon";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            // if the call fails it will cancel it
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            // this handles the response from the API, creating a Sermon Object with the information.
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String callResponse = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Sermon> sermons = new ArrayList<>();
                        try {
                            final JSONObject sermonObj = new JSONObject(callResponse);
                            Sermon lastSermon = new Sermon();
                            lastSermon.setId(sermonObj.getInt("id"));
                            lastSermon.setTitle(sermonObj.getString("title"));
                            lastSermon.setDesc(sermonObj.getString("desc").replace("&quot;", "\""));
                            lastSermon.setDate(sermonObj.getString("date"));
                            lastSermon.setLength(sermonObj.getInt("length"));
                            lastSermon.setImage_sd(sermonObj.getString("image_sd"));
                            lastSermon.setImage_hd(sermonObj.getString("image_hd"));
                            lastSermon.setMp4_sd(sermonObj.getString("mp4_sd"));
                            lastSermon.setMp4_hd(sermonObj.getString("mp4_hd"));
                            sermons.add(lastSermon);

                            initSermonAdaptor(sermons);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * This method sends a request to the API and grabs all the different series.
     * @throws IOException
     */
    public void populateSeriesList() throws IOException {
        // sets up the request to the API and makes the request.
        OkHttpClient client = new OkHttpClient();
        String url = "https://sandalschurch.com/feeds/roku-v2/library.json";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            // if the call fails it will cancel it
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            // this handles the response from the API, creating a Series Object with the information.
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String callResponse = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Series> series = new ArrayList<>();
                        try {
                            final JSONArray seriesArray = new JSONArray(callResponse);
                            final int n = seriesArray.length();
                            for (int i = 0; i < n; i++) {
                                final JSONObject seriesObj = seriesArray.getJSONObject(i);
                                Series seriesItem = new Series();
                                seriesItem.setId(seriesObj.getInt("id"));
                                seriesItem.setTitle(seriesObj.getString("title"));
                                seriesItem.setDesc(seriesObj.getString("desc").replace("&quot;", "\""));
                                seriesItem.setDate(seriesObj.getString("date"));
                                seriesItem.setNum_sermons(seriesObj.getString("num_sermons"));
                                seriesItem.setImage_sd(seriesObj.getString("image_sd"));
                                seriesItem.setImage_hd(seriesObj.getString("image_hd"));
                                seriesItem.setFeed_url(seriesObj.getString("feed_url"));
                                series.add(seriesItem);
                            }

                            initSeriesAdapter(series);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * Initializes the adapter that will populate the Sermon info on the main page
     * @param sermons a list of sermons gathered from the API
     */
    private void initSermonAdaptor(ArrayList<Sermon> sermons) {
        RecyclerView recyclerView = findViewById(R.id.sermonlist);
        SermonAdapter adapter = new SermonAdapter(sermons, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Iniitalizes the adapter that will populate the Series info on the main page
     * @param series a list of series gathered from the API
     */
    private void initSeriesAdapter(ArrayList<Series> series) {
        RecyclerView recyclerView = findViewById(R.id.seriesList);
        SeriesAdapter adapter = new SeriesAdapter(series, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
