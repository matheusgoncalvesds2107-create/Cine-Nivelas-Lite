package com.cinenovelas.lite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
        setContentView(webView);

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(
                WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            );
        }

        webView.setWebChromeClient(
            new WebChromeClient()
        );

        webView.setWebViewClient(
            new WebViewClient()
        );

        /*
         * PLAYER NATIVO
         */
        webView.addJavascriptInterface(
            new PlayerBridge(),
            "AndroidPlayer"
        );

        /*
         * LEITURA DAS PÁGINAS PÚBLICAS
         */
        webView.addJavascriptInterface(
            new SiteBridge(),
            "AndroidSite"
        );

        webView.setLayerType(
            WebView.LAYER_TYPE_HARDWARE,
            null
        );

        webView.loadUrl(
            "file:///android_asset/index.html"
        );
    }


    /*
     * PLAYER
     */

    public class PlayerBridge {

        @JavascriptInterface
        public void playList(
            final String tituloNovela,
            final String urls,
            final String titulos,
            final String aberturas,
            final int episodioInicial
        ) {

            runOnUiThread(
                new Runnable() {

                    @Override
                    public void run() {

                        Intent intent =
                            new Intent(
                                MainActivity.this,
                                PlayerActivity.class
                            );

                        intent.putExtra(
                            "novela",
                            tituloNovela
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

                        startActivity(
                            intent
                        );
                    }
                }
            );
        }
    }


    /*
     * SITE
     */

    public class SiteBridge {

        @JavascriptInterface
        public void get(
            final String endereco,
            final String callback
        ) {

            new Thread(
                new Runnable() {

                    @Override
                    public void run() {

                        HttpURLConnection conexao = null;

                        try {

                            URL url =
                                new URL(
                                    endereco
                                );

                            /*
                             * Nosso app só lê páginas HTTPS
                             * do domínio escolhido.
                             */
                            String protocolo =
                                url.getProtocol();

                            String host =
                                url.getHost();

                            if (
                                !"https".equalsIgnoreCase(
                                    protocolo
                                )
                            ) {

                                enviarSite(
                                    callback,
                                    0,
                                    "",
                                    "Somente HTTPS permitido."
                                );

                                return;
                            }

                            boolean hostPermitido =
                                "noveflix.lol".equalsIgnoreCase(
                                    host
                                )
                                ||
                                "www.noveflix.lol".equalsIgnoreCase(
                                    host
                                );

                            if (!hostPermitido) {

                                enviarSite(
                                    callback,
                                    0,
                                    "",
                                    "Domínio não permitido."
                                );

                                return;
                            }

                            conexao =
                                (HttpURLConnection)
                                url.openConnection();

                            conexao.setRequestMethod(
                                "GET"
                            );

                            conexao.setConnectTimeout(
                                12000
                            );

                            conexao.setReadTimeout(
                                15000
                            );

                            conexao.setInstanceFollowRedirects(
                                true
                            );

                            conexao.setUseCaches(
                                false
                            );

                            conexao.setRequestProperty(
                                "User-Agent",
                                "Mozilla/5.0 (Linux; Android 6.0.1) AppleWebKit/537.36 Chrome/55 Mobile Safari/537.36"
                            );

                            conexao.setRequestProperty(
                                "Accept",
                                "text/html,application/xhtml+xml"
                            );

                            conexao.setRequestProperty(
                                "Accept-Language",
                                "pt-BR,pt;q=0.9"
                            );

                            int status =
                                conexao.getResponseCode();

                            InputStream stream;

                            if (
                                status >= 200 &&
                                status < 400
                            ) {

                                stream =
                                    conexao.getInputStream();

                            } else {

                                stream =
                                    conexao.getErrorStream();
                            }

                            String html =
                                ler(
                                    stream
                                );

                            enviarSite(
                                callback,
                                status,
                                html,
                                ""
                            );

                        } catch (Exception e) {

                            enviarSite(
                                callback,
                                0,
                                "",
                                e.getClass()
                                    .getSimpleName()
                                    + ": "
                                    + e.getMessage()
                            );

                        } finally {

                            if (
                                conexao != null
                            ) {

                                conexao.disconnect();
                            }
                        }
                    }
                }
            ).start();
        }
    }


    private String ler(
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

        StringBuilder texto =
            new StringBuilder();

        String linha;

        while (
            (linha = reader.readLine())
            != null
        ) {

            texto.append(
                linha
            );

            texto.append(
                "\n"
            );
        }

        reader.close();

        return texto.toString();
    }


    private void enviarSite(
        final String callback,
        final int status,
        final String html,
        final String erro
    ) {

        runOnUiThread(
            new Runnable() {

                @Override
                public void run() {

                    String javascript =

                        callback
                        + "("
                        + status
                        + ",'"
                        + escapar(
                            html
                        )
                        + "','"
                        + escapar(
                            erro
                        )
                        + "')";

                    webView.evaluateJavascript(
                        javascript,
                        null
                    );
                }
            }
        );
    }


    private String escapar(
        String texto
    ) {

        if (texto == null) {
            return "";
        }

        return texto

            .replace(
                "\\",
                "\\\\"
            )

            .replace(
                "'",
                "\\'"
            )

            .replace(
                "\r",
                ""
            )

            .replace(
                "\n",
                "\\n"
            )

            .replace(
                "\u2028",
                ""
            )

            .replace(
                "\u2029",
                ""
            );
    }


    @Override
    public void onBackPressed() {

        if (
            webView.canGoBack()
        ) {

            webView.goBack();

        } else {

            super.onBackPressed();
        }
    }
}
