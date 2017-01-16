package org.learning.socket;

import org.learning.socket.imgutil.JPEGCompressor;
import org.learning.socket.imgutil.ImageScaler;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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

public class TCPServer {

    public static void main(String[] args) throws IOException {
        BlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(5);

        FrameDataProvider provider = new FrameDataProvider(queue);
        FrameDataNetworker frameDataNetworker = new FrameDataNetworker(queue);
        CommandDataNetworker commandDataNetworker = new CommandDataNetworker();

        provider.start();
        frameDataNetworker.start();
        commandDataNetworker.start();
    }

    private static class FrameDataProvider extends Thread {

        public static final String PATH = "/home/sg/lf489159/Documents/Pictures/raw";
        private BlockingQueue<byte[]> queue;

        public FrameDataProvider(BlockingQueue<byte[]> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            File directory = new File(PATH);
            File[] files = directory.listFiles();
            byte[][] filesBytes = new byte[files.length][];
            try {
                for (int i = 0; i < files.length; i++) {
                    FileInputStream fis = new FileInputStream(files[i]);
                    MappedByteBuffer mbb = fis.getChannel().map(MapMode.READ_ONLY, 0, files[i].length());
                    filesBytes[i] = new byte[(int) files[i].length()];
                    mbb.get(filesBytes[i]);
                    fis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
            try {
                int index = 0;
                while (!isInterrupted()) {
                    byte[] frameData = filesBytes[index];
                    queue.offer(frameData);
                    Thread.sleep(40);
                    index++;
                    if (index == filesBytes.length) {
                        index = 0;
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static class FrameDataNetworker extends Thread {

        public static final int PORT = 13579;
        public static final int HEADER_LENGTH = 4;
        public static final ByteBuffer HEADER_PACKER = ByteBuffer.allocate(HEADER_LENGTH);
        private ServerSocket serverSocket;
        private BlockingQueue<byte[]> frameDataQueue;
        private JPEGCompressor compressor;
        private ImageScaler scaler;
        private boolean networking;

        public FrameDataNetworker(BlockingQueue<byte[]> frameDataQueue) throws IOException {
            this.frameDataQueue = frameDataQueue;
            this.compressor = new JPEGCompressor(BufferedImage.TYPE_BYTE_GRAY, 1000, 1000, JPEGCompressor.JPEG, 0.85f);
            this.scaler = new ImageScaler(BufferedImage.TYPE_BYTE_GRAY, 1000, 1000, 750, 750);
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(PORT);
                Socket client = null;
                networking = true;
                while (!isInterrupted() && networking && (client = serverSocket.accept()) != null) {
                    try {
                        System.out.println("client " + client.getInetAddress().getHostName() + " connected to FrameDataNetworker");
                        sendFrameData(client.getOutputStream());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        client.close();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void sendFrameData(OutputStream oStream) throws IOException, InterruptedException {
            byte[] frameData = null;
            long startTime = 0;
            while (!isInterrupted() && networking) {
                frameData = frameDataQueue.take();

//                startTime = System.currentTimeMillis();
//                frameData = scaler.fastScalePixelData(frameData);
//                System.out.println("scale time: " + (System.currentTimeMillis() - startTime) + ", the length of after scaled: " + frameData.length);

                startTime = System.currentTimeMillis();
                frameData = compressor.compressPixelData(frameData);
                System.out.println("compress time: " + (System.currentTimeMillis() - startTime) + " the length of after compressed: " + frameData.length);

                HEADER_PACKER.clear();
                HEADER_PACKER.putInt(frameData.length);
                oStream.write(HEADER_PACKER.array());
                oStream.write(frameData, 0, frameData.length);
                oStream.flush();
            }
        }

        public void stopNetwork() {
            networking = false;
            try {
                join(100);
            } catch (InterruptedException e) {
            }
            if (isAlive()) {
                interrupt();
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static class CommandDataNetworker extends Thread {

        public static final int PORT = 24680;
        private ServerSocket serverSocket;
        private boolean networking;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(PORT);
                Socket client = null;
                networking = true;
                while (!isInterrupted() && networking && (client = serverSocket.accept()) != null) {
                    try {
                        System.out.println("client " + client.getInetAddress().getHostName() + " connected to CommandDataNetworker");
                        readCommandData(new ObjectInputStream(client.getInputStream()));
                        //sendReturnData(client.getOutputStream());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        client.close();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void sendReturnData(OutputStream oStream) throws IOException {
            while (!isInterrupted() && networking) {
                oStream.write(new byte[]{0x1, 0x1, 0x1, 0x1, 0x1, 0x1});
                System.out.println("send return data");
            }
        }

        private void readCommandData(ObjectInputStream iStream) throws IOException, InterruptedException {
            while (!isInterrupted() && networking) {
                try {
                    Command command = (Command) iStream.readObject();
                    System.out.println("Command: {type: " + command.getCommandType() + ", value" + command.getCommandValue() + "}");
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void stopNetwork() {
            networking = false;
            try {
                join(100);
            } catch (InterruptedException e) {
            }
            if (isAlive()) {
                interrupt();
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static class Command {

        private int commandType;
        private int commandValue;

        public int getCommandType() {
            return commandType;
        }

        public int getCommandValue() {
            return commandValue;
        }
    }
}
