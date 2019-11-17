package com.sandboxnu.annotator.activitylogger;

import java.io.FileInputStream;

public interface ActivityLogger {

    String saveLogFile();

    void nameLogFile(String name);

    String getLogFileName();

    void startLogFile();

    FileInputStream getLogFileByName(String name);
}
