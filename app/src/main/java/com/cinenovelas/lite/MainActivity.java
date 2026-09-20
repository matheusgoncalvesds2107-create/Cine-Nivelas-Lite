package com.cinenovelas.lite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import org.json.JSONArray;

public class MainActivity extends Activity {

    private WebView webView;
    private FrameLayout root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = new FrameLayout(this);

        webView = new WebView(this);

        root.addView(
            webView,
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        );

        setContentView(root);

        WebSettings settings =
            webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        if (
            android.os.Build.VERSION.SDK_INT >= 21
        ) {

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
         * PLAYER EXOPLAYER
         */
        webView.addJavascriptInterface(
            new PlayerBridge(),
            "AndroidPlayer"
        );


        /*
         * WEBVIEW INVISÍVEL
         */
        webView.addJavascriptInterface(
            new RenderBridge(),
            "AndroidRendered"
        );


        webView.loadUrl(
            "file:///android_asset/index.html"
        );
    }



    /*
     * ===============================
     * PLAYER
     * ===============================
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
     * ===============================
     * PÁGINA RENDERIZADA
     * ===============================
     */

    public class RenderBridge {

        @JavascriptInterface
        public void findAssistir(
            final String endereco,
            final String callback
        ) {

            runOnUiThread(
                new Runnable() {

                    @Override
                    public void run() {

                        abrirRenderizador(
                            endereco,
                            callback
                        );
                    }
                }
            );
        }
    }



    private void abrirRenderizador(
        final String endereco,
        final String callback
    ) {

        if (
            endereco == null ||
            !endereco.startsWith(
                "https://noveflix.lol/"
            )
        ) {

            enviarRender(
                callback,
                "",
                "URL não permitida."
            );

            return;
        }


        final WebView oculto =
            new WebView(this);


        /*
         * 1 x 1 pixel e invisível.
         */
        FrameLayout.LayoutParams params =
            new FrameLayout.LayoutParams(
                1,
                1
            );

        root.addView(
            oculto,
            params
        );

        oculto.setAlpha(
            0.01f
        );


        WebSettings s =
            oculto.getSettings();

        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadsImagesAutomatically(false);

        s.setMediaPlaybackRequiresUserGesture(
            true
        );


        oculto.setWebChromeClient(
            new WebChromeClient()
        );


        oculto.setWebViewClient(

            new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(
                    WebView view,
                    String url
                ) {

                    return !permitida(
                        url
                    );
                }


                @Override
                public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request
                ) {

                    String url =
                        request
                        .getUrl()
                        .toString();

                    return !permitida(
                        url
                    );
                }


                @Override
                public void onPageFinished(
                    final WebView view,
                    String url
                ) {

                    super.onPageFinished(
                        view,
                        url
                    );


                    /*
                     * Dá um tempinho para o JS
                     * da página terminar.
                     */
                    new Handler()
                        .postDelayed(

                            new Runnable() {

                                @Override
                                public void run() {

                                    procurarAssistir(
                                        view,
                                        callback
                                    );
                                }

                            },

                            2500
                        );
                }
            }
        );


        oculto.loadUrl(
            endereco
        );
    }



    private void procurarAssistir(
        final WebView oculto,
        final String callback
    ) {

        /*
         * Só procura destinos públicos
         * relacionados ao botão ASSISTIR.
         *
         * Não procura mídia, tokens,
         * m3u8 ou URLs internas de player.
         */

        String js =
            "(function(){"
            +
            "var els=document.querySelectorAll("
            +
            "'a,button,[role=\"button\"],[data-href],[data-url]'"
            +
            ");"
            +
            "for(var i=0;i<els.length;i++){"
            +
            "var e=els[i];"
            +
            "var t=(e.innerText||e.textContent||'')"
            +
            ".replace(/\\s+/g,' ')"
            +
            ".trim()"
            +
            ".toLowerCase();"
            +
            "if(t.indexOf('assistir')>=0){"
            +
            "var u="
            +
            "e.href||"
            +
            "e.getAttribute('href')||"
            +
            "e.getAttribute('data-href')||"
            +
            "e.getAttribute('data-url')||"
            +
            "e.getAttribute('formaction')||"
            +
            "'';"
            +
            "if(u){return u;}"
            +
            "}"
            +
            "}"
            +
            "return '';"
            +
            "})()";


        oculto.evaluateJavascript(

            js,

            value -> {

                String resultado =
                    javascriptString(
                        value
                    );


                if (
                    resultado != null &&
                    permitida(
                        resultado
                    )
                ) {

                    enviarRender(
                        callback,
                        resultado,
                        ""
                    );

                } else {

                    enviarRender(
                        callback,
                        "",
                        "Nenhuma página pública de ASSISTIR encontrada após renderizar."
                    );
                }


                root.removeView(
                    oculto
                );

                oculto.destroy();
            }
        );
    }



    /*
     * SOMENTE O PRÓPRIO SITE
     */

    private boolean permitida(
        String url
    ) {

        if (url == null) {
            return false;
        }

        return
            url.startsWith(
                "https://noveflix.lol/"
            )
            ||
            url.startsWith(
                "https://www.noveflix.lol/"
            );
    }



    /*
     * CONVERTE O RESULTADO DO
     * evaluateJavascript
     */

    private String javascriptString(
        String valor
    ) {

        try {

            if (
                valor == null ||
                valor.equals("null")
            ) {

                return "";
            }

            JSONArray a =
                new JSONArray(
                    "[" + valor + "]"
                );

            return a.getString(
                0
            );

        } catch (Exception e) {

            return "";
        }
    }



    /*
     * DEVOLVE RESULTADO AO INDEX.HTML
     */

    private void enviarRender(
        final String callback,
        final String url,
        final String erro
    ) {

        runOnUiThread(
            new Runnable() {

                @Override
                public void run() {

                    String js =
                        callback
                        + "('"
                        + escapar(url)
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
        String texto
    ) {

        if (
            texto == null
        ) {

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
