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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
         * PLAYER
         */
        webView.addJavascriptInterface(
            new PlayerBridge(),
            "AndroidPlayer"
        );


        /*
         * LEITURA HTML
         */
        webView.addJavascriptInterface(
            new SiteBridge(),
            "AndroidSite"
        );


        /*
         * PÁGINA RENDERIZADA
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
     * =====================================
     * PLAYER
     * =====================================
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
     * =====================================
     * BAIXA HTML PÚBLICO
     * =====================================
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


                            if (
                                !permitida(
                                    endereco
                                )
                            ) {

                                enviarSite(
                                    callback,
                                    0,
                                    "",
                                    "URL não permitida."
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

        if (
            stream == null
        ) {

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

                    String js =
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
                        js,
                        null
                    );
                }
            }
        );
    }



    /*
     * =====================================
     * WEBVIEW OCULTO / RENDERIZADO
     * =====================================
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
            !permitida(
                endereco
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
        s.setMediaPlaybackRequiresUserGesture(true);


        if (
            android.os.Build.VERSION.SDK_INT >= 21
        ) {

            s.setMixedContentMode(
                WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            );
        }


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

                            3000
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
     * =====================================
     * RESULTADO DA RENDERIZAÇÃO
     * =====================================
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
                        + escapar(
                            url
                        )
                        + "','"
                        + escapar(
                            erro
                        )
                        + "')";


                    webView.evaluateJavascript(
                        js,
                        null
                    );
                }
            }
        );
    }



    /*
     * =====================================
     * DOMÍNIOS PERMITIDOS
     * =====================================
     */

    private boolean permitida(
        String url
    ) {

        if (
            url == null
        ) {

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
     * =====================================
     * CONVERTE RESULTADO JAVASCRIPT
     * =====================================
     */

    private String javascriptString(
        String valor
    ) {

        try {

            if (
                valor == null ||
                valor.equals(
                    "null"
                )
            ) {

                return "";
            }


            JSONArray array =
                new JSONArray(
                    "["
                    + valor
                    + "]"
                );


            return array.getString(
                0
            );

        } catch (
            Exception e
        ) {

            return "";
        }
    }



    /*
     * =====================================
     * ESCAPE JAVASCRIPT
     * =====================================
     */

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
