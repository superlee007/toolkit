package com.lee.io;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author superlee
 */
public class ReadAndReplaceUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReadAndReplaceUtil.class);

    public static void main(String[] args) {
        File file = new File("D:\\idea_workspace\\gitlab\\iam-service");
        List<File> controllerFiles = new ArrayList<>();
        searchControllerFiles(file, controllerFiles);
        ReadAndReplaceUtil generate = new ReadAndReplaceUtil();
        generate.dealWithControllerFiles(controllerFiles);
    }

    private void dealWithControllerFiles(List<File> controllerFiles) {
        String search = "@Permission\\(.*\\)";
        for (File file : controllerFiles) {
            FileReader fileReader = null;
            BufferedReader bufferedReader = null;
            FileWriter fileWriter = null;
            StringBuilder sb = new StringBuilder();
            try {
                fileReader = new FileReader(file);
                fileWriter = new FileWriter(file);
                bufferedReader = new BufferedReader(fileReader);
                readAndReplace(search, bufferedReader, sb);
                fileWriter.write(sb.toString());
            } catch (FileNotFoundException e) {
                logger.info(e.getMessage());
            } catch (IOException e) {
                logger.info(e.getMessage());
            } finally {
                close(bufferedReader);
                close(fileWriter);
                close(fileReader);
            }
        }
    }

    private void close (Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        }
    }

    private void readAndReplace(String search, BufferedReader bufferedReader, StringBuilder sb) throws IOException {
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            String trimString = str.trim();
            if (Pattern.matches(search, trimString)) {
                String prefix = str.substring(0, str.indexOf('@'));
                sb.append(prefix);
                sb.append(trimString.substring(0, trimString.length() - 1));
                sb.append(", UUID = ");
                sb.append(UUID.randomUUID().toString());
                sb.append(")");
                str.replaceAll(str, sb.toString());
            } else {
                sb.append(str);
            }
            sb.append("\n");
        }
    }

    private static List<File> searchControllerFiles(File file, List<File> controllerFiles) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                searchControllerFiles(f, controllerFiles);
            }
        } else if (file.getName().endsWith("Controller.java")) {
            controllerFiles.add(file);
        }
        return controllerFiles;
    }
}
