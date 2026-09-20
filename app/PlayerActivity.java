package com.cinenovelas.lite;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import android.net.Uri;
import android.graphics.Color;
import android.view.Gravity;

public class PlayerActivity extends Activity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = getIntent().getStringExtra("url");
        String titulo = getIntent().getStringExtra("titulo");

        LinearLayout tela = new LinearLayout(this);
        tela.setOrientation(LinearLayout.VERTICAL);
        tela.setBackgroundColor(Color.BLACK);

        TextView tituloView = new TextView(this);

        tituloView.setText(
            titulo == null
                ? "NOVELASPLAY"
                : titulo
        );

        tituloView.setTextColor(Color.WHITE);
        tituloView.setTextSize(20);
        tituloView.setGravity(Gravity.CENTER_VERTICAL);
        tituloView.setPadding(25, 20, 15, 20);

        tela.addView(
            tituloView,
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        );

        videoView = new VideoView(this);

        tela.addView(
            videoView,
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        );

        setContentView(tela);

        MediaController controles =
            new MediaController(this);

        controles.setAnchorView(videoView);

        videoView.setMediaController(controles);

        if (url != null && !url.equals("")) {

            videoView.setVideoURI(
                Uri.parse(url)
            );

            videoView.requestFocus();

            videoView.setOnPreparedListener(
                mp -> {

                    mp.setLooping(false);

                    videoView.start();
                }
            );

            videoView.setOnCompletionListener(
                mp -> {

                    finish();
                }
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (videoView != null) {
            videoView.pause();
        }
    }
}
