package com.spacefactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceFactory {
    public static final Logger LOGGER = LoggerFactory.getLogger("spacefactory");

    public static void initialize() {
        LOGGER.info("Initializing...");
    }
}