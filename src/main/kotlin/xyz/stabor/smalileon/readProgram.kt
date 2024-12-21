package xyz.stabor.smalileon

import java.io.File
import java.nio.file.Path

fun readProgram(path: Path): Program {
    val bufferedReader = File(path.toString()).bufferedReader()
    val lines = bufferedReader.readLines()
    return Program(filepath = path, lines = lines)
}