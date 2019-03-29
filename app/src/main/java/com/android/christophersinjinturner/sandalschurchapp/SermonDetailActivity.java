package com.android.christophersinjinturner.sandalschurchapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * This handles the displaying of the Sermon Detail
 */
public class SermonDetailActivity extends AppCompatActivity {

    private MediaController mediaController;
    private VideoView sermonVid;

    /**
     * This creates the layout and populates the data.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sermon_detail);

        // sets up the back button
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        // handles converting all the data to the layout items needed to display
        final Sermon sermon = (Sermon) getIntent().getSerializableExtra("sermon");

        TextView sermonTitle = findViewById(R.id.title);
        sermonTitle.setText(sermon.getTitle());

        TextView sermonDate = findViewById(R.id.date);
        sermonDate.setText(sermon.getDate());

        TextView sermonLength = findViewById(R.id.length);
        sermonLength.setText(String.valueOf(sermon.getLength() + " minutes"));

        TextView sermonDesc = findViewById(R.id.desc);
        sermonDesc.setText(sermon.getDesc());
        sermonDesc.setMovementMethod(new ScrollingMovementMethod()); // allows you to scroll if the desc is too long

        // sets up the video player and locks the controls to the videoview.
        sermonVid = findViewById(R.id.sermonVideo);
        mediaController = new MediaController(this);
        sermonVid.setMediaController(mediaController);
        mediaController.setAnchorView(sermonVid);
        Uri uri = Uri.parse(sermon.getMp4_sd());
        sermonVid.setVideoURI(uri);
        sermonVid.requestFocus();
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

    @Override
    public void onDestroy() {
        mediaController.hide();
        sermonVid.stopPlayback();
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AudioServiceContext.getContext(newBase));
    }
}
