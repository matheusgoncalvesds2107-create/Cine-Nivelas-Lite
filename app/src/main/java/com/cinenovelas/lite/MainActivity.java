<!DOCTYPE html>
<html lang="pt-BR">

<head>

<meta charset="UTF-8">

<meta
    name="viewport"
    content="
        width=device-width,
        initial-scale=1.0,
        maximum-scale=1.0
    "
>

<title>NovelasPlay</title>


<style>

*{
    box-sizing:border-box;
}

body{
    margin:0;
    background:#0d0d0e;
    color:#ffffff;
    font-family:Arial,sans-serif;
}


/* =========================
   TOPO
   ========================= */

.topo{
    height:82px;
    background:#101011;

    display:flex;
    align-items:center;
    justify-content:center;

    border-bottom:1px solid #292929;
}

.logo{
    font-size:29px;
    font-weight:bold;
}

.logo1{
    color:#ef1741;
}

.logo2{
    color:#ffffff;
}


/* =========================
   BUSCA
   ========================= */

.buscaBox{

    display:flex;

    margin:17px;

    background:#050505;

    border-radius:28px;

    overflow:hidden;
}

.buscaBox input{

    flex:1;

    min-width:0;

    border:0;

    outline:0;

    background:#050505;

    color:#ffffff;

    padding:15px 18px;

    font-size:17px;
}

.buscaBox button{

    width:62px;

    border:0;

    background:#e31326;

    color:#ffffff;

    font-size:22px;
}


/* =========================
   CONTEÚDO
   ========================= */

main{
    padding:17px;
}

.status{

    background:#1d1d20;

    border-radius:9px;

    padding:14px;

    margin-bottom:18px;

    color:#bbbbbb;

    line-height:1.5;
}

.ok{
    color:#8cff9b;
}

.erro{
    color:#ff8585;
}


/* =========================
   TÍTULO DA SEÇÃO
   ========================= */

.titulo{

    display:flex;

    align-items:center;

    margin:20px 0 15px;

    font-size:23px;

    font-weight:bold;
}

.linha{

    width:5px;

    height:34px;

    background:#ef1837;

    margin-right:11px;
}


/* =========================
   GRADE
   ========================= */

.grade{

    display:grid;

    grid-template-columns:
        repeat(3,1fr);

    gap:11px;
}

.card{
    min-width:0;
}

.capa{

    height:175px;

    border-radius:7px;

    overflow:hidden;

    display:flex;

    align-items:center;

    justify-content:center;

    padding:9px;

    text-align:center;

    background:
        linear-gradient(
            145deg,
            #53111a,
            #18181b 55%,
            #a51c2f
        );
}

.capa img{

    width:100%;

    height:100%;

    object-fit:cover;
}

.nome{

    margin-top:7px;

    font-size:14px;

    line-height:1.3;

    font-weight:bold;
}

.episodios{

    margin-top:3px;

    font-size:12px;

    color:#999999;
}


/* =========================
   DETALHES
   ========================= */

#detalheTela{
    display:none;
}

.subTopo{

    min-height:70px;

    background:#101011;

    display:flex;

    align-items:center;

    padding:12px 16px;

    border-bottom:
        2px solid #ef1837;
}

.voltar{

    width:44px;

    height:44px;

    border:0;

    border-radius:50%;

    background:#252525;

    color:#ffffff;

    font-size:27px;

    margin-right:13px;
}

.tituloTopo{

    font-size:18px;

    font-weight:bold;
}

.detalhes{
    padding:18px;
}

.painel{

    background:#1d1d20;

    border-radius:10px;

    padding:16px;
}

.painel h2{
    margin-top:0;
}

.info{

    color:#bbbbbb;

    line-height:1.6;
}


/* =========================
   CELULAR MENOR
   ========================= */

@media(
    max-width:420px
){

    .capa{
        height:165px;
    }

    .nome{
        font-size:13px;
    }

}

</style>

</head>


<body>


<!-- =========================
     CATÁLOGO
     ========================= -->

<div id="catalogoTela">


<div class="topo">

    <div class="logo">

        <span class="logo1">
            NOVELAS
        </span>

        <span class="logo2">
            PLAY
        </span>

    </div>

</div>


<div class="buscaBox">

    <input
        id="busca"
        placeholder="
            Pesquisar novela turca...
        "
    >

    <button id="btnBusca">
        🔍
    </button>

</div>


<main>


<div
    id="status"
    class="status"
>

    Carregando catálogo...

</div>


<div class="titulo">

    <div class="linha"></div>

    Novelas Turcas

</div>


<div
    id="grade"
    class="grade"
>
</div>


</main>

</div>



<!-- =========================
     DETALHE
     ========================= -->

<div id="detalheTela">


<div class="subTopo">

    <button
        id="voltar"
        class="voltar"
    >
        ‹
    </button>


    <div
        id="tituloTopo"
        class="tituloTopo"
    >
        Novela
    </div>

</div>


<div class="detalhes">

    <div class="painel">

        <h2 id="tituloDetalhe">
            Novela
        </h2>


        <div
            id="infoDetalhe"
            class="info"
        >
            Carregando...
        </div>

    </div>

</div>


</div>



<script>


/* ================================
   CONFIGURAÇÃO
   ================================ */

var URL_BASE =
    "https://assistirfarah.com/";

var catalogo = [];

var exibindo = [];



/* ================================
   INICIAR
   ================================ */

function iniciar(){


    if(
        typeof AndroidSite ===
        "undefined"
    ){

        mostrarStatus(
            "AndroidSite não encontrado.",
            "erro"
        );

        return;
    }


    mostrarStatus(
        "Buscando catálogo..."
    );


    AndroidSite.get(
        URL_BASE,
        "receberCatalogo"
    );

}



/* ================================
   RECEBE HTML
   ================================ */

function receberCatalogo(
    codigo,
    html,
    erro
){


    if(erro){

        mostrarStatus(
            erro,
            "erro"
        );

        return;
    }


    if(
        codigo < 200
        ||
        codigo >= 400
    ){

        mostrarStatus(
            "HTTP "+codigo,
            "erro"
        );

        return;
    }


    catalogo =
        extrair(
            html
        );


    exibindo =
        catalogo.slice(0);


    mostrarStatus(

        catalogo.length+
        " títulos encontrados ✅",

        "ok"

    );


    montarGrade(
        exibindo
    );

}



/* ================================
   EXTRATOR DO CATÁLOGO
   ================================ */

function extrair(html){


    var doc =

        new DOMParser()
        .parseFromString(

            html,

            "text/html"

        );


    var links =

        doc.querySelectorAll(
            "a[href]"
        );


    var lista = [];

    var usados = {};


    for(
        var i=0;
        i<links.length;
        i++
    ){


        var a =
            links[i];


        var href =

            normalizarUrl(

                a.getAttribute(
                    "href"
                )
                ||
                ""

            );


        if(!href){

            continue;
        }


        /*
         * Ignora a própria home.
         */

        if(
            removerBarraFinal(
                href
            )
            ===
            removerBarraFinal(
                URL_BASE
            )
        ){

            continue;
        }


        var texto =

            limpar(

                a.textContent

            );


        if(!texto){

            continue;
        }


        /*
         * PROCURA A CONTAGEM:
         *
         * 88 episódios
         * 200 episódios
         * 161 episódios
         */

        var match =

            texto.match(

                /(\d+)\s*epis[oó]dios?/i

            );


        /*
         * Caso a contagem esteja
         * no elemento pai.
         */

        if(!match){


            var pai =
                a.parentNode;


            if(pai){


                var textoPai =

                    limpar(

                        pai.textContent

                    );


                var matchPai =

                    textoPai.match(

                        /(\d+)\s*epis[oó]dios?/i

                    );


                if(matchPai){

                    match =
                        matchPai;


                    /*
                     * Se o próprio link
                     * não tinha o texto
                     * completo, usamos
                     * o texto do card.
                     */

                    if(
                        texto.length < 3
                    ){

                        texto =
                            textoPai;

                    }

                }

            }

        }


        if(!match){

            continue;
        }


        /*
         * LIMPA O NOME.
         *
         * Exemplo:
         *
         * Adım FarahMeu Nome é Farah
         * Completa
         * 88 episódios
         */

        var nome =
            texto;


        nome =
            nome.replace(

                /\s*Completa.*$/i,

                ""

            );


        nome =
            nome.replace(

                /\s*\d+\s*epis[oó]dios?.*$/i,

                ""

            );


        nome =
            limpar(
                nome
            );


        /*
         * Se o texto do link ficou
         * ruim, tenta ALT da imagem.
         */

        var img =

            a.querySelector(
                "img"
            );


        if(
            (
                !nome
                ||
                nome.length < 3
            )
            &&
            img
        ){


            var alt =

                limpar(

                    img.getAttribute(
                        "alt"
                    )

                );


            if(alt){

                nome =
                    alt;
            }

        }


        if(
            !nome
            ||
            nome.length < 3
        ){

            continue;
        }


        nome =
            corrigirNome(
                nome
            );


        var chave =

            href.toLowerCase();


        /*
         * Remove duplicados.
         */

        if(
            usados[chave]
        ){

            continue;
        }


        usados[chave] =
            true;



        /* ===========================
           CAPA
           =========================== */

        var capa = "";


        if(img){


            capa =

                img.getAttribute(
                    "data-src"
                )

                ||

                img.getAttribute(
                    "data-lazy-src"
                )

                ||

                img.getAttribute(
                    "data-original"
                )

                ||

                img.getAttribute(
                    "src"
                )

                ||

                "";

        }


        capa =
            normalizarImagem(
                capa
            );



        /* ===========================
           ADICIONA
           =========================== */

        lista.push({


            nome:
                nome,


            episodios:
                match[1],


            url:
                href,


            capa:
                capa

        });

    }


    return lista;

}



/* ================================
   CORRIGE NOMES GRUDADOS
   ================================ */

function corrigirNome(nome){


    nome =
        limpar(
            nome
        );


    if(!nome){

        return "";
    }



    /*
     * Eşref RüyaEşref Rüya
     *
     * vira:
     *
     * Eşref Rüya
     */

    if(
        nome.length % 2 === 0
    ){


        var metade =
            nome.length / 2;


        var esquerda =
            nome.substring(
                0,
                metade
            );


        var direita =
            nome.substring(
                metade
            );


        if(
            esquerda.toLowerCase()
            ===
            direita.toLowerCase()
        ){

            return limpar(
                direita
            );

        }

    }



    /*
     * YargıYargı:
     * Segredos de Família
     */

    var posDoisPontos =

        nome.indexOf(
            ":"
        );


    if(
        posDoisPontos > 0
    ){


        var antes =

            nome.substring(
                0,
                posDoisPontos
            );


        if(
            antes.length % 2 === 0
        ){


            var meioAntes =
                antes.length / 2;


            var p1 =

                antes.substring(
                    0,
                    meioAntes
                );


            var p2 =

                antes.substring(
                    meioAntes
                );


            if(
                p1.toLowerCase()
                ===
                p2.toLowerCase()
            ){


                nome =

                    p2
                    +
                    nome.substring(
                        posDoisPontos
                    );

            }

        }

    }



    /*
     * Adım FarahMeu Nome é Farah
     *
     * vira:
     *
     * Adım Farah / Meu Nome é Farah
     *
     *
     * Sefirin KızıA Filha do Embaixador
     */

    nome =

        nome.replace(

            /([a-záàâãéêíóôõúçğıöşü])([A-ZÁÀÂÃÉÊÍÓÔÕÚÇĞİÖŞÜ])/g,

            "$1 / $2"

        );



    /*
     * Se ficou:
     *
     * Hercai / Hercai:
     * Amor e Vingança
     *
     * remove o primeiro.
     */

    if(
        nome.indexOf(
            " / "
        ) >= 0
    ){


        var partes =

            nome.split(
                " / "
            );


        if(
            partes.length === 2
        ){


            var primeira =

                limpar(
                    partes[0]
                );


            var segunda =

                limpar(
                    partes[1]
                );


            if(

                segunda
                .toLowerCase()
                .indexOf(
                    primeira.toLowerCase()
                )
                ===
                0

            ){

                nome =
                    segunda;

            }

        }

    }


    return limpar(
        nome
    );

}



/* ================================
   MONTA GRADE
   ================================ */

function montarGrade(lista){


    var grade =

        document.getElementById(
            "grade"
        );


    grade.innerHTML = "";


    for(
        var i=0;
        i<lista.length;
        i++
    ){


        var item =
            lista[i];


        var card =

            document.createElement(
                "div"
            );


        card.className =
            "card";


        var visual = "";


        if(
            item.capa
        ){


            visual =

                '<img src="' +
                escapar(
                    item.capa
                ) +
                '" alt="">';


        }else{


            visual =

                escapar(
                    item.nome
                );

        }


        card.innerHTML =


            '<div class="capa">'+
                visual+
            '</div>'+


            '<div class="nome">'+

                escapar(
                    item.nome
                )+

            '</div>'+


            '<div class="episodios">'+

                escapar(
                    item.episodios
                )+

                ' episódios'+

            '</div>';



        card.setAttribute(
            "data-index",
            i
        );


        card.onclick =

            function(){


                var indice =

                    parseInt(

                        this.getAttribute(
                            "data-index"
                        )

                    );


                abrirNovela(

                    exibindo[
                        indice
                    ]

                );

            };


        grade.appendChild(
            card
        );

    }

}



/* ================================
   ABRIR NOVELA
   ================================ */

function abrirNovela(item){


    document
    .getElementById(
        "catalogoTela"
    )
    .style.display =
        "none";


    document
    .getElementById(
        "detalheTela"
    )
    .style.display =
        "block";


    document
    .getElementById(
        "tituloTopo"
    )
    .innerHTML =

        escapar(
            item.nome
        );


    document
    .getElementById(
        "tituloDetalhe"
    )
    .innerHTML =

        escapar(
            item.nome
        );


    document
    .getElementById(
        "infoDetalhe"
    )
    .innerHTML =

        '<strong>'+
        escapar(
            item.episodios
        )+
        ' episódios encontrados.</strong>'+

        '<br><br>'+

        'Página pública:<br>'+

        escapar(
            item.url
        );

}



/* ================================
   PESQUISAR
   ================================ */

function pesquisar(){


    var termo =

        document
        .getElementById(
            "busca"
        )
        .value
        .toLowerCase();


    exibindo = [];


    for(
        var i=0;
        i<catalogo.length;
        i++
    ){


        var nome =

            catalogo[i]
            .nome
            .toLowerCase();


        if(
            nome.indexOf(
                termo
            ) >= 0
        ){


            exibindo.push(
                catalogo[i]
            );

        }

    }


    montarGrade(
        exibindo
    );

}



/* ================================
   NORMALIZA URL
   ================================ */

function normalizarUrl(url){


    if(!url){

        return "";
    }


    url =
        limpar(
            url
        );


    if(
        url.indexOf(
            "javascript:"
        ) === 0
    ){

        return "";
    }


    if(
        url.indexOf(
            "mailto:"
        ) === 0
    ){

        return "";
    }


    if(
        url.charAt(0) === "#"
    ){

        return "";
    }


    if(
        url.indexOf(
            "https://"
        ) === 0
    ){

        return url;
    }


    if(
        url.indexOf(
            "//"
        ) === 0
    ){

        return "https:"+url;
    }


    if(
        url.charAt(0) === "/"
    ){

        return

            "https://assistirfarah.com"+
            url;

    }


    /*
     * Link relativo.
     */

    return

        "https://assistirfarah.com/"+
        url.replace(
            /^\/+/,
            ""
        );

}



/* ================================
   NORMALIZA IMAGEM
   ================================ */

function normalizarImagem(url){


    if(!url){

        return "";
    }


    url =
        limpar(
            url
        );


    if(
        url.indexOf(
            "https://"
        ) === 0
        ||
        url.indexOf(
            "http://"
        ) === 0
    ){

        return url;
    }


    if(
        url.indexOf(
            "//"
        ) === 0
    ){

        return "https:"+url;
    }


    if(
        url.charAt(0) === "/"
    ){

        return

            "https://assistirfarah.com"+
            url;

    }


    return

        "https://assistirfarah.com/"+
        url.replace(
            /^\/+/,
            ""
        );

}



/* ================================
   REMOVE /
   ================================ */

function removerBarraFinal(url){


    if(!url){

        return "";
    }


    return String(url)
        .replace(
            /\/+$/,
            ""
        );

}



/* ================================
   LIMPA TEXTO
   ================================ */

function limpar(texto){


    if(
        texto === null
        ||
        texto === undefined
    ){

        return "";
    }


    return String(texto)

        .replace(
            /\s+/g,
            " "
        )

        .replace(
            /^\s+|\s+$/g,
            ""
        );

}



/* ================================
   ESCAPA HTML
   ================================ */

function escapar(texto){


    if(
        texto === null
        ||
        texto === undefined
    ){

        return "";
    }


    return String(texto)

        .replace(
            /&/g,
            "&amp;"
        )

        .replace(
            /</g,
            "&lt;"
        )

        .replace(
            />/g,
            "&gt;"
        )

        .replace(
            /"/g,
            "&quot;"
        )

        .replace(
            /'/g,
            "&#039;"
        );

}



/* ================================
   STATUS
   ================================ */

function mostrarStatus(
    texto,
    tipo
){


    var box =

        document.getElementById(
            "status"
        );


    box.className =
        "status";


    if(tipo){

        box.className +=
            " "+tipo;

    }


    box.innerHTML =

        escapar(
            texto
        );

}



/* ================================
   EVENTOS
   ================================ */

document
.getElementById(
    "busca"
)
.addEventListener(

    "keyup",

    pesquisar

);


document
.getElementById(
    "btnBusca"
)
.addEventListener(

    "click",

    pesquisar

);


document
.getElementById(
    "voltar"
)
.addEventListener(

    "click",

    function(){


        document
        .getElementById(
            "detalheTela"
        )
        .style.display =
            "none";


        document
        .getElementById(
            "catalogoTela"
        )
        .style.display =
            "block";

    }

);



/* ================================
   COMEÇAR
   ================================ */

setTimeout(

    iniciar,

    500

);

</script>


</body>

</html>
