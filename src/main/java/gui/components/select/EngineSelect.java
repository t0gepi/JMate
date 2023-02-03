package gui.components.select;

import config.ConfigManager;
import io.engine.CLI;
import io.engine.Engine;
import io.engine.Opening;
import io.engine.Variant;

import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;


public class EngineSelect extends JComboBox<String> {

    private final List<Engine> ENGINES = List.of(Engine.values());

    private VariantSelect variantsSelect;
    private OpeningSelect openingSelect;

    public EngineSelect(){
        ENGINES.forEach(this::addItem);
        String engine = ConfigManager.getProperty("engine");
        if(engine != null){
            Optional<Engine> e = ENGINES.stream().filter(engine1 -> engine1.name().equals(engine)).findFirst();
            e.ifPresent(value -> setSelectedIndex(ENGINES.indexOf(value)));
        }
        addActionListener(new EngineChangeListener(this));
    }

    private void addItem(Engine engine) {
        addItem(engine.toString());
    }

    public void setVariantsSelect(VariantSelect variantsSelect){
        this.variantsSelect = variantsSelect;
    }

    public void setOpeningSelect(OpeningSelect openingSelect){
        this.openingSelect = openingSelect;
    }

    public void select(Engine engine){
        setSelectedIndex(ENGINES.indexOf(engine));
    }

    public Engine getSelectedEngine(){
        return ENGINES.get(getSelectedIndex());
    }

    private class EngineChangeListener implements ActionListener{

        private final EngineSelect engineSelect;
        public EngineChangeListener(EngineSelect engineSelect) {
            this.engineSelect = engineSelect;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ConfigManager.setProperty("engine", ENGINES.get(engineSelect.getSelectedIndex()).name());
            if(getSelectedIndex() != ENGINES.indexOf(Engine.FairyStockfish)){
                variantsSelect.select(Variant.Chess);
                openingSelect.setEnabled(true);
            }
            else if(variantsSelect.getSelectedVariant() != Variant.Chess){
                openingSelect.select(Opening.Engine);
                openingSelect.setEnabled(false);
            }
        }
    }
}
