package org.learning.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class TCPClient {

    public static final String IP = "192.168.1.2";
    public static final int PORT = 12345;

    public static final int HEADER_LENGTH = 4;
    public static final int BUFFER_LENGTH = 1024;
    public static final byte[] HEADER_DATA = new byte[HEADER_LENGTH];
    public static final byte[] DATA_BUFFER = new byte[BUFFER_LENGTH];
    public static final ByteBuffer HEADER_PARSER = ByteBuffer.allocate(8);

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket client = new Socket(IP, PORT);
        InputStream mInputStream = client.getInputStream();
        int count = 0;
        while (true) {
            readFrom(mInputStream, HEADER_DATA);
            HEADER_PARSER.clear();
            HEADER_PARSER.put(HEADER_DATA);
            HEADER_PARSER.flip();
            int frameDataLength = HEADER_PARSER.getInt();

            int totalLength = 0;
            ByteBuffer frameDataBuffer = ByteBuffer.allocate(frameDataLength);
            for (int i = 0; i < frameDataLength / BUFFER_LENGTH; i++) {
                totalLength += readFrom(mInputStream, DATA_BUFFER);
                frameDataBuffer.put(DATA_BUFFER);
            }
            byte[] remainData = new byte[frameDataLength % BUFFER_LENGTH];
            totalLength += readFrom(mInputStream, remainData);
            frameDataBuffer.put(remainData);

            System.out.println("read length: " + totalLength + ", " + count++);
        }
    }

    public static int readFrom(InputStream is, byte[] buffer) throws IOException {
        int readLength = 0;
        do {
            readLength += is.read(buffer, readLength, buffer.length - readLength);
        } while (readLength < buffer.length);
        return readLength;
    }
}
