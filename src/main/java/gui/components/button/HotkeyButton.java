package gui.components.button;

import bot.Lichess;
import config.ConfigManager;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Handler;
import java.util.logging.Level;

public class HotkeyButton extends JButton {

    Logger LOGGER = LoggerFactory.getLogger(HotkeyButton.class);

    public static volatile boolean wasPressed = false;

    private boolean isChoosing = false;
    private int keycode = -1;

    public HotkeyButton(){
        super("Hotkey");
        addActionListener(new HotkeyButtonListener());
    }


    private class HotkeyButtonListener implements ActionListener, NativeKeyListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Dimension sizeBefore = getSize();
            setText("...");
            setPreferredSize(sizeBefore);
            isChoosing = true;
            try{
                GlobalScreen.registerNativeHook();
            }
            catch(NativeHookException ex){
                ex.printStackTrace();
                LOGGER.error("Failed registering NativeHook");
            }
            java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            // Change the level for all handlers attached to the default logger.
            Handler[] handlers = java.util.logging.Logger.getLogger("").getHandlers();
            for (int i = 0; i < handlers.length; i++) {
                handlers[i].setLevel(Level.OFF);
            }
            GlobalScreen.addNativeKeyListener(this);
            System.out.println("test");
        }

        @Override
        public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
            if(isChoosing){
                if(nativeKeyEvent.getKeyCode() == 3667){
                    setText("Hotkey");
                    ConfigManager.removeProperty("hotkey");
                    try {
                        GlobalScreen.unregisterNativeHook();
                    } catch (NativeHookException e) {
                        LOGGER.error("Failed unregistering NativeHook");
                    }
                    isChoosing = false;
                    return;
                }
                String key = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
                setText(key);
                ConfigManager.setProperty("hotkey", nativeKeyEvent.getKeyCode()+"");
                keycode = nativeKeyEvent.getKeyCode();
                isChoosing = false;
            }
            else if(keycode == nativeKeyEvent.getKeyCode()){
                if(Lichess.hotkeyReady){
                    wasPressed = true; // the CLI will cancel the calculation of the best move and return the best move it has calculated so far.
                                        // then plays that move and resets this boolean to false
                                        // This boolean can only be set or this button can only be pressed when it is your turn.
                }

            }

        }

        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

        }

        @Override
        public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

        }

    }






}
