package org.galal.sqlrunner.services.verticles.messages;


import java.util.Map;

public record SqlFilePathMsg(String file, Map<String, String> params) {}
