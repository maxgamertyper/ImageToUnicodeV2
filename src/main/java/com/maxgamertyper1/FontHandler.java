package com.maxgamertyper1;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.util.*;

public class FontHandler {
    private Font font = null;
    private final ArrayList<String> changedCharacters = new ArrayList<String>();
    private final HashMap<String, Float> changedCharacterFillPercent = new HashMap<String, Float>();

    public FontHandler(File fontFile) {
        try {
             font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
             font = font.deriveFont(16f);
        } catch ( IOException | FontFormatException  e) {
            System.out.printf("Error occured when trying to read font file, %s\n",fontFile.getName());
            System.exit(0);
        }
    }

    public void SetChangedUnicodeCharacters() {
        for (int codePoint = 0; codePoint <= 0x10FFFF; codePoint++) {
            if (Character.isDefined(codePoint) && font.canDisplay(codePoint)) {
                changedCharacters.add(Character.toString(codePoint));
            }
        }
    }

    public HashMap<String,String> GetCharacterFills() {
        final HashMap<String,String> FillPercents = new HashMap<String,String>();
        final HashMap<String,Integer> SymbolWidths = new HashMap<String,Integer>();

        for (String character : changedCharacters) {
            int[] characterDimensions = GetCharacterDimensions(character);
            float fillpercent = GetCharacterFill(character,characterDimensions[0],characterDimensions[1]);
            if (fillpercent==2) {
                continue;
            }
            FillPercents.put(character,String.valueOf(fillpercent));
            SymbolWidths.put(character,characterDimensions[0]);
        }


        Map<Integer, Integer> frequency = new HashMap<>();

        for (Integer value : SymbolWidths.values()) {
            frequency.put(value, frequency.getOrDefault(value, 0) + 1);
        }

        Integer modeValue = Collections.max(frequency.entrySet(), Map.Entry.comparingByValue()).getKey();

        for (Map.Entry<String, Integer> entry : SymbolWidths.entrySet()) {
            String character = entry.getKey();
            int width = entry.getValue();

            if (width!=modeValue) {
                FillPercents.remove(character);
            }
        }

        return FillPercents;
    }

    public HashMap<String,String> CreateFinalHashmap(HashMap<String,String> SimplifiedPercents, int Step) {
        final HashMap<String,String> characterBrightnessMap = new HashMap<String,String>();

        for (int brightnessValue=0; brightnessValue<=256; brightnessValue+=Step) {
            String currentBestSymbol = null;
            float lowestDifference = 256;

            for (Map.Entry<String, String> entry : SimplifiedPercents.entrySet()) {
                String characterKey = entry.getKey();
                float fillValue = Float.parseFloat(entry.getValue());
                float difference = Math.abs(brightnessValue - fillValue * 255);

                if (difference<lowestDifference) {
                    lowestDifference = difference;
                    currentBestSymbol = characterKey;
                }

            }

            characterBrightnessMap.put(String.valueOf(brightnessValue),currentBestSymbol);
            System.out.printf("Best symbol for a brightness of %d is %s with a saturation difference of %f%n",brightnessValue,currentBestSymbol,lowestDifference);

        }

        characterBrightnessMap.put("stepData",String.valueOf(Step));


        return characterBrightnessMap;
    }

    public HashMap<String,String> SimplifyCharacterFills(HashMap<String,String> FillPercents) {
        final HashMap<String,String> singleCharacterFills = new HashMap<String,String>();
        final Set<String> usedFills = new HashSet<>();

        for (Map.Entry<String, String> entry : FillPercents.entrySet()) {
            String characterKey = entry.getKey();
            String fillValue = entry.getValue();

            if (usedFills.contains(fillValue)) {
                continue;
            }

            usedFills.add(fillValue);
            singleCharacterFills.put(characterKey, fillValue);
        }

        return singleCharacterFills;
    }

    public float GetCharacterFill(String character, int fontWidth, int fontHeight) {
        if (fontWidth<=0) {
            System.out.printf("Warning, character %s has a width of 0, skipping%n",character);
            return 2f;
        }

        int emptyCount = getEmptyCount(character, fontWidth, fontHeight);

        float emptyPercent = (float) emptyCount /(fontHeight*fontWidth);
        return 1-emptyPercent;
    }

    private int getEmptyCount(String character, int fontWidth, int fontHeight) {
        BufferedImage img = new BufferedImage(fontWidth, fontHeight,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, fontWidth, fontHeight);
        g2d.setColor(Color.WHITE);
        g2d.drawString(character, 0, font.getSize());

        int emptyCount = 0;
        for (int pixelY = 0; pixelY< fontHeight; pixelY++) {
            for (int pixelX = 0; pixelX< fontWidth; pixelX++) {
                if (img.getRGB(pixelX, pixelY)==Color.red.getRGB()) {
                    emptyCount++;
                }
            }
        }
        return emptyCount;
    }

    private int[] GetCharacterDimensions(String Character) {
        BufferedImage dataimg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics g = dataimg.getGraphics();
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics();

        Rectangle bounds = metrics.getStringBounds(Character, g).getBounds();
        int fontHeight = bounds.height;
        int fontWidth = bounds.width;

        return new int[] {fontWidth,fontHeight};
    }


}
