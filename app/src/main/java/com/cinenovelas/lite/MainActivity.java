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
         * Ponte antiga da API.
         */
        webView.addJavascriptInterface(
            new ApiBridge(),
            "AndroidApi"
        );

        /*
         * NOVA PONTE DO PLAYER NATIVO.
         */
        webView.addJavascriptInterface(
            new PlayerBridge(),
            "AndroidPlayer"
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
     * PLAYER NATIVO
     */

    public class PlayerBridge {

        @JavascriptInterface
        public void play(
            final String url,
            final String titulo
        ) {

            if (url == null || url.length() == 0) {
                return;
            }

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
                            "url",
                            url
                        );

                        intent.putExtra(
                            "titulo",
                            titulo
                        );

                        startActivity(intent);
                    }
                }
            );
        }
    }


    /*
     * PONTE HTTP ANTIGA
     */

    public class ApiBridge {

        @JavascriptInterface
        public void get(
            final String path,
            final String callback
        ) {

            new Thread(
                new Runnable() {

                    @Override
                    public void run() {

                        HttpURLConnection c = null;

                        try {

                            URL url =
                                new URL(
                                    "https://cinenovelas.xyz/v1/"
                                    + path
                                );

                            c =
                                (HttpURLConnection)
                                url.openConnection();

                            c.setRequestMethod(
                                "GET"
                            );

                            c.setConnectTimeout(
                                8000
                            );

                            c.setReadTimeout(
                                8000
                            );

                            c.setUseCaches(
                                false
                            );

                            c.setRequestProperty(
                                "Accept",
                                "application/json"
                            );

                            c.setRequestProperty(
                                "User-Agent",
                                "Mozilla/5.0 (Linux; Android 6.0.1)"
                            );

                            int status =
                                c.getResponseCode();

                            InputStream stream;

                            if (
                                status >= 200 &&
                                status < 400
                            ) {

                                stream =
                                    c.getInputStream();

                            } else {

                                stream =
                                    c.getErrorStream();
                            }

                            String body =
                                read(stream);

                            resposta(
                                callback,
                                status,
                                body,
                                ""
                            );

                        } catch (Exception e) {

                            resposta(
                                callback,
                                0,
                                "",
                                e.getClass()
                                    .getSimpleName()
                                    + ": "
                                    + e.getMessage()
                            );

                        } finally {

                            if (c != null) {
                                c.disconnect();
                            }
                        }
                    }
                }
            ).start();
        }
    }


    private String read(
        InputStream in
    ) throws Exception {

        if (in == null) {
            return "";
        }

        BufferedReader br =
            new BufferedReader(
                new InputStreamReader(
                    in,
                    "UTF-8"
                )
            );

        StringBuilder sb =
            new StringBuilder();

        String linha;

        while (
            (linha = br.readLine())
            != null
        ) {

            sb.append(
                linha
            );
        }

        br.close();

        return sb.toString();
    }


    private void resposta(
        final String callback,
        final int status,
        final String body,
        final String erro
    ) {

        runOnUiThread(
            new Runnable() {

                @Override
                public void run() {

                    String js =
                        callback
                        + "("
                        + status
                        + ",'"
                        + escapar(body)
                        + "','"
                        + escapar(erro)
                        + "')";

                    webView.evaluateJavascript(
                        js,
                        null
                    );
                }
            }
        );
    }


    private String escapar(
        String s
    ) {

        if (s == null) {
            return "";
        }

        return s
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\r", "")
            .replace("\n", "\\n");
    }


    @Override
    public void onBackPressed() {

        if (webView.canGoBack()) {

            webView.goBack();

        } else {

            super.onBackPressed();
        }
    }
}
