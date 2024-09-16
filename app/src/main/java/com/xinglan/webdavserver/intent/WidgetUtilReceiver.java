package com.xinglan.webdavserver.intent;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.xinglan.webdavserver.R;

public abstract class WidgetUtilReceiver extends BroadcastReceiver {
    /* JADX INFO: Access modifiers changed from: protected */
    public void onWidgetClick(Context context, Class<?> widgetProvider, String receiveAction, String receiveActionUpdate, int image, String sendAction) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widgetutil);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, widgetProvider);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        PendingIntent pendingIntent = WidgetUtilProvider.buildButtonPendingIntent(context, allWidgetIds, receiveAction);
        remoteViews.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
        WidgetUtilProvider.pushAllWidgetUpdate(context.getApplicationContext(), remoteViews, widgetProvider);
        Intent intent = new Intent();
        intent.setAction(sendAction);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("startedFromWidget", true);
        context.sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onWidgetUpdate(Context context, Class<?> widgetProvider, String receiveAction, int image) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widgetutil);
        remoteViews.setImageViewResource(R.id.widget_image, image);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, widgetProvider);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        PendingIntent pendingIntent = WidgetUtilProvider.buildButtonPendingIntent(context, allWidgetIds, receiveAction);
        remoteViews.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
        WidgetUtilProvider.pushAllWidgetUpdate(context.getApplicationContext(), remoteViews, widgetProvider);
    }
}
