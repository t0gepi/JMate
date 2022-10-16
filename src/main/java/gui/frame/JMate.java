package gui.frame;

import com.formdev.flatlaf.FlatDarkLaf;
import gui.components.button.AboutButton;
import gui.panel.AboutPanel;
import gui.panel.PageViewer;
import gui.panel.SettingsPanel;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
    * Serves as the container of the CardLayout that contains all the panels.
    * Used to call navigate() to switch between views in the CardLayout
 */
public class JMate extends JPanel implements Navigator {
    private final JFrame frame;
    private View activeView;
    public JPanel settingsPanel;
    public JPanel playingPanel;
    public PageViewer cardLayout;

    public JMate(JFrame frame){
        this.frame = frame;

        settingsPanel = new SettingsPanel(this);
        playingPanel = new AboutPanel(this);

        cardLayout = new PageViewer();
        setLayout(cardLayout);

        add(settingsPanel, View.SETTINGS);
        add(playingPanel, View.ABOUT);

        navigate(View.SETTINGS);
    }

    protected void add(JComponent component, View view) {
        add(component, view.name());
    }

    @Override
    public void navigate(View view) {
        cardLayout.show(this, view.name());
        activeView = view;
        frame.pack();
    }

    @Override
    public View getActiveView() {
        return activeView;
    }

    public JFrame getFrame() {
        return frame;
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel( new FlatDarkLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setTitle("JMate v1.0.0");
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.setResizable(false);
                JMate jMate = new JMate(frame);
                frame.add(jMate);
                frame.pack();
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        if(jMate.getActiveView() == View.SETTINGS){
                            System.exit(0);
                        }
                        else{
                            jMate.navigate(View.SETTINGS);
                            frame.setLocation(AboutButton.frameLocationBeforeClick);
                            //TODO: stop engine etc.
                        }
                    }
                });
                Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
                frame.setLocation(screenDimension.width / 2 - frame.getWidth() / 2, screenDimension.height / 2 - frame.getHeight() / 2);
                frame.setVisible(true);
            }
        });

        System.out.println("aasas");
    }

}
