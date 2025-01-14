package xyz.realms.mws.corefunc;

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (externalDirs.length < 2 || externalDirs[1] == null) {
            return null;
        }
        try {
            if (!externalDirs[1].exists()) {
                externalDirs[1].mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return externalDirs[1].getPath();
    }
}
