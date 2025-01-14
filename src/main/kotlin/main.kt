import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.command.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tongfei.progressbar.ProgressBar
import xyz.stabor.smalileon.SmaliObfuscationsProducerConfiguration
import xyz.stabor.smalileon.obfuscateApplication
import xyz.stabor.smalileon.reassembleApplication
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory

enum class ObfuscationType {
    REPACKAGE,
    RENAME_CLASSES,
    RENAME_METHODS,
    RENAME_FIELDS,
    ADD_INSTRUCTIONS,
    ALL
}

class App : SuspendingCliktCommand() {
    val obfuscationType: ObfuscationType? by option("--obfuscations").enum<ObfuscationType> { it.name.lowercase() }
    val dir: String? by argument("<dir>")
    val outDirName: String? by option("--out_dir_name")
    val inDirName: String? by option("--in_dir_name")

    companion object {
        fun makeConfiguration(obfuscationType: ObfuscationType) = when(obfuscationType) {
            ObfuscationType.RENAME_CLASSES -> SmaliObfuscationsProducerConfiguration(obfuscateClassNames = true)
            ObfuscationType.RENAME_METHODS -> SmaliObfuscationsProducerConfiguration(obfuscateMethodNames = true)
            ObfuscationType.RENAME_FIELDS -> SmaliObfuscationsProducerConfiguration(obfuscateMethodNames = true)
            ObfuscationType.ADD_INSTRUCTIONS -> SmaliObfuscationsProducerConfiguration(addDummyInstructions = true)
            ObfuscationType.ALL -> SmaliObfuscationsProducerConfiguration(
                obfuscateClassNames = true,
                obfuscateMethodNames = true,
                obfuscateFieldNames = true,
                addDummyInstructions = true
            )
            else -> SmaliObfuscationsProducerConfiguration()
        }
    }

    override suspend fun run() {
        val dirPath = Paths.get(dir) ?: throw Error("Bad dir path")
        val allApps = withContext(Dispatchers.IO) {
            Files.walk(dirPath, 1)
        }
            .filter { it.isDirectory() }
            .filter { it != dirPath}
            .toList()
        if (obfuscationType == ObfuscationType.REPACKAGE) {
            executeRepackageObfuscations(allApps)
        } else {
            executeParserObfuscations(allApps)
        }
    }

    suspend fun executeParserObfuscations(allApps: List<Path>) = coroutineScope {
        val progressBar = ProgressBar("Obfuscating...", allApps.count().toLong())
        val configuration = makeConfiguration(obfuscationType ?: ObfuscationType.ALL)
        val jobs = allApps.map { app ->
            launch {
                try {
                    obfuscateApplication(app, outDirName ?: "smali-obfuscated", configuration)
                } catch (e: Exception) {
                    println("Error processing $app: $e")
                }
                progressBar.step()
            }
        }

        jobs.forEach { it.join() }

        progressBar.close()
    }

    fun executeRepackageObfuscations(allApps: List<Path>) {
        val progressBar = ProgressBar("Obfuscating...", allApps.count().toLong())
        for (app in allApps) {
            try {
                reassembleApplication(app, outDirName ?: "smali-obfuscated")
            } catch (e: Exception) {
                println("Error processing $app: $e")
            }
            progressBar.step()
        }

        progressBar.close()
    }
}


suspend fun main(args: Array<String>) = App().main(args)