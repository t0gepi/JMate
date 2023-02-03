package bot;

import gui.panel.SettingsPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;

/**
 *  The StopWorker executes when the bot is being stopped. It logs every second while the bot is stopping.
 *  When the bot is stopped, it re-enables the fields in the gui, that got disabled when the bot was started.
 */
public class StopWorker extends SwingWorker<Void, Void> {

    private final Logger LOGGER = LoggerFactory.getLogger(StopWorker.class);

    private final Thread thread;

    private final SettingsPanel settingsPanel;


    public StopWorker(Thread thread, SettingsPanel settingsPanel){
        this.thread = thread;
        this.settingsPanel = settingsPanel;
    }

    @Override
    protected Void doInBackground() {
        long last = System.currentTimeMillis();
        int seconds = 0;
        while(thread.isAlive()){
            long now = System.currentTimeMillis();
            if(now-last > 1000){
                seconds++;
                last = now;
                LOGGER.info(String.format("Stopping bot...(%ds)", seconds));
            }
        }
        return null;
    }

    @Override
    protected void done() {
        super.done();
        settingsPanel.getStartButton().setText("Start");
        settingsPanel.getStartButton().setEnabled(true);
        settingsPanel.getEngineSelect().setEnabled(true);
        settingsPanel.getVariantSelect().setEnabled(true);
        settingsPanel.getOpeningSelect().setEnabled(true);
        settingsPanel.getHotkeyButton().setEnabled(true);
        LOGGER.info("Bot stopped");
    }
}
