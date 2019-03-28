package com.android.christophersinjinturner.sandalschurchapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SeriesListActivity extends AppCompatActivity {
    /**
     * This creates the layout and populates the data.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.series_list);

        // handles the back button
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        // handles converting all the data to the layout items needed to display
        final Series series = (Series) getIntent().getSerializableExtra("series");

        TextView seriesTitle = findViewById(R.id.seriesTitle);
        seriesTitle.setText(series.getTitle());

        TextView seriesDesc = findViewById(R.id.seriesDesc);
        seriesDesc.setText(series.getDesc());
        seriesDesc.setMovementMethod(new ScrollingMovementMethod()); // allows you to scroll if the desc gets too large

        TextView seriesDate = findViewById(R.id.seriesDate);
        seriesDate.setText(series.getDate());

        String url = series.getFeed_url();

        try {
            populateSermonList(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        client.newCall(request).enqueue(new Callback() {
            // cancels if the call fails
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            // sets all the data into an object and puts it in an array
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("RESPONSE", "onResponse: HELLO");
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
                            Log.d("NEW_SERMON", "run: " + sermons.toString());
                            initSermonAdaptor(sermons);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void initSermonAdaptor(ArrayList<Sermon> sermons) {
        RecyclerView recyclerView = findViewById(R.id.seriesList);
        SermonAdapter adapter = new SermonAdapter(sermons, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
