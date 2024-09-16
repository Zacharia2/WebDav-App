package com.xinglan.webdavserver.utils;

import android.content.Context;

import java.io.File;

public class FileUtil {
    public static String GetSecondaryPrivateDirectory(Context context) {
        File[] externalDirs;
        if ((externalDirs = context.getExternalFilesDirs(null)) == null) {
            return null;
        }
        if (externalDirs.length >= 1 && externalDirs[0] != null) {
            try {
                if (!externalDirs[0].exists()) {
                    externalDirs[0].mkdirs();
                }
            } catch (Exception ignored) {
            }
        }
        if (externalDirs.length < 2 || externalDirs[1] == null) {
            return null;
        }
        try {
            if (!externalDirs[1].exists()) {
                externalDirs[1].mkdirs();
            }
        } catch (Exception ignored) {
        }
        return externalDirs[1].getPath();
    }
}
