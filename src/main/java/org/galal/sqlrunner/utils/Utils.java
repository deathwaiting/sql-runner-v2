package org.galal.sqlrunner.utils;

import org.jboss.logging.Logger;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class);

    public static Optional<Integer> safeIntParsing(String intString){
        try{
            return ofNullable(intString).map(Integer::parseInt);
        }catch(Throwable t){
            LOG.error(t);
            return empty();
        }
    }
}
