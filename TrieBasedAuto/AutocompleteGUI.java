package TrieBasedAuto;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AutocompleteGUI {
    private Trie trie;
    private JFrame frame;
    private JTextField textField;
    private JList<String> list;
    private DefaultListModel<String> listModel;
    private JLabel statusBar;
    private JTextArea historyArea;
    private JTextArea meaningArea;
    private final int SUGGESTION_LIMIT = 10;
    private boolean darkMode = false;

    public AutocompleteGUI(Trie trie) {
        this.trie = trie;
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {
        frame = new JFrame("Trie Autocomplete + Dictionary");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(new BorderLayout(10, 10));

      
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        textField = new JTextField();
        searchPanel.add(new JLabel("🔍 Search:"), BorderLayout.WEST);
        searchPanel.add(textField, BorderLayout.CENTER);

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createTitledBorder("Suggestions"));

        historyArea = new JTextArea(10, 20);
        historyArea.setEditable(false);
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setBorder(BorderFactory.createTitledBorder("History"));

        meaningArea = new JTextArea(10, 30);
        meaningArea.setLineWrap(true);
        meaningArea.setWrapStyleWord(true);
        meaningArea.setEditable(false);
        JScrollPane meaningScroll = new JScrollPane(meaningArea);
        meaningScroll.setBorder(BorderFactory.create
                                
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.add(historyScroll, BorderLayout.NORTH);
        rightPanel.add(meaningScroll, BorderLayout.CENTER);
        statusBar = new JLabel("Type something to get suggestions...");
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        frame.add(searchPanel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(rightPanel, BorderLayout.EAST);
        frame.add(statusBar, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu viewMenu = new JMenu("View");
        JMenuItem darkModeItem = new JMenuItem("Toggle Dark Mode");
        darkModeItem.addActionListener(e -> toggleDarkMode());
        viewMenu.add(darkModeItem);
        menuBar.add(viewMenu);
        frame.setJMenuBar(menuBar);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { showSuggestions(); }
            public void removeUpdate(DocumentEvent e) { showSuggestions(); }
            public void changedUpdate(DocumentEvent e) { showSuggestions(); }
        });

        textField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (listModel.isEmpty()) return;

                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    int idx = Math.min(list.getSelectedIndex() + 1, listModel.size() - 1);
                    list.setSelectedIndex(idx);
                    list.ensureIndexIsVisible(idx);
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    int idx = Math.max(list.getSelectedIndex() - 1, 0);
                    list.setSelectedIndex(idx);
                    list.ensureIndexIsVisible(idx);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (list.getSelectedIndex() >= 0) {
                        acceptSuggestion(list.getSelectedValue());
                        e.consume();
                    } else {
                        String typed = textField.getText().trim();
                        if (!typed.isEmpty()) fetchAndShowMeaning(typed);
                    }
                }
            }
        });

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 1) {
                    String val = list.getSelectedValue();
                    if (val != null) acceptSuggestion(val);
                }
                if (e.getClickCount() == 2) {
                    String val = list.getSelectedValue();
                    if (val != null) fetchAndShowMeaning(val);
                }
            }
        });

        JPopupMenu popup = new JPopupMenu();
        JMenuItem getMeaningItem = new JMenuItem("Get Meaning");
        getMeaningItem.addActionListener(e -> {
            String sel = list.getSelectedValue();
            if (sel != null) fetchAndShowMeaning(sel);
            else {
                String typed = textField.getText().trim();
                if (!typed.isEmpty()) fetchAndShowMeaning(typed);
            }
        });
        popup.add(getMeaningItem);
        list.setComponentPopupMenu(popup);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showSuggestions() {
        String text = textField.getText();
        listModel.clear();
        meaningArea.setText("");

        if (text == null || text.isEmpty()) {
            statusBar.setText("Type something to get suggestions...");
            return;
        }

        List<String> suggestions = trie.autocomplete(text, SUGGESTION_LIMIT);
        if (suggestions.isEmpty()) {
            statusBar.setText("No suggestions found.");
            return;
        }

        for (String s : suggestions) listModel.addElement(s);
        list.setSelectedIndex(0);
        statusBar.setText(suggestions.size() + " suggestion(s) found.");
    }

    private void acceptSuggestion(String s) {
        textField.setText(s);
        trie.incrementFrequency(s);
        historyArea.append("✔ " + s + "\n");
        listModel.clear();
        statusBar.setText("You selected: " + s);
        fetchAndShowMeaning(s);
    }

    private void fetchAndShowMeaning(String word) {
        meaningArea.setText("Loading meaning for '" + word + "'...");
        statusBar.setText("Fetching meaning...");
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return DictionaryFetcher.fetchMeaning(word);
            }

            @Override
            protected void done() {
                try {
                    String meaning = get();
                    meaningArea.setText(meaning);
                    statusBar.setText("Meaning loaded for: " + word);
                } catch (Exception ex) {
                    meaningArea.setText("Error retrieving meaning: " + ex.getMessage());
                    statusBar.setText("Error fetching meaning.");
                }
            }
        }.execute();
    }

    private void toggleDarkMode() {
        darkMode = !darkMode;
        Color bg = darkMode ? new Color(45, 45, 45) : Color.WHITE;
        Color fg = darkMode ? Color.WHITE : Color.BLACK;

        frame.getContentPane().setBackground(bg);
        textField.setBackground(darkMode ? new Color(60,60,60) : Color.WHITE);
        textField.setForeground(fg);
        list.setBackground(darkMode ? new Color(60,60,60) : Color.WHITE);
        list.setForeground(fg);
        historyArea.setBackground(darkMode ? new Color(60,60,60) : Color.WHITE);
        historyArea.setForeground(fg);
        meaningArea.setBackground(darkMode ? new Color(60,60,60) : Color.WHITE);
        meaningArea.setForeground(fg);
        statusBar.setForeground(fg);
        statusBar.setBackground(bg);
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        String dictPath = args.length > 0 ? args[0] : "words.txt";
        try {
            DictionaryLoader.loadFromFile(dictPath, trie);
            System.out.println("Loaded dictionary from " + dictPath);
        } catch (Exception e) {
            System.out.println("Could not load dictionary: " + e.getMessage());
        }
        new AutocompleteGUI(trie);
    }
}
