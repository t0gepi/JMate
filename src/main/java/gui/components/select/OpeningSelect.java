package gui.components.select;

import config.ConfigManager;
import io.engine.Engine;
import io.engine.Opening;

import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class OpeningSelect extends JComboBox<String> {
    private final List<Opening> OPENINGS = List.of(Opening.values());

    public OpeningSelect(){
        OPENINGS.forEach(this::addItem);
        String opening = ConfigManager.getProperty("opening");
        if(opening != null){
            Optional<Opening> e = OPENINGS.stream().filter(opening1 -> opening1.name().equals(opening)).findFirst();
            e.ifPresent(value -> setSelectedIndex(OPENINGS.indexOf(value)));
        }
        addActionListener(new OpeningChangeListener(this));
    }

    private void addItem(Opening opening) {
        addItem(opening.toString());
    }

    public void select(Opening opening){
        setSelectedIndex(OPENINGS.indexOf(opening));
    }

    public Opening getSelectedOpening(){
        return OPENINGS.get(getSelectedIndex());
    }

    private class OpeningChangeListener implements ActionListener {

        private final OpeningSelect openingSelect;
        public OpeningChangeListener(OpeningSelect openingSelect){
            this.openingSelect = openingSelect;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            select(OPENINGS.get(getSelectedIndex()));
            ConfigManager.setProperty("opening", OPENINGS.get(openingSelect.getSelectedIndex()).name());
        }
    }


}
