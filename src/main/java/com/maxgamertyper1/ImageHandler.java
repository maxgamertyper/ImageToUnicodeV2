package com.maxgamertyper1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageHandler {
    private final File imageFile;

    public ImageHandler(File imagefile) {
        imageFile = imagefile;
    }

    public static int roundToNearestStep(double input, int step) {
        return (int) Math.round(input / step) * step;
    }

    public int[][] ImageToPixelBrightness(int step) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.out.printf("Error occured when trying to read image file, %s\n",imageFile.getName());
            throw new RuntimeException(e);
        }


        final int imageHeight = image.getHeight();
        final int imageWidth = image.getWidth();

        int[][] imagePixelBrightness = new int[imageHeight][imageWidth];

        for (int pixelYValue = 0; pixelYValue <imageHeight; pixelYValue++) {
            for (int pixelXValue = 0; pixelXValue < imageWidth; pixelXValue++) {

                final int pixelColorData = image.getRGB(pixelXValue, pixelYValue);
                final Color pixelColor = new Color(pixelColorData,false);
                final int red = pixelColor.getRed();
                final int green = pixelColor.getGreen();
                final int blue = pixelColor.getBlue();
                final double luminance = 0.2126*red + 0.7152*green + 0.0722*blue;

                imagePixelBrightness[pixelYValue][pixelXValue] = roundToNearestStep(luminance ,step);
            }
        }

        return imagePixelBrightness;
    }
}
