package gui.components.button;

import gui.frame.JMate;
import gui.frame.View;

import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutButton extends JButton {

    public static Point frameLocationBeforeClick;
    private final JMate jMate;
    public AboutButton(JMate jMate) {
        super("About");
        this.jMate = jMate;
        addActionListener(new AboutButtonListener());
    }

    private class AboutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            frameLocationBeforeClick = jMate.getFrame().getLocation();
            jMate.navigate(View.ABOUT);
            Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
            jMate.getFrame().setLocation(screenDimension.width / 2 - jMate.getFrame().getWidth() / 2, screenDimension.height / 2 - jMate.getFrame().getHeight() / 2);
        }
    }

}
