package kkk.to.services

import kkk.to.models.Image
import kkk.to.util.Size
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.FileInputStream

class MinimizingTest {

    private val mnService = MinimizingService()
    @Test
    fun testMinimizeImage() {
        //given
        val fileInputStream = FileInputStream("src/test/resources/cat.jpeg")
        val originalImage = fileInputStream.readAllBytes()
        fileInputStream.close()

        val fileInputStream2 = FileInputStream("src/test/resources/medium_cat.png")
        val mediumImage = fileInputStream2.readAllBytes()
        fileInputStream2.close()

        //when
        val testImage =   Image("", originalImage)
        val minimizedImage = mnService.minimize(testImage, Size.MEDIUM)

        //then
        assertNotNull(minimizedImage)
        assertArrayEquals(minimizedImage, mediumImage)
    }


    @Test
    fun testMinimizeImageWithUnsupportedFormat() {
        //given
        val fileInputStream = FileInputStream("src/test/resources/test.svg")
        val originalImage = fileInputStream.readAllBytes()
        fileInputStream.close()
        val size = Size.MEDIUM

        //when
        val minimizedImage = mnService.minimize(Image("test_file",originalImage), size)

        //then
        assertNull(minimizedImage)
    }
}