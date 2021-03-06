import Client.Server;
import ElasticSearch.CreateNode;
import ElasticSearch.DbConnect;
import PST.PSTFileEmail;
import com.pff.PSTFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static java.awt.Desktop.getDesktop;
import static java.awt.Desktop.isDesktopSupported;


/**
 * Created by Viktor on 15/12/2014.
 */
public class Main extends JFrame {

    static String fileName;
    JButton btnStart, btnSearch, btnOpenBrowser;
    JLabel labFile, labLaunch, labStatus;
    JTextField displayField;

    /**
     * Initializes Elasticsearch, Spark, loads data into Eleasticsearch.
     */
    public Main() {

        JFrame frame = new JFrame("PSTAnalyzerr");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = gbc.CENTER;

        gbc.gridwidth = 3;
        frame.add(createPane(frame.getBackground()), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        labFile = new JLabel("1. Load .pst file ");
        frame.add(labFile, gbc);

        gbc.gridx = 1;
        displayField = new JTextField(30);
        displayField.setEditable(false);
        frame.add(displayField, gbc);

        gbc.gridx = 2;
        btnSearch = new JButton("...");
        frame.add(btnSearch, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        labLaunch = new JLabel("2. Launch client ");
        frame.add(labLaunch, gbc);

        gbc.gridx = 2;
        btnStart = new JButton("Launch");
        btnStart.setEnabled(false);
        frame.add(btnStart, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        labFile = new JLabel("3. Go to localhost:4567 ");
        frame.add(labFile, gbc);

        gbc.gridx = 2;
        btnOpenBrowser = new JButton("Open browser");
        btnOpenBrowser.setEnabled(false);
        frame.add(btnOpenBrowser, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        frame.add(createPane(frame.getBackground()), gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        labStatus = new JLabel("Status: Waiting for .pst file");
        frame.add(labStatus, gbc);

        /**
         * Action for ... button
         */
        btnSearch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser openFile = new JFileChooser();

                openFile.showOpenDialog(null);

                File selectedPst = openFile.getSelectedFile();
                displayField.setText(selectedPst.getAbsolutePath());
                fileName = selectedPst.getAbsolutePath();

                //check if file is  pst
                if (fileName.endsWith(".pst")) {
                    labStatus.setText("Status:  Valid pst file selected.");
                    btnStart.setEnabled(true);
                } else {
                    labStatus.setText("Status:  Invalid pst file selected");
                }
            }
        });

        //Launches default Web browser.
        btnOpenBrowser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isDesktopSupported()) {
                    try {
                        getDesktop().browse(new URI("http://localhost:4567"));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        /**
         * Action for launch button
         */
        btnStart.setMnemonic(KeyEvent.VK_M);
        btnStart.addActionListener(e -> {
            labStatus.setText("Status:  Parsing .pst file ");
            System.out.println("Client launch started..");
            initElasticSearch(fileName);
            labStatus.setText("Status:  ElasticSearch is up and running ");
            Server s = new Server();
            s.initServer();
            labStatus.setText(labStatus.getText() + " - Point your browser to localhost:4567");
            btnOpenBrowser.setEnabled(true);
        });
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Creates JPanel for frame.
     *
     * @param color
     * @return
     */
    public JPanel createPane(Color color) {
        JPanel pane = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(20, 20);
            }

        };
        pane.setBackground(color);
        return pane;
    }

    /**
     * Creates node, checks if index exist, creates mapping, loads data into created Index.
     *
     * @param filePath File path for PST
     */
    public static void initElasticSearch(String filePath) {
        System.out.println(fileName);
        CreateNode createNode = CreateNode.getInstance();
        DbConnect db = new DbConnect();

        if (db.CheckIfIndexExists() == true) {
            System.out.println("INDEX DELETED");
            db.DeleteIndex("pstindex");
        }
        if (db.CheckIfIndexExists() == false) {
            if (db.CreateIndex("pstindex") == true) {
                System.out.println("INDEX CREATED");
            }
        }

        //creates mapping
        db.CreateMapping();

        PSTFileEmail fileEmail = new PSTFileEmail();
        try {
            PSTFile pstFile = new PSTFile(fileName);
            fileEmail.proccessFolder(pstFile.getRootFolder());
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        /*createNode.GetEMailCountPerTime("2014-01-01T14:29:50.000Z", "2014-12-05T14:29:50.000Z","day");
        createNode.GetEmailsPerSize();
        createNode.GetEmailsPerDataField("folderName","Inbox");
        createNode.GetEmailCountPerSenderInbox();
        createNode.GetEmailCountPerSenderSent();*/
    }


    private static void initGUI() {
        Main m = new Main();
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initGUI();
            }
        });
    }
}
