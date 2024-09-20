package com.xinglan.webdavserver.intent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xinglan.webdavserver.MainActivity;
import com.xinglan.webdavserver.activity.WebdavServerApp;
import com.xinglan.webdavserver.corefunc.BerryUtil;
import com.xinglan.webdavserver.corefunc.Helper;

public class WebDavServerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        onReceiveImplementation(context, intent, MainActivity.class, WidgetWebDavReceiver.UpdateStatusAction);
        onReceiveImplementation(context, intent, WidgetWebDavReceiver.UpdateStatusAction);
    }

    public void onReceiveImplementation(Context context, Intent intent, Class<?> activity, String widgetUpdateAction) {
        try {
            BerryUtil server = WebdavService.getServer();
            if (server == null) {
                boolean startedFromWidget = intent.getBooleanExtra("startedFromWidget", false);
                boolean result = Helper.StartService(WebdavServerApp.getAppContext(), activity, null, widgetUpdateAction, startedFromWidget);
                if (isOrderedBroadcast()) {
                    setResultCode(result ? 0 : 3);
                }
                if (!result) {
                    WebdavService.updateWidgets(context, widgetUpdateAction, startedFromWidget, false);
                    return;
                }
                return;
            }
            if (isOrderedBroadcast()) {
                setResultCode(1);
            }
        } catch (Exception ex) {
            if (isOrderedBroadcast()) {
                setResult(2, ex.getMessage(), null);
            }
        }
    }

    public void onReceiveImplementation(Context context, Intent intent, String widgetUpdateAction) {
        try {
            Intent intentServer = new Intent(WebdavServerApp.getAppContext(), WebdavService.class);
            BerryUtil server = WebdavService.getServer();
            if (server != null) {
                if (server.isStarted()) {
                    server.stopBerry();
                }
                context.stopService(intentServer);
                WebdavService.updateWidgets(context, widgetUpdateAction, false, true);
                if (isOrderedBroadcast()) {
                    setResultCode(0);
                    return;
                }
                return;
            }
            if (isOrderedBroadcast()) {
                setResultCode(1);
            }
        } catch (Exception ex) {
            if (isOrderedBroadcast()) {
                setResult(2, ex.getMessage(), null);
            }
        }
    }


}
