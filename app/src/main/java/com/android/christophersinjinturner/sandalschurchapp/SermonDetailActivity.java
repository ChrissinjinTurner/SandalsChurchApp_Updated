package com.android.christophersinjinturner.sandalschurchapp;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

/**
 * This handles the displaying of the Sermon Detail
 *
 * Fullscreen implementation was added using these websites as help:
 * https://github.com/GeoffLedak/ExoplayerFullscreen/blob/master/app/src/main/java/com/geoffledak/exoplayerfullscreen/MainActivity.java
 * https://codelabs.developers.google.com/codelabs/exoplayer-intro/index.html#0
 */
public class SermonDetailActivity extends AppCompatActivity {

    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";

    private Uri sermonUri;
    private PlayerView mExoPlayerView;
    private SimpleExoPlayer player;
    private MediaSource mVideoSource;
    private boolean mExoPlayerFullscreen = false;
    private FrameLayout mFullScreenButton;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;

    private int mResumeWindow;
    private long mResumePosition;

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

        sermonUri = Uri.parse(sermon.getMp4_sd());

        mExoPlayerView = findViewById(R.id.sermonVideo);

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }
    }

    /**
     * Helps with resuming the video on a screen orientation change
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);

        super.onSaveInstanceState(outState);
    }

    /**
     * Default setup for exoplayer video
     */
    private void initExoPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());
        mExoPlayerView.setPlayer(player);

        boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

        if (haveResumePosition) {
            mExoPlayerView.getPlayer().seekTo(mResumeWindow, mResumePosition);
        }
    }

    /**
     * Handles when the fullscreen button is pressed while already in full screen
     */
    private void initFullscreenDialog() {
        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    /**
     * Opens the full screen view
     */
    private void openFullscreenDialog() {
        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(SermonDetailActivity.this, R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    /**
     * closes the full screen view
     */
    private void closeFullscreenDialog() {
        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        ((FrameLayout) findViewById(R.id.sermonVideoFrame)).addView(mExoPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(SermonDetailActivity.this, R.drawable.ic_fullscreen_expand));
    }

    /**
     * Handles the creation and implementation of the button that changes fullscreen
     */
    private void initFullscreenButton() {
        PlayerControlView controlView = mExoPlayerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });
    }

    /**
     * When the view is loaded, this handles starting the video and setting the URI
     */
    @Override
    protected void onResume() {
        super.onResume();
        initExoPlayer();
        if (mExoPlayerView != null) {

            mExoPlayerView = findViewById(R.id.sermonVideo);
            initFullscreenDialog();
            initFullscreenButton();

            mVideoSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("Sandals-sermosn")).createMediaSource(sermonUri);
            player.prepare(mVideoSource, true, false);
        }

        if (mExoPlayerFullscreen) {
            ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
            mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(SermonDetailActivity.this, R.drawable.ic_fullscreen_skrink));
            mFullScreenDialog.show();
        }
    }

    /**
     * This is what kills the video so that memory doesnt leak
     */
    @Override
    protected void onPause() {

        super.onPause();

        if (mExoPlayerView != null && mExoPlayerView.getPlayer() != null) {
            mResumeWindow = mExoPlayerView.getPlayer().getCurrentWindowIndex();
            mResumePosition = Math.max(0, mExoPlayerView.getPlayer().getContentPosition());

            player.release();
        }

        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();
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
