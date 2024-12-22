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
        val className = classNameNode.text
        val to = className
            .replace(classNameRegex, "${newClassName};")
            .let { it.slice(0 until it.length - 1) }
        val from = className.slice(0 until className.length - 1)
        globalObfuscations.add(IdentifierRenameObfuscation(from, to))
    }

    override fun visitFieldDirective(ctx: SmaliParser.FieldDirectiveContext?) {
        val fieldNode = ctx?.fieldNameAndType() ?: return
        val identifierNode = fieldNode.fieldName()?.identifier() ?: return
        for (i in 0 until identifierNode.childCount) {
            val part = identifierNode.IDENTIFIER(i) ?: continue
            val newIdentifierName = generateRandomIdentifier()
            val from = fieldNode.text
            val to = from.replace(part.text, newIdentifierName)
            globalObfuscations.add(IdentifierRenameObfuscation(from, to))
        }
    }

    override fun visitMethodDirective(ctx: SmaliParser.MethodDirectiveContext?) {
        if (ctx == null) {
            return
        }
        obfuscateMethodName(ctx)
        addDummyInstructions(ctx)
    }

    private fun obfuscateMethodName(ctx: SmaliParser.MethodDirectiveContext) {
        val methodSignatureNode = ctx.methodDeclaration()?.methodSignature()
        val methodSignatureText = methodSignatureNode?.text
        val methodIdentifierNode = methodSignatureNode?.methodIdentifier()
        val isBuiltIn = (methodIdentifierNode?.getChild(0)?.text ?: "") == "<"
        if (isBuiltIn) {
            return
        }
        val methodName = methodIdentifierNode?.identifier()?.IDENTIFIER(0)?.text ?: return
        val newMethodName = generateRandomIdentifier()
        val from = methodSignatureText ?: return
        val to = methodSignatureText.replace("${methodName}(", "${newMethodName}(")
        globalObfuscations.add(IdentifierRenameObfuscation(from, to))
    }

    private fun addDummyInstructions(ctx: SmaliParser.MethodDirectiveContext) {
        localObfuscations.add(InstructionAppendingObfuscation(ctx.getStart().line))
    }

    fun globalObfuscations() = globalObfuscations.toList()
    fun localObfuscations() = localObfuscations.toList()
}