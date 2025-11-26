package com.maxgamertyper1;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static Config JSONConfig = null;
    public static DatatableHandler DH = null;


    public static void main(String[] args) {
        File configFile = new File("ImageToUnicodeConfig.json");
        boolean defaultConfigExists = FileHandler.CheckIfFileExists(configFile);

        if (!defaultConfigExists) {
            configFile = HandleNonDefaultConfig();
        }

        System.out.println("Config File Found! Loading data!");


        JSONConfig = ConfigHandler.LoadJSONToConfig(configFile);
        System.out.println("Checking for any empty Config fields!");
        JSONConfig.FillInNullValues();
        System.out.println("If you were expecting a question but didnt get one, check the config file and see if the value is set!");

        boolean FoundOrCreatedDirectories = FileHandler.CheckOrCreateDirectories(JSONConfig);

        if (!FoundOrCreatedDirectories) {
            System.out.println("Couldn't find or create directories!");
            System.exit(0);
        }

        if (!JSONConfig.IsDisplayingPrecached) {
            PrecachedDisplay();
            return;
        }

        if (JSONConfig.CreatingDatatable) {
            if (JSONConfig.DatatableStep>255 || JSONConfig.DatatableStep<=0) {
                System.err.println("Invalid step size, it should be between 1 and 255");
                System.exit(0);
            }

            FontHandler FH = new FontHandler(FileHandler.CheckForFileInInputs(JSONConfig,JSONConfig.FontFile),JSONConfig.DatatableStep);
            HashMap<String,String> finalDatatable = FH.ConvertFontFile();

            String datatableLocation = QuestionHandler.AskStringQuestion("Where would you like the datatable saved to? *in your inputs folder*",FileHandler.GetFileName(JSONConfig.FontFile)+"datatable.json");
            FileHandler.WriteToJson(FileHandler.FileInInputDir(JSONConfig,datatableLocation), new JSONObject(finalDatatable));

            boolean saveDatatableToConfigDefaults = QuestionHandler.AskBooleanQuestion("Would you like to update the config with this datatable as the default?",new String[]{"yes","no"},"no");
            if (!saveDatatableToConfigDefaults) {
                FileHandler.UpdateJSONFile(configFile,"DatatableFile",datatableLocation);
            }
            JSONConfig.DatatableFile = datatableLocation;
        }

        DH = new DatatableHandler(FileHandler.CheckForFileInInputs(JSONConfig,JSONConfig.DatatableFile));
        DH.IntegrateDatatable();
        JSONConfig.DatatableStep = DH.datatableStep;

        if (JSONConfig.IsImageFile) {
            ImageHandler IH = new ImageHandler(FileHandler.CheckForFileInInputs(JSONConfig,JSONConfig.ImageOrVideoFile));
            int[][] ImageBrightness = IH.ImageToPixelBrightness(JSONConfig.DatatableStep);

            SaveText(ImageBrightness, new File("secret"), new File("mode"));
        } else {
            VideoHandler VH = new VideoHandler(JSONConfig);
            VH.MakeOutputDirectories();
            VH.IterateOverFrames();
        }


        System.out.println("Completed! \n Make sure to change the notepad or whatever text editor your using's font for the files. Otherwise, it will look weird and wrong");
    }

    public static File HandleNonDefaultConfig() {
        File configFile;
        String configLocation = QuestionHandler.AskStringQuestion("Where is your config located? it should be in the same folder as this program. *will be created if not found*","ImageToUnicodeConfig.json");
        File userSpecifiedConfig = new File(configLocation);
        boolean configExists = FileHandler.CheckIfFileExists(userSpecifiedConfig);

        if (!configExists) {
            System.out.println("Warning: Config not found, creating one in this folder");
            String configFileName = QuestionHandler.AskStringQuestion("What would you like the name of the config to be? *if not default, you'll have to specify each time*","ImageToUnicodeConfig.json");

            configFile = new File(configFileName);
            ConfigHandler.CreateConfig(configFile);

            System.out.println("Created new config folder!");

            boolean shouldNotContinue = QuestionHandler.AskBooleanQuestion("Would you like to edit the config before continuing?",new String[]{"yes","no"},"yes");
            if (shouldNotContinue) {
                System.exit(0);
            }
        } else {
            configFile = userSpecifiedConfig;
        }

        return configFile;
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

    public static void SaveText(int[][] ImageBrightness, File InvertedFile, File NormalFile) {
        if (JSONConfig.IsImageFile) {
            InvertedFile = FileHandler.FileInOutputDir(JSONConfig,JSONConfig.InvertedFileOutput);
            NormalFile = FileHandler.FileInOutputDir(JSONConfig,JSONConfig.NormalFileOutput);
        }

        if (JSONConfig.BothBrightnesses) {
            String[][] InvertTranslatedImage = DH.ValueTranslation(ImageBrightness,true);
            FileHandler.WriteStringToFile(InvertedFile,ListToString(InvertTranslatedImage));

            String[][] NormalTranslatedImage = DH.ValueTranslation(ImageBrightness,false);
            FileHandler.WriteStringToFile(NormalFile,ListToString(NormalTranslatedImage));
        } else {
            String[][] TranslatedImage = DH.ValueTranslation(ImageBrightness,JSONConfig.InvertBrighntess);

            File fileForBrightness = JSONConfig.InvertBrighntess ? InvertedFile : NormalFile;

            FileHandler.WriteStringToFile(fileForBrightness,ListToString(TranslatedImage));
        }
    }

    public static void PrecachedDisplay() {
        System.out.println("If you are displaying a video, I recommend using EmEditor with auto-reload on");

        int framenumber = 0;
        File currentTextFrame = FileHandler.FileInOutputDir(JSONConfig, JSONConfig.PrecachedVideoDirectory+"/"+"frame_"+framenumber+".txt");
        File displayFile = FileHandler.FileInOutputDir(JSONConfig,JSONConfig.DisplayTxtFile);

        if (!FileHandler.CheckIfFileExists(FileHandler.FileInOutputDir(JSONConfig,JSONConfig.PrecachedVideoDirectory))) {
            System.err.println("Video Directory not found!");
            System.exit(0);
        }

        if (!FileHandler.CheckIfFileExists(displayFile)) {
            System.out.println("Warning: display text file doesnt exist!");

            boolean makeDisplay = QuestionHandler.AskBooleanQuestion("Would you like to create your set display text file? (in outputs directory)",new String[]{"yes","no"},"yes");
            if (makeDisplay) {
                FileHandler.WriteStringToFile(displayFile,"");
                System.out.println("Created file at: "+displayFile.getAbsolutePath());
            } else {
                System.out.println("Okay! ending");
                System.exit(0);
            }
        }

        System.out.println("Type anything when you would like to start the display, you get a 2 second delay to switch to the txt document");
        new Scanner(System.in).nextLine();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            System.out.println("Error occured while awaitng start time for display");
        }


        while (FileHandler.CheckIfFileExists(currentTextFrame)) {
            String currentFrameData = FileHandler.ReadStringFile(currentTextFrame);
            FileHandler.WriteStringToFile(displayFile,currentFrameData);

            try {
                long sleepMillis = (long) (1000.0 / JSONConfig.DisplaySpeedFPS);
                TimeUnit.MILLISECONDS.sleep(sleepMillis);
            } catch (InterruptedException e) {
                System.out.println("Error occured while awaitng fps for display, continuing");
            }

            framenumber++;
            currentTextFrame = FileHandler.FileInOutputDir(JSONConfig,JSONConfig.PrecachedVideoDirectory+"/"+"frame_"+framenumber+".txt");
        }

        System.out.println("Video ended!");
    }
}
