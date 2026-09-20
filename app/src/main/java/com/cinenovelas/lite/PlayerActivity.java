package com.cinenovelas.lite;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class PlayerActivity extends Activity {

    private SimpleExoPlayer player;
    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url =
            getIntent().getStringExtra("url");

        String titulo =
            getIntent().getStringExtra("titulo");

        LinearLayout tela =
            new LinearLayout(this);

        tela.setOrientation(
            LinearLayout.VERTICAL
        );

        tela.setBackgroundColor(
            Color.BLACK
        );

        TextView tituloView =
            new TextView(this);

        if (
            titulo == null ||
            titulo.equals("")
        ) {
            titulo = "NOVELASPLAY";
        }

        tituloView.setText(titulo);
        tituloView.setTextColor(Color.WHITE);
        tituloView.setTextSize(20);
        tituloView.setPadding(
            20,
            20,
            20,
            20
        );

        tela.addView(
            tituloView,
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        );

        playerView =
            new PlayerView(this);

        playerView.setUseController(true);

        tela.addView(
            playerView,
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
            )
        );

        setContentView(tela);

        player =
            new SimpleExoPlayer.Builder(this)
                .build();

        playerView.setPlayer(player);

        if (
            url != null &&
            !url.equals("")
        ) {

            MediaItem item =
                MediaItem.fromUri(url);

            player.setMediaItem(item);

            player.prepare();

            player.setPlayWhenReady(true);

            player.addListener(
                new Player.Listener() {

                    @Override
                    public void onPlaybackStateChanged(
                        int playbackState
                    ) {

                        if (
                            playbackState ==
                            Player.STATE_ENDED
                        ) {

                            finish();
                        }
                    }
                }
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (player != null) {
            player.play();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (player != null) {

            player.release();

            player = null;
        }
    }
}
