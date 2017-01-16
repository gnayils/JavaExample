package com.example.jpegcompressor.server;


import com.example.jpegcompressor.imgutl.ImageCompressor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageDataServer {

	public static void main(String[] args) throws IOException {
		ArrayBlockingQueue<byte[]> frameDataQueue = new ArrayBlockingQueue<byte[]>(5);
		ImageDataProvider frameDataProvider = new ImageDataProvider(frameDataQueue);
		ImageDataNetworker frameDataNetworker = new ImageDataNetworker(frameDataQueue);
		frameDataProvider.start();
		frameDataNetworker.start();
	}

	private static class ImageDataNetworker extends Thread {
		public static final int PORT = 13579;
		public static final int HEADER_LENGTH = 4;
		public static final ByteBuffer HEADER_PACKER = ByteBuffer.allocate(4);
		private ServerSocket serverSocket;
		private BlockingQueue<byte[]> frameDataQueue;
		private ImageCompressor compressor;
		private boolean networking;

		public ImageDataNetworker(BlockingQueue<byte[]> frameDataQueue) throws IOException {
			this.frameDataQueue = frameDataQueue;
			this.compressor = new ImageCompressor(1000, 1000, BufferedImage.TYPE_BYTE_GRAY);
			this.compressor.setCompressionParams(ImageCompressor.COMPRESSION_TYPE_JPEG, 0.85F);
			this.compressor.setScaleParams(750, 750);
		}

		public void run() {
			try {
				this.serverSocket = new ServerSocket(13579);
				Socket socket = null;
				this.networking = true;
				while (!this.isInterrupted() && this.networking && (socket = this.serverSocket.accept()) != null) {
					try {
						System.out.println("client " + socket.getInetAddress().getHostName() + " connected to FrameDataNetworker");
						this.sendFrameData(socket.getOutputStream());
					} catch (IOException ex) {
						ex.printStackTrace();
					} finally {
						socket.close();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

		private void sendFrameData(OutputStream oStream) throws IOException, InterruptedException {

			while (!this.isInterrupted() && this.networking) {
				byte[] frameData = (byte[]) this.frameDataQueue.take();
				frameData = this.compressor.contractRawData(frameData);
				long startTime = System.currentTimeMillis();
				HEADER_PACKER.clear();
				HEADER_PACKER.putInt(frameData.length);
				oStream.write(HEADER_PACKER.array());
				oStream.write(frameData, 0, frameData.length);
				oStream.flush();
				System.out.println("send time: " + (System.currentTimeMillis() - startTime));
			}

		}

		public void stopNetwork() {
			this.networking = false;

			try {
				this.join(100L);
			} catch (InterruptedException ex) {
			}

			if (this.isAlive()) {
				this.interrupt();
			}

			if (this.serverSocket != null) {
				try {
					this.serverSocket.close();
				} catch (IOException ex) {
				}
			}

		}
	}

	private static class ImageDataProvider extends Thread {
		public static final String PATH = "image";
		private BlockingQueue<byte[]> queue;

		public ImageDataProvider(BlockingQueue<byte[]> queue) {
			this.queue = queue;
		}

		public void run() {
			File directory = new File("image");
			File[] files = directory.listFiles();
			byte[][] filesBytes = new byte[files.length][];

			int index;
			try {
				for (index = 0; index < files.length; ++index) {
					FileInputStream frameData = new FileInputStream(files[index]);
					MappedByteBuffer mbb = frameData.getChannel().map(MapMode.READ_ONLY, 0L, files[index].length());
					filesBytes[index] = new byte[(int) files[index].length()];
					mbb.get(filesBytes[index]);
					frameData.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}

			try {
				index = 0;
				while (!this.isInterrupted()) {
					byte[] fileBytes = filesBytes[index];
					this.queue.put(fileBytes);
					Thread.sleep(40);
					++index;
					if (index == filesBytes.length) {
						index = 0;
					}
				}
			} catch (InterruptedException ex) {
				Logger.getLogger(ImageDataServer.class.getName()).log(Level.SEVERE, (String) null, ex);
			}

		}
	}
}
