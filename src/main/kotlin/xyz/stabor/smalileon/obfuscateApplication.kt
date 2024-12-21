package xyz.stabor.smalileon

import SmaliLexer
import SmaliParser
import org.antlr.v4.runtime.*
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

class FileReporter(
) : BaseErrorListener() {
    val errors = mutableListOf<String>()

    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: RecognitionException?
    ) {
        if (msg != null) {
            errors.add("$line:$charPositionInLine $msg")
        }
    }

    fun writeReport(baseDir: Path, appName: String) {
        val reportPath = baseDir.resolve("${appName}_errors_report.txt")
        Files.write(reportPath, errors)
    }

    val hasErrors: Boolean
        get() = errors.isNotEmpty()
}

fun obfuscateApplication(path: Path, newSmaliDirName: String): Boolean {
    val smaliDir = path.resolve("smali")
    val smaliFiles = buildList {
        Files.walkFileTree(smaliDir, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                if (file.toString().endsWith(".smali")) {
                    add(file)
                }
                return FileVisitResult.CONTINUE
            }
        })
    }
    val globalObfuscations: MutableList<Obfuscation> = mutableListOf()
    val programs: MutableList<Program> = mutableListOf()
    for (file in smaliFiles) {
        val errorListener = FileReporter()
        val lexer = SmaliLexer(CharStreams.fromFileName(file.toString()))
        lexer.removeErrorListeners()
        val tokens = CommonTokenStream(lexer)
        val parser = SmaliParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(errorListener)
        val tree = parser.parse()
        if (errorListener.hasErrors) {
            errorListener.writeReport(file.parent, file.fileName.toString())
            return false
        }
        val obfuscationsProducer = SmaliObfuscationsProducer()
        obfuscationsProducer.visit(tree)
        globalObfuscations.addAll(obfuscationsProducer.globalObfuscations())
        val program = obfuscationsProducer.localObfuscations().fold(readProgram(file)) { program, obfuscation ->
            obfuscation.apply(program)
        }
        programs.add(program)
    }
    for (program in programs) {
        val fullProgram = globalObfuscations.fold(program) { program, obfuscation ->
            obfuscation.apply(program)
        }
        val obfuscated = fullProgram.applyModifications().joinToString("\n")
        val newFile = program.filepath.toString().replace("/smali/", "/${newSmaliDirName}/")
        Files.createDirectories(Path.of(newFile).parent)
        Files.write(Path.of(newFile), obfuscated.toByteArray())
    }
    return true
}