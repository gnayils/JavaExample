package com.example.jpegcompressor.server;

import com.example.jpegcompressor.imgutl.ImageCompressor;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ScreenDataServer {

	private static final Dimension DEMENSION = Toolkit.getDefaultToolkit()
			.getScreenSize();

	public static void main(String[] args) throws Exception {
		ArrayBlockingQueue<byte[]> screenDataQueue = new ArrayBlockingQueue<byte[]>(5);
		ScreenDataProvider screenDataProvider = new ScreenDataProvider(
				screenDataQueue);
		ScreenDataNetworker screenDataNetworker = new ScreenDataNetworker(
				screenDataQueue);
		screenDataProvider.start();
		screenDataNetworker.start();
	}

	private static class ScreenDataNetworker extends Thread {
		public static final int PORT = 24680;
		public static final int HEADER_LENGTH = 4;
		public static final ByteBuffer HEADER_PACKER = ByteBuffer.allocate(4);
		private ServerSocket serverSocket;
		private BlockingQueue<byte[]> frameDataQueue;
		private ImageCompressor compressor;
		private boolean networking;

		public ScreenDataNetworker(BlockingQueue<byte[]> frameDataQueue)
				throws IOException {
			this.frameDataQueue = frameDataQueue;
			this.compressor = new ImageCompressor(
					ScreenDataServer.DEMENSION.width,
					ScreenDataServer.DEMENSION.height, BufferedImage.TYPE_INT_RGB);
			this.compressor.setCompressionParams(ImageCompressor.COMPRESSION_TYPE_JPEG, 0.85F);
			this.compressor.setScaleParams(960, 540);
		}

		public void run() {
			try {
				this.serverSocket = new ServerSocket(24680);
				Socket socket = null;
				this.networking = true;
				while (!this.isInterrupted() && this.networking
						&& (socket = this.serverSocket.accept()) != null) {
					try {
						System.out.println("client "
								+ socket.getInetAddress().getHostName()
								+ " connected to FrameDataNetworker");
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

		private void sendFrameData(OutputStream oStream) throws IOException,
				InterruptedException {
			while (!this.isInterrupted() && this.networking) {
				byte[] frameData = (byte[]) this.frameDataQueue.take();
				frameData = this.compressor.contractImageData(frameData);
				long startTime = System.currentTimeMillis();
				HEADER_PACKER.clear();
				HEADER_PACKER.putInt(frameData.length);
				oStream.write(HEADER_PACKER.array());
				oStream.write(frameData, 0, frameData.length);
				oStream.flush();
				System.out.println("send time: "
						+ (System.currentTimeMillis() - startTime));
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

	private static class ScreenDataProvider extends Thread {
		private BlockingQueue<byte[]> queue;
		private Rectangle rect;
		private Robot robot;

		public ScreenDataProvider(BlockingQueue<byte[]> queue)
				throws AWTException {
			this.queue = queue;
			this.robot = new Robot();
			this.rect = new Rectangle(0, 0, ScreenDataServer.DEMENSION.width,
					ScreenDataServer.DEMENSION.height);
		}

		public void run() {
			while (true) {
				try {
					if (!this.isInterrupted()) {
						long startTime = System.currentTimeMillis();
						this.queue.put(ImageCompressor.imageDataFrom(this.robot
								.createScreenCapture(this.rect)));
						System.out.println("screen capture time: "
								+ (System.currentTimeMillis() - startTime));
						Thread.sleep(0);
						continue;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return;
			}
		}
	}
}
