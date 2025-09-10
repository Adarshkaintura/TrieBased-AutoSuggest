# Trie Autocomplete + Dictionary Lookup

A Java project that implements an **autocomplete system using Trie data structure** with both **CLI (Command-Line)** and **GUI (Swing)** interfaces.  
It suggests words based on user input and fetches **word meanings** using the [Free Dictionary API](https://dictionaryapi.dev/).

---

## 🚀 Features
- Trie-based autocomplete implementation.
- CLI mode:
  - Type prefixes to get suggestions.
  - Add new words to the dictionary.
  - Select a word to increase its frequency.
  - Fetch meaning of a selected word.
- GUI mode (Swing):
  - Search bar with real-time autocomplete.
  - Dark mode toggle.
  - Word history tracking.
  - Fetch word meanings (async).
- Dictionary loader (`words.txt`) support for initial word list.
- Word frequency ranking (more frequently selected words appear first).

---

## 📂 Project Structure
```

TrieBasedAuto/
├── AutocompleteCLI.java       # CLI version
├── AutocompleteGUI.java       # GUI version (Swing)
├── DictionaryFetcher.java     # Fetches word meanings from API
├── DictionaryLoader.java      # Loads dictionary file
├── Trie.java                  # Trie implementation
├── TrieNode.java              # Trie node definition
└── words.txt                  # Sample dictionary file (optional)

````

---

## 🛠️ Installation & Running

### 1. Clone repository
```bash
git clone https://github.com/<your-username>/Trie-Autocomplete-Dictionary.git
cd Trie-Autocomplete-Dictionary
````

### 2. Compile

```bash
javac TrieBasedAuto/*.java
```

### 3. Run CLI

```bash
java TrieBasedAuto.AutocompleteCLI words.txt
```

### 4. Run GUI

```bash
java TrieBasedAuto.AutocompleteGUI words.txt
```

---

## 📸 Screenshots

### CLI

```
> hello
Suggestions:
1. hello
2. help
3. helm
Pick number to select (increase rank), or press Enter to skip:
```

### GUI

(Add screenshot of GUI with dark/light mode, history, and meaning panel)

---

## 🌐 API

Uses [Free Dictionary API](https://dictionaryapi.dev/) to fetch word meanings.
Example:
`https://api.dictionaryapi.dev/api/v2/entries/en/hello`

---

## 📜 License

MIT License – free to use and modify.

````

---



src/
 └── TrieBasedAuto/
       ├── AutocompleteCLI.java
       ├── AutocompleteGUI.java
       ├── DictionaryFetcher.java
       ├── DictionaryLoader.java
       ├── Trie.java
       ├── TrieNode.java
```

to Execute - java TrieBasedAuto.AutocompleteGUI
