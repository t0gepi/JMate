package gui.panel;

import gui.components.button.AboutButton;
import gui.frame.JMate;
import gui.frame.View;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class AboutPanel extends JPanel {

    private final JMate jMate;


    // ---------------Components---------------
    private JButton stopButton;
    private JEditorPane editorPane;

    public AboutPanel(JMate jMate){
        this.jMate = jMate;
        setLayout(new FlowLayout());
        initComponents();
        setVisible(true);
    }
    private void initComponents() {

        stopButton = new JButton("Back to Settings");
        editorPane = new JEditorPane();
        //editorPane.setBackground(new Color(111, 111, 142));
        editorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        try {
            editorPane.setText(Files.readString(new File(JMate.CONTENT_ROOT_DIR + "/about.html").toPath(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        editorPane.setEditable(false);
        editorPane.setFont(editorPane.getFont().deriveFont(18f));
        editorPane.addHyperlinkListener(new HyperLinkListener());
        add(editorPane);
        add(stopButton);
        stopButton.addActionListener(e -> {
            jMate.navigate(View.SETTINGS);
            jMate.getFrame().setLocation(AboutButton.frameLocationBeforeClick);
        });
    }

    private static class HyperLinkListener implements HyperlinkListener {
        @Override
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
