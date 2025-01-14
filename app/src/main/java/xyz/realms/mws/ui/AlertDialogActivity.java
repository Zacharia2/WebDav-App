package xyz.realms.mws.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import xyz.realms.mws.corefunc.Net;

public class AlertDialogActivity extends Activity {
    public int cancel;
    public DialogInterface.OnClickListener listener;
    public DialogInterface.OnCancelListener listenerCancel;
    public int message;
    public int ok;
    public int title;

    public AlertDialogActivity(int parOk, int parCancel, int parTitle, int parMessage, DialogInterface.OnClickListener parListener, DialogInterface.OnCancelListener parListenerCancel) {
        this.ok = parOk;
        this.cancel = parCancel;
        this.title = parTitle;
        this.message = parMessage;
        this.listener = parListener;
        this.listenerCancel = parListenerCancel;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Net.showAlert(this, this.ok, this.cancel, this.title, this.message, this.listener, this.listenerCancel);
    }
}
