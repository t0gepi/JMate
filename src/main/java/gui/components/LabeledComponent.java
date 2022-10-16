package gui.components;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LabeledComponent<E extends JComponent> extends JPanel {

    private JLabel label;
    private E component;

    public enum LabelPosition {
        North,
        South,
        East,
        West
    }

    public LabeledComponent(String label, E component, LabelPosition position) {
        this.label = new JLabel(label);
        this.component = component;
        this.label.setAlignmentX(LEFT_ALIGNMENT);
        this.component.setAlignmentX(LEFT_ALIGNMENT);

        switch (position) {
            case North -> {
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                add(this.label);
                add(component);

            }
            case South -> {
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                add(component);
                add(this.label);
            }
            case East -> {
                setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                add(component);
                add(this.label);
            }
            case West -> {
                setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                add(this.label);
                add(component);
            }
        }
    }

    public JLabel getLabel() {
        return label;
    }

    public E getComponent() {
        return component;
    }

}
