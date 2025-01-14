package xyz.realms.mws.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Objects;

import xyz.realms.mws.MwsApp;
import xyz.realms.mws.R;
import xyz.realms.mws.corefunc.Helper;
import xyz.realms.mws.ui.MainActivity;

public class WebDavReceiver extends BroadcastReceiver {
    public static final String ChangeStatusAction = "xyz.realms.mws.widget.action.CHANGE_STATUS";
    public static final String UpdateStatusAction = "xyz.realms.mws.widget.action.UPDATE_STATUS";

    @Override
    public void onReceive(Context context, Intent intent) {
        onReceiveImplementation(context, intent, MainActivity.class, WebDavReceiver.UpdateStatusAction);
        onReceiveImplementation(context, intent, WebDavReceiver.UpdateStatusAction);

        int image = R.drawable.on;
        Helper server = WebdavService.getServer();
        if (Objects.equals(intent.getAction(), ChangeStatusAction)) {
            if (server != null) {
                image = R.drawable.off;
            }
            String sendAction = server != null ? "xyz.realms.mws.StopWebDavServerPro" : "xyz.realms.mws.StartWebDavServerPro";
            onWidgetClick(context, UpdateStatusAction, image, sendAction);
            return;
        }
        if (Objects.equals(intent.getAction(), UpdateStatusAction)) {
            if (server == null) {
                image = R.drawable.off;
            }
            onWidgetUpdate(context, image);
            boolean startedFromWidget = intent.getBooleanExtra("startedFromWidget", false);
            boolean serviceStartOk = intent.getBooleanExtra("serviceStartOk", true);
            if (server == null && startedFromWidget && !serviceStartOk) {
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("startFromWidgetError", true);
                context.startActivity(i);
            }
        }
    }

    public void onReceiveImplementation(Context context, Intent intent, Class<?> activity, String widgetUpdateAction) {
        try {
            Helper server = WebdavService.getServer();
            if (server == null) {
                boolean startedFromWidget = intent.getBooleanExtra("startedFromWidget", false);
                boolean result = Helper.StartService(MwsApp.getAppContext(), activity, null, widgetUpdateAction, startedFromWidget);
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
            Intent intentServer = new Intent(MwsApp.getAppContext(), WebdavService.class);
            Helper server = WebdavService.getServer();
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

    protected void onWidgetClick(Context context, String receiveActionUpdate, int image, String sendAction) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widgetutil);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, WebDavWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        PendingIntent pendingIntent = WebDavWidgetProvider.buildButtonPendingIntent(context, allWidgetIds, WebDavReceiver.ChangeStatusAction);
        remoteViews.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
        WebDavWidgetProvider.pushAllWidgetUpdate(context.getApplicationContext(), remoteViews, WebDavWidgetProvider.class);
        Intent intent = new Intent();
        intent.setAction(sendAction);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("startedFromWidget", true);
        context.sendBroadcast(intent);
    }

    protected void onWidgetUpdate(Context context, int image) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widgetutil);
        remoteViews.setImageViewResource(R.id.widget_image, image);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, WebDavWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        PendingIntent pendingIntent = WebDavWidgetProvider.buildButtonPendingIntent(context, allWidgetIds, WebDavReceiver.ChangeStatusAction);
        remoteViews.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
        WebDavWidgetProvider.pushAllWidgetUpdate(context.getApplicationContext(), remoteViews, WebDavWidgetProvider.class);
    }
}
