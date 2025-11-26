package com.maxgamertyper1;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;

public class ConfigHandler {

    public static void CreateConfig(File ConfigFile) {

        if (FileHandler.CheckIfFileExists(ConfigFile)) {
            boolean shouldContinue = QuestionHandler.AskBooleanQuestion(ConfigFile.getName()+" already exists! continue?", new String[]{"yes", "no"},"no");
            if (shouldContinue) {
                System.out.println("Ok! exitting!");
                System.exit(0);
            }
        }

        final JSONObject configDefaults = new JSONObject();
        final Config defaultConfig = new Config();

        for (Field field : Config.class.getDeclaredFields()) {
            field.setAccessible(true);

            final String variableName = field.getName();

            if (variableName.equals("ConfigDescriptions") || variableName.equals("VariableQuestions")) {continue;}

            final String variableType = field.getType().getSimpleName();
            Object variableValue = null;
            try {
                variableValue = field.get(defaultConfig);
            } catch (IllegalAccessException e) {
                System.err.println("Error: IllegalAccessException occured when finding defaults for the config!");
                System.exit(0);
            }

            final String description = Config.ConfigDescriptions.get(variableName);

            final JSONObject currentHandle = new JSONObject();
            currentHandle.put("type",variableType);
            currentHandle.put("value",variableValue == null ? JSONObject.NULL : variableValue);
            currentHandle.put("description",description);

            configDefaults.put(variableName,currentHandle);
        }

        FileHandler.WriteToJson(ConfigFile,configDefaults);
    }

    public static Config LoadJSONToConfig(File ConfigJSON) {
        JSONObject fileData = FileHandler.ReadJSONFile(ConfigJSON);
        Config adjustedConfig = new Config();

        for (Field field : adjustedConfig.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();

            if (name.equals("EXAMPLEKEY") || name.equals("VariableQuestions") || name.equals("ConfigDescriptions")) continue;

            if (fileData.has(name) && !fileData.isNull(name)) {
                Object value = ((JSONObject)fileData.get(name)).get("value");
                if (value == JSONObject.NULL) {
                    value=null;
                }

                try {
                    field.set(adjustedConfig, value);
                } catch (IllegalArgumentException e) {
                    System.err.println("Type mismatch for field '" + name + "': " + e.getMessage());
                } catch (IllegalAccessException e) {
                    System.err.println("Illegal Access Exception when setting config! field: "+name);
                }
            }
        }

        return adjustedConfig;
    }
}
