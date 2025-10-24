package com.maxgamertyper1;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class DatatableHandler {
    final File datatableFile;
    private int datatableStep = 0;
    HashMap<String,String> dataset = new HashMap<String,String>();

    public DatatableHandler(File datatable) {
        datatableFile = datatable;
    }

    public void ReadValues() {
        String fileContent = null;
        try {
            fileContent = Files.readString(datatableFile.toPath());
        } catch (IOException e) {
            System.out.printf("Error occured when trying to read database file, %s\n",datatableFile.getName());
            System.exit(0);
        }

        JSONObject jsonObject = new JSONObject(fileContent);

        for (String key : jsonObject.keySet()) {
            dataset.put(key, (String) jsonObject.get(key));
        }

        datatableStep = Integer.parseInt(dataset.getOrDefault("stepData", "2"));
    }

    public void SetValues(HashMap<String,String> datatable) {
        dataset = datatable;
    }

    public String[][] ValueTranslation(int[][] brightnessData, boolean invertBrightness) {
        String[][] translated = new String[brightnessData.length][];

        for (int pixelYValue = 0; pixelYValue < brightnessData.length; pixelYValue++) {
            translated[pixelYValue] = new String[brightnessData[pixelYValue].length];
            for (int pixelXValue = 0; pixelXValue < brightnessData[pixelYValue].length; pixelXValue++) {

                if (invertBrightness) {
                    translated[pixelYValue][pixelXValue]=dataset.get(String.valueOf(255-brightnessData[pixelYValue][pixelXValue]));
                } else {
                    translated[pixelYValue][pixelXValue]=dataset.get(String.valueOf(brightnessData[pixelYValue][pixelXValue]));
                }
            }
        }

        return translated;
    }

    public void WriteDatatable(HashMap<String,String> datatable, File storingFile) {
        JSONObject jsonizedDatatable = new JSONObject(datatable);

        try {
            Files.writeString(Path.of(storingFile.toURI()),jsonizedDatatable.toString(4));
        } catch (IOException e) {
            System.out.printf("Error occured when saving datatable to %s%n",storingFile.getAbsolutePath());
            System.exit(0);
        }

    }

    public int getStep() {
        return datatableStep;
    }
}
