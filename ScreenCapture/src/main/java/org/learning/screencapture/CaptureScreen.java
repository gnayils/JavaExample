package org.learning.screencapture;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lf489159 on 4/10/17.
 */
public class CaptureScreen implements NativeKeyListener {

    private Robot robot;
    private Rectangle screenRegion;
    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public CaptureScreen() throws NativeHookException, AWTException {
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(this);
        robot = new Robot();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenRegion = new Rectangle(0, 0, screenSize.width, screenSize.height);
    }

    private void capture() throws IOException {
        BufferedImage image = robot.createScreenCapture(screenRegion);
        ImageIO.write(image, "png", new File("./" + dateFormat.format(new Date()) + ".png"));
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent event) {
        if(event.getKeyCode() == NativeKeyEvent.VC_PAUSE) {
            System.exit(0);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent event) {
        if(event.getKeyCode() == NativeKeyEvent.VC_PRINTSCREEN) {
            try {
                capture();
            } catch (IOException e) {
                System.err.println("capture screen failed: " + e.getMessage());
            }
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent event) {

    }


    public static void main(String[] args) throws AWTException, NativeHookException {
        new CaptureScreen();
    }
}
