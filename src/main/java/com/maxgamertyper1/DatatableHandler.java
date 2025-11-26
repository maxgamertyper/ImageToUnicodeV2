package com.maxgamertyper1;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class DatatableHandler {
    private final File datatable;
    public int datatableStep = 2;
    private final HashMap<String,String> dataset = new HashMap<String,String>();

    public DatatableHandler(File Datatable) {
        datatable = Datatable;
    }

    public void IntegrateDatatable() {
        JSONObject datatableValues = FileHandler.ReadJSONFile(datatable);

        for (String key : datatableValues.keySet()) {
            dataset.put(key, (String) datatableValues.get(key));
        }

        datatableStep = Integer.parseInt(dataset.getOrDefault("stepData", "2"));
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
}
