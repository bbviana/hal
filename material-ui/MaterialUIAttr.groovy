import groovy.xml.MarkupBuilder
import org.jsoup.Jsoup

def out = new File("Material UI - attr.xml")
out.write("")

def dir = new File("attr/")

def writer = new StringWriter()
def root = new MarkupBuilder(writer)
root.doubleQuotes = true

root.templateSet(group: "Material UI - attr") {
    dir.listFiles().each {
        if (it.name.endsWith("html")) {
            def fileName = "attr/${it.name}"
            def file = new File(fileName)
            def doc = Jsoup.parse(file, "UTF-8")
            def tables = doc.select(".propTypeDescription")

            tables.collect {
                def componentName = it.select("h3").text().replace("Properties", "").trim()
                if (componentName == "") componentName = file.name.replace(".html", "")

                it.select("table tbody tr").each {
                    def attrName = it.select("td:nth-child(1)").text()
                    def attrType = it.select("td:nth-child(2)").text()

                    def attrValue = "{\$END\$}"
                    if(attrType == "string"){
                        attrValue = "\"\$END\$\""
                    }

                    def attrDescription = attrType
                    if (attrType == "function") {
                        attrDescription = it.select("td:last-child code:contains(function)").text()
                    }

                    template(
                            name: "aui ${componentName} ${attrName}",
                            value: "${attrName}=${attrValue}",
                            description: "${attrDescription}",
                            toReformat: "true",
                            toShortenFQNames: "true"
                    ) {
                        context {
                            option(name: "JAVA_SCRIPT", value: "true")
                            option(name: "JS_EXPRESSION", value: "true")
                            option(name: "JSX_HTML", value: "true")
                            option(name: "JS_STATEMENT", value: "true")
                        }
                    }
                }
            }
        }
    }
}

println writer
out << writer.toString()
