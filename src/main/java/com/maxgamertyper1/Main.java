package com.maxgamertyper1;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(
                "This program needs some disk space. This should be set up in an isolated folder for ease of use. Unless you want to go digging for folders, be my guest"
        );


        final Scanner scanner = new Scanner(System.in);

        System.out.println("Continue? (exit to stop, anything else will continue)");
        final String continueResponse = scanner.nextLine();

        if (continueResponse.equalsIgnoreCase("exit")) {
            System.exit(0);
        }

        System.out.println("Alright, attempting creation of folders");

        InputHandler inputHandler = new InputHandler(scanner);
        HashMap<String,File> importantFiles = inputHandler.getFiles();

        DatatableHandler datatableHandler = null;

        int step = 0;
        if (!inputHandler.shouldCalculateDatatable()) {
            datatableHandler = new DatatableHandler(importantFiles.get("datatableFile"));
            datatableHandler.ReadValues();
            step = datatableHandler.getStep();
        } else {
            FontHandler fontHandler = new FontHandler(importantFiles.get("fontFile"));
            String fontName = importantFiles.get("fontFile").getName();

            System.out.println("What brightness step size would you like? (default:2) [1,2...255] - lower numbers result in more detail");
            int userSetStep = 2;
            if (scanner.hasNextInt()) {
                userSetStep = scanner.nextInt();
            }
            scanner.nextLine();

            if (userSetStep>255 || userSetStep<=0) {
                System.out.println("Invalid step size, it should be between 1 and 255");
                System.exit(0);
            }
            step = userSetStep;
            System.out.printf("Calculating datatable for %s with step size %d%n",fontName , userSetStep);

            System.out.println("Checking font for changed Unicode Characters");
            fontHandler.SetChangedUnicodeCharacters();

            System.out.println("Found all changed font characters");
            System.out.println("Getting fill percentage of changed characters");

            final HashMap<String,String> characterFills = fontHandler.GetCharacterFills();
            final HashMap<String,String> simplifiedFills = fontHandler.SimplifyCharacterFills(characterFills);

            System.out.println("Creating datatable");
            final HashMap<String,String> finalDatatable = fontHandler.CreateFinalHashmap(simplifiedFills, step);


            int dotIndex = fontName.lastIndexOf('.');
            if (dotIndex > 0) {
                fontName = fontName.substring(0, dotIndex);
            }

            System.out.printf("Saving datatable to %sdatatable.json%n (it will be in the inputs folder for future use)",fontName);

            File newDatatableFile = new File("data/inputs/"+fontName+"datatable.json");
            datatableHandler = new DatatableHandler(newDatatableFile);
            datatableHandler.WriteDatatable(finalDatatable,newDatatableFile);

            datatableHandler.SetValues(finalDatatable);
        }

        Boolean conversionType = inputHandler.shouldInvertBrightness();

        OutputHandler outputHandler = new OutputHandler(conversionType,scanner);

        if (inputHandler.IsInputImage()) {
            ImageConversion(conversionType,importantFiles.get("inputFile"),step,datatableHandler,outputHandler);
        } else  {
            VideoConversion(conversionType,importantFiles.get("inputFile"),step,datatableHandler,outputHandler);
        }


        System.out.println("Completed!");
        System.out.println("Make sure to change the notepad or whatever text editor your using's font for the files. Otherwise, it will look weird and wrong");
    }

    public static void ImageConversion(Boolean conversionType, File inputFile, int step, DatatableHandler datatableHandler, OutputHandler outputHandler) {
        HashMap<String,String[][]> imageData = ConvertImage(conversionType,inputFile,step,datatableHandler);
        SaveImage(imageData,outputHandler);
    }

    public static void VideoConversion(Boolean conversionType, File inputFile, int step, DatatableHandler datatableHandler, OutputHandler outputHandler) throws IOException {
        String videoName = inputFile.getName();

        int dotIndex = videoName.lastIndexOf('.');
        if (dotIndex > 0) {
            videoName = videoName.substring(0, dotIndex);
        }

        String normalPath = InputHandler.getFileName(outputHandler.GetBaseNormalFilePath());
        String invertPath = InputHandler.getFileName(outputHandler.GetBaseInvertedFilePath());

        VideoHandler videoHandler = new VideoHandler(inputFile,videoName,normalPath,invertPath,conversionType,datatableHandler,step);
        videoHandler.IterateOverFrames();
    }

    public static HashMap<String,String[][]> ConvertImage(Boolean conversionType, File file, int step, DatatableHandler datatableHandler) {

        HashMap<String,String[][]> returnMap = new HashMap<String,String[][]>();

        int[][] brightness = null;
        String[][] mapped = null;
        ImageReader imageHandler = new ImageReader(file);
        brightness = imageHandler.ImageToPixelBrightness(step);
        if (conversionType!=Boolean.TRUE) {
            mapped = datatableHandler.ValueTranslation(brightness, false);
            returnMap.put("normal",mapped);
        }
        if (conversionType!=Boolean.FALSE) {
            mapped = datatableHandler.ValueTranslation(brightness, true);
            returnMap.put("inverse",mapped);
        }

        return returnMap;
    }

    public static void SaveImage(HashMap<String,String[][]> data, OutputHandler outputHandler) {

        for (Map.Entry<String,String[][]> entry : data.entrySet()) {
            String conversionType = entry.getKey();
            String[][] conversion = entry.getValue();

            if (conversionType.equals("normal")) {
                try {
                    Files.writeString(Path.of(outputHandler.GetNormalFilePath()), ListToString(conversion));
                } catch (IOException e) {
                    System.out.println("an error occured while saving to the output file");
                }
            } else if (conversionType.equals("inverse")) {
                try {
                    Files.writeString(Path.of(outputHandler.GetInvertedFilePath()), ListToString(conversion));
                } catch (IOException e) {
                    System.out.println("an error occured while saving to the output file");
                }
            }
        }
    }

    public static String ListToString(String[][] input) {
        StringBuilder result = new StringBuilder();

        for (String[] strings : input) {
            for (String string : strings) {
                result.append(string);
            }
            result.append("\n");
        }
        return result.toString();
    }
}