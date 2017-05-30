var page = require('webpage').create();
var system = require('system');
var fs = require('fs');

page.settings.userAgent = 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36';

var testindex = 0;
var loadInProgress = false;

var ctx = {
    stepInterval: 3000, //ms
    cpf: "",
    senha: "",
    extratoPeriodo: "", // 3, 7, 15, 30, 45, 60 dias
    outDir: "", // adicionar / no fim
    extratoFile: "",
    cartaoFile: ""
};

function extractArgs(){
    var args = system.args;
    for (var i = 0; i < args.length; i++) {
        var argParts = args[i].split('=');

        if(argParts.length == 2){
            ctx[argParts[0].trim()] = argParts[1].trim();
        }
    }
}

var hasContent = function(array, contentText) {
    for (var i = 0; i < array.length; i++) {
        var el = array[i];

        if(el.text.trim() === contentText){
            return el;
        }
    }

    return null;
};

page.onConsoleMessage = function (msg) {
    console.log(msg);
};

page.onLoadStarted = function () {
    loadInProgress = true;
    console.log("request iniciada");
};

page.onLoadFinished = function () {
    loadInProgress = false;
    console.log("request finalizada");
};

var steps = [
    // 1: abrir pagina
    function () {
        page.open("https://www.santandernet.com.br/");
    },

    // 2: Inserir CPF
    function () {
        page.evaluate(function (ctx) {
            var doc = document.querySelector("frame[name=Principal]").contentWindow.document;
            doc = doc.querySelector("frame[name=MainFrame]").contentWindow.document;

            var cpf = doc.querySelector("#txtCPF");
            cpf.value = ctx.cpf;

            doc.frmLogin.EscolhaAcesso.value = "accessCPF";
            doc.frmLogin.action = "CPFPre_Bridge.asp";
            doc.frmLogin.submit();

            //doc.querySelector("input.loginOk").click();

        }, ctx);
    },

    // 3: debug
    function () {
        page.render(ctx.outDir + 'santander1.pdf', {format: 'pdf', quality: '100'});
    },

    // 4: inserir senha
    function () {
        page.evaluate(function (ctx, hasContent) {
            var doc = document.querySelector("frame[name=Principal]").contentWindow.document;
            doc = doc.querySelector("frame[name=MainFrame]").contentWindow.document;

            var inputSenha = doc.querySelector("#txtSenha");
            inputSenha.value = ctx.senha;

            //contentWindow.tratapwd();
            //doc.frmLogin.submit();

            var btnContinuar = hasContent(doc.querySelectorAll("#divBotoes a"), "continuar");
            btnContinuar.click();
        }, ctx, hasContent);
    },

    // 5: debug
    function () {
        page.render(ctx.outDir + 'santander2.pdf', {format: 'pdf', quality: '100'})
    },

    // 6: Selecionar periodo Extrato
    function () {
        page.evaluate(function (ctx, hasContent) {
            var doc = document.querySelector("frame[name=Principal]").contentWindow.document;
            doc = doc.querySelector("frame[name=Corpo]").contentWindow.document;
            doc = doc.querySelector("iframe[name=iframePainel]").contentWindow.document;

            var periodo = doc.querySelector("#extrato [name=cboSelectPeriodoExtrato]");
            periodo.value = ctx.extratoPeriodo;

            var btnVisualizar = hasContent(doc.querySelectorAll("#extrato a"), "visualizar");
            btnVisualizar.click();
        }, ctx, hasContent);
    },

    // 7: debug
    function () {
        page.render(ctx.outDir + 'santander3.pdf', {format: 'pdf', quality: '100'})
    },

    // 8: Tela de extrato
    function () {
        var content = page.evaluate(function () {
            var doc = document.querySelector("frame[name=Principal]").contentWindow.document;
            doc = doc.querySelector("frame[name=Corpo]").contentWindow.document;
            doc = doc.querySelector("iframe[name=iframePrinc]").contentWindow.document;
            doc = doc.querySelector("iframe[name=extrato]").contentWindow.document;

            var table = doc.frmExtrato;

            return table.outerHTML;
        });

        fs.write(ctx.outDir + ctx.extratoFile, content, 'w');
    },

    // 9: Menu Cartoes
    function () {
        page.evaluate(function (hasContent) {
            var doc = document.querySelector("frame[name=Principal]").contentWindow.document;
            doc = doc.querySelector("frame[name=Menu]").contentWindow.document;

            var menu = hasContent(doc.querySelectorAll("a"), "Cartões");
            menu.click();
        }, hasContent);
    },

    // 10: debug
    function () {
        page.render(ctx.outDir + 'santander4.pdf', {format: 'pdf', quality: '100'})
    },

    // 11: Link Faturas
    function () {
        page.evaluate(function (hasContent) {
            var doc = document.querySelector("frame[name=Principal]").contentWindow.document;
            doc = doc.querySelector("frame[name=Corpo]").contentWindow.document;

            var link = hasContent(doc.querySelectorAll("#montaMenu a"), "Faturas");
            link.click();
        }, hasContent)
    },

    // 12: debug
    function () {
        page.render(ctx.outDir + 'santander5.pdf', {format: 'pdf', quality: '100'})
    },

    // 13: Link Nome do Cartao
    function () {
        page.evaluate(function (hasContent) {
            var doc = document.querySelector("frame[name=Principal]").contentWindow.document;
            doc = doc.querySelector("frame[name=Corpo]").contentWindow.document;
            doc = doc.querySelector("iframe[name=iframePrinc]").contentWindow.document;

            var link = hasContent(doc.querySelectorAll("#cartaoTitular a"), "SANTANDER ELITE MASTER VG");
            link.click();
        }, hasContent)
    },

    // 14: debug
    function () {
        page.render(ctx.outDir + 'santander6.pdf', {format: 'pdf', quality: '100'})
    },

    // 15: Tabela Fatura
    function () {
        var content = page.evaluate(function () {
            var doc = document.querySelector("frame[name=Principal]").contentWindow.document;
            doc = doc.querySelector("frame[name=Corpo]").contentWindow.document;
            doc = doc.querySelector("iframe[name=iframePrinc]").contentWindow.document;
            doc = doc.querySelector("iframe[name=iDetalhes]").contentWindow.document;

            var table = doc.querySelector("#detFatura table");

            return table.outerHTML;
        });

        fs.write(ctx.outDir + ctx.cartaoFile, content, 'w');
    }
];

// ---

extractArgs();

interval = setInterval(function () {
    if (!loadInProgress && typeof steps[testindex] == "function") {
        console.log("Passo " + (testindex + 1));
        steps[testindex]();
        testindex++;
    }
    if (typeof steps[testindex] != "function") {
        console.log("Processo finalizado.");
        phantom.exit();
    }
}, ctx.stepInterval);

