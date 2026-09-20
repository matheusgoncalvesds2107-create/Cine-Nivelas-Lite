package com.cinenovelas.lite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

                        startActivity(intent);
                    }
                }
            );
        }
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
