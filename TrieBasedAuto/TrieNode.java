package TrieBasedAuto;

import java.util.*;

public class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord = false;
    int frequency = 0; 
}
