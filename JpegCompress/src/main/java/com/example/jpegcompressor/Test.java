package com.example.jpegcompressor;

import com.example.jpegcompressor.imgutl.ImageCompressor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;


public class Test {

	public static void main(String[] args) throws IOException {
		ImageCompressor compressor = new ImageCompressor(1000, 1000, 10);
        compressor.setCompressionParams("JPEG", 0.75f);
        //compressor.setScaleParams(750, 750);
        
        
        
        File inputFile = new File("/storage/Documents/Pictures/raw/image.1338688262672");
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        MappedByteBuffer mbb = fileInputStream.getChannel().map(MapMode.READ_ONLY, 0, inputFile.length());
        byte[] inputFileBytes = new byte[(int)inputFile.length()];
        mbb.get(inputFileBytes);
        
        byte[] contractedInputFileBytes = null;
        for(int i=0; i<100;i++) {
        	contractedInputFileBytes = compressor.contractRawData(inputFileBytes);
        }
        
        File outputFile = new File("/storage/Documents/Pictures/raw/image.1338688262672.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        fileOutputStream.write(contractedInputFileBytes, 0, contractedInputFileBytes.length);
        fileOutputStream.flush();
        
        
        

        fileOutputStream.close();
        fileInputStream.close();
	}
}
