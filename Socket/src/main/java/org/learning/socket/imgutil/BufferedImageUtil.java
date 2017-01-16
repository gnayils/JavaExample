/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.learning.socket.imgutil;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author lf489159
 */
public class BufferedImageUtil {

    public static BufferedImage bufferedImageFrom(byte[] data, int imageType, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, imageType);
        resizedImage.setData(Raster.createRaster(resizedImage.getSampleModel(), new DataBufferByte(data, data.length), new Point()));
        return resizedImage;
    }

    public static BufferedImage bufferedImageFrom(byte[] data) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
        return image;
    }
    
    public static BufferedImage bufferedImageFrom(byte[] data, String format) throws IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(data));
        ImageReader reader  = ImageIO.getImageReadersByFormatName(format).next();
        reader.setInput(iis, true);
        return reader.read(0);
    }

    public static byte[] byteArrayCome(BufferedImage bufferedImage) {
        //byte[] imageBytes = ((DataBufferByte) bufferedImage.getData().getDataBuffer()).getData();
        byte[] imageBytes = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        return imageBytes;
    }

    public static byte[] byteArrayFrom(BufferedImage originalImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.setUseCache(false);
        ImageIO.write(originalImage, "bmp", baos);
        baos.flush();
        return baos.toByteArray();
    }
}

