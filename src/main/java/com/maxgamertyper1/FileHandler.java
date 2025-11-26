package com.maxgamertyper1;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandler {

    public static void WriteToJson(File JSONfile, JSONObject data) {
        try {
            Files.writeString(Path.of(JSONfile.toURI()),data.toString(4));
        } catch (IOException e) {
            System.out.printf("Error occured when saving to JSON file: %s%n",JSONfile.getAbsolutePath());
            System.exit(0);
        }
    }

    public static void UpdateJSONFile(File JSONfile, String fieldName, String fieldValue) {
        JSONObject current = ReadJSONFile(JSONfile);
        ((JSONObject) current.get(fieldName)).put("value",fieldValue);
        WriteToJson(JSONfile,current);
    }

    public static boolean CheckIfFileExists(File JSONConfig) {return JSONConfig.exists();}

    public static JSONObject ReadJSONFile(File ConfigFile) {
        String fileContent = ReadStringFile(ConfigFile);
        return new JSONObject(fileContent);
    }

    public static boolean CheckOrCreateDirectories(Config adjustedConfig) {
        File dataDirectory = new File(adjustedConfig.DataDirectory);
        File inputDirectory = new File(adjustedConfig.DataDirectory+"/"+adjustedConfig.InputDirectory);
        File outputDirectory = new File(adjustedConfig.DataDirectory+"/"+adjustedConfig.OutputDirectory);

        CheckOrCreateDirectory(dataDirectory);
        CheckOrCreateDirectory(inputDirectory);
        CheckOrCreateDirectory(outputDirectory);

        return CheckIfFileExists(dataDirectory) && CheckIfFileExists(inputDirectory) && CheckIfFileExists(outputDirectory);
    }

    public static File CheckForFileInInputs(Config data, String fileName) {
        File potentialFile = FileInInputDir(data,fileName);
        if (potentialFile.exists()) {
            return  potentialFile;
        } else {
            System.err.printf("File with name %s not found in your set input directory%n",fileName);
            System.exit(0);
        }
        return new File("secret file mode!");
    }

    public static File FileInInputDir(Config data, String fileName) {return new File(data.DataDirectory+"/"+data.InputDirectory+"/"+fileName);}

    public static void WriteStringToFile(File output, String data) {
        try {
            Files.writeString(output.toPath(), data);
        } catch (IOException e) {
            System.err.println("Error occured when saving the string data to a file! "+e);
            System.exit(0);
        }
    }

    public static File FileInOutputDir(Config data, String fileName) {return new File(data.DataDirectory+"/"+data.OutputDirectory+"/"+fileName);}

    public static String GetFileName(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filePath.length() - 1) {
            return "";
        }
        return filePath.substring(0,dotIndex);
    }

    public static boolean CheckOrCreateDirectory(File directory) {
        if (!CheckIfFileExists(directory)) {
            directory.mkdir();
        }
        return CheckIfFileExists(directory);
    }

    public static String ReadStringFile(File file) {
        String fileContent = null;
        try {
            fileContent = Files.readString(file.toPath());
        } catch (IOException e) {
            System.err.printf("Error occured when trying to read JSON file, %s\n",file.getName());
            System.exit(0);
        }

        return fileContent;
    }
}
