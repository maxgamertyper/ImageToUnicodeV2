package com.maxgamertyper1;

import java.io.File;
import java.util.Scanner;

public class OutputHandler {
    private String NormalFilePath;
    private String InvertFilePath;


    public OutputHandler(Boolean conversionType, Scanner scanner) {
        System.out.println("Conversion finished!");

        if (conversionType!=Boolean.TRUE) {
            System.out.println("Where would you like the normal text extracted to? (outputs/yourinput.txt, normal.txt is default) ex: normal.txt, ascii.txt");
            String userinput = scanner.nextLine();
            if (userinput.isEmpty()) {
                userinput = "normal.txt";
            }
            NormalFilePath = new File("data/outputs/"+userinput).getAbsolutePath();
        }
        if (conversionType!=Boolean.FALSE) {
            System.out.println("Where would you like the inverted text extracted to? (outputs/yourinput.txt, inverted.txt is default) ex: inverted.txt, invert.txt");
            String userinput = scanner.nextLine();
            if (userinput.isEmpty()) {
                userinput = "inverted.txt";
            }
            InvertFilePath = new File("data/outputs/"+userinput).getAbsolutePath();
        }
    }

    public String GetNormalFilePath() {
        return NormalFilePath;
    }

    public String GetInvertedFilePath() {
        return InvertFilePath;
    }
}
