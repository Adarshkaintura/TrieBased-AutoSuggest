package TrieBasedAuto;

import java.io.*;

public class DictionaryLoader {
    public static void loadFromFile(String filepath, Trie trie) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\s+");
                if (parts.length == 1) {
                    trie.insert(parts[0]);
                } else {
                    try {
                        int freq = Integer.parseInt(parts[1]);
                        trie.insert(parts[0], freq);
                    } catch (NumberFormatException e) {
                        trie.insert(parts[0]);
                    }
                }
            }
        }
    }
}
