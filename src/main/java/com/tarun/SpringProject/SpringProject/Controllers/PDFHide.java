package com.tarun.SpringProject.SpringProject.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin(origins = {"http://16.171.134.100","http://16.171.134.100:3000","http://16.171.134.100:3001","http://16.171.134.100:3002","http://16.171.134.100:3003", "http://hidenreveal.duckdns.org", "http://hidenreveal.duckdns.org:3000", "http://hidenreveal.duckdns.org:3001", "http://hidenreveal.duckdns.org:3002", "http://hidenreveal.duckdns.org:3003"})
public class PDFHide {

    private static final Logger logger = LoggerFactory.getLogger(PDFHide.class);

    @PostMapping("/hidePdfInImage")
    public ResponseEntity<Map<String, String>> hidePdfInImage(@RequestBody Map<String, String> request) {
        try {
            String base64Image = request.get("image");
            String base64Pdf = request.get("pdf");

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            byte[] pdfBytes = Base64.getDecoder().decode(base64Pdf);

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            BufferedImage stegoImage = hidePdfInImage(image, pdfBytes);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(stegoImage, "png", baos);
            String stegoImageBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            Map<String, String> response = new HashMap<>();
            response.put("image", stegoImageBase64);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error hiding PDF in image", e);
            return handleException(e);
        }
    }

    @PostMapping("/extractPdfFromImage")
    public ResponseEntity<Map<String, String>> extractPdfFromImage(@RequestBody Map<String, String> request) {
        try {
            String base64Image = request.get("image");

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

            byte[] extractedPdfBytes = extractPdfFromImage(image);
            String extractedPdfBase64 = Base64.getEncoder().encodeToString(extractedPdfBytes);

            Map<String, String> response = new HashMap<>();
            response.put("pdf", extractedPdfBase64);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error extracting PDF from image", e);
            return handleException(e);
        }
    }

    private BufferedImage hidePdfInImage(BufferedImage image, byte[] pdfBytes) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();
        int pdfLength = pdfBytes.length;

        if (pdfLength > width * height * 3) {
            throw new IOException("PDF is too large to hide in the image");
        }

        BufferedImage stegoImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int byteIndex = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);

                if (byteIndex < pdfLength) {
                    int pdfByte = pdfBytes[byteIndex++] & 0xFF;
                    int red = (rgb >> 16 & 0xFC) | (pdfByte >> 6);
                    int green = (rgb >> 8 & 0xFC) | ((pdfByte >> 4) & 0x03);
                    int blue = (rgb & 0xFC) | ((pdfByte >> 2) & 0x03);
                    rgb = (red << 16) | (green << 8) | blue;
                }

                stegoImage.setRGB(x, y, rgb);
            }
        }

        return stegoImage;
    }

    private byte[] extractPdfFromImage(BufferedImage image) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);

                int red = (rgb >> 16 & 0x03) << 6;
                int green = (rgb >> 8 & 0x03) << 4;
                int blue = (rgb & 0x03) << 2;

                int pdfByte = red | green | blue;
                baos.write(pdfByte);
            }
        }

        return baos.toByteArray();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}