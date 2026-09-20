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

    private int episodioAtual = 0;

    private String[] urls = new String[] {
        "https://www.w3schools.com/html/mov_bbb.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
    };

    /*
     * Tempo da abertura de cada episódio.
     * Em milissegundos.
     *
     * Episódio 1: 3 segundos
     * Episódio 2: 5 segundos
     * Episódio 3: 5 segundos
     *
     * É só teste.
     */
    private long[] abertura = new long[] {
        3000,
        5000,
        5000
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout tela = new LinearLayout(this);

        tela.setOrientation(
            LinearLayout.VERTICAL
        );

        tela.setBackgroundColor(
            Color.BLACK
        );

        /*
         * TÍTULO
         */
        tituloView = new TextView(this);

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

        /*
         * PLAYER
         */
        playerView = new PlayerView(this);

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

        /*
         * STATUS
         */
        statusView = new TextView(this);

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
            statusView,
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        );

        /*
         * BOTÕES
         */
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

        /*
         * CRIA EXOPLAYER
         */
        player =
            new SimpleExoPlayer.Builder(this)
                .build();

        playerView.setPlayer(
            player
        );

        /*
         * EVENTOS DO PLAYER
         */
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
                            "Reproduzindo Episódio "
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

        /*
         * BOTÃO PULAR ABERTURA
         */
        btnPular.setOnClickListener(
            new View.OnClickListener() {

                @Override
                public void onClick(
                    View v
                ) {

                    long tempo =
                        abertura[
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

        /*
         * BOTÃO PRÓXIMO
         */
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

        /*
         * BOTÃO ANTERIOR
         */
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

        /*
         * COMEÇA NO EPISÓDIO 1
         */
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
            + (episodioAtual + 1)
        );

        statusView.setText(
            "Carregando..."
        );

        MediaItem item =
            MediaItem.fromUri(
                urls[
                    episodioAtual
                ]
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
            novo <
            urls.length
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
