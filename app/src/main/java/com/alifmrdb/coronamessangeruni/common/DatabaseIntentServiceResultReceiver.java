package com.alifmrdb.coronamessangeruni.common;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class DatabaseIntentServiceResultReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public DatabaseIntentServiceResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        //super.onReceiveResult(resultCode, resultData);
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
