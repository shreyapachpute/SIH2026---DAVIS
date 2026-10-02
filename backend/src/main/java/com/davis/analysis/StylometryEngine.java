package com.davis.analysis;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class StylometryEngine {

    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by",
            "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't",
            "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have",
            "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him",
            "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't",
            "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor",
            "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out",
            "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so",
            "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then",
            "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those",
            "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll",
            "we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which",
            "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd",
            "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"
    ));

    private static final List<String> FORMAL_AI_PATTERNS = Arrays.asList(
            "kindly be advised", "strictly required", "comprehensive disclosure", "inevitably follow",
            "in accordance with", "it is imperative", "designated cryptocurrency", "prompt remediation"
    );

    public static class StylometryResult {
        private final double similarityPercentage;
        private final double rawCosineSimilarity;
        private final boolean aiRewriteDetected;
        private final String aiRewriteExplanation;
        private final double confidenceMultiplier;
        private final Map<String, Object> metricsSampleA;
        private final Map<String, Object> metricsSampleB;

        public StylometryResult(double similarityPercentage, double rawCosineSimilarity, 
                                boolean aiRewriteDetected, String aiRewriteExplanation, 
                                double confidenceMultiplier, Map<String, Object> metricsSampleA, 
                                Map<String, Object> metricsSampleB) {
            this.similarityPercentage = similarityPercentage;
            this.rawCosineSimilarity = rawCosineSimilarity;
            this.aiRewriteDetected = aiRewriteDetected;
            this.aiRewriteExplanation = aiRewriteExplanation;
            this.confidenceMultiplier = confidenceMultiplier;
            this.metricsSampleA = metricsSampleA;
            this.metricsSampleB = metricsSampleB;
        }

        public double getSimilarityPercentage() { return similarityPercentage; }
        public double getRawCosineSimilarity() { return rawCosineSimilarity; }
        public boolean isAiRewriteDetected() { return aiRewriteDetected; }
        public String getAiRewriteExplanation() { return aiRewriteExplanation; }
        public double getConfidenceMultiplier() { return confidenceMultiplier; }
        public Map<String, Object> getMetricsSampleA() { return metricsSampleA; }
        public Map<String, Object> getMetricsSampleB() { return metricsSampleB; }
    }

    public StylometryResult compareSamples(String sampleA, String sampleB) {
        if (sampleA == null || sampleB == null || sampleA.trim().isEmpty() || sampleB.trim().isEmpty()) {
            return new StylometryResult(0.0, 0.0, false, "Insufficient text samples for stylometric comparison.", 1.0, Collections.emptyMap(), Collections.emptyMap());
        }

        Map<String, Object> metricsA = extractMetrics(sampleA);
        Map<String, Object> metricsB = extractMetrics(sampleB);

        // TF-IDF cosine similarity
        double cosine = calculateTfIdfCosine(sampleA, sampleB);

        // Punctuation similarity bonus
        double punctA = (double) metricsA.getOrDefault("doubleHyphenCount", 0.0);
        double punctB = (double) metricsB.getOrDefault("doubleHyphenCount", 0.0);
        boolean sharedPunctPattern = (punctA > 0 && punctB > 0);

        double adjustedScore = cosine;
        if (sharedPunctPattern) {
            adjustedScore = Math.min(1.0, adjustedScore + 0.10);
        }

        // AI rewrite check on both samples
        boolean aiDetectedA = checkAiRewrite(sampleA);
        boolean aiDetectedB = checkAiRewrite(sampleB);
        boolean aiRewriteDetected = aiDetectedA || aiDetectedB;

        double confidenceMultiplier = 1.0;
        String aiRewriteExplanation = "Natural idiosyncratic human author patterns observed. Full stylometric weight applied.";

        if (aiRewriteDetected) {
            confidenceMultiplier = 0.50; // Reduce contribution by 50%
            aiRewriteExplanation = "Potential AI-assisted rewriting detected. Stylometric evidence contribution reduced by 50% to prevent synthetic obfuscation bias.";
        }

        double finalPercentage = Math.round(adjustedScore * 1000.0) / 10.0;

        return new StylometryResult(finalPercentage, cosine, aiRewriteDetected, aiRewriteExplanation, confidenceMultiplier, metricsA, metricsB);
    }

    public Map<String, Object> extractMetrics(String text) {
        Map<String, Object> metrics = new HashMap<>();
        String[] words = tokenize(text);
        int totalWords = words.length;

        // Sentence statistics
        String[] sentences = text.split("[.!?]+");
        int sentenceCount = Math.max(1, sentences.length);
        double avgSentenceLength = totalWords > 0 ? (double) totalWords / sentenceCount : 0.0;

        // Vocabulary richness (Type-Token Ratio)
        Set<String> uniqueWords = Arrays.stream(words).map(String::toLowerCase).collect(Collectors.toSet());
        double typeTokenRatio = totalWords > 0 ? (double) uniqueWords.size() / totalWords : 0.0;

        // Idiosyncratic markers
        long doubleHyphenCount = Pattern.compile("--").matcher(text).results().count();
        long semicolonCount = Pattern.compile(";").matcher(text).results().count();

        metrics.put("wordCount", totalWords);
        metrics.put("sentenceCount", sentenceCount);
        metrics.put("avgSentenceLength", Math.round(avgSentenceLength * 10.0) / 10.0);
        metrics.put("typeTokenRatio", Math.round(typeTokenRatio * 100.0) / 100.0);
        metrics.put("doubleHyphenCount", (double) doubleHyphenCount);
        metrics.put("semicolonCount", (double) semicolonCount);

        return metrics;
    }

    public boolean checkAiRewrite(String text) {
        if (text == null) return false;
        String lower = text.toLowerCase();

        // 1. Check for formal LLM phrases
        long formalMatches = FORMAL_AI_PATTERNS.stream().filter(lower::contains).count();
        if (formalMatches >= 2) {
            return true;
        }

        // 2. Check for low punctuation idiosyncrasy & flattened sentence length
        String[] sentences = text.split("[.!?]+");
        if (sentences.length >= 2) {
            List<Integer> lengths = Arrays.stream(sentences)
                    .map(s -> tokenize(s).length)
                    .filter(l -> l > 0)
                    .collect(Collectors.toList());
            if (!lengths.isEmpty()) {
                double mean = lengths.stream().mapToInt(Integer::intValue).average().orElse(0.0);
                double variance = lengths.stream().mapToDouble(l -> Math.pow(l - mean, 2)).average().orElse(0.0);
                // LLM outputs typically have very uniform sentence lengths (variance < 6) and high formal lexicon
                if (variance < 6.0 && formalMatches >= 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private double calculateTfIdfCosine(String doc1, String doc2) {
        List<String> words1 = Arrays.stream(tokenize(doc1))
                .map(String::toLowerCase)
                .filter(w -> !STOPWORDS.contains(w) && w.length() > 2)
                .collect(Collectors.toList());

        List<String> words2 = Arrays.stream(tokenize(doc2))
                .map(String::toLowerCase)
                .filter(w -> !STOPWORDS.contains(w) && w.length() > 2)
                .collect(Collectors.toList());

        Set<String> vocabulary = new HashSet<>(words1);
        vocabulary.addAll(words2);

        if (vocabulary.isEmpty()) return 0.0;

        Map<String, Double> tf1 = computeTf(words1);
        Map<String, Double> tf2 = computeTf(words2);

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String term : vocabulary) {
            // Document frequency across the 2-doc corpus
            int df = (words1.contains(term) ? 1 : 0) + (words2.contains(term) ? 1 : 0);
            double idf = Math.log(1.0 + (2.0 / (1.0 + df))) + 1.0;

            double v1 = tf1.getOrDefault(term, 0.0) * idf;
            double v2 = tf2.getOrDefault(term, 0.0) * idf;

            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }

        if (norm1 == 0.0 || norm2 == 0.0) return 0.0;
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    private Map<String, Double> computeTf(List<String> tokens) {
        Map<String, Double> tf = new HashMap<>();
        if (tokens.isEmpty()) return tf;
        for (String token : tokens) {
            tf.put(token, tf.getOrDefault(token, 0.0) + 1.0);
        }
        int total = tokens.size();
        for (Map.Entry<String, Double> entry : tf.entrySet()) {
            entry.setValue(entry.getValue() / total);
        }
        return tf;
    }

    private String[] tokenize(String text) {
        return text.split("[^a-zA-Z0-9_-]+");
    }
}
