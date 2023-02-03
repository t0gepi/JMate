package io.logging;

import ch.qos.logback.core.PropertyDefinerBase;
import gui.frame.JMate;

public class ContentRootDirPropertyDefiner extends PropertyDefinerBase {
    @Override
    public String getPropertyValue() {
        return JMate.CONTENT_ROOT_DIR;
    }
}
