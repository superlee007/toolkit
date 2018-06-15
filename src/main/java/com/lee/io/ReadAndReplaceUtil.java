package com.lee.io;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author superlee
 */
public class ReadAndReplaceUtil {

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
                bufferedReader = new BufferedReader(fileReader);
                readAndReplace(search, bufferedReader, sb);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeBufferedReader(bufferedReader);
                closeFileReader(fileReader);
            }
            try {
                fileWriter = new FileWriter(file);
                fileWriter.write(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeFileWriter(fileWriter);
            }
        }
    }

    private void closeFileWriter(FileWriter fileWriter) {
        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeFileReader(FileReader fileReader) {
        if (fileReader != null) {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeBufferedReader(BufferedReader bufferedReader) {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readAndReplace(String search, BufferedReader bufferedReader, StringBuilder sb) throws IOException {
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            String trimString = str.trim();
            if (Pattern.matches(search, trimString)) {
                String prefix = str.substring(0, str.indexOf("@"));
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