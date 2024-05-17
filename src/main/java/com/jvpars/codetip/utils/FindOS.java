package com.jvpars.codetip.utils;

import java.io.File;

public class FindOS {

    private static final boolean osIsMacOsX;
    private static final boolean osIsWindows;
    private static final boolean osIsWindowsXP;
    private static final boolean osIsWindows2003;
    private static final boolean osIsWindowsVista;
    private static final boolean osIsLinux;

    static {
        String os = System.getProperty("os.name");
        if (os != null)
            os = os.toLowerCase();
        osIsMacOsX = "mac os x".equals(os);
        osIsWindows = os != null && os.indexOf("windows") != -1;
        osIsWindowsXP = "windows xp".equals(os);
        osIsWindows2003 = "windows 2003".equals(os);
        osIsWindowsVista = "windows vista".equals(os);
        osIsLinux = os != null && os.indexOf("linux") != -1;
    }

    public static boolean isMacOSX() {
        return osIsMacOsX;
    }

    public static boolean isWindows() {
        return osIsWindows;
    }

    public static boolean isWindowsXP() {
        return osIsWindowsXP;
    }

    public static boolean isWindows2003() {
        return osIsWindows2003;
    }

    public static boolean isWindowsVista() {
        return osIsWindowsVista;
    }

    public static boolean isLinux() {
        return osIsLinux;
    }


    public static String getFolderPath() {
        String path = System.getProperty("user.home");
        folderCreateIfNotExist(path + File.separator + ".codetip");
        return path + File.separator + ".codetip";

    }


    public static void folderCreateIfNotExist(String folderPath) {
        File theDir = new File(folderPath);

        if (!theDir.exists()) {
            //System.out.println("=====creating directory: " + folderPath);
            boolean result = false;
            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                //System.out.println("DIR created");
            }
        }
    }


}
