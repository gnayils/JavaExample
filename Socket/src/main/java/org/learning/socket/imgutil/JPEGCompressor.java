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
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 *
 * @author lf489159
 */
public class JPEGCompressor {

    private ImageWriter imageWriter;
    private ImageWriteParam imageWriterParam;
    private ImageOutputStream imageOutputStream;
    private ByteArrayOutputStream byteArrayOutputStream;
    private BufferedImage imageFrame;
    public static final String JPEG = "JPEG";
    //public static final String JPEG2000 = "JPEG2000";

    public JPEGCompressor(int imageType, int imageWidth, int imageHeight, String compressType, float quality) throws IOException {
        imageWriter = ImageIO.getImageWritersByFormatName(compressType).next();
        
        byteArrayOutputStream = new ByteArrayOutputStream();
        imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);
        imageWriter.setOutput(imageOutputStream);

        imageWriterParam = imageWriter.getDefaultWriteParam();
        imageWriterParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriterParam.setCompressionType(compressType);
        imageWriterParam.setCompressionQuality(quality);

        imageFrame = new BufferedImage(imageWidth, imageHeight, imageType);
    }

    public byte[] compressImageData(byte[] data) throws IOException {
        byteArrayOutputStream.reset();
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(data));
        imageWriter.write(null, new IIOImage(bufferedImage, null, null), imageWriterParam);
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] compressPixelData(byte[] data) throws IOException {
        byteArrayOutputStream.reset();
        imageFrame.setData(Raster.createRaster(imageFrame.getSampleModel(), new DataBufferByte(data, data.length), new Point()));
        imageWriter.write(null, new IIOImage(imageFrame, null, null), imageWriterParam);
        imageOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    public void dispose() {
        if (imageWriter != null) {
            imageWriter.dispose();
        }
    }
}
