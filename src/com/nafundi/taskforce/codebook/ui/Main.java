
package com.nafundi.taskforce.codebook.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

import com.apple.eawt.Application;

public class Main {

    public static String APP_NAME = "Task Force Codebook";
    private JFrame frame;
    private JFormattedTextField filePath;
    private JTextArea statusLog;

    public static void main(String[] args) {

        if (System.getProperty("os.name").contains("Mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name", APP_NAME);

        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main window = new Main();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Main() {
        initialize();
    }

    private void initialize() {

        frame = new JFrame(APP_NAME);
        frame.setResizable(false);
        frame.setBounds(100, 100, 450, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(
                new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setLocationRelativeTo(null);

        ImageIcon mainLogo = new javax.swing.ImageIcon(getClass().getResource(
                "/taskforce-main-logo.png"));
        Image appLogo = Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/taskforce-app-logo.png"));

        if (System.getProperty("os.name").toUpperCase().contains("MAC")) {
            Application app = Application.getApplication();
            app.removeAboutMenuItem();  // TODO: depricated?
            app.setDockIconImage(appLogo);
        }

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel logo = new JLabel(mainLogo);
        logo.setBounds(21, 10, 408, 58);
        panel.add(logo);

        statusLog = new JTextArea();
        statusLog.setEditable(false);
        statusLog.setFont(new Font("Dialog", Font.PLAIN, 11));
        statusLog.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(21, 76, 408, 222);
        scrollPane
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(statusLog);
        panel.add(scrollPane);

        filePath = new JFormattedTextField();
        filePath.setEnabled(false);
        filePath.setEditable(false);
        filePath.setBounds(140, 310, 282, 28);
        panel.add(filePath);

        JButton selectForm = new JButton("Select form");
        selectForm.setBounds(21, 311, 122, 29);
        selectForm.addActionListener(new FileChooser());

        panel.add(selectForm);

        JLabel helpText = new JLabel(
                "Select a form to generate a coding guide in the same directory.");
        helpText.setBounds(21, 338, 408, 29);
        helpText.setHorizontalAlignment(SwingConstants.CENTER);
        helpText.setFont(new Font("Dialog", Font.PLAIN, 11));
        panel.add(helpText);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setIconImage(appLogo);

        CodebookEngine ce = new CodebookEngine();
        ArrayList<CodebookEntry> entries = ce
                .LoadForm("/Users/carlhartung/Desktop/gb-demo-forms/Widgets.xml");
        makedoc(entries);
        System.exit(0);

    }

    private void appendToStatus(String text) {
        statusLog.setText(statusLog.getText() + text + "\n");
    }

    class FileChooser implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            // Demonstrate "Open" dialog:
            int rVal = fileChooser.showOpenDialog(frame);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                filePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                appendToStatus("Selected file: " + fileChooser.getSelectedFile().getAbsolutePath());
                // selectedDir.setText(fileChooser.getCurrentDirectory().toString());
            }
        }
    }

    public void makedoc(ArrayList<CodebookEntry> entries) {
        // where do you want the file to appear
        String filePath = "/Users/carlhartung/Desktop/test.doc";

        String[][] tableData = new String[entries.size()][3];
        // set up headers
        tableData[0][0] = "Variable";
        tableData[0][1] = "Survey Question";
        tableData[0][2] = "Value";

        // populate table
        for (int i = 0; i < entries.size(); i++) {
            tableData[i][0] = entries.get(i).getVariable();
            tableData[i][1] = entries.get(i).getQuestion();
            tableData[i][2] = entries.get(i).getValue();
        }

        // https://code.google.com/p/zkpoi/source/browse/branches/zkpoi/src/examples/src/org/apache/poi/xwpf/usermodel/SimpleTable.java

        // Create a new document from scratch
        XWPFDocument doc = new XWPFDocument();

        // Create a new table with rows and columns
        int nRows = tableData.length;
        int nCols = tableData[0].length;
        XWPFTable table = doc.createTable(nRows, nCols);

        // table should have nice margins
        table.setCellMargins(50, 75, 50, 75);

        // Get a list of the rows in the table
        List<XWPFTableRow> rows = table.getRows();
        int rowCt = 0;
        int colCt = 0;
        for (XWPFTableRow row : rows) {
            // get table row properties (trPr)
            CTTrPr trPr = row.getCtRow().addNewTrPr();
            // set row height; units = twentieth of a point, 360 = 0.25"
            CTHeight ht = trPr.addNewTrHeight();
            ht.setVal(BigInteger.valueOf(360));

            // get the cells in this row
            List<XWPFTableCell> cells = row.getTableCells();
            // add content to each cell
            for (XWPFTableCell cell : cells) {

                // cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(360*9));

                // get a table cell properties element (tcPr)
                CTTcPr tcpr = cell.getCTTc().addNewTcPr();
                // set vertical alignment to "center"
                CTVerticalJc va = tcpr.addNewVAlign();
                va.setVal(STVerticalJc.CENTER);

                // create cell color element
                CTShd ctshd = tcpr.addNewShd();
                ctshd.setColor("auto");
                ctshd.setVal(STShd.CLEAR);
                if (rowCt == 0) {
                    // header row
                    ctshd.setFill("A7BFDE");
                } else if (rowCt % 2 == 0) {
                    // even row
                    ctshd.setFill("D3DFEE");
                } else {
                    // odd row
                    ctshd.setFill("EDF2F8");
                }

                // get 1st paragraph in cell's paragraph list
                XWPFParagraph para = cell.getParagraphs().get(0);
                // create a run to contain the content
                XWPFRun rh = para.createRun();
                rh.setFontSize(11);
                rh.setFontFamily("Helvetica");

                // style cell as desired
                if (rowCt == 0) {
                    // header row
                    rh.setText(tableData[rowCt][colCt]);
                    rh.setBold(true);
                    para.setAlignment(ParagraphAlignment.CENTER);
                } else if (rowCt % 2 == 0) {
                    // even row
                    rh.setText(tableData[rowCt][colCt]);
                    para.setAlignment(ParagraphAlignment.LEFT);
                } else {
                    // odd row
                    rh.setText(tableData[rowCt][colCt]);
                    para.setAlignment(ParagraphAlignment.LEFT);
                }
                colCt++;
            } // for cell
            colCt = 0;
            rowCt++;
        } // for row

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            doc.write(outStream);
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
