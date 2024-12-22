package xyz.stabor.smalileon

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun reassembleApplication(path: Path, newSmaliDirName: String) {
    val outDex = assemble(path)
    disassemble(outDex, outDex.parent.resolve(newSmaliDirName))
    cleanUp(outDex)
}

private fun assemble(path: Path): Path {
    val smaliJarPath = Paths.get("").toAbsolutePath().resolve("src/main/resources/jars/smali.jar")
    val outDex = path.resolve("out.dex")
    val processBuilder = ProcessBuilder(
        "java",
        "-jar",
        smaliJarPath.toString(),
        "assemble",
        path.resolve("smali").toString(),
        "-o",
        outDex.toString()
    )
    processBuilder.inheritIO()
    val process = processBuilder.start()
    process.waitFor()
    return outDex
}

private fun disassemble(outDex: Path, outSmali: Path): Path {
    val baksmaliJarPath = Paths.get("").toAbsolutePath().resolve("src/main/resources/jars/baksmali.jar")
    val processBuilder = ProcessBuilder(
        "java",
        "-jar",
        baksmaliJarPath.toString(),
        "disassemble",
        outDex.toString(),
        "-o",
        outSmali.toString()
    )
    processBuilder.inheritIO()
    val process = processBuilder.start()
    process.waitFor()
    return outSmali
}

private fun cleanUp(outDex: Path) {
    Files.delete(outDex)
}