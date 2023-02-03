package io.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import javax.swing.JEditorPane;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EditorPaneAppender extends AppenderBase<ILoggingEvent> {

    private final static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    public final static JEditorPane editorPane;

    static{
        editorPane = new JEditorPane();
        editorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        editorPane.setEditable(false);
    }

    @Override
    protected void append(final ILoggingEvent e) {
        String txt = editorPane.getText();
        txt = txt.replace("<html>","")
                .replace("</html>","")
                .replace("<body>","")
                .replace("</body>","") // im too lazy to do it properly
                .replace("<head>","")
                .replace("</head>","")
                .replace("<p style=\"margin-top: 0\">","")
                .replace("</p>","");


        String colored_level_template = switch (e.getLevel().toInt()) {
            case 20000 -> //info
                    "<span style=\"color:2259E3FF\">%s</span>";
            case 30000 -> //warn
                    "<span style=\"color:orange\">%s</span>";
            case 40000 -> //error
                    "<span style=\"color:red\">%s</span>";
            default -> "";
        };

        editorPane.setText(txt + String.format("<span>[%s]</span> %s - %s"
                , sdf.format(new Date(e.getTimeStamp()))
                , String.format(colored_level_template, e.getLevel())
                , e.getFormattedMessage()) + "<br>");
    }

}
