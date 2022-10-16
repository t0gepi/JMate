package gui.components.select;

import io.engine.CLI;
import io.engine.Engine;
import io.engine.Opening;
import io.engine.Variant;

import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

;

public class EngineSelect extends JComboBox<String> {

    private final List<Engine> ENGINES = List.of(Engine.values());

    private VariantSelect variantsSelect;
    private OpeningSelect openingSelect;

    public EngineSelect(){
        ENGINES.forEach(this::addItem);
        addActionListener(new EngineChangeListener());
        CLI.setEngine(ENGINES.get(getSelectedIndex()));
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
        //TODO: store selected engine in config
    }

    private class EngineChangeListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
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
