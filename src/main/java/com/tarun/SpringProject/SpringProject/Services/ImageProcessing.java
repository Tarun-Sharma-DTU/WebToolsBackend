package com.tarun.SpringProject.SpringProject.Services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.awt.image.BufferedImage;


import java.awt.Color;
import java.awt.Graphics2D;



@Component
public class ImageProcessing {

    public void hideImageInPdf(PDDocument document, BufferedImage image) throws IOException {
        ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();
        ImageIO.write(image, "png", imageBytes);
        String encodedImage = Base64.getEncoder().encodeToString(imageBytes.toByteArray());

        PDMetadata metadata = new PDMetadata(document);
        try (OutputStream os = metadata.createOutputStream()) {
            os.write(encodedImage.getBytes());
        }

        document.getDocumentCatalog().setMetadata(metadata);
    }

    public BufferedImage extractImageFromPdf(PDDocument document) throws IOException {
        PDMetadata metadata = document.getDocumentCatalog().getMetadata();
        if (metadata != null) {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(metadata.toByteArray())) {
                byte[] encodedBytes = bais.readAllBytes();
                String encodedImage = new String(encodedBytes);

                if (encodedImage != null && !encodedImage.isEmpty()) {
                    byte[] decodedBytes = Base64.getDecoder().decode(encodedImage);
                    return ImageIO.read(new ByteArrayInputStream(decodedBytes));
                }
            }
        }
        throw new IOException("No hidden image found in PDF metadata");
    }

    public String encodeImage(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public BufferedImage hideImageInImage(BufferedImage coverImage, BufferedImage hiddenImage) {
        int width = coverImage.getWidth();
        int height = coverImage.getHeight();

        BufferedImage stegoImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int coverPixel = coverImage.getRGB(x, y);
                int hiddenPixel = hiddenImage.getRGB(x % hiddenImage.getWidth(), y % hiddenImage.getHeight());

                int stegoPixel = embedPixel(coverPixel, hiddenPixel);
                stegoImage.setRGB(x, y, stegoPixel);
            }
        }

        return stegoImage;
    }

    public BufferedImage extractImageFromImage(BufferedImage stegoImage) {
        int width = stegoImage.getWidth();
        int height = stegoImage.getHeight();

        BufferedImage extractedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int stegoPixel = stegoImage.getRGB(x, y);
                int extractedPixel = extractPixel(stegoPixel);

                extractedImage.setRGB(x, y, extractedPixel);
            }
        }

        return extractedImage;
    }

    public int embedPixel(int coverPixel, int hiddenPixel) {
        int redCover = (coverPixel >> 16) & 0xFF;
        int greenCover = (coverPixel >> 8) & 0xFF;
        int blueCover = coverPixel & 0xFF;

        int redHidden = (hiddenPixel >> 16) & 0xFF;
        int greenHidden = (hiddenPixel >> 8) & 0xFF;
        int blueHidden = hiddenPixel & 0xFF;

        int redStego = (redCover & 0xF0) | ((redHidden >> 4) & 0x0F);
        int greenStego = (greenCover & 0xF0) | ((greenHidden >> 4) & 0x0F);
        int blueStego = (blueCover & 0xF0) | ((blueHidden >> 4) & 0x0F);

        return (coverPixel & 0xFF000000) | (redStego << 16) | (greenStego << 8) | blueStego;
    }

    public int extractPixel(int stegoPixel) {
        int redStego = (stegoPixel >> 16) & 0xFF;
        int greenStego = (stegoPixel >> 8) & 0xFF;
        int blueStego = stegoPixel & 0xFF;

        int redHidden = (redStego & 0x0F) << 4;
        int greenHidden = (greenStego & 0x0F) << 4;
        int blueHidden = (blueStego & 0x0F) << 4;

        return (0xFF << 24) | (redHidden << 16) | (greenHidden << 8) | blueHidden;
    }

    public BufferedImage convertToBinaryImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = binaryImage.createGraphics();
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 9)); // Adjust font size for visibility

        for (int x = 0; x < width; x += 6) { // Adjust step size to match font size
            for (int y = 0; y < height; y += 7) { // Adjust step size to match font size
                int pixel = image.getRGB(x, y);
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;
                int avg = (red + green + blue) / 3;
                if (avg > 127) {
                    g2d.setColor(Color.GREEN);
                    g2d.drawString("0", x, y);
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("1", x, y);
                }
            }
        }

        g2d.dispose();
        return binaryImage;
    }

    public BufferedImage blackandwhite(BufferedImage originalImage) {
        // Implement cartoonization logic here
        // This can include converting the image to grayscale, detecting edges,
        // reducing colors to create a "cartoon" effect, etc.

        BufferedImage blackAndWhite = new BufferedImage(
                originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Example: Simple grayscale conversion (just for demonstration)
        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                int pixel = originalImage.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                int gray = (red + green + blue) / 3;
                int newPixel = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
                blackAndWhite.setRGB(x, y, newPixel);
            }
        }

        return blackAndWhite;
    }

}