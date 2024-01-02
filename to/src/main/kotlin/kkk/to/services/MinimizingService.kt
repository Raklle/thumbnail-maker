package kkk.to.services

import kkk.to.models.Image
import kkk.to.util.Size
import net.coobird.thumbnailator.Thumbnails
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.imageio.ImageIO

@Service
class MinimizingService {
    private val format: String = "png"
    @Throws(IOException::class)
    private fun toByteArray(bufferedImage: BufferedImage?): ByteArray {
        val baos = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, format, baos)
        baos.flush()
        val byteArray: ByteArray = baos.toByteArray()
        baos.close()
        return byteArray
    }
    /**
    supports  JPEG, PNG, GIF and BMP
     **/
    fun minimize(image: Image, size: Size) : ByteArray{

        var thumbnail: BufferedImage? = null
        try {
            val bufferedImage = ImageIO.read(ByteArrayInputStream(image.original))
            thumbnail = Thumbnails.of(bufferedImage)
                    .size(size.width,size.height)
                    .outputFormat(format)
                    .asBufferedImage()

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return toByteArray(thumbnail)
    }

}