package TrieBasedAuto;

import java.util.*;

public class AutocompleteCLI {
    private static final int DEFAULT_LIMIT = 10;

    public static void main(String[] args) {
        Trie trie = new Trie();
        String dictPath = args.length > 0 ? args[0] : "words.txt";

        try {
            DictionaryLoader.loadFromFile(dictPath, trie);
            System.out.println("Loaded dictionary from " + dictPath);
        } catch (Exception e) {
            System.out.println("Warning: couldn't load dictionary from " + dictPath + ". Starting with empty trie.");
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Trie Autocomplete CLI");
        System.out.println("Commands: type a prefix to get suggestions, 'add <word>' to add, 'list <prefix>' to list all, 'exit' to quit.");
        System.out.println("After suggestions are shown, pick number to select (increase rank and fetch meaning).");

        while (true) {
            System.out.print("\n> ");
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("exit")) break;

            if (line.toLowerCase().startsWith("add ")) {
                String w = line.substring(4).trim();
                if (!w.isEmpty()) {
                    trie.insert(w, 1);
                    System.out.println("Added '" + w + "' to dictionary.");
                } else {
                    System.out.println("No word provided.");
                }
                continue;
            }

            if (line.toLowerCase().startsWith("list ")) {
                String p = line.substring(5).trim();
                List<String> words = trie.autocomplete(p, 1000);
                System.out.println("Words with prefix '" + p + "': " + words);
                continue;
            }

            String prefix = line;
            List<String> suggestions = trie.autocomplete(prefix, DEFAULT_LIMIT);
            if (suggestions.isEmpty()) {
                System.out.println("No suggestions found.");
                continue;
            }
            System.out.println("Suggestions:");
            for (int i = 0; i < suggestions.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, suggestions.get(i));
            }

            System.out.print("Pick number to select (increase rank), or press Enter to skip: ");
            String pick = sc.nextLine().trim();
            if (!pick.isEmpty()) {
                try {
                    int idx = Integer.parseInt(pick) - 1;
                    if (idx >= 0 && idx < suggestions.size()) {
                        String selected = suggestions.get(idx);
                        trie.incrementFrequency(selected);
                        System.out.println("Selected '" + selected + "'. Frequency increased.");

                        // Fetch meaning synchronously and print
                        System.out.println("Fetching meaning for '" + selected + "' ...");
                        String meaning = DictionaryFetcher.fetchMeaning(selected);
                        System.out.println("\nMeaning:\n" + meaning + "\n");

                    } else {
                        System.out.println("Invalid selection.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                }
            }
        }

        System.out.println("Goodbye!");
        sc.close();
    }
}
