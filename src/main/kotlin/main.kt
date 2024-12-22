import SmaliParser.ExtendedParamDirectiveContext
import kotlinx.coroutines.coroutineScope
import xyz.stabor.smalileon.obfuscateApplication
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.tongfei.progressbar.ProgressBar
import xyz.stabor.smalileon.reassembleApplication

//suspend fun main(args: Array<String>) = coroutineScope {
//    val dir = Paths.get("").toAbsolutePath().toString().let { Paths.get(it, "/src/main/resources/data/benign") } ?:
//        throw Error("No dir specified")
//    val allApps = Files.walk(dir, 1)
//        .filter { it.isDirectory() }
//        .filter { it != dir}
//        .toList()
//    val progressBar = ProgressBar("Obfuscating...", allApps.count().toLong())
//    allApps.map { app ->
//        try {
//            reassembleApplication(app, "smali-reassembled")
//        } catch (e: Exception) {
//            println("Error processing $app: $e")
//        }
//        progressBar.step()
//    }
//
//    progressBar.close()
//}

suspend fun main(args: Array<String>) = coroutineScope{
//    val dir = args.getOrNull(0)?.let { Paths.get(it) } ?: throw Error("No dir specified")
    val dir = Paths.get("").toAbsolutePath().toString().let { Paths.get(it, "/src/main/resources/data/benign") } ?:
    throw Error("No dir specified")
    val allApps = Files.walk(dir, 1)
        .filter { it.isDirectory() }
        .filter { it != dir}
        .toList()
    val progressBar = ProgressBar("Obfuscating...", allApps.count().toLong())
    val jobs = allApps.map { app ->
        launch {
            try {
                obfuscateApplication(app, "smali-classes")
            } catch (e: Exception) {
                println("Error processing $app: $e")
            }
            progressBar.step()
        }
    }

    jobs.forEach { it.join() }

    progressBar.close()
}