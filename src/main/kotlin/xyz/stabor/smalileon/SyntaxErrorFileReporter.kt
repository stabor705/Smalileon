package xyz.stabor.smalileon

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import java.nio.file.Files
import java.nio.file.Path

class SyntaxErrorFileReporter : BaseErrorListener() {
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