package com.cinenovelas.lite;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class PlayerActivity extends Activity {

    private SimpleExoPlayer player;
    private PlayerView playerView;

    private String[] urls = new String[] {
        "https://www.w3schools.com/html/mov_bbb.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
    };

    private int episodioAtual = 0;

    private TextView tituloView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        tituloView =
            new TextView(this);

        if (
            titulo == null ||
            titulo.equals("")
        ) {
            titulo = "NOVELASPLAY";
        }

        tituloView.setText(
            titulo
        );

        tituloView.setTextColor(
            Color.WHITE
        );

        tituloView.setTextSize(
            20
        );

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

        playerView.setUseController(
            true
        );

        tela.addView(
            playerView,
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
            )
        );

        setContentView(
            tela
        );

        player =
            new SimpleExoPlayer.Builder(this)
                .build();

        playerView.setPlayer(
            player
        );

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

                        proximoEpisodio();
                    }
                }
            }
        );

        carregarEpisodio(
            0
        );
    }


    private void carregarEpisodio(
        int indice
    ) {

        if (
            indice < 0 ||
            indice >= urls.length
        ) {
            return;
        }

        episodioAtual =
            indice;

        tituloView.setText(
            "NOVELASPLAY - Episódio "
            + (indice + 1)
        );

        MediaItem item =
            MediaItem.fromUri(
                urls[indice]
            );

        player.setMediaItem(
            item
        );

        player.prepare();

        player.setPlayWhenReady(
            true
        );
    }


    private void proximoEpisodio() {

        int proximo =
            episodioAtual + 1;

        if (
            proximo <
            urls.length
        ) {

            carregarEpisodio(
                proximo
            );

        } else {

            tituloView.setText(
                "Fim dos episódios disponíveis"
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
