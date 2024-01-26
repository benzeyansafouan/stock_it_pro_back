package com.pedramero.sms.pmsms.config;

import org.slf4j.LoggerFactory;

public interface Logger {
    default org.slf4j.Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }
}
