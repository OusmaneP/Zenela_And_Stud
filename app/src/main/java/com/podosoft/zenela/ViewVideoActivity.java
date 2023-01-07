package com.podosoft.zenela;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class ViewVideoActivity extends AppCompatActivity {

    StyledPlayerView styledPlayerView;
    ExoPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);

        styledPlayerView = findViewById(R.id.styled_player);
        String videoUrl = getIntent().getStringExtra("videoUrl");

        player = new ExoPlayer.Builder(this).build();


        styledPlayerView.setPlayer(player);
        styledPlayerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (player != null)
            player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null)
            player.release();
    }
}