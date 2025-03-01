package com.tarun.SpringProject.SpringProject.Services;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Scrapping {

    public void scrapePage(String url, Set<String> emails, Set<String> phones, HashSet<String> set) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String text = doc.text();

        // Extract emails
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
        Matcher emailMatcher = emailPattern.matcher(text);
        while (emailMatcher.find()) {
            emails.add(emailMatcher.group());
        }

        // Extract phone numbers
        Pattern phonePattern = Pattern.compile("\\+?\\d[\\d\\s.-]{8,}\\d|\\(\\d{3}\\)\\s*\\d{3}-\\d{4}|\\d{3}-\\d{3}-\\d{4}");
        Matcher phoneMatcher = phonePattern.matcher(text);
        while (phoneMatcher.find()) {
            phones.add(phoneMatcher.group());
        }

        // Scan for text starting with "Address" or "Phone"
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            if (line.toLowerCase().startsWith("phone")) {
                phones.add(line.substring(6).trim());
            }
        }

        // Follow links to other pages on the same domain
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            if (set.contains(link.absUrl("href"))) {
                continue;
            }
            set.add(link.absUrl("href"));
            String absUrl = link.absUrl("href");
            if (absUrl.contains(url)) {
                scrapePage(absUrl, emails, phones, set);
            }
        }
    }

    public void scanAnchorLinks(String url, List<TreeMap<String, String>> anchors) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            Map<String, String> anchorData = new HashMap<>();
            anchorData.put("text", link.text());
            anchorData.put("url", link.absUrl("href"));
            anchors.add((TreeMap<String, String>) anchorData);
        }
    }

    public void extractImageUrls(String url, Set<String> imageUrls) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements images = doc.select("img[src]");
        for (Element img : images) {
            imageUrls.add(img.absUrl("src"));
        }
    }

    public void extractHeaderTags(String url, Map<String, List<String>> headers) throws IOException {
        Document doc = Jsoup.connect(url).get();
        for (int i = 1; i <= 6; i++) {
            Elements headerElements = doc.select("h" + i);
            List<String> headerTexts = new ArrayList<>();
            for (Element header : headerElements) {
                headerTexts.add(header.text());
            }
            headers.put("h" + i, headerTexts);
        }
    }

    public void extractDuplicateText(String url, Set<String> duplicateTexts) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String text = doc.text();
        String[] words = text.split("\\s+");
        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : words) {
            word = word.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
            if (!word.isEmpty()) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            if (entry.getValue() > 1) {
                duplicateTexts.add(entry.getKey());
            }
        }
    }

    public void extractDuplicateSentencesFromPage(String url, Set<String> duplicateSentences) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String text = doc.text();
        String[] sentences = text.split("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?)\\s");

        Map<String, Integer> sentenceCount = new HashMap<>();

        for (String sentence : sentences) {
            sentence = sentence.trim().toLowerCase();
            if (!sentence.isEmpty()) {
                sentenceCount.put(sentence, sentenceCount.getOrDefault(sentence, 0) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : sentenceCount.entrySet()) {
            if (entry.getValue() > 1) {
                duplicateSentences.add(entry.getKey());
            }
        }
    }

    public double[] extractKeywordCount(String url, String keyword) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String text = doc.text();
        String[] words = text.split("\\s+");
        double count = 0;

        for (String word : words) {
            if (word.equalsIgnoreCase(keyword)) {
                count++;
            }
        }

        double[] res = {count, words.length};

        return res;
    }


    // Scrapping.java
    public void extractSocialMediaLinks(String url, Set<String> socialMediaLinks, Set<String> visitedUrls) throws IOException {
        if (visitedUrls.contains(url)) {
            return;
        }
        visitedUrls.add(url);

        Connection connection = Jsoup.connect(url).ignoreContentType(true);
        connection.header("Accept", "text/*, application/xml, application/*+xml");
        Document doc = connection.get();

        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String absUrl = link.absUrl("href");
            if (absUrl.contains("facebook.com") || absUrl.contains("twitter.com") || absUrl.contains("instagram.com") ||
                    absUrl.contains("linkedin.com") || absUrl.contains("youtube.com")) {
                socialMediaLinks.add(absUrl);
            } else if (absUrl.contains(url) && !absUrl.contains("wp-content")) {
                extractSocialMediaLinks(absUrl, socialMediaLinks, visitedUrls);
            }
        }
    }

    public long analyzePageLoadTime(String url) throws IOException {
        long startTime = System.currentTimeMillis();

        Connection connection = Jsoup.connect(url).ignoreContentType(true);
        connection.header("Accept", "text/*, application/xml, application/*+xml");
        Document doc = connection.get();

        long endTime = System.currentTimeMillis();
        return (endTime - startTime);
    }


}
