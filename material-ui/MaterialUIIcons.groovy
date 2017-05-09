import groovy.xml.MarkupBuilder
import org.jsoup.Jsoup

def out = new File("Material UI - Import SVG icons.xml")
out.write("")

def dir = new File("client/node_modules/material-ui/svg-icons")

def writer = new StringWriter()
def root = new MarkupBuilder(writer)
root.doubleQuotes = true

root.templateSet(group: "Material UI - Import SVG icons") {
    dir.eachFileRecurse {
        if (it.name.endsWith("js") && it.parentFile.name != "svg-icons") {
            def category = it.parentFile.name
            def iconName = it.name.replace(".js", "")
            def componentName = iconName.split("-").collect { it.capitalize() }.join("")

            template(
                    name: "iui icon ${category}/${iconName}",
                    value: "import ${componentName} from 'material-ui/svg-icons/${category}/${iconName}';",
                    description: "",
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

println writer
out << writer.toString()
