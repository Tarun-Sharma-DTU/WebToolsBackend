package com.tarun.SpringProject.SpringProject.Controllers;

import com.tarun.SpringProject.SpringProject.Services.ImageProcessing;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin(origins = {"http://localhost", "https://hidenrevealapp.netlify.app"})
public class ImageHide {

    private static final Logger logger = LoggerFactory.getLogger(ImageHide.class);

    @Autowired
    ImageProcessing imageProcessing;

    @PostMapping("/hideImageInPdf")
    public ResponseEntity<Map<String, String>> hideImageInPdf(@RequestBody Map<String, String> request) {
        try {
            String base64Pdf = request.get("pdf");
            String base64Image = request.get("image");

            byte[] pdfBytes = Base64.getDecoder().decode(base64Pdf);
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes));
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

            imageProcessing.hideImageInPdf(document, image);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            document.close();
            String stegoPdfBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            Map<String, String> response = new HashMap<>();
            response.put("pdf", stegoPdfBase64);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error hiding image in PDF", e);
            return handleException(e);
        }
    }

    @PostMapping("/extractImageFromPdf")
    public ResponseEntity<Map<String, String>> extractImageFromPdf(@RequestBody Map<String, String> request) {
        try {
            String base64Pdf = request.get("pdf");

            byte[] pdfBytes = Base64.getDecoder().decode(base64Pdf);
            PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes));

            BufferedImage extractedImage = imageProcessing.extractImageFromPdf(document);
            document.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(extractedImage, "png", baos);
            String extractedImageBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            Map<String, String> response = new HashMap<>();
            response.put("image", extractedImageBase64);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error extracting image from PDF", e);
            return handleException(e);
        }
    }

    @PostMapping("/hideImageInImage")
    public ResponseEntity<Map<String, String>> hideImageInImage(@RequestBody Map<String, String> request) {
        try {
            String base64CoverImage = request.get("coverImage");
            String base64HiddenImage = request.get("hiddenImage");

            byte[] coverImageBytes = Base64.getDecoder().decode(base64CoverImage);
            byte[] hiddenImageBytes = Base64.getDecoder().decode(base64HiddenImage);

            BufferedImage coverImage = ImageIO.read(new ByteArrayInputStream(coverImageBytes));
            BufferedImage hiddenImage = ImageIO.read(new ByteArrayInputStream(hiddenImageBytes));

            BufferedImage stegoImage = imageProcessing.hideImageInImage(coverImage, hiddenImage);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(stegoImage, "png", baos);
            String stegoImageBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            Map<String, String> response = new HashMap<>();
            response.put("stegoImage", stegoImageBase64);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/extractImageFromImage")
    public ResponseEntity<Map<String, String>> extractImageFromImage(@RequestBody Map<String, String> request) {
        try {
            String base64StegoImage = request.get("stegoImage");

            byte[] stegoImageBytes = Base64.getDecoder().decode(base64StegoImage);
            BufferedImage stegoImage = ImageIO.read(new ByteArrayInputStream(stegoImageBytes));

            BufferedImage extractedImage = imageProcessing.extractImageFromImage(stegoImage);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(extractedImage, "png", baos);
            String extractedImageBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            Map<String, String> response = new HashMap<>();
            response.put("extractedImage", extractedImageBase64);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/processImage")
    public ResponseEntity<Map<String, String>> processImage(@RequestBody Map<String, String> request) {
        try {
            String base64Image = request.get("image");
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

            BufferedImage processedImage = imageProcessing.convertToBinaryImage(image);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(processedImage, "png", baos);
            String processedImageBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            Map<String, String> response = new HashMap<>();
            response.put("image", processedImageBase64);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }


    @PostMapping("/blackAndWhite")
    public ResponseEntity<Map<String, String>> blackandwhite(@RequestBody Map<String, String> request) {
        try {
            String base64Image = request.get("image");
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

            BufferedImage cartoonizedImage = imageProcessing.blackandwhite(image);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(cartoonizedImage, "png", baos);
            String cartoonizedImageBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            Map<String, String> response = new HashMap<>();
            response.put("image", cartoonizedImageBase64);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}