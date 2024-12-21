package xyz.stabor.smalileon

import SmaliParser
import SmaliParserBaseVisitor

class SmaliObfuscationsProducer : SmaliParserBaseVisitor<Unit>() {
    private val globalObfuscations: MutableList<Obfuscation> = mutableListOf()
    private val localObfuscations: MutableList<Obfuscation> = mutableListOf()

    companion object {
        val classNameRegex = Regex("\\w+;")
    }

    override fun visitClassDirective(ctx: SmaliParser.ClassDirectiveContext?) {
        val classNameNode = ctx?.className()?.referenceType()?.QUALIFIED_TYPE_NAME() ?: return
        val newClassName = generateRandomIdentifier()
        val from = classNameNode.text
        val to = from.replace(classNameRegex, "${newClassName};")
        globalObfuscations.add(IdentifierRenameObfuscation(from, to))
    }

    fun globalObfuscations() = globalObfuscations.toList()
    fun localObfuscations() = localObfuscations.toList()
}