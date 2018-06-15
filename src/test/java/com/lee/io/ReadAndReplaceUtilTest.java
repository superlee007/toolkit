package com.lee.io;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author superlee
 */
public class ReadAndReplaceUtilTest {
    @Test
    public void execute() throws Exception {
        String searchPattern = "@Permission\\(.*\\)";
        String fileSuffix = "Controller.txt";
        ReadAndReplaceUtil.execute(".", searchPattern, fileSuffix);
    }

}