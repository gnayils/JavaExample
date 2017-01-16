package org.learning.socket.imgutil;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class RawImageReader {

    private int width;
    private int height;
    private ComponentColorModel compColorModel;
    private SampleModel sampleModel;
    private int transferType;

    public RawImageReader(int width, int height, int[] bits) {
        this.width = width;
        this.height = height;
        if (bits.length == 1 && bits[0] == 8) {
            transferType = DataBuffer.TYPE_BYTE;
        } else if (bits.length == 2 && bits[0] == 6 && bits[1] == 8) {
            transferType = DataBuffer.TYPE_USHORT;
        } else {
            throw new IllegalArgumentException("Unsupported bit");
        }
        this.compColorModel = new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_GRAY),
                bits,
                false,
                true,
                Transparency.OPAQUE,
                transferType);
        this.sampleModel = this.compColorModel.createCompatibleSampleModel(width, height);
    }

    public BufferedImage read(String path) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        ByteBuffer bb = fis.getChannel().map(MapMode.READ_ONLY, 0, file.length());
        DataBuffer db = null;
        if (transferType == DataBuffer.TYPE_BYTE) {
            byte[] bytes = new byte[width * height];
            bb.get(bytes);
            db = new DataBufferByte(bytes, bytes.length);
        } else if (transferType == DataBuffer.TYPE_USHORT) {
            short[] shorts = new short[width * height];
            ShortBuffer sb = bb.asShortBuffer();
            sb.position(sb.capacity() - shorts.length);
            sb.get(shorts);
            db = new DataBufferUShort(shorts, shorts.length);
        }
        WritableRaster raster = Raster.createWritableRaster(
                sampleModel,
                db,
                new Point(0, 0));
        return new BufferedImage(compColorModel, raster, false, null);
    }

    public static void main(String[] args) throws Exception {
       
        File directory = new File("/home/sg/lf489159/Documents/Pictures/raw");
        final File[] images = directory.listFiles();


        final RawImageReader reader = new RawImageReader(1312, 1312, new int[]{6, 8});


        JFrame frame = new JFrame("Raw Image Reader");
        final JScrollPane panel = new JScrollPane();
        final JLabel label = new JLabel();
        JButton prevButton = new JButton("Previous");
        prevButton.addActionListener(new ActionListener() {

            int index = images.length;

            public void actionPerformed(ActionEvent e) {
                try {
                    if (index > 1) {
                        index--;
                        BufferedImage bufferedImage = reader.read(images[index].getAbsolutePath());
//                        ImageScaler scaler = new ImageScaler(BufferedImage.TYPE_USHORT_GRAY, bufferedImage.getWidth(), bufferedImage.getHeight(), 500, 500);
//                        bufferedImage = scaler.scale(bufferedImage);
                        label.setIcon(new ImageIcon(bufferedImage));
                        panel.invalidate();
                    } else {
                        index = images.length;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {

            int index = -1;

            public void actionPerformed(ActionEvent e) {
                try {
                    if (index < images.length - 1) {
                        index++;
                        BufferedImage bufferedImage = reader.read(images[index].getAbsolutePath());
//                        ImageScaler scaler = new ImageScaler(BufferedImage.TYPE_USHORT_GRAY, bufferedImage.getWidth(), bufferedImage.getHeight(), 500, 500);
//                        bufferedImage = scaler.scale(bufferedImage);
                        label.setIcon(new ImageIcon(bufferedImage));
                        panel.invalidate();
                    } else {
                        index = -1;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.setViewportView(label);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.getContentPane().add(prevButton, BorderLayout.WEST);
        frame.getContentPane().add(nextButton, BorderLayout.EAST);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}