package com.example.jpegcompressor.imgutl;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public class ImageCompressor {
	 private int imageWidth;
	    private int imageHeight;
	    private int imageType;
	    private String compressionType;
	    private float compressionQuality;
	    private BufferedImage bufferedImage;
	    private ImageWriter imageWriter;
	    private ImageWriteParam imageWriteParam;
	    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    private ImageOutputStream imageOutputStream;
	    private boolean compressionEnabled = false;
	    private boolean scaleEnabled = false;
	    private int scaledWidth;
	    private int scaledHeight;
	    private AffineTransform scaleTransform;
	    private static final String IMAGE_FORMAT = "bmp";
	    public static final String COMPRESSION_TYPE_JPEG = "JPEG";

	    public ImageCompressor(int imageWidth, int imageHeight, int imageType) {
	        this.imageWidth = imageWidth;
	        this.imageHeight = imageHeight;
	        this.imageType = imageType;
	        this.bufferedImage = new BufferedImage(imageWidth, imageHeight, imageType);
	        ImageIO.setUseCache(false);
	    }

	    public void setCompressionParams(String compressionType, float compressionQuality) throws IOException {
	        this.compressionType = compressionType;
	        this.compressionQuality = compressionQuality;
	        this.imageWriter = (ImageWriter)ImageIO.getImageWritersByFormatName(compressionType).next();
	        this.imageWriteParam = this.imageWriter.getDefaultWriteParam();
	        this.imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	        this.imageWriteParam.setCompressionType(compressionType);
	        this.imageWriteParam.setCompressionQuality(compressionQuality);
	        this.imageOutputStream = ImageIO.createImageOutputStream(this.byteArrayOutputStream);
	        this.imageWriter.setOutput(this.imageOutputStream);
	        this.compressionEnabled = true;
	    }

	    public void setScaleParams(int scaleWidth, int scaleHeight) {
	        this.scaledWidth = scaleWidth;
	        this.scaledHeight = scaleHeight;
	        this.scaleTransform = AffineTransform.getScaleInstance((double)((float)scaleWidth / (float)this.imageWidth), (double)((float)scaleHeight / (float)this.imageHeight));
	        this.scaleEnabled = true;
	    }

	    public byte[] contractImageData(byte[] data) throws IOException {
	        if(this.scaleEnabled) {
	            data = this.scaleImageData(data);
	        }

	        if(this.compressionEnabled) {
	            data = this.compressImageData(data);
	        }

	        return data;
	    }

	    public byte[] contractRawData(byte[] data) throws IOException {
	        if(this.scaleEnabled) {
	            data = this.scaleRawData(data);
	        }

	        if(this.compressionEnabled) {
	            if(this.scaleEnabled) {
	                data = this.compressImageData(data);
	            } else {
	                data = this.compressRawData(data);
	            }
	        }
	        return data;
	    }

	    private byte[] scaleRawData(byte[] data) throws IOException {
	        BufferedImage originalBufferedImage = new BufferedImage(this.imageWidth, this.imageHeight, this.imageType);
	        originalBufferedImage.setData(Raster.createRaster(originalBufferedImage.getSampleModel(), new DataBufferByte(data, data.length), new Point()));
	        this.byteArrayOutputStream.reset();
	        ImageIO.write(originalBufferedImage, IMAGE_FORMAT, this.byteArrayOutputStream);
	        BufferedImage newBufferedImage = new BufferedImage(this.scaledWidth, this.scaledHeight, this.imageType);
	        Graphics2D g = newBufferedImage.createGraphics();
	        g.drawRenderedImage(ImageIO.read(new ByteArrayInputStream(this.byteArrayOutputStream.toByteArray())), this.scaleTransform);
	        g.dispose();
	        this.byteArrayOutputStream.reset();
	        ImageIO.write(newBufferedImage, IMAGE_FORMAT, this.byteArrayOutputStream);
	        return this.byteArrayOutputStream.toByteArray();
	    }

	    private byte[] compressRawData(byte[] data) throws IOException {
	        this.byteArrayOutputStream.reset();
	        this.bufferedImage.setData(Raster.createRaster(this.bufferedImage.getSampleModel(), new DataBufferByte(data, data.length), new Point()));
	        this.imageWriter.write((IIOMetadata)null, new IIOImage(this.bufferedImage, (List)null, (IIOMetadata)null), this.imageWriteParam);
	        this.imageOutputStream.flush();
	        return this.byteArrayOutputStream.toByteArray();
	    }

	    private byte[] scaleImageData(byte[] data) throws IOException {
	        long startTime = System.currentTimeMillis();
	        BufferedImage newBufferedImage = new BufferedImage(this.scaledWidth, this.scaledHeight, this.imageType);
	        Graphics2D g = newBufferedImage.createGraphics();
	        g.drawRenderedImage(ImageIO.read(new ByteArrayInputStream(data)), this.scaleTransform);
	        g.dispose();
	        this.byteArrayOutputStream.reset();
	        ImageIO.write(newBufferedImage, IMAGE_FORMAT, this.byteArrayOutputStream);
	        System.out.println("scale time: " + (System.currentTimeMillis() - startTime));
	        return this.byteArrayOutputStream.toByteArray();
	    }

	    private byte[] compressImageData(byte[] data) throws IOException {
	        this.byteArrayOutputStream.reset();
	        BufferedImage tempBufferedImage = ImageIO.read(new ByteArrayInputStream(data));
	        this.imageWriter.write((IIOMetadata)null, new IIOImage(tempBufferedImage, (List)null, (IIOMetadata)null), this.imageWriteParam);
	        return this.byteArrayOutputStream.toByteArray();
	    }

	    public BufferedImage scaleImage(BufferedImage originalImage) {
	        BufferedImage newBufferedImage = new BufferedImage(this.imageWidth, this.imageHeight, this.imageType);
	        Graphics2D g = newBufferedImage.createGraphics();
	        g.drawRenderedImage(originalImage, this.scaleTransform);
	        g.dispose();
	        return newBufferedImage;
	    }

	    public byte[] wrapRawData(byte[] data) throws IOException {
	        BufferedImage originalBufferedImage = new BufferedImage(this.imageWidth, this.imageHeight, this.imageType);
	        originalBufferedImage.setData(Raster.createRaster(originalBufferedImage.getSampleModel(), new DataBufferByte(data, data.length), new Point()));
	        this.byteArrayOutputStream.reset();
	        ImageIO.write(originalBufferedImage, IMAGE_FORMAT, this.byteArrayOutputStream);
	        return this.byteArrayOutputStream.toByteArray();
	    }

	    public void dispose() {
	        if(this.imageWriter != null) {
	            this.imageWriter.dispose();
	        }

	    }

	    public static BufferedImage bufferedImageFrom(byte[] data, int imageType, int width, int height) {
	        BufferedImage bufferedImage = new BufferedImage(width, height, imageType);
	        bufferedImage.setData(Raster.createRaster(bufferedImage.getSampleModel(), new DataBufferByte(data, data.length), new Point()));
	        return bufferedImage;
	    }

	    public static BufferedImage bufferedImageFrom(byte[] data) throws IOException {
	        return ImageIO.read(new ByteArrayInputStream(data));
	    }

	    public static BufferedImage bufferedImageFrom(byte[] data, String format) throws IOException {
	        ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(data));
	        ImageReader reader = (ImageReader)ImageIO.getImageReadersByFormatName(format).next();
	        reader.setInput(iis, true);
	        return reader.read(0);
	    }

	    public static byte[] rawDataFrom(BufferedImage bufferedImage) {
	        return ((DataBufferByte)bufferedImage.getRaster().getDataBuffer()).getData();
	    }

	    public static byte[] imageDataFrom(BufferedImage originalImage) throws IOException {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ImageIO.setUseCache(false);
	        ImageIO.write(originalImage, IMAGE_FORMAT, baos);
	        return baos.toByteArray();
	    }
}
