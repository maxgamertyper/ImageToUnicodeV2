package com.maxgamertyper1;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class InputHandler {
    private boolean calculateDatatable;
    private File inputFile = null;
    private final boolean isImageFile;
    private File fontFile = null;
    private File datatableFile = null;
    private final File dataDirectory = new File("data");
    private final File inputsDirectory = new File("data/inputs");
    private final File outputsDirectory = new File("data/outputs");
    private final Boolean conversionTypeIsInvert;


    public InputHandler(Scanner scanner) {
        // try to make the data folders
        createFolders();

        System.out.println("You will be asked for your input file at the end.");

        // check if the user has already generated a datatable
        datatableSetup(scanner); // sets the datatableJSON if they say no

        if (calculateDatatable) {
            fontFile=attemptGetFile("What's the name of the font file? (should be in data/inputs) ex: superawesomefont.ttf or comicsans.ttf  *This is made for Mono typed fonts, I can't gaurantee it will work with a non-mono-typed font",scanner); // find font file
        }


        System.out.println("Should the Brightness of the file be inverted? - Null will do both Inverted and Normal. (default: null) [True,False,Null]");
        String userInput = scanner.nextLine();

        conversionTypeIsInvert = userInput.equalsIgnoreCase("false") ? Boolean.FALSE : userInput.equalsIgnoreCase("true") ? Boolean.TRUE : null;

        System.out.println("Will you be converting an image file? (False means you are using a video file) [True,False, default:true]");
        isImageFile=!scanner.nextLine().equalsIgnoreCase("false");

        if (isImageFile) {
            inputFile = attemptGetFile("What's the name of the image file? (should be in data/inputs) ex: banana.png or apple.jpg",scanner);
            String fileExtension = getFileExtension(inputFile);
            System.out.println(fileExtension);
            if (!(fileExtension.equals("png") || fileExtension.equals("jpg"))) {
                System.out.println("Wrong file type, this might be a video file or an unsupported type!");
                System.exit(0);
            }
        } else {
            inputFile = attemptGetFile("What's the name of the video file? (should be in data/inputs) ex: banana.mp4 or apple.mp4",scanner);
            if (!getFileExtension(inputFile).equals("mp4")) {
                System.out.println("Wrong file type, this might be a image file or an unsupported type!");
                System.exit(0);
            }
        }

    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }

    public static String getFileName(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filePath.length() - 1) {
            return "";
        }
        return filePath.substring(0,dotIndex);
    }

    public File attemptGetFile(String printText, Scanner scanner) {
        System.out.println(printText);
        final String fileLocation = scanner.nextLine(); // get file name from user

        System.out.printf("Checking inputs folder for %s\n",fileLocation);

        File resultingFile = checkFoldersForFileName(fileLocation); // check if the file is there

        if (resultingFile!=null) {
            System.out.printf("Found %s!\n",fileLocation);
        } else {
            System.out.printf("%s not found, please try again\n",fileLocation);
            System.exit(0); // stop running
        }

        return resultingFile;
    }

    public boolean IsInputImage() {
        return isImageFile;
    }



    public void createFolders() {
        dataDirectory.mkdir();
        inputsDirectory.mkdir();
        outputsDirectory.mkdir();

        if (dataDirectory.exists() && inputsDirectory.exists() && outputsDirectory.exists()) {
            System.out.println("All directories created successfully!");
        } else {
            System.out.println("Failed to create some directories.");
            System.out.println("Please try again, or make a folder called \"data\" and put 2 more foldes called \"inputs\" and \"outputs\" in it");
            System.exit(1);
        }
    }


    public void datatableSetup(Scanner scanner) {
        System.out.println("Calculate datatable for font? (anyform of \"no\", default is yes)");
        calculateDatatable = !scanner.nextLine().equalsIgnoreCase("no");

        if (!calculateDatatable) {
            datatableFile = attemptGetFile("What's the name of the datatable file? (should be in data/inputs, for the best results, do a mono typed font) ex: arialdatatable.json or comicsansdatatable.json. Or restart to calculate it",scanner);
        }
    }


    public File checkFoldersForFileName(String name) {
        final File[] inputsFiles = inputsDirectory.listFiles(); // folders in dir
        File outputFile = null;

        assert inputsFiles != null;
        for (File file : inputsFiles) {
            if (file.getName().equals(name)) {
                outputFile=file; // set the file if it has the same name
                break; // stop searching
            }
        }

        return outputFile; // return the file
    }


    public HashMap<String,File> getFiles() {
        final HashMap<String,File> returnMap = new HashMap<String,File>();
        returnMap.put("inputFile",inputFile);
        returnMap.put("fontFile",fontFile);
        returnMap.put("datatableFile",datatableFile);
        return returnMap; // return a map of the font and image files
    }


    public boolean shouldCalculateDatatable() {
        return calculateDatatable;
    }

    public Boolean shouldInvertBrightness() {
        return conversionTypeIsInvert;
    }
}
