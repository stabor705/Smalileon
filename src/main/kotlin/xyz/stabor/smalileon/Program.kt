package xyz.stabor.smalileon

import java.nio.file.Path

data class Program(
    val filepath: Path,
    val lines: List<String>,
    private val linesToReplace: Map<Int, String> = mapOf(),
    private val linesToAppend: Map<Int, List<String>> = mapOf()
) {
    fun replaceLine(lineNumber: Int, newLine: String) =
        copy(linesToReplace = linesToReplace.plus(Pair(lineNumber, newLine)))

    fun appendLines(lineNumber: Int, newLines: List<String>) =
        copy(linesToAppend = linesToAppend.plus(Pair(lineNumber, newLines)))

    fun applyModifications(): List<String> {
        val modifiedLines = lines.toMutableList()
        linesToReplace.forEach { (lineNumber, newLine) ->
            modifiedLines[lineNumber] = newLine
        }
        linesToAppend.forEach { (lineNumber, newLines) ->
            modifiedLines.addAll(lineNumber, newLines)
        }
        return modifiedLines.toList()
    }
}