package kkk.to.services

import kkk.to.models.Directory
import kkk.to.models.Image
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import javax.imageio.ImageIO
import kotlin.collections.HashSet

@Service
class ZipService(private val dbService: MongoService) {
    private fun roamFolder(entries: Enumeration<out ZipEntry>, zip: ZipFile) {
        val rootDirs = HashSet<String>()

        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            val (path, name) = splitPath(entry.name)
            val root = rootName(entry.name)
            rootDirs.add(root)

            if (!entry.isDirectory) {

//                println("FileName: \t${name} \t Path:\t${path} ")

                if (entry.name.endsWith(".jpg")) {
                    try {
                        val inputStream = zip.getInputStream(entry)
                        val image = ImageIO.read(inputStream)

                        val baos = ByteArrayOutputStream()
                        ImageIO.write(image, "jpg", baos)

                        val bitarray = baos.toByteArray()
//                        println("Bitarray:\t${bitarray}")
                        dbService.saveImages(Flux.just(Image(original = bitarray, path = path))).subscribe()

                        inputStream.close()
                    } catch (e: IOException) {
                        println("Error reading image file: ${e.message}")
                    }
                }
            } else {
                dbService.saveDirectory(Directory(path=path, name=name)).subscribe()
//                println("DirName: \t${name} \t Path:\t${path} ")
            }
        }
        rootDirs.forEach{
//            println("RootName: \t${it}")
            dbService.saveDirectory(Directory(path="", name=it)).subscribe()
        }
    }

    fun rootName(input: String): String {
        val firsSlashIndex = input.indexOf('/')

        return if (firsSlashIndex != -1) {
            input.substring(0, firsSlashIndex)
        } else {
            ""
        }
    }

    fun splitPath(input: String): Pair<String, String> {
        var modifiedInput = input

        if (modifiedInput.endsWith('/')) {
            modifiedInput = modifiedInput.substring(0, modifiedInput.length - 1)
        }

        val lastSlashIndex = modifiedInput.lastIndexOf('/')

        return if (lastSlashIndex != -1) {
            val firstPart = '/' + modifiedInput.substring(0, lastSlashIndex)
            val secondPart = modifiedInput.substring(lastSlashIndex + 1)
            Pair(firstPart, secondPart)
        } else {
            Pair("", input)
        }
    }


    fun createZipFileFromByteArray(zipByteArray: ByteArray): ZipFile {
        val tempFile = createTempFile()
        tempFile.writeBytes(zipByteArray)
        return ZipFile(tempFile)
    }

    fun handleZip(data: ByteArray){
        val zipFile = createZipFileFromByteArray(data)
        zipFile.use { zip ->
            val entries = zip.entries()
            roamFolder(entries, zip)
        }
    }
}