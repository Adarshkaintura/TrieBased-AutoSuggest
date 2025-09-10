package TrieBasedAuto;

import java.io.*;
import java.net.*;
import java.util.regex.*;

public class DictionaryFetcher {

    // Returns a friendly text (first definition + example if present) or an error message.
    public static String fetchMeaning(String word) {
        if (word == null || word.trim().isEmpty()) return "No word provided.";
        String encoded;
        try {
            encoded = URLEncoder.encode(word.trim(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encoded = word.trim();
        }
        String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + encoded;

        HttpURLConnection conn = null;
        BufferedReader br = null;
        try {
            URL url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(7000);
            conn.setRequestProperty("User-Agent", "TrieAutocompleteApp/1.0");

            int status = conn.getResponseCode();
            InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);

            String json = sb.toString();
            return extractFirstDefinition(json);

        } catch (IOException e) {
            return "Could not fetch meaning: " + e.getMessage();
        } finally {
            try { if (br != null) br.close(); } catch (IOException ignored) {}
            if (conn != null) conn.disconnect();
        }
    }

    // Very small extractor: looks for "definition":"...". Returns first match plus example if available.
    private static String extractFirstDefinition(String json) {
        if (json == null || json.isEmpty()) return "No response.";

        // Check for API error message (dictionaryapi returns {"title":...} on not found)
        if (json.contains("\"title\"") && json.contains("No Definitions Found")) {
            return "No definition found for this word.";
        }

        // Pattern for "definition":"...."
        Pattern defPattern = Pattern.compile("\"definition\"\\s*:\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
        Matcher defMatcher = defPattern.matcher(json);
        String definition = null;
        if (defMatcher.find()) {
            definition = defMatcher.group(1);
        }

        // Pattern for "example":"...."
        Pattern exPattern = Pattern.compile("\"example\"\\s*:\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
        Matcher exMatcher = exPattern.matcher(json);
        String example = null;
        if (exMatcher.find()) {
            example = exMatcher.group(1);
        }

        if (definition == null) {
            // fallback: try to capture a short "meaning" like word/phonetics/etc.
            // Attempt to capture "word":"..."
            Pattern wPattern = Pattern.compile("\"word\"\\s*:\\s*\"([^\"]+)\"");
            Matcher wMatcher = wPattern.matcher(json);
            if (wMatcher.find()) {
                return "No clear definition found, raw response contains word: " + wMatcher.group(1);
            }
            return "No definition found.";
        }

        StringBuilder out = new StringBuilder();
        out.append(definition);
        if (example != null && !example.isEmpty()) {
            out.append("\n\nExample: ").append(example);
        }
        return out.toString();
    }
}
