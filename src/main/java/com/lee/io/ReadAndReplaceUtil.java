package com.lee.io;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 一个读取特定后缀名文件，然后按指定正则匹配字符串，然后替换的工具
 * @author superlee
 */
public class ReadAndReplaceUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReadAndReplaceUtil.class);

    public static void execute(String path, String searchPattern, String fileSuffix) {
        File file = new File(path);
        List<File> controllerFiles = new ArrayList<>();
        searchControllerFiles(file, controllerFiles, fileSuffix);
        dealWithControllerFiles(controllerFiles, searchPattern);
    }

    private static void dealWithControllerFiles(List<File> controllerFiles, String searchPattern) {
        for (File file : controllerFiles) {
            FileReader fileReader = null;
            BufferedReader bufferedReader = null;
            FileWriter fileWriter = null;
            StringBuilder sb = new StringBuilder();
            try {
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);
                readAndReplace(searchPattern, bufferedReader, sb);
                fileWriter = new FileWriter(file);
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

    private static void close (Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        }
    }

    private static void readAndReplace(String search, BufferedReader bufferedReader, StringBuilder sb) throws IOException {
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            String trimString = str.trim();
            if (Pattern.matches(search, trimString) && !trimString.contains("UUID")) {
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

    private static List<File> searchControllerFiles(File file, List<File> controllerFiles, String fileSuffix) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                searchControllerFiles(f, controllerFiles, fileSuffix);
            }
        } else if (file.getName().endsWith(fileSuffix)) {
            controllerFiles.add(file);
        }
        return controllerFiles;
    }
}
