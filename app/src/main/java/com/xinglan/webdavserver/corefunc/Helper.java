package com.xinglan.webdavserver.corefunc;

import android.content.Context;
import android.content.Intent;

import com.bradmcevoy.http.SecurityManager;
import com.ettrema.http.fs.NullSecurityManager;
import com.ettrema.http.fs.SimpleSecurityManager;
import com.xinglan.webdavserver.R;
import com.xinglan.webdavserver.intent.WebdavService;
import com.xinglan.webdavserver.intent.CustomResultReceiver;

import org.apache.commons.io.IOUtils;

import java.util.HashMap;
import java.util.List;

public class Helper {
    public static BerryUtil StartServer(Context context, Class<?> pThis) throws Exception {
        SecurityManager nullSecurityManager;
        BerryUtil server = null;
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
            server = new BerryUtil();
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

    public static BerryUtil StartServerOnly(String ip, int port, String homeDir, boolean passwordEnabled, String userName, String userPass) throws Exception {
        SecurityManager nullSecurityManager;
        BerryUtil server = new BerryUtil();
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
}
