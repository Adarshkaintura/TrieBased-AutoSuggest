package TrieBasedAuto;

import java.util.*;

public class Trie {
    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        insert(word, 0);
    }

    public void insert(String word, int frequency) {
        if (word == null || word.isEmpty()) return;
        word = word.toLowerCase();
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            curr = curr.children.computeIfAbsent(c, k -> new TrieNode());
        }
        curr.isEndOfWord = true;
        curr.frequency = Math.max(curr.frequency, frequency);
    }

    public boolean search(String word) {
        TrieNode node = getNode(word == null ? "" : word.toLowerCase());
        return node != null && node.isEndOfWord;
    }

    private TrieNode getNode(String prefix) {
        TrieNode curr = root;
        for (char c : prefix.toCharArray()) {
            curr = curr.children.get(c);
            if (curr == null) return null;
        }
        return curr;
    }

    public List<String> autocomplete(String prefix, int limit) {
        prefix = (prefix == null) ? "" : prefix.toLowerCase();
        TrieNode node = getNode(prefix);
        if (node == null) return Collections.emptyList();

        List<WordFreq> result = new ArrayList<>();
        dfs(node, new StringBuilder(prefix), result);

        result.sort((a, b) -> {
            if (b.freq != a.freq) return Integer.compare(b.freq, a.freq);
            return a.word.compareTo(b.word);
        });

        List<String> out = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, result.size()); i++) {
            out.add(result.get(i).word);
        }
        return out;
    }

    private void dfs(TrieNode node, StringBuilder prefix, List<WordFreq> collector) {
        if (node.isEndOfWord) {
            collector.add(new WordFreq(prefix.toString(), node.frequency));
        }
        for (Map.Entry<Character, TrieNode> e : node.children.entrySet()) {
            prefix.append(e.getKey());
            dfs(e.getValue(), prefix, collector);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    public boolean incrementFrequency(String word) {
        TrieNode node = getNode(word == null ? "" : word.toLowerCase());
        if (node != null && node.isEndOfWord) {
            node.frequency++;
            return true;
        }
        return false;
    }

    public static class WordFreq {
        String word;
        int freq;
        WordFreq(String w, int f) { word = w; freq = f; }
    }
}
