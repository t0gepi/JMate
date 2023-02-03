package bot;

import gui.panel.SettingsPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartStopListener implements ActionListener {
    private final Logger LOGGER = LoggerFactory.getLogger(StartStopListener.class);

    // read by the bot thread to know when to stop
    private volatile boolean stop = true;


    private Thread botThread;

    private final SettingsPanel settingsPanel;
    public StartStopListener(SettingsPanel settingsPanel){
        this.settingsPanel = settingsPanel;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(stop){
            // disable engine, variant, opening, hotkey while bot is running.
            // depth, arrows, autoplay can still be used while bot is running.
            stop = false;
            settingsPanel.getStartButton().setText("Stop");
            settingsPanel.getEngineSelect().setEnabled(false);
            settingsPanel.getVariantSelect().setEnabled(false);
            settingsPanel.getOpeningSelect().setEnabled(false);
            settingsPanel.getHotkeyButton().setEnabled(false);
            LOGGER.info("Starting bot...");
            Runnable lichess = new Lichess(this, settingsPanel);
            botThread = new Thread(lichess);
            botThread.start();
        }
        else{
            stop = true;
            settingsPanel.getStartButton().setEnabled(false);
            LOGGER.info("Stopping bot...");
            //wait for the bot thread to die and then re-enable the fields in settingsPanel
            new StopWorker(botThread, settingsPanel).execute();
        }
    }


    public boolean isStop() {
        return stop;
    }

}
