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
        int totalFrames = 0;
        FrameGrab grab = null;
        try {
            grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(FileHandler.CheckForFileInInputs(JSONConfig,JSONConfig.ImageOrVideoFile)));
            totalFrames=grab.getVideoTrack().getMeta().getTotalFrames();
        } catch (IOException e) {
            System.out.println("Error occured while reading video file");
            throw new RuntimeException(e);
        } catch (JCodecException e) {
            System.out.println("Invalid video File type!");
            throw new RuntimeException(e);
        }

        int frameNumber = 0;
        Picture picture;

        File FramesLocation = new File(JSONConfig.DataDirectory+"/"+JSONConfig.InputDirectory+"/TempVideoFrames");

        FileHandler.CheckOrCreateDirectory(FramesLocation);

        System.out.println("Grabbing all video Frames!");
        System.out.printf("Total frames: %s%n",totalFrames);

        double messageTracker=totalFrames/50d;
        double nextUpdateFrame = messageTracker;


        VideoProgressBar grabbingBar = new VideoProgressBar(totalFrames,"Grabbing Complete");
        for (int framesthrough=0;framesthrough!=totalFrames;framesthrough++) {
            if (frameNumber >= nextUpdateFrame) {
                grabbingBar.UpdateBar(frameNumber);
                nextUpdateFrame += messageTracker;
            }
            try {
                if ((picture = grab.getNativeFrame()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            String currentFrameName = String.format("frame_%d.png", frameNumber++);
            File currentFrame = new File(JSONConfig.DataDirectory + "/" + JSONConfig.InputDirectory + "/TempVideoFrames/", currentFrameName);

            try {
                ImageIO.write(bufferedImage, "png", currentFrame);
            } catch (IOException e) {
                System.out.printf("Error writing frame %s to data%n", currentFrame);
            }
        }
        grabbingBar.UpdateBar(frameNumber);

        System.out.println("\nGrabbed all video Frames!");
        System.out.println("Translating Frames to text!");

        VideoProgressBar translatingBar = new VideoProgressBar(totalFrames,"Translating Complete");
        nextUpdateFrame=messageTracker;
        for (int currentFrameNumber=0;currentFrameNumber!=frameNumber;currentFrameNumber++) {
            if (currentFrameNumber >= nextUpdateFrame) {
                translatingBar.UpdateBar(currentFrameNumber);
                nextUpdateFrame += messageTracker;
            }


            String currentFrameName = String.format("frame_%d.png", currentFrameNumber);
            File currentFrame = new File(JSONConfig.DataDirectory + "/" + JSONConfig.InputDirectory + "/TempVideoFrames/", currentFrameName);

            if (!FileHandler.CheckIfFileExists(currentFrame)) {
                System.out.printf("Frame: %s not found! %n",currentFrameNumber);
                continue;
            }

            ImageHandler IH = new ImageHandler(currentFrame);
            int[][] ImageBrightness = IH.ImageToPixelBrightness(JSONConfig.DatatableStep);

            String videoName = FileHandler.GetFileName(JSONConfig.ImageOrVideoFile);
            String invertedFile = FileHandler.GetFileName(JSONConfig.InvertedFileOutput);
            String normalFile = FileHandler.GetFileName(JSONConfig.NormalFileOutput);

            Main.SaveText(ImageBrightness,FileHandler.FileInOutputDir(JSONConfig,videoName+invertedFile+"/"+FileHandler.GetFileName(currentFrameName)+".txt"),FileHandler.FileInOutputDir(JSONConfig,videoName+normalFile+"/"+FileHandler.GetFileName(currentFrameName)+".txt"));

            if (!currentFrame.delete()) {
                System.out.printf("Error deleting translated frame: %s%n",currentFrameNumber);
            }
        }
        translatingBar.UpdateBar(frameNumber);
        System.out.println();


        if (!FramesLocation.delete()) {
            System.out.printf("Error deleting frame directory: %s%n",FramesLocation.getAbsolutePath());
        }
    }
}
