package org.galal.sqlrunner.test;

import com.google.common.io.Resources;
import org.jboss.logging.Logger;

import java.io.IOException;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

public class TestUtils {
    private final static Logger LOG = Logger.getLogger(TestUtils.class);

    public static String readTestResourceAsString(String resourceRelativePath){
        try {
            var url = Resources.getResource(resourceRelativePath);
            return Resources.toString(url , UTF_8);
        } catch (IOException e) {
            LOG.error(e);
            throw new RuntimeException(format("Failed to read resource[%s]!", resourceRelativePath), e);
        }
    }
}
