package com.xinglan.webdavserver.intent;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.ResultReceiver;

import com.xinglan.webdavserver.R;
import com.xinglan.webdavserver.corefunc.BerryUtil;
import com.xinglan.webdavserver.corefunc.Helper;

public class WebdavService extends Service {
    public static final int SCREEN_DIM_WAKE_LOCK = 0;
    public static final int WIFI_MODE_FULL = 1;
    public static final int WIFI_MODE_FULL_HIGH_PERF = 2;
    private static final int NOTIFICATION_STARTED_ID = 1;
    private static BerryUtil server = null;
    protected IBinder mBinder;
    private PowerManager.WakeLock wakeLock;
    private NotificationManager notifyManager = null;
    private WifiManager.WifiLock wifiLock = null;

    public WebdavService() {
        this.mBinder = null;
        this.wakeLock = null;
        this.mBinder = createServiceBinder();
        this.wakeLock = null;
    }

    public static BerryUtil getServer() {
        return server;
    }

    public static class WebdavBinder extends Binder {
        public String configurationString;

        public WebdavBinder() {
            this.configurationString = null;
        }

        public BerryUtil getServer() {
            return WebdavService.server;
        }

        public void setServer(BerryUtil par) {
            WebdavService.server = par;
        }
    }

    public static void updateWidgets(Context context, String updateAction, boolean startedFromWidget, boolean startOk) {
        Intent intentUpdate = new Intent();
        intentUpdate.setAction(updateAction);
        intentUpdate.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intentUpdate.putExtra("startedFromWidget", startedFromWidget);
        intentUpdate.putExtra("serviceStartOk", startOk);
        context.sendBroadcast(intentUpdate);
    }

    protected IBinder createServiceBinder() {
        return new WebdavService.WebdavBinder();
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        this.notifyManager.cancel(1);
        this.notifyManager = null;
        if (this.wakeLock != null) {
            this.wakeLock.release();
            this.wakeLock = null;
        }
        if (this.wifiLock != null) {
            this.wifiLock.release();
            this.wifiLock = null;
        }
        if (server != null) {
            server.stopBerry();
            server = null;
        }
    }

    protected void ensureNotificationCancelled() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
    }

    protected void getScreenLock(String tag) {
        if (tag != null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            this.wakeLock = pm.newWakeLock(6, tag);
            this.wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        }
    }

    protected void getWifiLock(int lockType, String tag) {
        if (tag != null) {
            WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            this.wifiLock = wm.createWifiLock(lockType, tag);
            this.wifiLock.acquire();
        }
    }

    protected void handleStart(Intent intent, int flags, int startId, int notificationTextId, int notificationIconId, int notificationStartedTitleId, int notificationStartedTextId) {
        String tag = intent.getStringExtra("WakeLockTag");
        int lock = intent.getIntExtra("WakeLock", 0);
        boolean foreground = intent.getBooleanExtra("Foreground", false);
        String ipDetail = intent.getStringExtra("IpDetail");
        String className = intent.getStringExtra("className");
        switch (lock) {
            case 1:
                getWifiLock(1, tag);
                break;
            case 2:
                getWifiLock(3, tag);
                break;
            default:
                getScreenLock(tag);
                break;
        }
        showNotification(className, notificationTextId, notificationIconId, notificationStartedTitleId, notificationStartedTextId, ipDetail, foreground);
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    private void showNotification(String className, int notificationTextId, int notificationIconId, int notificationStartedTitleId, int notificationStartedTextId, String ipDetail, boolean startForeground) {
        Notification notification;
        Intent startIntent = new Intent();
        startIntent.setClassName(this, className);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0, startIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        notification = new Notification.BigTextStyle(new Notification.Builder(this).setTicker(getString(notificationTextId)).setContentTitle(getString(notificationStartedTitleId)).setContentText(ipDetail).setSmallIcon(notificationIconId).setContentIntent(intent)).bigText(ipDetail).build();
        notification.flags |= 34;
        if (startForeground) {
            startForeground(NOTIFICATION_STARTED_ID, notification);
        } else {
            this.notifyManager.notify(1, notification);
        }
    }

    @Override // android.app.Service
    public void onStart(Intent intent, int startId) {
        onStartCommand(intent, 0, startId);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        ((WebdavService.WebdavBinder) this.mBinder).configurationString = intent.getStringExtra("Data");
        String ip = intent.getStringExtra("sticky_ip");
        int port = intent.getIntExtra("sticky_port", 0);
        String homeDir = intent.getStringExtra("sticky_homeDir");
        boolean passwordEnabled = intent.getBooleanExtra("sticky_passwordEnabled", false);
        String userName = intent.getStringExtra("sticky_userName");
        String userPass = intent.getStringExtra("sticky_userPass");
        ResultReceiver receiver = intent.getParcelableExtra("sticky_resultReceiver");
        String widgetUpdateAction = intent.getStringExtra("widgetUpdateAction");
        boolean startedFromWidget = intent.getBooleanExtra("startedFromWidget", false);
        try {
            server = Helper.StartServerOnly(ip, port, homeDir, passwordEnabled, userName, userPass);
        } catch (Exception e) {
            server = null;
        }
        if (server == null) {
            ensureNotificationCancelled();
            stopSelf();
            if (receiver != null) {
                Bundle b = new Bundle();
                receiver.send(0, b);
            }
            if (widgetUpdateAction != null) {
                updateWidgets(this, widgetUpdateAction, startedFromWidget, false);
            }
            return Service.START_NOT_STICKY;
        }
        if (receiver != null) {
            Bundle b2 = new Bundle();
            receiver.send(1, b2);
        }
        if (widgetUpdateAction != null) {
            updateWidgets(this, widgetUpdateAction, startedFromWidget, true);
        }
        handleStart(intent, flags, startId, R.string.service_started, R.drawable.on, R.string.notification_started_title, R.string.notification_started_text);
        return Service.START_REDELIVER_INTENT;
    }
}
