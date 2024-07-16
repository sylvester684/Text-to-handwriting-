package com.example.t2h;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TextToHandwritingConverter extends JFrame {

    private String text;
    private Font handwritingFont;
    private int fontSize;
    private int pageWidth;
    private int pageHeight;
    private int currentY;
    private int currentPageIndex;
    private BufferedImage currentPage;
    private Graphics2D currentGraphics;

    public TextToHandwritingConverter() {
        // Create a JFrame with a title
        super("Text to Handwriting Converter");

        // Initialize page size (A4: 210mm x 297mm at 72 DPI)
        pageWidth = (int) (210 * 72 / 25.4);
        pageHeight = (int) (297 * 72 / 25.4);

        // Initialize font and font size
        fontSize = 20;  // Start with a default font size
        loadHandwritingFont();

        // Create a JPanel to hold the text
        JPanel panel = new JPanel();

        // Create a JTextArea for text input
        JTextArea textArea = new JTextArea(10, 30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Create a button to update the text and save as an image
        JButton convertButton = new JButton("Convert and Save");
        convertButton.addActionListener(e -> {
            text = textArea.getText();
            currentPageIndex = 1;

            // Create a new BufferedImage for the current page
            currentPage = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_INT_ARGB);
            currentGraphics = currentPage.createGraphics();
            currentGraphics.setFont(handwritingFont);
            currentGraphics.setColor(Color.BLACK);

            // Clear the current page with a white background
            currentGraphics.setColor(Color.WHITE);
            currentGraphics.fillRect(0, 0, pageWidth, pageHeight);
            currentGraphics.setColor(Color.BLACK);

            // Process and write the text to the page
            writeTextToPage(text, currentGraphics, fontSize);

            // Save the current page
            saveCurrentPage();

            // Update the remaining text
            textArea.setText(text);

            // Show a message after saving
            JOptionPane.showMessageDialog(this, "Images saved as page" + currentPageIndex + ".png and so on.");
        });

        // Add components to the panel
        panel.add(scrollPane);
        panel.add(convertButton);

        // Add the panel to the frame
        add(panel);

        // Set frame properties
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadHandwritingFont() {
        try {
            handwritingFont = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\Users\\vijay\\IdeaProjects\\t2h\\src\\main\\java\\com\\example\\t2h\\ChopinScript.ttf"));
            handwritingFont = handwritingFont.deriveFont(Font.PLAIN, fontSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeTextToPage(String text, Graphics2D g2d, int fontSize) {
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight();
        int maxLines = pageHeight / lineHeight;
        currentY = lineHeight;

        for (String line : text.split("\n")) {
            String[] words = line.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String lineWithWord = currentLine + (currentLine.length() > 0 ? " " : "") + word;
                int lineWidth = fm.stringWidth(lineWithWord);

                if (lineWidth > pageWidth - 20) {
                    if (currentY + lineHeight < maxLines * lineHeight) {
                        g2d.drawString(currentLine.toString(), 10, currentY);
                        currentY += lineHeight;
                        currentLine = new StringBuilder(word);
                    } else {
                        // Save the current page and create a new page
                        saveCurrentPage();
                        currentPageIndex++;
                        currentPage = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_INT_ARGB);
                        currentGraphics = currentPage.createGraphics();
                        currentGraphics.setFont(handwritingFont);
                        currentGraphics.setColor(Color.BLACK);

                        // Clear the current page with a white background
                        currentGraphics.setColor(Color.WHITE);
                        currentGraphics.fillRect(0, 0, pageWidth, pageHeight);
                        currentGraphics.setColor(Color.BLACK);

                        currentY = lineHeight;
                        g2d = currentGraphics;
                        g2d.drawString(currentLine.toString(), 10, currentY);
                        currentY += lineHeight;
                        currentLine = new StringBuilder(word);
                    }
                } else {
                    currentLine = new StringBuilder(lineWithWord);
                }
            }

            if (currentY + lineHeight < maxLines * lineHeight) {
                g2d.drawString(currentLine.toString(), 10, currentY);
                currentY += lineHeight;
            } else {
                // Save the current page and create a new page
                saveCurrentPage();
                currentPageIndex++;
                currentPage = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_INT_ARGB);
                currentGraphics = currentPage.createGraphics();
                currentGraphics.setFont(handwritingFont);
                currentGraphics.setColor(Color.BLACK);

                // Clear the current page with a white background
                currentGraphics.setColor(Color.WHITE);
                currentGraphics.fillRect(0, 0, pageWidth, pageHeight);
                currentGraphics.setColor(Color.BLACK);

                currentY = lineHeight;
                g2d = currentGraphics;
                g2d.drawString(currentLine.toString(), 10, currentY);
                currentY += lineHeight;
            }
        }
    }

    private void saveCurrentPage() {
        try {
            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
            String folderName = "t2h";
            File folder = new File(desktopPath, folderName);
            if (!folder.exists()) {
                folder.mkdir();
            }

            File outputFile = new File(folder, "page" + currentPageIndex + ".png");
            ImageIO.write(currentPage, "png", outputFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TextToHandwritingConverter());
    }
}