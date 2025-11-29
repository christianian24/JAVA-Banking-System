package com.aureobank.util;

import org.slf4j.LoggerFactory;

public class LoggerSetup {
    public static void init() {
        // Placeholder: logback.xml in resources configures logging
        LoggerFactory.getLogger(LoggerSetup.class).info("Logging initialized");
    }
}
