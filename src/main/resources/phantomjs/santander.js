var page = require('webpage').create();
var system = require('system');
var fs = require('fs');

page.settings.userAgent = 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36';

var testindex = 0;
var loadInProgress = false;

var ctx = {
    stepInterval: 1000, //ms
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

var run = function(callback){
    try {
        callback();
        return true;
    }catch(e){
        console.log("erro", e);
        return false;
    }
};

var steps = [
    // 1: abrir pagina
    function () {
        page.open("https://www.santandernet.com.br/");
        return true;
    },


    // 2: Inserir CPF
    function () {
        return page.evaluate(function (ctx) {
            var doc = document.querySelector("frame[name=Principal]").contentWindow.document;
            doc = doc.querySelector("frame[name=MainFrame]").contentWindow.document;

            var cpf = doc.querySelector("#txtCPF");
            cpf.value = ctx.cpf;

            doc.frmLogin.EscolhaAcesso.value = "accessCPF";
            doc.frmLogin.action = "CPFPre_Bridge.asp";
            doc.frmLogin.submit();

            return true;
        }, ctx);
    },

    // 3: debug
    function () {
        page.render(ctx.outDir + 'santander1.pdf', {format: 'pdf', quality: '100'});
        return true;
    },

    // 4: inserir senha
    function () {
        return page.evaluate(function (ctx, hasContent) {
            var doc = document.querySelector("frame[name=Principal]").contentWindow.document;
            doc = doc.querySelector("frame[name=MainFrame]").contentWindow.document;

            var inputSenha = doc.querySelector("#txtSenha");
            inputSenha.value = ctx.senha;

            //contentWindow.tratapwd();
            //doc.frmLogin.submit();

            var btnContinuar = hasContent(doc.querySelectorAll("#divBotoes a"), "continuar");
            btnContinuar.click();

            return true;
        }, ctx, hasContent);
    },

    // 5: debug
    function () {
        page.render(ctx.outDir + 'santander2.pdf', {format: 'pdf', quality: '100'});
        return true;
    },

    // 6: Selecionar periodo Extrato
    function () {
        return page.evaluate(function (ctx, hasContent) {
            var doc = document.querySelector("frame[name=Principal]").contentWindow.document;
            doc = doc.querySelector("frame[name=Corpo]").contentWindow.document;
            doc = doc.querySelector("iframe[name=iframePainel]").contentWindow.document;

            var periodo = doc.querySelector("#extrato [name=cboSelectPeriodoExtrato]");
            periodo.value = ctx.extratoPeriodo;

            var btnVisualizar = hasContent(doc.querySelectorAll("#extrato a"), "visualizar");
            btnVisualizar.click();

            return true;
        }, ctx, hasContent);
    },

    // 7: debug
    function () {
        page.render(ctx.outDir + 'santander3.pdf', {format: 'pdf', quality: '100'});
        return true;
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

        if(!content){
            return false;
        }

        fs.write(ctx.outDir + ctx.extratoFile, content, 'w');

        return true;
    },

    // 9: Menu Cartoes
    function () {
        return page.evaluate(function (hasContent) {
            var doc = document.querySelector("frame[name=Principal]").contentWindow.document;
            doc = doc.querySelector("frame[name=Menu]").contentWindow.document;

            var menu = hasContent(doc.querySelectorAll("a"), "Cartões");
            menu.click();

            return true;
        }, hasContent);
    },

    // 10: debug
    function () {
        page.render(ctx.outDir + 'santander4.pdf', {format: 'pdf', quality: '100'});
        return true;
    },

    // 11: Link Faturas
    function () {
        return page.evaluate(function (hasContent) {
            var doc = document.querySelector("frame[name=Principal]").contentWindow.document;
            doc = doc.querySelector("frame[name=Corpo]").contentWindow.document;

            var link = hasContent(doc.querySelectorAll("#montaMenu a"), "Faturas");
            link.click();

            return true;
        }, hasContent)
    },

    // 12: debug
    function () {
        page.render(ctx.outDir + 'santander5.pdf', {format: 'pdf', quality: '100'});
        return true;
    },

    // 13: Link Nome do Cartao
    function () {
        return page.evaluate(function (hasContent) {
            var doc = document.querySelector("frame[name=Principal]").contentWindow.document;
            doc = doc.querySelector("frame[name=Corpo]").contentWindow.document;
            doc = doc.querySelector("iframe[name=iframePrinc]").contentWindow.document;

            var link = hasContent(doc.querySelectorAll("#cartaoTitular a"), "SANTANDER ELITE MASTER VG");
            link.click();

            return true;
        }, hasContent)
    },

    // 14: debug
    function () {
        page.render(ctx.outDir + 'santander6.pdf', {format: 'pdf', quality: '100'});
        return true;
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

        if(!content){
            return false;
        }

        fs.write(ctx.outDir + ctx.cartaoFile, content, 'w');
        return true;
    }
];

// ---

extractArgs();

var one_minute = 1000*60;

var startTime = Date.now();

interval = setInterval(function () {
    if((Date.now() - startTime) > one_minute){
        console.log("Timeout 1 minuto.");
        phantom.exit(1);
    }

    if (!loadInProgress && typeof steps[testindex] == "function") {
        console.log("Passo " + (testindex + 1));

        var success =  steps[testindex]();

        if(!success){
            console.log("Abortando...");
            phantom.exit(1);
        }

        testindex++;
    }
    if (typeof steps[testindex] != "function") {
        console.log("Processo finalizado.");
        phantom.exit();
    }
}, ctx.stepInterval);

