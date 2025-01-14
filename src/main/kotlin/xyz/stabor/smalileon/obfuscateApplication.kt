package xyz.stabor.smalileon

import SmaliLexer
import SmaliParser
import org.antlr.v4.runtime.*
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

fun obfuscateApplication(path: Path, newSmaliDirName: String, configuration: SmaliObfuscationsProducerConfiguration, inDirName: String = "smali") {
    val smaliDir = path.resolve(inDirName)
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
        val lexer = SmaliLexer(CharStreams.fromFileName(file.toString()))
        lexer.removeErrorListeners()
        val tokens = CommonTokenStream(lexer)
        val parser = SmaliParser(tokens)
        parser.removeErrorListeners()
        val tree = parser.parse()
        val obfuscationsProducer = SmaliObfuscationsProducer(configuration)
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
        val newFile = program.filepath.toString().replace("/${inDirName}/", "/${newSmaliDirName}/")
        Files.createDirectories(Path.of(newFile).parent)
        Files.write(Path.of(newFile), obfuscated.toByteArray())
    }
}