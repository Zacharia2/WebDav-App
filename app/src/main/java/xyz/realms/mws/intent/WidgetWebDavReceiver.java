package xyz.realms.mws.intent;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Objects;

import xyz.realms.mws.ui.MainActivity;
import xyz.realms.mws.R;
import xyz.realms.mws.corefunc.BerryUtil;

public class WidgetWebDavReceiver extends BroadcastReceiver {
    public static final String ChangeStatusAction = "xyz.realms.mws.widget.action.CHANGE_STATUS";
    public static final String UpdateStatusAction = "xyz.realms.mws.widget.action.UPDATE_STATUS";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        int image = R.drawable.on;
        BerryUtil server = WebdavService.getServer();
        if (Objects.equals(intent.getAction(), ChangeStatusAction)) {
            if (server != null) {
                image = R.drawable.off;
            }
            String sendAction = server != null ? "xyz.realms.mws.StopWebDavServerPro" : "xyz.realms.mws.StartWebDavServerPro";
            onWidgetClick(context, WidgetWebDavProvider.class, ChangeStatusAction, UpdateStatusAction, image, sendAction);
            return;
        }
        if (Objects.equals(intent.getAction(), UpdateStatusAction)) {
            if (server == null) {
                image = R.drawable.off;
            }
            onWidgetUpdate(context, WidgetWebDavProvider.class, ChangeStatusAction, image);
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

    protected void onWidgetClick(Context context, Class<?> widgetProvider, String receiveAction, String receiveActionUpdate, int image, String sendAction) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widgetutil);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, widgetProvider);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        PendingIntent pendingIntent = WidgetWebDavProvider.buildButtonPendingIntent(context, allWidgetIds, receiveAction);
        remoteViews.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
        WidgetWebDavProvider.pushAllWidgetUpdate(context.getApplicationContext(), remoteViews, widgetProvider);
        Intent intent = new Intent();
        intent.setAction(sendAction);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("startedFromWidget", true);
        context.sendBroadcast(intent);
    }

    protected void onWidgetUpdate(Context context, Class<?> widgetProvider, String receiveAction, int image) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widgetutil);
        remoteViews.setImageViewResource(R.id.widget_image, image);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, widgetProvider);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        PendingIntent pendingIntent = WidgetWebDavProvider.buildButtonPendingIntent(context, allWidgetIds, receiveAction);
        remoteViews.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
        WidgetWebDavProvider.pushAllWidgetUpdate(context.getApplicationContext(), remoteViews, widgetProvider);
    }
}
