package com.tarun.SpringProject.SpringProject.Controllers;

import com.tarun.SpringProject.SpringProject.Services.TextProcessing;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.*;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin(origins = {"http://localhost", "https://hidenrevealapp.netlify.app"})
public class TextHide {

    private static final Logger logger = LoggerFactory.getLogger(TextHide.class);

    @Autowired
    TextProcessing textProcessing;

    @PostMapping("/hideTextInImage")
    public Map<String, String> hideTextInImage(@RequestBody Map<String, String> request) throws IOException {
        try {
            String base64Image = request.get("image");
            String text = request.get("text");

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

            BufferedImage stegoImage = textProcessing.hideText(image, text);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(stegoImage, "png", baos);
            String stegoImageBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            Map<String, String> response = new HashMap<>();
            response.put("image", stegoImageBase64);
            return response;
        } catch (Exception e) {
            logger.error("Error hiding text in image", e);
            throw e;
        }
    }

    @PostMapping("/extractTextFromImage")
    public Map<String, String> extractTextFromImage(@RequestBody Map<String, String> request) throws IOException {
        try {
            String base64Image = request.get("image");

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

            String extractedText = textProcessing.extractText(image);

            Map<String, String> response = new HashMap<>();
            response.put("text", extractedText);
            return response;
        } catch (Exception e) {
            logger.error("Error extracting text from image", e);
            throw e;
        }
    }

    @PostMapping("/hideTextInPdf")
    public Map<String, String> hideTextInPdf(@RequestBody Map<String, String> request) throws IOException {
        try {
            String base64Pdf = request.get("pdf");
            String text = request.get("text");
            System.out.println(text);

            byte[] pdfBytes = Base64.getDecoder().decode(base64Pdf);
            PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes));

            textProcessing.hideTextInPdf(document, text);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            document.close();
            String stegoPdfBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());

            Map<String, String> response = new HashMap<>();
            response.put("pdf", stegoPdfBase64);
            System.out.println("Stego PDF: " + stegoPdfBase64);
            return response;
        } catch (Exception e) {
            logger.error("Error hiding text in PDF", e);
            throw e;
        }
    }

    @PostMapping("/extractTextFromPdf")
    public Map<String, String> extractTextFromPdf(@RequestBody Map<String, String> request) throws IOException {
        try {
            String base64Pdf = request.get("pdf");

            byte[] pdfBytes = Base64.getDecoder().decode(base64Pdf);
            PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes));

            String extractedText = textProcessing.extractTextFromPdf(document);
            document.close();

            Map<String, String> response = new HashMap<>();
            response.put("text", extractedText);
            System.out.println("Extracted text: " + extractedText);
            return response;
        } catch (Exception e) {
            logger.error("Error extracting text from PDF", e);
            throw e;
        }
    }

    @PostMapping("/hideTextInAudio")
    public ResponseEntity<Map<String, String>> hideTextInAudio(@RequestBody Map<String, String> request) throws UnsupportedAudioFileException, IOException {
        try {
            String base64Audio = request.get("audio");
            String text = request.get("text");

            byte[] audioBytes = Base64.getDecoder().decode(base64Audio);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioBytes));

            byte[] stegoAudioBytes = textProcessing.hideTextInAudio(audioInputStream, text);

            String stegoAudioBase64 = Base64.getEncoder().encodeToString(stegoAudioBytes);

            Map<String, String> response = new HashMap<>();
            response.put("audio", stegoAudioBase64);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error extracting text from PDF", e);
            throw e;
        }
    }

    @PostMapping("/extractTextFromAudio")
    public ResponseEntity<Map<String, String>> extractTextFromAudio(@RequestBody Map<String, String> request) throws UnsupportedAudioFileException, IOException {
        try {
            String base64Audio = request.get("audio");

            byte[] audioBytes = Base64.getDecoder().decode(base64Audio);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioBytes));

            String extractedText = textProcessing.extractTextFromAudio(audioInputStream);

            Map<String, String> response = new HashMap<>();
            response.put("text", extractedText);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error extracting text from PDF", e);
            throw e;
        }
    }

    @PostMapping("/hideTextInVideo")
    public ResponseEntity<Map<String, String>> hideTextInVideo(@RequestBody Map<String, String> request) {
        try {
            String base64Video = request.get("video");
            String textToHide = request.get("text");

            byte[] videoBytes = Base64.getDecoder().decode(base64Video);
            InputStream videoStream = new ByteArrayInputStream(videoBytes);

            FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoStream);
            frameGrabber.start();

            File outputVideoFile = new File("output_with_hidden_text.mp4");
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputVideoFile, frameGrabber.getImageWidth(), frameGrabber.getImageHeight());
            recorder.setVideoCodec(frameGrabber.getVideoCodec());
            recorder.setFormat(frameGrabber.getFormat());
            recorder.setFrameRate(frameGrabber.getFrameRate());
            recorder.start();

            Frame frame;
            int frameNumber = 0;
            Java2DFrameConverter converter = new Java2DFrameConverter();
            while ((frame = frameGrabber.grabFrame()) != null) {
                if (frameNumber % 100 == 0) { // Embed text every 100 frames
                    BufferedImage image = converter.convert(frame);
                    textProcessing.embedTextInImage(image, textToHide);
                    frame = converter.convert(image);
                }
                recorder.record(frame);
                frameNumber++;
            }

            recorder.stop();
            frameGrabber.stop();

            byte[] modifiedVideoBytes = textProcessing.getBytesFromFile(outputVideoFile);
            String base64ModifiedVideo = Base64.getEncoder().encodeToString(modifiedVideoBytes);

            Map<String, String> response = new HashMap<>();
            response.put("video", base64ModifiedVideo);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return (ResponseEntity<Map<String, String>>) handleException(e);
        }
    }

    @PostMapping("/extractTextFromVideo")
    public ResponseEntity<Map<String, String>> extractTextFromVideo(@RequestBody Map<String, String> request) {
        try {
            String base64Video = request.get("video");

            byte[] videoBytes = Base64.getDecoder().decode(base64Video);
            InputStream videoStream = new ByteArrayInputStream(videoBytes);

            FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoStream);
            frameGrabber.start();

            Frame frame;
            StringBuilder extractedText = new StringBuilder();
            int frameNumber = 0;
            Java2DFrameConverter converter = new Java2DFrameConverter();
            while ((frame = frameGrabber.grabFrame()) != null) {
                if (frameNumber % 100 == 0) { // Extract text every 100 frames
                    BufferedImage image = converter.convert(frame);
                    extractedText.append(extractTextFromImage((Map<String, String>) image));
                }
                frameNumber++;
            }

            frameGrabber.stop();

            Map<String, String> response = new HashMap<>();
            response.put("text", extractedText.toString());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return (ResponseEntity<Map<String, String>>) handleException(e);
        }
    }


    @ExceptionHandler(Exception.class)
    public Map<String, String> handleException(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return response;
    }
}