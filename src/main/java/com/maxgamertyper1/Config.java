package com.maxgamertyper1;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Config {
    public String EXAMPLEKEY = null;
    public static final HashMap<String, String> ConfigDescriptions = new HashMap<>() {{
        put("EXAMPLEKEY","this key is an example of how this config works, a null value means that the program will ask for your input on the option. You can delete this if you understand the structure");
        put("DataDirectory", "Where the working area of the code will be");
        put("OutputDirectory", "Where the outputs of the code will go inside of the specified Data directory");
        put("InputDirectory", "Where the inputs of the code will go inside of the specified Data directory");
        put("CreatingDatatable", "Determines if you are creating the datatable from a font file or using an already made one");
        put("DatatableFile", "The file that specifies the conversion calculation *should be a json file*, normally named: (fontname)datatable.json *overrules font file conversion if set*");
        put("FontFile", "The font file that will be converted to a json file for image translation");
        put("DatatableStep", "The step size that will be calculated for the datatable file if being calculated. Lower results in more accurate but longer time");
        put("IsImageFile", "Whether the program will be converting an image file or a video file");
        put("ImageOrVideoFile", "The image or video file that will get converted into text");
        put("BothBrightnesses", "If the code will calculate both brightnesses *inverted and normal, overrides InvertBrightness*");
        put("InvertBrighntess", "If the code will invert the brightness of the image");
        put("InvertedFileOutput", "The file destination of an inverted file (in the outputs directory)");
        put("NormalFileOutput", "The file destination of a normal file (in the outputs directory)");
        put("IsDisplayingPrecached", "Whether you are loading a video into a file to watch, or generating data");
        put("DisplayTxtFile", "The text file where precached videos are displayed");
        put("DisplaySpeedFPS", "The speed at which precached videos are played");
    }};
    private static final HashMap<String, Question> VariableQuestions = new HashMap<>() {{
        put("DataDirectory", new Question("Where is the data directory?","data"));
        put("OutputDirectory", new Question("Where is the output directory? (inside your data directory)","outputs"));
        put("InputDirectory", new Question("Where is the input directory? (inside your data directory)","inputs"));
        put("CreatingDatatable", new Question("Are you creating a datatable?","yes",new String[]{"yes","no"}));
        put("DatatableFile", new Question("What is your datatable file? (inside your inputs directory)","datatable.json"));
        put("FontFile", new Question("What is your font file? (inside your inputs directory)","font.ttf"));
        put("DatatableStep", new Question("What step size would you like for conversion?",1));
        put("IsImageFile", new Question("Are you converting an image or a video?","image",new String[]{"image","video"}));
        put("ImageOrVideoFile", new Question("What is your image or video file? (inside your inputs directory)","image.png"));
        put("BothBrightnesses", new Question("Will you be converting both brightnesses?","yes",new String[]{"yes","no"}));
        put("InvertBrighntess", new Question("Will you be inverting the brightness or not?","normal",new String[]{"normal","invert"}));
        put("InvertedFileOutput", new Question("What should the name of the inverted file be? (inside the output directory)","inverted.txt"));
        put("NormalFileOutput", new Question("What should the name of the normal file be? (inside the output directory)","normal.txt"));
        put("IsDisplayingPrecached", new Question("Are you trying to display an already converted video file?","no",new String[]{"yes","no"}));
        put("PrecachedVideoDirectory", new Question("What is the directory where your video is cached? (should be inside outputs)","video"));
        put("DisplayTxtFile", new Question("What is the text file that you are going to watch the display in? (should be inside outputs)","display.txt"));
        put("DisplaySpeedFPS", new Question("How fast do you want to display the video?(FPS)",60));
    }};
    public String DataDirectory = "data";
    public String OutputDirectory = "outputs";
    public String InputDirectory = "inputs";
    public Boolean IsDisplayingPrecached = null;
    public Boolean CreatingDatatable = null;
    public String DatatableFile = null;
    public String FontFile = null;
    public Integer DatatableStep = null;
    public Boolean IsImageFile = null;
    public String ImageOrVideoFile = null;
    public Boolean BothBrightnesses = null;
    public Boolean InvertBrighntess = null;
    public String InvertedFileOutput = null;
    public String NormalFileOutput = null;
    public String PrecachedVideoDirectory = null;
    public String DisplayTxtFile = null;
    public Integer DisplaySpeedFPS = null;


    public record Question(String question, String defaultOption, String[] options) {

        // Boolean Question Constructor
        public Question(String question, String defaultOption, String[] options) {
            this.question = question;
            this.defaultOption = defaultOption;
            this.options = options;
        }

        // String Question Constructor
        public Question(String question, String defaultOption) {
            this(question, defaultOption, null);
        }

        // Integer Question Constructor
        public Question(String question, int defaultOption) {
            this(question, String.valueOf(defaultOption), null);
        }
    }



    public void FillInNullValues() {
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            final String name = field.getName();

            if (name.equals("EXAMPLEKEY") || name.equals("VariableQuestions") || name.equals("ConfigDescriptions")) continue;

            Object value = null;
            System.out.println(name);

            try {
                value = field.get(this);
            } catch (IllegalAccessException e) {
                System.err.println("Illegal Access Exception when checking for null values in Config, for field: "+name);
            }

            if (value!=null || !this.QuestionRestrictions(name)) continue;

            Question currentQuestion = VariableQuestions.get(name);

            try {
                switch (field.getType().getSimpleName()) {
                    case "String" ->
                            field.set(this, QuestionHandler.AskStringQuestion(currentQuestion.question, currentQuestion.defaultOption));
                    case "Integer" ->
                            field.set(this, QuestionHandler.AskIntegerQuestion(currentQuestion.question, Integer.parseInt(currentQuestion.defaultOption)));
                    case "Boolean" ->
                            field.set(this, QuestionHandler.AskBooleanQuestion(currentQuestion.question, currentQuestion.options, currentQuestion.defaultOption));
                }
            } catch (IllegalAccessException e) {
                System.err.println("Illegal Access Exception when setting for null values in Config, for field: "+name);
                System.exit(0);
            }
        }
    }

    public boolean QuestionRestrictions(String name) {
        if (name.equals("DataDirectory") || name.equals("InputDirectory") || name.equals("OutputDirectory") || name.equals("IsDisplayingPrecached")) {return true;}

        if (!IsDisplayingPrecached) {

            return name.equals("PrecachedVideoDirectory") || name.equals("DisplayTxtFile") || name.equals("DisplaySpeedFPS");

        } else {
            switch (name) {
                case ("DatatableFile") -> {
                    return !this.CreatingDatatable;
                }
                case ("FontFile"), ("DatatableStep") -> {
                    return this.CreatingDatatable;
                }
                case ("InvertBrighntess") -> {
                    return !this.BothBrightnesses;
                }
                case ("InvertedFileOutput") -> {
                    return this.BothBrightnesses || this.InvertBrighntess;
                }
                case ("NormalFileOutput") -> {
                    return this.BothBrightnesses || !this.InvertBrighntess;
                }
                case ("PrecachedVideoDirectory"), ("DisplayTxtFile"), ("DisplaySpeedFPS") -> {
                    return false;
                }
                default -> {
                    return true;
                }
            }
        }

    }
}
