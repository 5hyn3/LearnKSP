import com.example.Mockable
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration


class SampleProcessor : SymbolProcessor {

    private lateinit var codeGenerator: CodeGenerator
    private lateinit var logger: KSPLogger

    private var invoked = false

    override fun init(
        options: Map<String, String>,
        kotlinVersion: KotlinVersion,
        codeGenerator: CodeGenerator,
        logger: KSPLogger
    ) {
        this.codeGenerator = codeGenerator
        this.logger = logger
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }
        val symbols = resolver.getSymbolsWithAnnotation(Mockable::class.java.canonicalName)

        symbols.filterIsInstance<KSClassDeclaration>().forEach { classDecl ->

            val methods = classDecl.getAllFunctions()

            val code = """
package ${classDecl.packageName.asString()}

class ${classDecl.simpleName.asString()}Mock(): ${classDecl.simpleName.asString()}  {
${
                methods.mapNotNull {
                    if (!it.isAbstract) return@mapNotNull null else "    override fun " + it.simpleName.asString() + "(" + it.parameters.map {
                        it.name?.asString() + ": " + it.type.resolve().toString()
                    }.joinToString(" ") + ")" + it.returnType?.let {
                        ": ${it.resolve()} { return ${
                            if (it.resolve().toString() == "Unit") "Unit" else it.resolve().toString() + "()"
                        } }"
                    }
                }.joinToString("\n")
            }
}
             """.trimIndent()

            val file = codeGenerator.createNewFile(
                Dependencies(true, classDecl.containingFile!!),
                classDecl.packageName.asString(),
                "${classDecl.simpleName.asString()}Mock"
            )

            file.write(code.toByteArray())
            file.close()
        }

        invoked = true
        return emptyList()
    }

    override fun finish() {
    }
}