package com.cinenovelas.lite;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import android.media.MediaPlayer;

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

        if (titulo == null) {
            titulo = "NOVELASPLAY";
        }

        tituloView.setText(titulo);
        tituloView.setTextColor(Color.WHITE);
        tituloView.setTextSize(20);
        tituloView.setPadding(20, 20, 20, 20);

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
                0,
                1
            )
        );

        setContentView(tela);

        MediaController controller =
            new MediaController(this);

        controller.setAnchorView(videoView);

        videoView.setMediaController(controller);

        if (url != null && !url.equals("")) {

            videoView.setVideoURI(
                Uri.parse(url)
            );

            videoView.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {

                    @Override
                    public void onPrepared(
                        MediaPlayer mp
                    ) {
                        videoView.start();
                    }
                }
            );
        }
    }
}
