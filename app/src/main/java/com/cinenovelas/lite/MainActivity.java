package com.cinenovelas.lite;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);

        webView.setBackgroundColor(Color.BLACK);

        WebSettings settings =
                webView.getSettings();

        settings.setJavaScriptEnabled(true);

        settings.setDomStorageEnabled(true);

        settings.setAllowFileAccess(true);

        settings.setAllowContentAccess(true);

        settings.setLoadsImagesAutomatically(true);

        settings.setDatabaseEnabled(true);

        settings.setUseWideViewPort(true);

        settings.setLoadWithOverviewMode(true);


        /*
         * Interface do player.
         */
        webView.addJavascriptInterface(
                new AndroidPlayer(),
                "AndroidPlayer"
        );


        /*
         * Interface que baixa
         * as páginas das fontes.
         */
        webView.addJavascriptInterface(
                new AndroidSite(),
                "AndroidSite"
        );


        webView.setWebViewClient(
                new WebViewClient()
        );


        setContentView(webView);


        /*
         * Abre nosso NovelasPlay.
         */
        webView.loadUrl(
                "file:///android_asset/index.html"
        );
    }


    /*
     * =========================================
     * PLAYER
     * =========================================
     */

    public class AndroidPlayer {

        @JavascriptInterface
        public void playList(
                String novela,
                String urls,
                String titulos,
                String aberturas,
                int episodioInicial
        ) {

            try {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                PlayerActivity.class
                        );


                intent.putExtra(
                        "novela",
                        novela
                );


                intent.putExtra(
                        "urls",
                        urls
                );


                intent.putExtra(
                        "titulos",
                        titulos
                );


                intent.putExtra(
                        "aberturas",
                        aberturas
                );


                intent.putExtra(
                        "episodioInicial",
                        episodioInicial
                );


                startActivity(intent);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }


    /*
     * =========================================
     * MOTOR DAS FONTES
     * =========================================
     */

    public class AndroidSite {

        @JavascriptInterface
        public void get(
                final String endereco,
                final String callback
        ) {

            /*
             * Segurança:
             * somente as fontes liberadas.
             */
            if (!permitida(endereco)) {

                responderJavascript(
                        callback,
                        0,
                        "",
                        "URL não permitida."
                );

                return;
            }


            new Thread(
                    new Runnable() {

                        @Override
                        public void run() {

                            HttpURLConnection conexao =
                                    null;

                            try {

                                URL url =
                                        new URL(
                                                endereco
                                        );


                                conexao =
                                        (HttpURLConnection)
                                                url.openConnection();


                                conexao.setRequestMethod(
                                        "GET"
                                );


                                conexao.setConnectTimeout(
                                        15000
                                );


                                conexao.setReadTimeout(
                                        20000
                                );


                                conexao.setInstanceFollowRedirects(
                                        true
                                );


                                /*
                                 * User-Agent parecido
                                 * com navegador Android.
                                 */
                                conexao.setRequestProperty(
                                        "User-Agent",
                                        "Mozilla/5.0 " +
                                        "(Linux; Android 6.0.1) " +
                                        "AppleWebKit/537.36 " +
                                        "(KHTML, like Gecko) " +
                                        "Chrome/55.0 Mobile Safari/537.36"
                                );


                                conexao.setRequestProperty(
                                        "Accept",
                                        "text/html,application/xhtml+xml," +
                                        "application/xml;q=0.9,*/*;q=0.8"
                                );


                                conexao.setRequestProperty(
                                        "Accept-Language",
                                        "pt-BR,pt;q=0.9,en;q=0.8"
                                );


                                conexao.connect();


                                int codigo =
                                        conexao.getResponseCode();


                                InputStream stream;


                                if (
                                        codigo >= 200 &&
                                        codigo < 400
                                ) {

                                    stream =
                                            conexao.getInputStream();

                                } else {

                                    stream =
                                            conexao.getErrorStream();
                                }


                                String html =
                                        lerStream(
                                                stream
                                        );


                                responderJavascript(
                                        callback,
                                        codigo,
                                        html,
                                        ""
                                );


                            } catch (Exception e) {

                                responderJavascript(
                                        callback,
                                        0,
                                        "",
                                        e.getClass()
                                                .getSimpleName()
                                                +
                                                ": "
                                                +
                                                (
                                                        e.getMessage()
                                                        == null
                                                        ?
                                                        "Erro de conexão"
                                                        :
                                                        e.getMessage()
                                                )
                                );

                            } finally {

                                if (conexao != null) {

                                    conexao.disconnect();
                                }
                            }
                        }
                    }
            ).start();
        }
    }


    /*
     * =========================================
     * FONTES PERMITIDAS
     * =========================================
     */

    private boolean permitida(
            String endereco
    ) {

        if (endereco == null) {
            return false;
        }


        try {

            Uri uri =
                    Uri.parse(
                            endereco
                    );


            String protocolo =
                    uri.getScheme();


            String host =
                    uri.getHost();


            if (
                    protocolo == null ||
                    host == null
            ) {

                return false;
            }


            /*
             * Só HTTPS.
             */
            if (
                    !protocolo.equalsIgnoreCase(
                            "https"
                    )
            ) {

                return false;
            }


            /*
             * ASSISTIR FARAH
             */
            if (
                    host.equalsIgnoreCase(
                            "assistirfarah.com"
                    )
                    ||
                    host.equalsIgnoreCase(
                            "www.assistirfarah.com"
                    )
            ) {

                return true;
            }


            /*
             * NETIDIZIS
             */
            if (
                    host.equalsIgnoreCase(
                            "netidizis.club"
                    )
                    ||
                    host.equalsIgnoreCase(
                            "www.netidizis.club"
                    )
            ) {

                return true;
            }


            /*
             * DRAMAS
             */
            if (
                    host.equalsIgnoreCase(
                            "dramas.com.br"
                    )
                    ||
                    host.equalsIgnoreCase(
                            "www.dramas.com.br"
                    )
            ) {

                return true;
            }


            return false;


        } catch (Exception e) {

            return false;
        }
    }


    /*
     * =========================================
     * LÊ HTML
     * =========================================
     */

    private String lerStream(
            InputStream stream
    ) throws Exception {

        if (stream == null) {
            return "";
        }


        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                stream,
                                "UTF-8"
                        )
                );


        StringBuilder resultado =
                new StringBuilder();


        String linha;


        while (
                (linha = reader.readLine())
                        != null
        ) {

            resultado
                    .append(linha)
                    .append("\n");
        }


        reader.close();


        return resultado.toString();
    }


    /*
     * =========================================
     * DEVOLVE RESULTADO PARA O index.html
     * =========================================
     */

    private void responderJavascript(
            final String callback,
            final int codigo,
            final String html,
            final String erro
    ) {

        runOnUiThread(
                new Runnable() {

                    @Override
                    public void run() {

                        try {

                            /*
                             * JSONObject.quote evita
                             * quebrar o JavaScript
                             * quando o HTML tem aspas,
                             * quebra de linha etc.
                             */
                            String js =

                                    "javascript:" +
                                    callback +
                                    "(" +
                                    codigo +
                                    "," +
                                    JSONObject.quote(
                                            html == null
                                                    ?
                                                    ""
                                                    :
                                                    html
                                    ) +
                                    "," +
                                    JSONObject.quote(
                                            erro == null
                                                    ?
                                                    ""
                                                    :
                                                    erro
                                    ) +
                                    ");";


                            webView.loadUrl(js);

                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }
                }
        );
    }


    /*
     * =========================================
     * BOTÃO VOLTAR DO CELULAR
     * =========================================
     */

    @Override
    public void onBackPressed() {

        if (
                webView != null &&
                webView.canGoBack()
        ) {

            webView.goBack();

        } else {

            super.onBackPressed();
        }
    }


    /*
     * =========================================
     * DESTROY
     * =========================================
     */

    @Override
    protected void onDestroy() {

        if (webView != null) {

            webView.removeJavascriptInterface(
                    "AndroidPlayer"
            );

            webView.removeJavascriptInterface(
                    "AndroidSite"
            );

            webView.destroy();

            webView = null;
        }


        super.onDestroy();
    }
}
