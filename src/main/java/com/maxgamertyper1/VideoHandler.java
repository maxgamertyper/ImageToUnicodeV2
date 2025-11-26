package com.maxgamertyper1;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VideoHandler {
    private final Config JSONConfig;

    public VideoHandler(Config cfg) {
        JSONConfig=cfg;
    }

    public void MakeOutputDirectories() {
        String videoName = FileHandler.GetFileName(JSONConfig.ImageOrVideoFile);
        String invertedFile = FileHandler.GetFileName(JSONConfig.InvertedFileOutput);
        String normalFile = FileHandler.GetFileName(JSONConfig.NormalFileOutput);

        if (JSONConfig.BothBrightnesses) {
            FileHandler.CheckOrCreateDirectory(FileHandler.FileInOutputDir(JSONConfig,videoName+normalFile));
            FileHandler.CheckOrCreateDirectory(FileHandler.FileInOutputDir(JSONConfig,videoName+invertedFile));
        } else {
            if (JSONConfig.InvertBrighntess) {
                FileHandler.CheckOrCreateDirectory(FileHandler.FileInOutputDir(JSONConfig,videoName+invertedFile));
            } else {
                FileHandler.CheckOrCreateDirectory(FileHandler.FileInOutputDir(JSONConfig,videoName+normalFile));
            }
        }

    }

    public void IterateOverFrames() {
        FrameGrab grab = null;
        try {
            grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(FileHandler.CheckForFileInInputs(JSONConfig,JSONConfig.ImageOrVideoFile)));
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
            String currentFrameName = String.format("frame_%d.png", frameNumber++);
            File currentFrame = new File(JSONConfig.DataDirectory+"/"+JSONConfig.InputDirectory+"/", currentFrameName);

            try {
                ImageIO.write(bufferedImage,"png",currentFrame);
            } catch (IOException e) {
                System.out.printf("Error writing frame %s to data%n",currentFrame);
            }


            System.out.printf("Processing frame: %s%n",frameNumber);

            ImageHandler IH = new ImageHandler(FileHandler.CheckForFileInInputs(JSONConfig,currentFrameName));
            int[][] ImageBrightness = IH.ImageToPixelBrightness(JSONConfig.DatatableStep);

            String videoName = FileHandler.GetFileName(JSONConfig.ImageOrVideoFile);
            String invertedFile = FileHandler.GetFileName(JSONConfig.InvertedFileOutput);
            String normalFile = FileHandler.GetFileName(JSONConfig.NormalFileOutput);

            Main.SaveText(ImageBrightness,FileHandler.FileInOutputDir(JSONConfig,videoName+invertedFile+"/"+FileHandler.GetFileName(currentFrameName)+".txt"),FileHandler.FileInOutputDir(JSONConfig,videoName+normalFile+"/"+FileHandler.GetFileName(currentFrameName)+".txt"));

            currentFrame.delete();
        }
    }
}
