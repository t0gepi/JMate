package gui.panel;

import gui.components.LabeledComponent;
import gui.components.button.AboutButton;
import gui.components.button.HotkeyButton;
import gui.components.button.SwitchButton;
import gui.components.select.EngineSelect;
import gui.components.select.OpeningSelect;
import gui.components.select.VariantSelect;
import gui.frame.JMate;
import io.TextAreaAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
 * Settings Panel is divided in top and bottom Panel. Top Panel contains all Components to configure the bot.
 * Bottom Panel only contains the Text Area to display logs.
 */
public class SettingsPanel extends JPanel {
    private final static Logger LOGGER = LoggerFactory.getLogger(SettingsPanel.class);
    //----Reference to CardLayout Navigator----
    private final JMate jMate;

    // ---------------Components---------------
    private LabeledComponent<SwitchButton> autoplaySwitch;
    private LabeledComponent<SwitchButton> arrowsSwitch;
    private LabeledComponent<EngineSelect> engineSelect;
    private LabeledComponent<VariantSelect> variantSelect;
    private LabeledComponent<OpeningSelect> openingSelect;
    private LabeledComponent<JSlider> depthSlider;
    private JButton startButton;
    private HotkeyButton hotkeyButton;
    private AboutButton aboutButton;
    private JTextArea textArea;
    private JScrollPane scrollPane;

    // ---------------Top Panel---------------
    private JPanel topPanel;        // will contain Buttons, Slider, etc. to manage the bot
    private JPanel vBoxContainer;    // container for all the rows of components (BoxLayout Vertical)
    private JPanel flowRow1;        // container for the first row of components (FlowLayout)
    private JPanel flowRow2;        // container for the second row of components (FlowLayout)
    private JPanel flowRow3;        // container for the third row of components (FlowLayout)
    private JPanel flowRow4;        // container for the fourth row of components (FlowLayout)

    // ---------------Bottom Panel---------------
    private JPanel bottomPanel; // will contain the TextArea to display the logs


    public SettingsPanel(JMate jMate) {
        this.jMate = jMate;
        setLayout(new GridBagLayout());
        initComponents(new GridBagConstraints());
        setVisible(true);
    }

    private void initComponents(GridBagConstraints gbc){
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;

        initTopPanel();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(topPanel, gbc);

        initBottomPanel();
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(bottomPanel, gbc);
    }



    private void initTopPanel() {
        topPanel = new JPanel();
        topPanel.setBackground(new Color(61, 61, 63));
        topPanel.setLayout(new FlowLayout());

        // start new code

        // init components for topPanel
        engineSelect = new LabeledComponent<>("Engine", new EngineSelect(), LabeledComponent.LabelPosition.North);
        variantSelect = new LabeledComponent<>("Variant", new VariantSelect(), LabeledComponent.LabelPosition.North);
        openingSelect = new LabeledComponent<>("Opening", new OpeningSelect(), LabeledComponent.LabelPosition.North);

        depthSlider = new LabeledComponent<>("Depth", new JSlider(1, 21, 11), LabeledComponent.LabelPosition.North);
        depthSlider.getComponent().setForeground(new Color(0, 174, 255));
        startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            LOGGER.info("Start Button clicked");
            //TODO: start Engine etc.
        });

        autoplaySwitch = new LabeledComponent<>("  Autoplay", new SwitchButton(), LabeledComponent.LabelPosition.East);
        arrowsSwitch = new LabeledComponent<>("  Arrows          ", new SwitchButton(), LabeledComponent.LabelPosition.East);
        hotkeyButton = new HotkeyButton();

        aboutButton = new AboutButton(jMate);


        // create references between selects. (e.g. if variant changes to 960, engine should change to FairyStockfish)
        engineSelect.getComponent().setVariantsSelect(variantSelect.getComponent());
        engineSelect.getComponent().setOpeningSelect(openingSelect.getComponent());
        variantSelect.getComponent().setEngineSelect(engineSelect.getComponent());
        variantSelect.getComponent().setOpeningSelect(openingSelect.getComponent());


        // Styling
        vBoxContainer = new JPanel();
        vBoxContainer.setLayout(new BoxLayout(vBoxContainer, BoxLayout.Y_AXIS));

        flowRow1 = new JPanel();
        flowRow1.setLayout(new FlowLayout());
        flowRow1.add(engineSelect);
        flowRow1.add(variantSelect);
        flowRow1.add(openingSelect);
        flowRow1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        vBoxContainer.add(flowRow1);

        flowRow2 = new JPanel();
        flowRow2.setLayout(new FlowLayout());
        flowRow2.add(depthSlider);
        flowRow2.add(startButton);
        flowRow2.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        vBoxContainer.add(flowRow2);

        flowRow3 = new JPanel();
        flowRow3.setLayout(new FlowLayout());
        flowRow3.add(autoplaySwitch);
        flowRow3.add(arrowsSwitch);
        flowRow3.add(hotkeyButton);
        flowRow3.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        vBoxContainer.add(flowRow3);

        flowRow4 = new JPanel();
        flowRow4.setLayout(new FlowLayout(FlowLayout.RIGHT));
        flowRow4.add(aboutButton);
        flowRow4.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
        vBoxContainer.add(flowRow4);

        // add vBoxContainer to topPanel
        topPanel.add(vBoxContainer);
    }


    private void initBottomPanel() {
        bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.RED);

        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        //bottomPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        textArea = TextAreaAppender.fTextArea;
        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(jMate.getWidth(), 100));

        bottomPanel.add(scrollPane);
    }


}
