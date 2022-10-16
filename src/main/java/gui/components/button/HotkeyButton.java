package gui.components.button;

import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HotkeyButton extends JButton {


    public HotkeyButton(){
        super("Hotkey");
    }

    private class HotkeyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO: implement JNativeHook
        }
    }

}
