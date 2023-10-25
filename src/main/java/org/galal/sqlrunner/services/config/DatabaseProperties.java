package org.galal.sqlrunner.services.config;


import org.eclipse.microprofile.config.inject.ConfigProperties;

@ConfigProperties(prefix = "org.galal.sql_runner.db")
public class DatabaseProperties {
    private String driver;
    private String protocol;
    private String password;
    private String username;
    private String host = null;
    private Integer port = null;
    private String database;
    private boolean sslMode = false;
}

