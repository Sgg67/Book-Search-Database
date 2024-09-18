package ass3;


import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.List;

import javax.swing.*;

import javax.swing.border.EtchedBorder;
import javax.swing.text.*;


/**
 * 
 * GUI for a book search.
 * Version Fall 2022
 */
public class BookSearchFrame extends JFrame {
    private static final long serialVersionUID = 3688148253476601561L;

    private BookFinder bookFinder;
    private JComboBox<String> searchType;
    private JTextField query;
    private JPanel output;
    private JScrollPane outputScrollPane;

    /**
     * Create a new BookSearchFrame using {@code bookFinder} as the underlying
     * database of books.
     * 
     * @param bookFinder The book database.
     */
    public BookSearchFrame(BookFinder bookFinder) {
        super();

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(800, 600));
        this.setTitle("Book Search");
        this.bookFinder = bookFinder;
        this.searchType = new JComboBox<String>(new String[] { "Author", "Title", "Publisher", "ISBN" });
        this.searchType.addActionListener(e -> performQuery());
        this.query = new JTextField();
        this.query.addActionListener(e -> performQuery());
        this.query.setPreferredSize(new Dimension(600, 24));
        this.query.setMinimumSize(new Dimension(100, 24));

        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        top.add(this.searchType);
        top.add(this.query);

        contentPane.add(top, BorderLayout.NORTH);

        this.output = new JPanel();
        this.output.setLayout(new BoxLayout(this.output, BoxLayout.Y_AXIS));

        this.outputScrollPane = new JScrollPane(this.output);
        contentPane.add(this.outputScrollPane, BorderLayout.CENTER);

        // Add some example queries.
        performQuery();
    }

    private void performQuery() {
        String queryText = this.query.getText().strip();
        String queryType = (String) this.searchType.getSelectedItem();
        List<BookData> results = null;

        if (!queryText.isEmpty()) {
            switch (queryType) {
            case "Author":
                results = bookFinder.searchByAuthor(queryText);
                break;
            case "Title":
                results = bookFinder.searchByTitle(queryText);
                break;
            case "Publisher":
                results = bookFinder.searchByPublisher(queryText);
                break;
            case "ISBN":
                BookData bd = bookFinder.searchByIsbn(queryText);
                if (bd != null) {
                    results = new ArrayList<BookData>();
                    results.add(bd);
                }
                break;
            }
        }

        this.output.removeAll();

        if (results == null || results.isEmpty()) {
            JTextPane text = new JTextPane();
            StyledDocument doc = text.getStyledDocument();

            text.setEditable(false);

            Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
            Style regular = doc.addStyle("regular", def);
            StyleConstants.setFontFamily(def, "SansSerif");

            Style errorStyle = doc.addStyle("errorStyle", regular);
            StyleConstants.setBold(errorStyle, true);
            StyleConstants.setForeground(errorStyle, Color.RED);

            Style titleStyle = doc.addStyle("titleStyle", regular);
            StyleConstants.setUnderline(titleStyle, true);
            StyleConstants.setForeground(titleStyle, Color.BLUE);
            titleStyle.addAttribute("link", "Title");

            Style authorStyle = doc.addStyle("authorStyle", regular);
            StyleConstants.setUnderline(authorStyle, true);
            StyleConstants.setForeground(authorStyle, Color.BLUE);
            authorStyle.addAttribute("link", "Author");

            Style publisherStyle = doc.addStyle("publisherStyle", regular);
            StyleConstants.setUnderline(publisherStyle, true);
            StyleConstants.setForeground(publisherStyle, Color.BLUE);
            publisherStyle.addAttribute("link", "Publisher");

            Style isbnStyle = doc.addStyle("isbnStyle", regular);
            StyleConstants.setUnderline(isbnStyle, true);
            StyleConstants.setForeground(isbnStyle, Color.BLUE);
            isbnStyle.addAttribute("link", "ISBN");

            try {
                if (!queryText.isEmpty()) {
                    doc.insertString(0, String.format("No result found for %s query: %s\n\n", queryType, queryText),
                            errorStyle);
                }
                doc.insertString(doc.getLength(), "Example queries:\n- Author: ", regular);
                doc.insertString(doc.getLength(), "Heidi Hayes Jacobs", authorStyle);
                doc.insertString(doc.getLength(), "\n- Title: ", regular);
                doc.insertString(doc.getLength(), "Dune (Dune #1)", titleStyle);
                doc.insertString(doc.getLength(), "\n- Publisher: ", regular);
                doc.insertString(doc.getLength(), "Harper", publisherStyle);
                doc.insertString(doc.getLength(), "\n- ISBN: ", regular);
                doc.insertString(doc.getLength(), "074348486X", isbnStyle);
            } catch (BadLocationException ble) {
                ble.printStackTrace();
            }
            addTextMouseListener(text);
            this.output.add(text);
        } else {
            // Add success output.
            for (BookData bd : results) {
                JTextPane text = new JTextPane();
                StyledDocument doc = text.getStyledDocument();

                text.setEditable(false);

                Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
                Style regular = doc.addStyle("regular", def);
                StyleConstants.setFontFamily(def, "SansSerif");

                Style titleStyle = doc.addStyle("titleStyle", regular);
                StyleConstants.setItalic(titleStyle, true);

                Style authorStyle = doc.addStyle("authorStyle", regular);
                StyleConstants.setUnderline(authorStyle, true);
                StyleConstants.setForeground(authorStyle, Color.BLUE);
                authorStyle.addAttribute("link", "Author");

                Style publisherStyle = doc.addStyle("publisherStyle", regular);
                StyleConstants.setUnderline(publisherStyle, true);
                StyleConstants.setForeground(publisherStyle, Color.BLUE);
                publisherStyle.addAttribute("link", "Publisher");

                try {
                    // Add authors.
                    doc.insertString(0, bd.authors.length > 1 ? "Authors: " : "Author: ", regular);
                    for (int i = 0; i < bd.authors.length; i++) {
                        if (i > 0 && i == bd.authors.length - 1) {
                            doc.insertString(doc.getLength(), ", and ", regular);
                        } else if (i > 0) {
                            doc.insertString(doc.getLength(), ", ", regular);
                        }
                        doc.insertString(doc.getLength(), bd.authors[i], authorStyle);
                    }

                    // Add title.
                    doc.insertString(doc.getLength(), "\nTitle: ", regular);
                    doc.insertString(doc.getLength(), bd.title, titleStyle);

                    // Add publisher.
                    doc.insertString(doc.getLength(), "\nPublisher: ", regular);
                    doc.insertString(doc.getLength(), bd.publisher, publisherStyle);

                    // Add ISBN and rating
                    String s = String.format("\nISBN: %s\nRating: %.2f", bd.isbn, bd.rating);
                    doc.insertString(doc.getLength(), s, regular);
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                    System.exit(1);
                }

                // Add a mouse listener to respond to clicks on links.
                addTextMouseListener(text);
                text.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                text.setMaximumSize(new Dimension(Integer.MAX_VALUE, text.getPreferredSize().height));
                this.output.add(text);
            }
        }
        this.output.repaint();
        this.validate();
        javax.swing.SwingUtilities.invokeLater(() -> {
            outputScrollPane.getHorizontalScrollBar().setValue(0);
            outputScrollPane.getVerticalScrollBar().setValue(0);
        });

    }

    /**
     * Add a mouse listener to respond to clicks on links.
     * 
     * @param text The JTextPane to add the listener to.
     */
    private void addTextMouseListener(JTextPane text) {
        StyledDocument doc = text.getStyledDocument();
        text.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // System.out.println("Click");
                Element element = doc.getCharacterElement(text.viewToModel2D(e.getPoint()));
                if (element == null)
                    return;
                // System.out.println(" On element");
                String linkType = (String) element.getAttributes().getAttribute("link");
                if (linkType == null)
                    return;

                // System.out.println(" With link attribute: " + linkType);
                int start = element.getStartOffset();
                int end = element.getEndOffset();
                searchType.setSelectedItem(linkType);
                try {
                    query.setText(doc.getText(start, end - start));
                    performQuery();
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            }
        });
    }

    /**
     * Run the application, creating the database from {@code GoodReadsData.txt}.
     * 
     * @param args The command line parameters. Ignored.
     */
    public static void main(String[] args) {
        BookFinder bookFinder = new BookFinder("GoodReadsData.txt");

        JFrame.setDefaultLookAndFeelDecorated(true);
        BookSearchFrame frame = new BookSearchFrame(bookFinder);
        frame.validate();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
