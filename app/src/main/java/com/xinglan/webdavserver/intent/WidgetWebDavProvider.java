package com.xinglan.webdavserver.intent;

import android.appwidget.AppWidgetManager;
import android.content.Context;

import com.xinglan.webdavserver.R;
import com.xinglan.webdavserver.corefunc.BerryUtil;

public class WidgetWebDavProvider extends WidgetUtilProvider {
    @Override // android.appwidget.AppWidgetProvider
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        BerryUtil server = WebdavService.getServer();
        int image = server != null ? R.drawable.on : R.drawable.off;
        onUpdateImplementation(context, appWidgetManager, appWidgetIds, WidgetWebDavProvider.class, image, WidgetWebDavReceiver.ChangeStatusAction);
    }
}
