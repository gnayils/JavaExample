/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.learning.socket.imgutil;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author lf489159
 */
public class ImageScaler {

    private int imageType;
    private int originalWidth;
    private int originalHeight;
    private int newWidth;
    private int newHeight;
    private AffineTransform transform;
    private ByteArrayOutputStream byteArrayOutputStream;
    private static final String FORMAT = "bmp";

    public ImageScaler(int imageType, int originalWidth, int originalHeight, int newWidth, int newHeight) {
        this.imageType = imageType;
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        this.transform = AffineTransform.getScaleInstance((float)newWidth / originalWidth, (float)newHeight / originalHeight);
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.setUseCache(false);
    }

    public byte[] fastScaleImageData(byte[] data) throws IOException {
        BufferedImage newBufferedImage = new BufferedImage(newWidth, newHeight, imageType);
        Graphics2D g = newBufferedImage.createGraphics();
        g.drawRenderedImage(ImageIO.read(new ByteArrayInputStream(data)), transform);
        g.dispose();

        byteArrayOutputStream.reset();
        ImageIO.write(newBufferedImage, FORMAT, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] fastScalePixelData(byte[] data) throws IOException {
        BufferedImage originalBufferedImage = new BufferedImage(originalWidth, originalHeight, imageType);
        originalBufferedImage.setData(Raster.createRaster(originalBufferedImage.getSampleModel(), new DataBufferByte(data, data.length), new Point()));
        byteArrayOutputStream.reset();
        ImageIO.write(originalBufferedImage, FORMAT, byteArrayOutputStream);
        
        BufferedImage newBufferedImage = new BufferedImage(newWidth, newHeight, imageType);
        Graphics2D g = newBufferedImage.createGraphics();
        g.drawRenderedImage(ImageIO.read(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())), transform);
        g.dispose();

        byteArrayOutputStream.reset();
        ImageIO.write(newBufferedImage, FORMAT, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    public byte[] wrapPixelData(byte[] data) throws IOException {        
        BufferedImage originalBufferedImage = new BufferedImage(originalWidth, originalHeight, imageType);
        originalBufferedImage.setData(Raster.createRaster(originalBufferedImage.getSampleModel(), new DataBufferByte(data, data.length), new Point()));
        byteArrayOutputStream.reset();
        ImageIO.write(originalBufferedImage, FORMAT, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public BufferedImage fastScale(BufferedImage originalImage) {
        BufferedImage newBufferedImage = new BufferedImage(newWidth, newHeight, imageType);
        Graphics2D g = newBufferedImage.createGraphics();
        g.drawRenderedImage(originalImage, transform);
        g.dispose();
        return newBufferedImage;
    }

    public byte[] scaleImageData(byte[] data) throws IOException {
        BufferedImage newBufferedImage = new BufferedImage(newWidth, newHeight, imageType);
        Graphics2D g = newBufferedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(ImageIO.read(new ByteArrayInputStream(data)), 0, 0, newWidth, newHeight, 0, 0, originalWidth, originalHeight, null);
        g.dispose();

        byteArrayOutputStream.reset();
        ImageIO.write(newBufferedImage, FORMAT, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public BufferedImage scale(BufferedImage originalImage) {
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, imageType);
        Graphics2D g = newImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);
        g.dispose();
        return newImage;
    }
}
