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
        val results = visitor.globalObfuscations()
        val obfuscation = results[0]

        assertTrue(obfuscation is IdentifierRenameObfuscation)
        assertEquals("Ljavazoom/jl/player/advanced/AdvancedPlayer", obfuscation.from)
        assertTrue(obfuscation.to.startsWith("Ljavazoom/jl/player/advanced/"))
    }

    @Test
    fun `Field search and replace are made`() {
        val input = ".field final synthetic this\$0:Lnet/virtualtechs/tyraisms/Quotes;"
        val charStream = CharStreams.fromString(input)
        val lexer = SmaliLexer(charStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = SmaliParser(tokenStream)
        val parseTree = parser.parse()
        val visitor = SmaliObfuscationsProducer()
        visitor.visit(parseTree)
        val results = visitor.globalObfuscations()
        val obfuscation = results[0]
        assertTrue(obfuscation is IdentifierRenameObfuscation)
        assertEquals("this\$0:Lnet/virtualtechs/tyraisms/Quotes;", obfuscation.from)
        assertTrue(obfuscation.to.endsWith(":Lnet/virtualtechs/tyraisms/Quotes;"))
    }

    @Test
    fun `Method search and replace work for built-in`() {
        val input = ".method public constructor <init>()V .end method"
        val charStream = CharStreams.fromString(input)
        val lexer = SmaliLexer(charStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = SmaliParser(tokenStream)
        val parseTree = parser.parse()
        val visitor = SmaliObfuscationsProducer()
        visitor.visit(parseTree)
        val results = visitor.globalObfuscations()
        assertTrue { results.isEmpty() }
    }

    @Test
    fun `Method search and replace work for regular methods`() {
        val input = ".method static decode(C)I .end method"
        val charStream = CharStreams.fromString(input)
        val lexer = SmaliLexer(charStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = SmaliParser(tokenStream)
        val parseTree = parser.parse()
        val visitor = SmaliObfuscationsProducer()
        visitor.visit(parseTree)
        val results = visitor.globalObfuscations()
        val obfuscation = results[0]
        assertTrue(obfuscation is IdentifierRenameObfuscation)
        assertEquals("decode(C)I", obfuscation.from)
        assertTrue(obfuscation.to.endsWith("(C)I"))
    }
}