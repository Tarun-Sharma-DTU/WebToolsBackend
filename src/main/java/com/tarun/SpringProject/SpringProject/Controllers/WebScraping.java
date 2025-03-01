package com.tarun.SpringProject.SpringProject.Controllers;

import com.tarun.SpringProject.SpringProject.Services.Scrapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"https://iamscrappy.netlify.app/"})
public class WebScraping {

    @Autowired
    Scrapping scrapping;

    @PostMapping("/scrapeWebsite")
    public ResponseEntity<Map<String, Object>> scrapeWebsite(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        Map<String, Object> response = new HashMap<>();
        Set<String> emails = new HashSet<>();
        Set<String> phones = new HashSet<>();
        Set<String> addresses = new HashSet<>();
        HashSet<String> set = new HashSet<>();

        try {
            scrapping.scrapePage(url, emails, phones, set);
            response.put("emails", emails);
            response.put("phones", phones);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/scanAnchors")
    public ResponseEntity<Map<String, Object>> scanAnchors(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        Map<String, Object> response = new HashMap<>();
        List<TreeMap<String, String>> anchors = new ArrayList<>();

        try {
            scrapping.scanAnchorLinks(url, anchors);
            response.put("anchors", anchors);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/extractImages")
    public ResponseEntity<Map<String, Object>> extractImages(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        Map<String, Object> response = new HashMap<>();
        Set<String> imageUrls = new HashSet<>();

        try {
            scrapping.extractImageUrls(url, imageUrls);
            response.put("imageUrls", imageUrls);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/extractHeaders")
    public ResponseEntity<Map<String, Object>> extractHeaders(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        Map<String, Object> response = new HashMap<>();
        Map<String, List<String>> headers = new HashMap<>();

        try {
            scrapping.extractHeaderTags(url, headers);
            response.put("headers", headers);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/extractDuplicates")
    public ResponseEntity<Map<String, Object>> extractDuplicates(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        Map<String, Object> response = new HashMap<>();
        Set<String> duplicateTexts = new HashSet<>();

        try {
            scrapping.extractDuplicateText(url, duplicateTexts);
            response.put("duplicateTexts", duplicateTexts);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/extractDuplicateSentences")
    public ResponseEntity<Map<String, Object>> extractDuplicateSentences(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        Map<String, Object> response = new HashMap<>();
        Set<String> duplicateSentences = new HashSet<>();

        try {
            scrapping.extractDuplicateSentencesFromPage(url, duplicateSentences);
            response.put("duplicateSentences", duplicateSentences);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/extractKeywordStuffing")
    public ResponseEntity<Map<String, Object>> extractKeywordStuffing(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        String keyword = request.get("keyword");
        Map<String, Object> response = new HashMap<>();
        double[] keywordCount = {};

        try {
            keywordCount = scrapping.extractKeywordCount(url, keyword);
            response.put("keyword", keyword);
            response.put("count", keywordCount[0]);
            double density = (keywordCount[0]*100)/keywordCount[1];
            String str = String.valueOf(density);
            System.out.println(density);
            response.put("density", str.substring(0, 5));
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


    // WebScraping.java
    @PostMapping("/extractSocialMediaLinks")
    public ResponseEntity<Map<String, Object>> extractSocialMediaLinks(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        Map<String, Object> response = new HashMap<>();
        Set<String> socialMediaLinks = new HashSet<>();
        Set<String> visitedUrls = new HashSet<>();

        try {
            scrapping.extractSocialMediaLinks(url, socialMediaLinks, visitedUrls);
            response.put("socialMediaLinks", socialMediaLinks);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/analyzePageLoadTime")
    public ResponseEntity<Map<String, Object>> analyzePageLoadTime(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        Map<String, Object> response = new HashMap<>();

        try {
            long loadTime = scrapping.analyzePageLoadTime(url);
            response.put("loadTime", loadTime);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


}