package gui.components.select;

import config.ConfigManager;
import io.engine.Engine;
import io.engine.Opening;
import io.engine.Variant;

import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class VariantSelect extends JComboBox<String> {

    private final List<Variant> VARIANTS = List.of(Variant.values());

    private EngineSelect engineSelect;  // set this to FairyStockfish when selecting a variant other than normal Chess
    private OpeningSelect openingSelect;    //sets this to Engine when playing Fairy variants

    public VariantSelect(){
        VARIANTS.forEach(this::addItem);
        String variant = ConfigManager.getProperty("variant");
        if(variant != null){
            Optional<Variant> e = VARIANTS.stream().filter(variant1 -> variant1.name().equals(variant)).findFirst();
            e.ifPresent(value -> setSelectedIndex(VARIANTS.indexOf(value)));
        }
        addActionListener(new VariantChangeListener(this));
    }

    private void addItem(Variant variant) {
        addItem(variant.toString());
    }

    public void setEngineSelect(EngineSelect engineSelect){
        this.engineSelect = engineSelect;
    }

    public void setOpeningSelect(OpeningSelect openingSelect){
        this.openingSelect = openingSelect;
    }

    public void select(Variant variant){
        setSelectedIndex(VARIANTS.indexOf(variant));
    }

    public Variant getSelectedVariant(){
        return VARIANTS.get(getSelectedIndex());
    }

    private class VariantChangeListener implements ActionListener {

        private final VariantSelect variantSelect;
        public VariantChangeListener(VariantSelect variantSelect) {
            this.variantSelect = variantSelect;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ConfigManager.setProperty("variant", VARIANTS.get(variantSelect.getSelectedIndex()).name());
            if(getSelectedIndex() != VARIANTS.indexOf(Variant.Chess)){
                engineSelect.select(Engine.FairyStockfish);
                openingSelect.select(Opening.Engine);
                openingSelect.setEnabled(false);
            }
            else{
                openingSelect.setEnabled(true);
            }
        }
    }
}
