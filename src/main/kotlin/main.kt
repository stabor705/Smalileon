import kotlinx.coroutines.coroutineScope
import xyz.stabor.smalileon.obfuscateApplication
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlinx.coroutines.launch
import me.tongfei.progressbar.ProgressBar
import java.util.concurrent.atomic.AtomicInteger

suspend fun main(args: Array<String>) = coroutineScope {
    val dir = args.getOrNull(0)?.let { Paths.get(it) } ?: throw Error("No dir specified")
//    val dir = Paths.get("").toAbsolutePath().toString()?.let { Paths.get(it, "/src/main/resources/data/benign") } ?:
//        throw Error("No dir specified")
    val allApps = Files.walk(dir, 1)
        .filter { it.isDirectory() }
        .filter { it != dir}
        .toList()
    val successesCounter = AtomicInteger()
    val invalidFiles: MutableList<String> = mutableListOf()
    val progressBar = ProgressBar("Obfuscating...", allApps.count().toLong())
    val jobs = allApps.map { app ->
        launch {
            val resultCode = obfuscateApplication(app, "smali-classes")
            if (resultCode) {
                successesCounter.incrementAndGet()
            } else {
                invalidFiles.add(app.toString())
            }
            progressBar.step()
            progressBar.setExtraMessage("${(successesCounter.toFloat() / progressBar.current.toFloat()) * 100}% successful")
        }
    }

    jobs.forEach { it.join() }

    if (invalidFiles.isNotEmpty()) {
        println(invalidFiles)
        Files.write(dir.resolve("invalid-apps.txt"), invalidFiles)
    }
}