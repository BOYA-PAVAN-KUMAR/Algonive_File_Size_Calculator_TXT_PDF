import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.StringTokenizer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class FileSizeCalculatorPDF extends JFrame implements ActionListener {

    private JButton chooseFileButton;
    private JTextArea outputArea;

    public FileSizeCalculatorPDF() {
        setTitle("File Size Calculator (TXT & PDF)");
        setSize(550, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chooseFileButton = new JButton("Choose TXT or PDF File");
        outputArea = new JTextArea();
        outputArea.setEditable(false);

        add(chooseFileButton, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        chooseFileButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".txt") && !file.getName().endsWith(".pdf")) {
                JOptionPane.showMessageDialog(this, "Select TXT or PDF only");
                return;
            }
            analyzeFile(file);
        }
    }

    private void analyzeFile(File file) {
        try {
            String content = file.getName().endsWith(".pdf")
                    ? readPdf(file)
                    : readTxt(file);

            int wordCount = 0, totalLen = 0;
            String longest = "";

            StringTokenizer st = new StringTokenizer(content);
            while (st.hasMoreTokens()) {
                String w = st.nextToken();
                wordCount++;
                totalLen += w.length();
                if (w.length() > longest.length()) longest = w;
            }

            long bytes = file.length();
            double kb = bytes / 1024.0;
            double mb = kb / 1024.0;

            outputArea.setText(
                    "File: " + file.getName() + "\n\n" +
                    "Size: " + bytes + " bytes\n" +
                    "Size: " + String.format("%.2f", kb) + " KB\n" +
                    "Size: " + String.format("%.2f", mb) + " MB\n\n" +
                    "Words: " + wordCount + "\n" +
                    "Characters: " + content.length() + "\n" +
                    "Longest word: " + longest + "\n" +
                    "Average word length: " +
                    (wordCount == 0 ? 0 : (double) totalLen / wordCount)
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private String readTxt(File file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line).append(" ");
        br.close();
        return sb.toString();
    }

    private String readPdf(File file) throws Exception {
        PDDocument doc = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(doc);
        doc.close();
        return text;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileSizeCalculatorPDF().setVisible(true));
    }
}
