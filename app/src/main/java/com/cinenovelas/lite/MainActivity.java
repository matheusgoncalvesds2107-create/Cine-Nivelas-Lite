package com.cinenovelas.lite;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
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
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient());

        webView.addJavascriptInterface(
                new ApiBridge(),
                "AndroidApi"
        );

        webView.loadUrl("file:///android_asset/index.html");
    }

    public class ApiBridge {

        @JavascriptInterface
        public void get(final String path, final String callback) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    HttpURLConnection connection = null;

                    try {

                        URL url = new URL(
                                "https://cinenovelas.xyz/v1/" + path
                        );

                        connection =
                                (HttpURLConnection) url.openConnection();

                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(15000);
                        connection.setReadTimeout(20000);

                        connection.setRequestProperty(
                                "Accept",
                                "application/json"
                        );

                        connection.setRequestProperty(
                                "User-Agent",
                                "CineNovelas/1.0.8 Android"
                        );

                        int status = connection.getResponseCode();

                        InputStream stream;

                        if (status >= 200 && status < 300) {
                            stream = connection.getInputStream();
                        } else {
                            stream = connection.getErrorStream();
                        }

                        String body = readStream(stream);

                        sendResult(
                                callback,
                                status,
                                body,
                                null
                        );

                    } catch (Exception e) {

                        sendResult(
                                callback,
                                0,
                                "",
                                e.toString()
                        );

                    } finally {

                        if (connection != null) {
                            connection.disconnect();
                        }

                    }

                }
            }).start();

        }
    }

    private String readStream(InputStream stream) throws Exception {

        if (stream == null) {
            return "";
        }

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(stream, "UTF-8")
                );

        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        reader.close();

        return result.toString();
    }

    private void sendResult(
            final String callback,
            final int status,
            final String body,
            final String error
    ) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String safeBody = escapeJs(body);
                String safeError = escapeJs(
                        error == null ? "" : error
                );

                String js =
                        callback +
                        "(" +
                        status +
                        ",'" +
                        safeBody +
                        "','" +
                        safeError +
                        "')";

                webView.evaluateJavascript(js, null);
            }
        });

    }

    private String escapeJs(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "");
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
