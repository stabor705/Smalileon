package xyz.stabor.smalileon

import SmaliLexer
import SmaliParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.nio.file.Path
//
//fun obfuscate(path: Path): String {
//    val lexer = SmaliLexer(CharStreams.fromFileName(path.toString()))
//    val tokens = CommonTokenStream(lexer)
//    val parser = SmaliParser(tokens)
//    val tree = parser.parse()
//    val obfuscationsProducer = SmaliObfuscationsProducer()
//    obfuscationsProducer.visit(tree)
//    val results = obfuscationsProducer.results()
//    val program = results.fold(readProgram(path)) { program, obfuscation ->
//        obfuscation.apply(program)
//    }
//    return program.applyModifications().joinToString("\n")
//}