package com.android.christophersinjinturner.sandalschurchapp;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

/**
 * This handles the displaying of the Sermon Detail
 */
public class SermonDetailActivity extends AppCompatActivity {

    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";

    private MediaController mediaController;
    private VideoView sermonVid;
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

        // sets up the video player and locks the controls to the videoview.
//        sermonVid = findViewById(R.id.sermonVideo);
//        mediaController = new MediaController(this);
//        sermonVid.setMediaController(mediaController);
//        mediaController.setAnchorView(sermonVid);
        sermonUri = Uri.parse(sermon.getMp4_sd());
//        sermonVid.setVideoURI(uri);
//        sermonVid.requestFocus();
        mExoPlayerView = findViewById(R.id.sermonVideo);

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }
    }

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
        Log.d("InitExoPlayer", "initExoPlayer: Made it here ***");
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());
        Log.d("EXO_Player", "initExoPlayer: " + player);
        Log.d("EXO_PLAYERVIEW", "initExoPlayer: " + mExoPlayerView);
        mExoPlayerView.setPlayer(player);

        mExoPlayerView.getPlayer().seekTo(mResumeWindow, mResumePosition);
    }

    private void initFullscreenDialog() {
        Log.d("FullScreenDialog", "initFullscreenDialog: Made it here ***");
        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }


    private void openFullscreenDialog() {
        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(SermonDetailActivity.this, R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }


    private void closeFullscreenDialog() {
        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        ((FrameLayout) findViewById(R.id.sermonVideoFrame)).addView(mExoPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(SermonDetailActivity.this, R.drawable.ic_fullscreen_expand));
    }


    private void initFullscreenButton() {
        Log.d("FullScreenButton", "initFullscreenButton: Made it here ***");
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

    @Override
    protected void onResume() {
        Log.d("Resume", "onResume: Made it here ***");
        super.onResume();
        initExoPlayer();
        if (mExoPlayerView != null) {

            mExoPlayerView = findViewById(R.id.sermonVideo);
            initFullscreenDialog();
            initFullscreenButton();
            Log.d("MADEITHERE", "onResume: " + sermonUri);

            mVideoSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("Sandals-sermosn")).createMediaSource(sermonUri);
            Log.d("MVIDEO", "onResume: " + mVideoSource);
            Log.d("PLAYER", "onResume: " + player);
            player.prepare(mVideoSource, true, false);
        }

        if (mExoPlayerFullscreen) {
            ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
            mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(SermonDetailActivity.this, R.drawable.ic_fullscreen_skrink));
            mFullScreenDialog.show();
        }
    }

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
