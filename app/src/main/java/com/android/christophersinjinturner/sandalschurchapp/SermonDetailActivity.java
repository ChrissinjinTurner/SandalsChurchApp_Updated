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

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

/**
 * This handles the displaying of the Sermon Detail
 */
public class SermonDetailActivity extends AppCompatActivity {

    private MediaController mediaController;
    private VideoView sermonVid;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private long playbackPosition = 0;
    private int currentWindow;
    private boolean playWhenReady = true;
    private Uri sermonUri;

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
//        sermonVid = findViewById(R.id.sermonVideo);
//        mediaController = new MediaController(this);
//        sermonVid.setMediaController(mediaController);
//        mediaController.setAnchorView(sermonVid);
        sermonUri = Uri.parse(sermon.getMp4_hd());
//        sermonVid.setVideoURI(uri);
//        sermonVid.requestFocus();

        // exoplayer setup
        playerView = findViewById(R.id.sermonVideo);
    }

    /**
     * Default setup for exoplayer video
     */
    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView.setPlayer(player);
//        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

        MediaSource mediaSource = buildMediaSource(sermonUri);
        player.prepare(mediaSource, true, false);
    }

    /**
     * Sets up the media source
     * @param uri
     * @return
     */
    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("sandals-church")).
                createMediaSource(uri);
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
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

//    @Override
//    public void onDestroy() {
//        mediaController.hide();
//        sermonVid.stopPlayback();
//        super.onDestroy();
//    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(AudioServiceContext.getContext(newBase));
//    }
}
