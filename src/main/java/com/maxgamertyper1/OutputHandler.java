package com.maxgamertyper1;

import java.io.File;
import java.util.Scanner;

public class OutputHandler {
    private String NormalFilePath;
    private String InvertFilePath;
    private String baseNormalInput;
    private String baseInvertInput;


    public OutputHandler(Boolean conversionType, Scanner scanner) {
        System.out.println("Conversion finished!");

        if (conversionType!=Boolean.TRUE) {
            System.out.println("Where would you like the normal text(s) extracted to? (outputs/yourinput.txt, normal.txt is default) ex: normal.txt, ascii.txt");
            baseNormalInput = scanner.nextLine();
            if (baseNormalInput.isEmpty()) {
                baseNormalInput = "normal.txt";
            }
            NormalFilePath = new File("data/outputs/"+baseNormalInput).getAbsolutePath();
        }
        if (conversionType!=Boolean.FALSE) {
            System.out.println("Where would you like the inverted text(s) extracted to? (outputs/yourinput.txt, inverted.txt is default) ex: inverted.txt, invert.txt");
            baseInvertInput = scanner.nextLine();
            if (baseInvertInput.isEmpty()) {
                baseInvertInput = "inverted.txt";
            }
            InvertFilePath = new File("data/outputs/"+baseInvertInput).getAbsolutePath();
        }
    }

    public String GetNormalFilePath() {
        return NormalFilePath;
    }

    public String GetInvertedFilePath() {
        return InvertFilePath;
    }

    public String GetBaseInvertedFilePath() {
        return baseInvertInput;
    }

    public String GetBaseNormalFilePath() {
        return baseNormalInput;
    }
}
