package com.maxgamertyper1;

import java.util.Arrays;
import java.util.Scanner;

public class QuestionHandler {
    private static final Scanner scanner = new Scanner(System.in);

    public static boolean AskBooleanQuestion(String Question, String[] options, String defaultOption) {
        System.out.println(Question+" ["+options[0]+","+options[1]+"] "+"(default:"+defaultOption+")");

        int Default = Arrays.asList(options).indexOf(defaultOption);
        int notDefaultIndex = 1 - Default;

        return !scanner.nextLine().trim().equalsIgnoreCase(options[notDefaultIndex]);
    }

    public static String AskStringQuestion(String Question, String defaultOption) {
        System.out.println(Question + " (default:"+defaultOption+")");
        String userInput = scanner.nextLine();
        if (userInput.isEmpty()) {
            userInput = defaultOption;
        }
        return userInput;
    }

    public static int AskIntegerQuestion(String Question, int defaultInteger) {
        System.out.println(Question + " (default:"+defaultInteger+")");

        int userSetInteger = defaultInteger;

        String line = scanner.nextLine();
        if (!line.isEmpty()) {
            try {
                userSetInteger = Integer.parseInt(line);
            } catch (NumberFormatException ignored) {
                // keep default
            }
        }

        return userSetInteger;
    }
}
