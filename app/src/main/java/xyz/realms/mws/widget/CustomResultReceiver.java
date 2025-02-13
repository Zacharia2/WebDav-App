package xyz.realms.mws.widget;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class CustomResultReceiver extends ResultReceiver {
    private Receiver mReceiverPrivate;

    public CustomResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        this.mReceiverPrivate = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (this.mReceiverPrivate != null) {
            this.mReceiverPrivate.onReceiveResult(resultCode, resultData);
        }
    }

    public interface Receiver {
        void onReceiveResult(int i, Bundle bundle);
    }
}
