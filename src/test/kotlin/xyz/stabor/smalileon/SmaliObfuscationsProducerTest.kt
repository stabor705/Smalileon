package xyz.stabor.smalileon

import SmaliLexer
import SmaliParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SmaliObfuscationsProducerTest {

    @Test
    fun `Class search and replace are made`() {
        val input = ".class public Ljavazoom/jl/player/advanced/AdvancedPlayer;"
        val charStream = CharStreams.fromString(input)
        val lexer = SmaliLexer(charStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = SmaliParser(tokenStream)
        val parseTree = parser.parse()
        val visitor = SmaliObfuscationsProducer()
        visitor.visit(parseTree)
        val results = visitor.results()
        val obfuscation = results[0]
        assertTrue(obfuscation is IdentifierRenameObfuscation)
        assertEquals("Ljavazoom/jl/player/advanced/AdvancedPlayer;", obfuscation.from)
        assertTrue(obfuscation.to.startsWith("Ljavazoom/jl/player/advanced/"))
    }
}