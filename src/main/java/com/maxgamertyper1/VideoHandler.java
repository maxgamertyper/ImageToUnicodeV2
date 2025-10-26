package com.maxgamertyper1;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import javax.imageio.ImageIO;

public class VideoHandler {
    private final File videoFile;
    private final String fileName;
    private final String normalPath;
    private final String invertPath;
    private final Boolean conversionType;
    private File normalDir;
    private File invertDir;
    private final int step;
    private final DatatableHandler datatableHandler;

    public VideoHandler(File inputFile, String filename, String normalpath, String invertpath, Boolean conversiontype, DatatableHandler DatatableHandler, int Step) {
        videoFile=inputFile;
        fileName=filename;
        normalPath=normalpath;
        invertPath=invertpath;
        conversionType = conversiontype;
        step = Step;
        datatableHandler = DatatableHandler;

        MakeOutputDirectories();
    }

    public void MakeOutputDirectories() {
        if (conversionType!=Boolean.TRUE) {
            System.out.printf("Saving video frames in %s%s/frame(number).txt%n",invertPath,fileName);
            invertDir = new File("data/outputs/"+fileName+invertPath);
            invertDir.mkdirs();

            if (invertDir.exists()) {
                System.out.println("Created normal directory successfully!");
            } else {
                System.out.println("Couldn't create normal output directory!");
                System.exit(0);
            }
        }
        if (conversionType!=Boolean.FALSE) {
            System.out.printf("Saving video frames in %s%s/frame(number).txt%n",normalPath,fileName);
            normalDir = new File("data/outputs/"+fileName+normalPath);
            normalDir.mkdirs();

            if (normalDir.exists()) {
                System.out.println("Created normal directory successfully!");
            } else {
                System.out.println("Couldn't create normal output directory!");
                System.exit(0);
            }
        }

    }

    public void SaveImage(HashMap<String,String[][]> data,String fileName) {

        for (Map.Entry<String,String[][]> entry : data.entrySet()) {
            String conversionType = entry.getKey();
            String[][] conversion = entry.getValue();

            if (conversionType.equals("normal")) {
                try {
                    Files.writeString(Path.of(normalDir.getPath()+"/"+fileName), Main.ListToString(conversion));
                } catch (IOException e) {
                    System.out.println("an error occured while saving to the output file");
                }
            } else if (conversionType.equals("inverse")) {
                try {
                    Files.writeString(Path.of(invertDir.getPath()+"/"+fileName), Main.ListToString(conversion));
                } catch (IOException e) {
                    System.out.println("an error occured while saving to the output file");
                }
            }
        }
    }

    public void IterateOverFrames() {

        FrameGrab grab = null;
        try {
            grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(videoFile));
        } catch (IOException e) {
            System.out.println("Error occured while reading video file");
            throw new RuntimeException(e);
        } catch (JCodecException e) {
            System.out.println("Invalid video File type!");
            throw new RuntimeException(e);
        }


        int frameNumber = 0;
        Picture picture;

        while (true) {

            try {
                if ((picture = grab.getNativeFrame()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            String currentFrameName = String.format("frame_%04d.png", frameNumber++);
            File currentFrame = new File("data/", currentFrameName);

            try {
                ImageIO.write(bufferedImage,"png",currentFrame);
            } catch (IOException e) {
                System.out.printf("Error writing frame %s to data%n",currentFrame);
            }


            System.out.printf("Processing frame: %s%n",frameNumber);

            HashMap<String,String[][]> data= Main.ConvertImage(conversionType,currentFrame,step,datatableHandler);
            SaveImage(data, InputHandler.getFileName(currentFrameName)+".txt");

            currentFrame.delete();
        }

    }
}
