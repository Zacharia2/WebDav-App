package xyz.realms.mws.corefunc;

import android.content.Context;
import android.content.Intent;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.milton.config.HttpManagerBuilder;
import io.milton.http.HttpManager;
import io.milton.http.SecurityManager;
import io.milton.http.fs.FileSystemResourceFactory;
import io.milton.http.fs.NullSecurityManager;
import io.milton.http.fs.SimpleSecurityManager;
import io.milton.simpleton.SimpletonServer;
import xyz.realms.mws.R;
import xyz.realms.mws.widget.CustomResultReceiver;
import xyz.realms.mws.widget.WebdavService;

public class Helper {
    private SimpletonServer simpletonServer = null;

    public static Helper StartServer(Context context, Class<?> pThis) throws Exception {
        SecurityManager nullSecurityManager;
        Helper server = null;
        List<String> addresses = Net.getIpAddress(context, Prefs.getInterfaces(context));
        if (!addresses.isEmpty()) {
            String address = addresses.get(0);
            int port = Prefs.getPort(context);
            String userName = Prefs.getUserName(context);
            String userPass = Prefs.getUserPass(context);
            String homeDir = Prefs.getHomeDir(context);
            boolean passwordEnabled = Prefs.getPasswordEnabled(context);
            int lockMode = Prefs.getLock(context);
            boolean foreground = Prefs.getForeground(context);
            boolean credentials = Prefs.getShowCredentials(context);
            server = new Helper();
            HashMap<String, String> nameAndPass = new HashMap<>();
            nameAndPass.put(userName, userPass);
            if (passwordEnabled) {
                nullSecurityManager = new SimpleSecurityManager("WebDAV Server", nameAndPass);
            } else {
                nullSecurityManager = new NullSecurityManager();
            }
            server.startBerry(port, homeDir, nullSecurityManager);
            String ipDetail = "http://" + address + ":" + port;
            String configurationString = context.getString(R.string.str_server_conf) + IOUtils.LINE_SEPARATOR_UNIX + "http://" + address + ":" + port + "\n\n";
            if (credentials) {
                String configurationString2 = configurationString + context.getString(R.string.conf_home_directory) + " " + homeDir + IOUtils.LINE_SEPARATOR_UNIX;
                if (passwordEnabled) {
                    configurationString = configurationString2 + context.getString(R.string.conf_username) + " " + userName + IOUtils.LINE_SEPARATOR_UNIX + context.getString(R.string.conf_userpass) + " " + userPass;
                } else {
                    configurationString = configurationString2 + context.getString(R.string.conf_password_disabled);
                }
            }
            Intent intent = new Intent(context, WebdavService.class);
            intent.putExtra("className", pThis.getName());
            intent.putExtra("Data", configurationString);
            intent.putExtra("WakeLock", lockMode);
            intent.putExtra("WakeLockTag", context.getString(R.string.app_name));
            intent.putExtra("Foreground", foreground);
            intent.putExtra("IpDetail", ipDetail);
            intent.putExtra("sticky_ip", address);
            intent.putExtra("sticky_port", port);
            intent.putExtra("sticky_homeDir", homeDir);
            intent.putExtra("sticky_passwordEnabled", passwordEnabled);
            intent.putExtra("sticky_userName", userName);
            intent.putExtra("sticky_userPass", userPass);
            context.startService(intent);
        }
        return server;
    }

    public static boolean StartService(Context context, Class<?> pThis, CustomResultReceiver receiver, String updateWidgetAction, boolean startedFromWidget) throws Exception {
        List<String> addresses = Net.getIpAddress(context, Prefs.getInterfaces(context));
        if (!addresses.isEmpty()) {
            String address = addresses.get(0);
            int port = Prefs.getPort(context);
            String userName = Prefs.getUserName(context);
            String userPass = Prefs.getUserPass(context);
            String homeDir = Prefs.getHomeDir(context);
            boolean passwordEnabled = Prefs.getPasswordEnabled(context);
            int lockMode = Prefs.getLock(context);
            boolean foreground = Prefs.getForeground(context);
            boolean credentials = Prefs.getShowCredentials(context);
            String ipDetail = "http://" + address + ":" + port;
            String configurationString = context.getString(R.string.str_server_conf) + IOUtils.LINE_SEPARATOR_UNIX + "http://" + address + ":" + port + "\n\n";
            if (credentials) {
                String configurationString2 = configurationString + context.getString(R.string.conf_home_directory) + " " + homeDir + IOUtils.LINE_SEPARATOR_UNIX;
                if (passwordEnabled) {
                    configurationString = configurationString2 + context.getString(R.string.conf_username) + " " + userName + IOUtils.LINE_SEPARATOR_UNIX + context.getString(R.string.conf_userpass) + " " + userPass;
                } else {
                    configurationString = configurationString2 + context.getString(R.string.conf_password_disabled);
                }
            }
            Intent intent = new Intent(context, WebdavService.class);
            intent.putExtra("className", pThis.getName());
            intent.putExtra("Data", configurationString);
            intent.putExtra("WakeLock", lockMode);
            intent.putExtra("WakeLockTag", context.getString(R.string.app_name));
            intent.putExtra("Foreground", foreground);
            intent.putExtra("IpDetail", ipDetail);
            intent.putExtra("sticky_ip", address);
            intent.putExtra("sticky_port", port);
            intent.putExtra("sticky_homeDir", homeDir);
            intent.putExtra("sticky_passwordEnabled", passwordEnabled);
            intent.putExtra("sticky_userName", userName);
            intent.putExtra("sticky_userPass", userPass);
            intent.putExtra("sticky_resultReceiver", receiver);
            intent.putExtra("widgetUpdateAction", updateWidgetAction);
            intent.putExtra("startedFromWidget", startedFromWidget);
            context.startService(intent);
            return true;
        }
        return false;
    }

    public static Helper StartServerOnly(String ip, int port, String homeDir, boolean passwordEnabled, String userName, String userPass) throws Exception {
        SecurityManager nullSecurityManager;
        Helper server = new Helper();
        HashMap<String, String> nameAndPass = new HashMap<>();
        nameAndPass.put(userName, userPass);
        if (passwordEnabled) {
            nullSecurityManager = new SimpleSecurityManager("WebDAV Server", nameAndPass);
        } else {
            nullSecurityManager = new NullSecurityManager();
        }
        if (!server.startBerry(port, homeDir, nullSecurityManager)) {
            return null;
        }
        return server;
    }

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

    public static void initBerry() {
//        不知道设置这个属性有啥用，设不设置都一样呀
        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
    }

    public boolean isStarted() {
        return this.simpletonServer != null;
    }

    public boolean startBerry(int port, String homeFolder, SecurityManager securityManager) {
        try {
            FileSystemResourceFactory resourceFactory = new FileSystemResourceFactory(new File(homeFolder), securityManager, Prefs.DEFAULT_CUSTOMFOLDER);
            resourceFactory.setAllowDirectoryBrowsing(true);
            HttpManagerBuilder b = new HttpManagerBuilder();
            b.setEnableFormAuth(false);
            b.setResourceFactory(resourceFactory);
            HttpManager httpManager = b.buildHttpManager();
            this.simpletonServer = new SimpletonServer(httpManager, b.getOuterWebdavResponseHandler(), 100, 10);
            this.simpletonServer.setHttpPort(port);
            this.simpletonServer.start();
            return true;
        } catch (Exception e) {
            this.simpletonServer = null;
            return false;
        }
    }

    public void stopBerry() {
        if (isStarted()) {
            this.simpletonServer.stop();
        }
        this.simpletonServer = null;
    }
}
