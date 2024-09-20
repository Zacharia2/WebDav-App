package com.xinglan.webdavserver.corefunc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;

public class Prefs {
    public static final String DEFAULT_CUSTOMFOLDER = "/";
    public static final boolean DEFAULT_FOREGROUND = true;
    public static final String DEFAULT_HOMEDIR = "1";
    public static final String DEFAULT_INTERFACES = "0";
    public static final String DEFAULT_LOCK = "0";
    public static final boolean DEFAULT_PASSWORD = false;
    public static final String DEFAULT_PORT = "8080";
    public static final boolean DEFAULT_SHOWCREDENTIALS = true;
    public static final String DEFAULT_USERNAME = "webdav";
    public static final String DEFAULT_USERPASS = "webdav";
    public static final String PREF_CUSTOMFOLDER = "prefCustomFolder";
    public static final String PREF_FOREGROUND = "prefForeground";
    public static final String PREF_HOMEDIR = "prefHomeDir";
    public static final String PREF_INTERFACES = "prefInterfaces";
    public static final String PREF_LOCK = "prefLock";
    public static final String PREF_PASSWORD = "prefPassword";
    public static final String PREF_PORT = "prefPort";
    public static final String PREF_RESETPREFS = "prefResetPrefs";
    public static final String PREF_SHOWCREDENTIALS = "prefShowCredentials";
    public static final String PREF_USERNAME = "prefUsername";
    public static final String PREF_USERPASS = "prefUserpass";

    public static int getPort(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return Integer.parseInt(pref.getString(PREF_PORT, DEFAULT_PORT));
        } catch (Exception e) {
            return Integer.parseInt(DEFAULT_PORT);
        }
    }

    public static String getUserName(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getString(PREF_USERNAME, "webdav");
        } catch (Exception e) {
            return "webdav";
        }
    }

    public static String getUserPass(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getString(PREF_USERPASS, "webdav");
        } catch (Exception e) {
            return "webdav";
        }
    }

    public static String getHomeDir(Context context) {
        String ext;
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String prefHomeDir = pref.getString(PREF_HOMEDIR, DEFAULT_HOMEDIR);
            if (prefHomeDir.equals(DEFAULT_HOMEDIR)) {
                ext = String.valueOf(Environment.getExternalStorageDirectory());
            } else if (prefHomeDir.equals("0")) {
                ext = DEFAULT_CUSTOMFOLDER;
            } else if (prefHomeDir.equals("2")) {
                ext = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
            } else if (prefHomeDir.equals("3")) {
                ext = getCustomFolder(context);
            } else if (prefHomeDir.equals("4")) {
                ext = FileUtil.GetSecondaryPrivateDirectory(context);
                if (ext == null) {
                    ext = String.valueOf(Environment.getExternalStorageDirectory());
                }
            } else {
                ext = DEFAULT_CUSTOMFOLDER;
            }
            return ext;
        } catch (Exception e) {
            return DEFAULT_CUSTOMFOLDER;
        }
    }

    public static boolean isHomeDirCustomDir(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String prefHomeDir = pref.getString(PREF_HOMEDIR, DEFAULT_HOMEDIR);
            return prefHomeDir.equals("3");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isHomeDirCustomDir(Context context, int value) {
        return 3 == value;
    }

    public static boolean getPasswordEnabled(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getBoolean(PREF_PASSWORD, false);
        } catch (Exception e) {
            return false;
        }
    }

    public static int getInterfaces(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String prefInterfaces = pref.getString(PREF_INTERFACES, "0");
            if (prefInterfaces.equals("0")) {
                return 32;
            }
            if (prefInterfaces.equals(DEFAULT_HOMEDIR)) {
                return 2;
            }
            if (prefInterfaces.equals("2")) {
                return 4;
            }
            if (prefInterfaces.equals("3")) {
                return 8;
            }
            return prefInterfaces.equals("4") ? 16 : 32;
        } catch (Exception e) {
            return 32;
        }
    }

    public static int getLock(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String prefInterfaces = pref.getString(PREF_LOCK, "0");
            if (prefInterfaces.equals("0")) {
                return 0;
            }
            if (prefInterfaces.equals(DEFAULT_HOMEDIR)) {
                return 1;
            }
            return prefInterfaces.equals("2") ? 2 : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean getForeground(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getBoolean(PREF_FOREGROUND, true);
        } catch (Exception e) {
            return true;
        }
    }

    public static String getCustomFolder(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String folder = pref.getString(PREF_CUSTOMFOLDER, DEFAULT_CUSTOMFOLDER);
            File theDir = new File(folder);
            if (theDir.exists()) {
                if (theDir.isDirectory()) {
                    return folder;
                }
            }
            return DEFAULT_CUSTOMFOLDER;
        } catch (Exception e) {
            return DEFAULT_CUSTOMFOLDER;
        }
    }

    public static void setCustomFolder(Context context, String folder) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PREF_CUSTOMFOLDER, folder);
            editor.apply();
        } catch (Exception ignored) {
        }
    }

    public static boolean getShowCredentials(Context context) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref.getBoolean(PREF_SHOWCREDENTIALS, true);
        } catch (Exception e) {
            return true;
        }
    }
}
