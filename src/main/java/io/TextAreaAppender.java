package io;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import javax.swing.JTextArea;

public class TextAreaAppender extends AppenderBase<ILoggingEvent> {

    public final static JTextArea fTextArea = new JTextArea();


    @Override
    protected void append(final ILoggingEvent eventObject)
    {
        fTextArea.append(eventObject.getFormattedMessage());
    }
}
