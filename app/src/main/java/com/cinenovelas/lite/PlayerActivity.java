package com.cinenovelas.lite;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class PlayerActivity extends Activity {

    private SimpleExoPlayer player;
    private PlayerView playerView;

    private TextView tituloView;
    private TextView statusView;

    private Button btnAnterior;
    private Button btnPular;
    private Button btnProximo;

    private String nomeNovela = "NOVELASPLAY";

    private String[] urls;
    private String[] titulos;
    private long[] aberturas;

    private int episodioAtual = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nomeNovela =
            getIntent().getStringExtra("novela");

        String urlsTexto =
            getIntent().getStringExtra("urls");

        String titulosTexto =
            getIntent().getStringExtra("titulos");

        String aberturasTexto =
            getIntent().getStringExtra("aberturas");

        episodioAtual =
            getIntent().getIntExtra(
                "episodioInicial",
                0
            );

        if (nomeNovela == null) {
            nomeNovela = "NOVELASPLAY";
        }

        if (urlsTexto == null) {
            urlsTexto = "";
        }

        if (titulosTexto == null) {
            titulosTexto = "";
        }

        if (aberturasTexto == null) {
            aberturasTexto = "";
        }

        urls =
            urlsTexto.split("\\|");

        titulos =
            titulosTexto.split("\\|");

        String[] tempos =
            aberturasTexto.split("\\|");

        aberturas =
            new long[urls.length];

        for (
            int i = 0;
            i < aberturas.length;
            i++
        ) {

            long valor = 0;

            if (i < tempos.length) {

                try {

                    valor =
                        Long.parseLong(
                            tempos[i]
                        );

                } catch (Exception e) {

                    valor = 0;
                }
            }

            aberturas[i] = valor;
        }


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


        statusView =
            new TextView(this);

        statusView.setTextColor(
            Color.LTGRAY
        );

        statusView.setTextSize(
            14
        );

        statusView.setPadding(
            20,
            12,
            20,
            12
        );

        tela.addView(
            statusView
        );


        LinearLayout botoes =
            new LinearLayout(this);

        botoes.setOrientation(
            LinearLayout.HORIZONTAL
        );

        botoes.setPadding(
            10,
            10,
            10,
            20
        );


        btnAnterior =
            new Button(this);

        btnAnterior.setText(
            "◀ Anterior"
        );


        btnPular =
            new Button(this);

        btnPular.setText(
            "⏩ Pular abertura"
        );


        btnProximo =
            new Button(this);

        btnProximo.setText(
            "Próximo ▶"
        );


        botoes.addView(
            btnAnterior,
            new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
            )
        );

        botoes.addView(
            btnPular,
            new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
            )
        );

        botoes.addView(
            btnProximo,
            new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
            )
        );

        tela.addView(
            botoes
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
                        Player.STATE_READY
                    ) {

                        statusView.setText(
                            "Reproduzindo episódio "
                            + (episodioAtual + 1)
                        );
                    }

                    if (
                        playbackState ==
                        Player.STATE_ENDED
                    ) {

                        proximoEpisodio();
                    }
                }
            }
        );


        btnPular.setOnClickListener(
            new View.OnClickListener() {

                @Override
                public void onClick(
                    View v
                ) {

                    long tempo =
                        aberturas[
                            episodioAtual
                        ];

                    player.seekTo(
                        tempo
                    );

                    player.play();

                    statusView.setText(
                        "Abertura pulada"
                    );
                }
            }
        );


        btnProximo.setOnClickListener(
            new View.OnClickListener() {

                @Override
                public void onClick(
                    View v
                ) {

                    proximoEpisodio();
                }
            }
        );


        btnAnterior.setOnClickListener(
            new View.OnClickListener() {

                @Override
                public void onClick(
                    View v
                ) {

                    episodioAnterior();
                }
            }
        );


        if (
            episodioAtual < 0 ||
            episodioAtual >= urls.length
        ) {

            episodioAtual = 0;
        }


        carregarEpisodio(
            episodioAtual
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

        String tituloEp =
            "Episódio "
            + (indice + 1);

        if (
            indice < titulos.length &&
            titulos[indice] != null &&
            !titulos[indice].equals("")
        ) {

            tituloEp =
                titulos[indice];
        }

        tituloView.setText(
            nomeNovela
            + " - "
            + tituloEp
        );

        statusView.setText(
            "Carregando..."
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

        atualizarBotoes();
    }


    private void proximoEpisodio() {

        int novo =
            episodioAtual + 1;

        if (
            novo < urls.length
        ) {

            carregarEpisodio(
                novo
            );

        } else {

            statusView.setText(
                "Fim dos episódios disponíveis."
            );
        }
    }


    private void episodioAnterior() {

        int novo =
            episodioAtual - 1;

        if (
            novo >= 0
        ) {

            carregarEpisodio(
                novo
            );

        } else {

            statusView.setText(
                "Este é o primeiro episódio."
            );
        }
    }


    private void atualizarBotoes() {

        btnAnterior.setEnabled(
            episodioAtual > 0
        );

        btnProximo.setEnabled(
            episodioAtual <
            urls.length - 1
        );

        btnPular.setEnabled(
            episodioAtual <
            aberturas.length &&
            aberturas[
                episodioAtual
            ] > 0
        );
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
