var system = require('system');
var fs = require('fs');

var testindex = 0;
var loadInProgress = false;

var ctx = {
    stepInterval: 0, // ms
    timeout: 0, // ms
    cpf: "",
    senha: "",
    extratoPeriodo: "", // 3, 7, 15, 30, 45, 60 dias
    outDir: "", // adicionar / no fim
    extratoFile: "",
    cartaoFile: ""
};

function extractArgs() {
    var args = system.args;
    console.log("args:");
    for (var i = 0; i < args.length; i++) {
        var argParts = args[i].split('=');

        if (argParts.length == 2) {
            ctx[argParts[0].trim()] = argParts[1].trim();
            console.log(args[i])
        }
    }
}

var hasContent = function (array, contentText) {
    for (var i = 0; i < array.length; i++) {
        var el = array[i];

        if (el.text.trim() === contentText) {
            return el;
        }
    }

    return null;
};

var page;

var createPage = function() {
    page && page.close();
    page = require('webpage').create();
    page.settings.userAgent = 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36';

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
};

var print = function () {
    page.render(ctx.outDir + 'santander2.pdf', {format: 'pdf', quality: '100'});
    return true;
};

var logingSteps = [
    // 1, 7: abrir pagina
    function () {
        createPage();
        page.open("https://www.santandernet.com.br/");
        return true;
    },

    // 2, 8: Inserir CPF
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

    // 3, 9: inserir senha
    function () {
        return page.evaluate(function (ctx) {
            var inputSenha = document.querySelector("#senha");
            inputSenha.value = ctx.senha;

            var btnEntrar = document.querySelector("#Entrar");
            btnEntrar.click();

            return true;
        }, ctx);
    }
];

var extratoContaSteps = [
    // 4: Selecionar periodo Extrato
    function () {
        return page.evaluate(function (ctx, hasContent) {
            var link = hasContent(document.querySelectorAll("#subMenu-ctacorrente a"), "Extrato Conta Corrente");
            link.click();
            return true;
        }, ctx, hasContent);
    },

    // 5
    function () {
        return page.evaluate(function (ctx) {
            var linkPeriodo = document.querySelector("#dateRangeExtrato [data-value='" + ctx.extratoPeriodo + "']");
            linkPeriodo.click();
            return true;
        }, ctx);
    },


    // 6: Tela de extrato
    function () {
        var content = page.evaluate(function () {
            var table = document.querySelectorAll(".isban-box-content")[1];
            return table.outerHTML;
        });

        if (!content) {
            return false;
        }

        fs.write(ctx.outDir + ctx.extratoFile, content, 'w');

        return true;
    }
];

var cartaoSteps = [
    // 10: Menu Cartoes
    function () {
        return page.evaluate(function (hasContent) {
            var link = hasContent(document.querySelectorAll("#subMenu-cartoes a"), "Consultar faturas");
            link.click();
            return true;
        }, hasContent);
    },


    // 11: Tabela Fatura
    function () {
        var content = page.evaluate(function () {
            var table = document.querySelectorAll(".tabla_datos table")[0];
            return table.outerHTML;
        });

        if (!content) {
            return false;
        }

        fs.write(ctx.outDir + ctx.cartaoFile, content, 'w');
        return true;
    }
];

// ---

extractArgs();

var steps = logingSteps
    .concat(extratoContaSteps)
    .concat(logingSteps)
    .concat(cartaoSteps);

var startTime = Date.now();

interval = setInterval(function () {
    if ((Date.now() - startTime) > ctx.timeout) {
        console.log("[PROCESS_TIMEOUT]");
        phantom.exit(1);
    }

    if (!loadInProgress && typeof steps[testindex] == "function") {
        console.log("[PASSO #" + (testindex + 1) + "]");

        var success = steps[testindex]();

        // Só passa para o proximo passo se o anterior foi bem sucedido;
        // Se houve erro, executa o passo novamente.
        if (success) {
            testindex++;
        } else {
            console.log("[ERROR]: passo será executado novamente")
        }
    }

    if (typeof steps[testindex] != "function") {
        console.log("[PROCESS_FINISHED]");
        phantom.exit();
    }
}, ctx.stepInterval);

