package com.xinglan.webdavserver.intent;

import android.content.Context;
import android.content.Intent;

import com.xinglan.webdavserver.MainActivity;
import com.xinglan.webdavserver.R;
import com.xinglan.webdavserver.corefunc.BerryUtil;

import java.util.Objects;

public class WidgetWebDavReceiver extends WidgetUtilReceiver {
    public static final String ChangeStatusAction = "com.xinglan.webdavserver.widget.action.CHANGE_STATUS";
    public static final String UpdateStatusAction = "com.xinglan.webdavserver.widget.action.UPDATE_STATUS";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        int image = R.drawable.on;
        BerryUtil server = WebdavService.getServer();
        if (Objects.equals(intent.getAction(), ChangeStatusAction)) {
            if (server != null) {
                image = R.drawable.off;
            }
            String sendAction = server != null ? "com.xinglan.webdavserver.StopWebDavServerPro" : "com.xinglan.webdavserver.StartWebDavServerPro";
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
}
