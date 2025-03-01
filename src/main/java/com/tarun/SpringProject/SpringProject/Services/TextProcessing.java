package com.tarun.SpringProject.SpringProject.Services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.springframework.stereotype.Component;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.image.BufferedImage;
import java.io.*;

@Component
public class TextProcessing {

    public BufferedImage hideText(BufferedImage image, String text) {

        StringBuilder binaryText = new StringBuilder();
        for (char c : text.toCharArray()) {
            binaryText.append(String.format("%8s", Integer.toBinaryString(c)).replaceAll(" ", "0"));
        }
        binaryText.append("00000000");

        int textIndex = 0;
        int textLength = binaryText.length();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (textIndex < textLength) {
                    int pixel = image.getRGB(x, y);

                    int blue = pixel & 0xFF;
                    blue = (blue & 0xFE) | (binaryText.charAt(textIndex) - '0');
                    textIndex++;

                    pixel = (pixel & 0xFFFFFF00) | blue;
                    image.setRGB(x, y, pixel);
                } else {
                    return image;
                }
            }
        }
        return image;
    }

    public String extractText(BufferedImage image) {
        StringBuilder binaryText = new StringBuilder();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);

                int blue = pixel & 0xFF;
                binaryText.append(blue & 1);
            }
        }

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binaryText.length(); i += 8) {
            String byteString = binaryText.substring(i, i + 8);
            if (byteString.equals("00000000")) {
                break;
            }
            int charCode = Integer.parseInt(byteString, 2);
            text.append((char) charCode);
        }

        return text.toString();
    }

    public void hideTextInPdf(PDDocument document, String text) throws IOException {
        PDDocumentOutline outline = new PDDocumentOutline();
        document.getDocumentCatalog().setDocumentOutline(outline);
        PDOutlineItem hiddenTextItem = new PDOutlineItem();
        hiddenTextItem.setTitle(text);
        outline.addLast(hiddenTextItem);
    }

    public String extractTextFromPdf(PDDocument document) {
        PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();
        if (outline != null) {
            PDOutlineItem item = outline.getFirstChild();
            if (item != null) {
                return item.getTitle();
            }
        }
        return "";
    }


    public byte[] hideTextInAudio(AudioInputStream audioInputStream, String text) throws IOException {
        byte[] audioBytes = audioInputStream.readAllBytes();
        StringBuilder binaryText = new StringBuilder();
        for (char c : text.toCharArray()) {
            binaryText.append(String.format("%8s", Integer.toBinaryString(c)).replaceAll(" ", "0"));
        }
        binaryText.append("00000000");

        int textIndex = 0;
        int textLength = binaryText.length();

        for (int i = 0; i < audioBytes.length && textIndex < textLength; i++) {
            audioBytes[i] = (byte) ((audioBytes[i] & 0xFE) | (binaryText.charAt(textIndex) - '0'));
            textIndex++;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(audioBytes), audioInputStream.getFormat(), audioBytes.length), AudioFileFormat.Type.WAVE, baos);
        return baos.toByteArray();
    }

    public String extractTextFromAudio(AudioInputStream audioInputStream) throws IOException {
        byte[] audioBytes = audioInputStream.readAllBytes();
        StringBuilder binaryText = new StringBuilder();

        for (byte audioByte : audioBytes) {
            binaryText.append(audioByte & 1);
        }

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binaryText.length(); i += 8) {
            String byteString = binaryText.substring(i, i + 8);
            if (byteString.equals("00000000")) {
                break; // Stop at null character
            }
            int charCode = Integer.parseInt(byteString, 2);
            text.append((char) charCode);
        }

        return text.toString();
    }


    public void embedTextInImage(BufferedImage image, String text) {
        byte[] textBytes = text.getBytes();
        int textIndex = 0;

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (textIndex < textBytes.length) {
                    int rgb = image.getRGB(x, y);
                    int lsb = (rgb & 0xFFFFFFFE) | (textBytes[textIndex] & 1);
                    image.setRGB(x, y, lsb);
                    textIndex++;
                } else {
                    return;
                }
            }
        }
    }

    public String extractTextFromImage(BufferedImage image) {
        StringBuilder extractedText = new StringBuilder();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                int lsb = rgb & 1;
                extractedText.append((char) lsb);
            }
        }
        return extractedText.toString();
    }

    public byte[] getBytesFromFile(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
}
