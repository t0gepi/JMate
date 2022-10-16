package gui.components.select;

import io.engine.Opening;

import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class OpeningSelect extends JComboBox<String> {
    private final List<Opening> OPENINGS = List.of(Opening.values());

    public OpeningSelect(){
        OPENINGS.forEach(this::addItem);
        addActionListener(new OpeningChangeListener());
    }

    private void addItem(Opening opening) {
        addItem(opening.toString());
    }

    public void select(Opening opening){
        setSelectedIndex(OPENINGS.indexOf(opening));
        //TODO: store selected opening in config
    }

    private class OpeningChangeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            select(OPENINGS.get(getSelectedIndex()));
        }
    }


}
