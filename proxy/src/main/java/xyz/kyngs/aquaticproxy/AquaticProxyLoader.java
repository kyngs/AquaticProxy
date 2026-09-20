package xyz.kyngs.aquaticproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AquaticProxyLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticProxyLoader.class);

    public static void main(String[] args) throws IOException {
        LOGGER.info("Starting Aquatic Proxy...");

        new AquaticProxy();
    }
}
