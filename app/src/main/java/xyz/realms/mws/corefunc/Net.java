package xyz.realms.mws.corefunc;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.apache.log4j.Priority;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

public class Net {
    public static final int ADDR_ALL = 255;
    public static final int ADDR_BLUETOOH_PAN = 16;
    public static final int ADDR_ETH = 2;
    public static final int ADDR_LOCAL = 8;
    public static final int ADDR_MOBILE = 4;
    public static final int ADDR_WIFI = 1;
    public static final int ADDR_WIFI_ALL = 32;

    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> getIpAddress(Context context, int mask) {
        ArrayList<String> retValue = new ArrayList<>();
        try {
            String wifiAddr = getWifiIpAddress(context);
            if (((mask & ADDR_WIFI) == ADDR_WIFI || (mask & ADDR_WIFI_ALL) == ADDR_WIFI_ALL) && wifiAddr != null) {
                retValue.add(wifiAddr);
            }
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                if (((mask & ADDR_ETH) == ADDR_ETH && intf.getDisplayName().startsWith("eth")) || (((mask & ADDR_MOBILE) == ADDR_MOBILE && intf.getDisplayName().startsWith("pdp")) || (((mask & ADDR_MOBILE) == ADDR_MOBILE && intf.getDisplayName().startsWith("rmnet")) || (((mask & ADDR_LOCAL) == ADDR_LOCAL && intf.getDisplayName().startsWith("lo")) || (((mask & ADDR_BLUETOOH_PAN) == ADDR_BLUETOOH_PAN && intf.getDisplayName().startsWith("bt-pan")) || (((mask & ADDR_WIFI_ALL) == ADDR_WIFI_ALL && intf.getDisplayName().startsWith("wl")) || mask == ADDR_ALL)))))) {
                    Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                    while (enumIpAddr.hasMoreElements()) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (inetAddress instanceof Inet4Address) {
                            String ipAddr = inetAddress.getHostAddress();
                            if (wifiAddr == null || !wifiAddr.equals(ipAddr)) {
                                retValue.add(ipAddr);
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return retValue;
    }

    public static String getEth0Address() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                if (intf.getDisplayName().startsWith("eth")) {
                    Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                    while (enumIpAddr.hasMoreElements()) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getWifiIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        if (ip == 0) {
            return null;
        }
        return String.format("%d.%d.%d.%d", Integer.valueOf(ip & ADDR_ALL), Integer.valueOf((ip >> ADDR_LOCAL) & ADDR_ALL), Integer.valueOf((ip >> 16) & ADDR_ALL), Integer.valueOf((ip >> 24) & ADDR_ALL));
    }

    public static String convertStreamToString(InputStream is) {
        if (is == null) {
            return "";
        }
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                while (true) {
                    int n = reader.read(buffer);
                    if (n == -1) {
                        break;
                    } else {
                        writer.write(buffer, 0, n);
                    }
                }
            } catch (UnsupportedEncodingException e2) {
                e2.printStackTrace();
                try {
                    is.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            } catch (IOException e4) {
                e4.printStackTrace();
                try {
                    is.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            return writer.toString();
        } finally {
            try {
                is.close();
            } catch (IOException e6) {
                e6.printStackTrace();
            }
        }
    }

    public static String openHTMLString(Context context, int id) {
        InputStream is = context.getResources().openRawResource(id);
        return convertStreamToString(is);
    }

    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder();
        Random generator = new Random();
        for (int i = 0; i < 6; i++) {
            int r = generator.nextInt("abcdefgijkmnopqrstwxyzABCDEFGHJKLMNPQRSTWXYZ123456789".length());
            password.append("abcdefgijkmnopqrstwxyzABCDEFGHJKLMNPQRSTWXYZ123456789".charAt(r));
        }
        return password.toString();
    }

    public static void showAlert(Context context, int ok, int cancel, int title, int message, DialogInterface.OnClickListener listener, DialogInterface.OnCancelListener listenerCancel) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(ok, listener != null ? listener : (dialog, which) -> dialog.dismiss());
        if (cancel != -1) {
            if (listener == null) {
                listener = (dialog, which) -> dialog.dismiss();
            }
            alertDialog.setNegativeButton(cancel, listener);
        }
        if (listenerCancel != null) {
            alertDialog.setOnCancelListener(listenerCancel);
        }
        alertDialog.show();
    }

    public static boolean isServiceRunning(Context context, String packageName, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Priority.OFF_INT);
        for (int i = 0; i < services.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = services.get(i);
            if (packageName.equals(serviceInfo.service.getPackageName()) && className.equals(serviceInfo.service.getClassName()) && services.get(i).started) {
                return true;
            }
        }
        return false;
    }
}
