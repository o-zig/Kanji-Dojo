import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class PrepareAssetsTask : DefaultTask() {

    enum class Platform(val assetLocation: AssetLocation) {
        Android(AppAssets.AndroidAssetsLocation),
        Desktop(AppAssets.DesktopAssetsLocation)
    }

    @Input
    lateinit var platform: Platform

    @TaskAction
    fun download() {
        println("Preparing Kanji Dojo Assets for $platform...")
        handleAssets(AppAssets.CommonAssetsLocation)
        handleAssets(platform.assetLocation)
    }

    private fun handleAssets(assetLocation: AssetLocation) {
        val assetsDir = File(project.rootDir, assetLocation.directory)
        if (!assetsDir.exists()) assetsDir.mkdirs()

        val expectedFileNames = assetLocation.expectedAssets.map { it.fileName }.toSet()
        val unexpectedFiles = assetsDir.listFiles()!!
            .filter { !expectedFileNames.contains(it.name) }

        if (unexpectedFiles.isNotEmpty()) {
            val unexpectedFileNames = unexpectedFiles.joinToString { it.name }
            println("Found ${unexpectedFiles.size} unknown assets [$unexpectedFileNames], removing...")

            unexpectedFiles.forEach { it.delete() }
        }

        assetLocation.expectedAssets.forEach { (fileName, url) ->
            url ?: return@forEach
            val assetFile = File(assetsDir, fileName)
            if (!assetFile.exists()) {
                println("Asset $fileName not found, downloading")
                downloadFile(assetFile, url)
            } else {
                println("Skipping $fileName downloading, already exist")
            }
        }
    }

    private fun downloadFile(file: File, url: String) {
        ant.invokeMethod("get", mapOf("src" to url, "dest" to file))
    }

}