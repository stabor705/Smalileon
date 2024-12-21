package xyz.stabor.smalileon

data class IdentifierRenameObfuscation(
    val from: String,
    val to: String
) : Obfuscation {
    override fun apply(program: Program): Program =
        program.lines.withIndex().fold(program) { state, (lineNumber, line) ->
            if (line.contains(from)) {
                state.replaceLine(lineNumber, line.replace(from, to))
            } else {
                state
            }
        }
}