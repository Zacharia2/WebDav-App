package xyz.realms.mws.intent;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import xyz.realms.mws.R;
import xyz.realms.mws.corefunc.BerryUtil;

public class WidgetWebDavProvider extends AppWidgetProvider {
    public static PendingIntent buildButtonPendingIntent(Context context, int[] appWidgetIds, String sendAction) {
        Intent intent = new Intent();
        intent.setAction(sendAction);
        intent.putExtra("appWidgetIds", appWidgetIds);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews, int myWidget) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }

    public static void pushAllWidgetUpdate(Context context, RemoteViews remoteViews, Class<?> widgetProvider) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName myWidget = new ComponentName(context, widgetProvider);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(myWidget);
        appWidgetManager.updateAppWidget(allWidgetIds, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        BerryUtil server = WebdavService.getServer();
        int image = server != null ? R.drawable.on : R.drawable.off;
        onUpdateImplementation(context, appWidgetManager, appWidgetIds, WidgetWebDavProvider.class, image, WidgetWebDavReceiver.ChangeStatusAction);
    }

    public void onUpdateImplementation(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, Class<?> widgetProvider, int image, String sendAction) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widgetutil);
        PendingIntent pendingIntent = buildButtonPendingIntent(context, appWidgetIds, sendAction);
        remoteViews.setOnClickPendingIntent(R.id.widget_image, pendingIntent);
        remoteViews.setImageViewResource(R.id.widget_image, image);
        for (int widgetId : appWidgetIds) {
            pushWidgetUpdate(context, remoteViews, widgetId);
        }
    }
}
