package com.android.christophersinjinturner.sandalschurchapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SeriesListActivity extends AppCompatActivity {

    private static ArrayList<Sermon> mSermons = new ArrayList<>();
    private SermonAdapter adapter;

    /**
     * This creates the layout and populates the data.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.series_list);

        // setup for custom action bar with sandals logo
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.custom_actionbar, null);
        actionBar.setCustomView(customView);
        actionBar.setDisplayShowCustomEnabled(true);

        // handles the back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        // handles converting all the data to the layout items needed to display
        final Series series = (Series) getIntent().getSerializableExtra("series");

        final String url = series.getFeed_url();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("CALL", "run: call made");
                    populateSermonList(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        TextView seriesTitle = findViewById(R.id.seriesTitle);
        seriesTitle.setText(series.getTitle());

        TextView seriesDesc = findViewById(R.id.seriesDesc);
        seriesDesc.setText(series.getDesc());
        seriesDesc.setMovementMethod(new ScrollingMovementMethod()); // allows you to scroll if the desc gets too large

        TextView seriesDate = findViewById(R.id.seriesDate);
        seriesDate.setText(series.getDate());

        //attempting to fix bug on pixel
        RecyclerView recyclerView = findViewById(R.id.seriesList);
        adapter = new SermonAdapter(mSermons, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * This is used to populate the list of sermons relating to that series
     * @param url url passed in from the mainActivity when you click on a series
     * @throws IOException
     */
    public void populateSermonList(String url) throws IOException {
        // sets up the request to the API and makes the request.
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Log.d("SERMON", "populateSermonList: in sermons list");
        client.newCall(request).enqueue(new Callback() {
            // cancels if the call fails
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("CALL", "onFailure: call failed\n" + e);
                call.cancel();
            }

            // sets all the data into an object and puts it in an array
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Log.d("RESPONSE", "onResponse: response received");
                final String callResponse = response.body().string();
                SeriesListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Sermon> sermons = new ArrayList<>();
                        try {
                            final JSONArray sermonsArray = new JSONArray(callResponse);
                            final int n = sermonsArray.length();
                            for (int i = 0; i < n; i++) {
                                final JSONObject sermonObj = sermonsArray.getJSONObject(i);
                                Sermon sermonItem = new Sermon();
                                sermonItem.setId(sermonObj.getInt("id"));
                                sermonItem.setTitle(sermonObj.getString("title"));
                                sermonItem.setDesc(sermonObj.getString("desc").replace("&quot;", "\""));
                                sermonItem.setDate(sermonObj.getString("date"));
                                sermonItem.setLength(sermonObj.getInt("length"));
                                sermonItem.setImage_sd(sermonObj.getString("image_sd"));
                                sermonItem.setImage_hd(sermonObj.getString("image_hd"));
                                sermonItem.setMp4_sd(sermonObj.getString("mp4_sd"));
                                sermonItem.setMp4_hd(sermonObj.getString("mp4_hd"));
                                sermons.add(sermonItem);
                            }
                            initSermonAdaptor(sermons);
                            response.close();
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
//        RecyclerView recyclerView = findViewById(R.id.seriesList);
//        SermonAdapter adapter = new SermonAdapter(sermons, this);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.refreshData(sermons);
    }

    /**
     * This method actually sends the view back when the back button is pressed.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
