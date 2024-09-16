package com.xinglan.webdavserver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xinglan.webdavserver.activities.AboutActivity;
import com.xinglan.webdavserver.activities.PrefsActivity;
import com.xinglan.webdavserver.activities.WebdavAdapter;
import com.xinglan.webdavserver.activities.WebdavServerApp;
import com.xinglan.webdavserver.corefunc.BerryUtil;
import com.xinglan.webdavserver.corefunc.Helper;
import com.xinglan.webdavserver.intent.WebdavService;
import com.xinglan.webdavserver.intent.WidgetWebDavReceiver;
import com.xinglan.webdavserver.utils.CustomResultReceiver;
import com.xinglan.webdavserver.utils.Net;
import com.xinglan.webdavserver.widget.viewflow.TitleFlowIndicator;
import com.xinglan.webdavserver.widget.viewflow.ViewFlow;

public class MainActivity extends AboutActivity implements CustomResultReceiver.Receiver {
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            WebdavService.WebdavBinder serviceBinder = (WebdavService.WebdavBinder) service;
            MainActivity.this.setViewsStarted(serviceBinder.configurationString);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            MainActivity.this.setViewsStopped();
        }
    };
    protected ViewFlow viewFlow = null;
    private CustomResultReceiver mReceiver;

    protected String getUpdateWidgetAction() {
        return WidgetWebDavReceiver.UpdateStatusAction;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultValues(false);
        Intent startIntent = getIntent();
        boolean startFromWidgetError = startIntent.getBooleanExtra("startFromWidgetError", false);
        if (startFromWidgetError) {
            Net.showAlert(this, R.string.ok, -1, R.string.app_name, R.string.notConnect, null, null);
        }
        setContentView(R.layout.title_layout);
        BerryUtil.init();
        LinearLayout bg = findViewById(R.id.titleParent);
        bg.getBackground().setDither(true);
        this.viewFlow = findViewById(R.id.view_flow);
        WebdavAdapter adapter = new WebdavAdapter(this, R.layout.main_common, R.layout.about);
        this.viewFlow.setAdapter(adapter);
        TitleFlowIndicator indicator = findViewById(R.id.view_flow_indic);
        indicator.setTitleProvider(adapter);
        this.viewFlow.setFlowIndicator(indicator);
        postOnCreate(savedInstanceState);
    }

    @Override // com.xinglan.webdavserver.webdavserverlib.AboutActivity
    public void postOnCreate(Bundle savedInstanceState) {
        super.postOnCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.mReceiver = new CustomResultReceiver(new Handler());
        this.mReceiver.setReceiver(this);
        if (!bindService(new Intent(this, WebdavService.class), this.mConnection, 0) || WebdavService.getServer() == null) {
            setViewsStopped();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService(this.mConnection);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mReceiver.setReceiver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mReceiver.setReceiver(null);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(1);
    }

    public void startStopClickHandler(View view) {
        String updateWidgetAction = getUpdateWidgetAction();
        if (WebdavService.getServer() == null) {
            startClickHandler(updateWidgetAction);
        } else {
            stopClickHandler(updateWidgetAction);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.viewFlow.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_preference) {
            Intent intent = new Intent(this, PrefsActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 82) {
            return super.onKeyDown(keyCode, event);
        }
        startActivityForResult(new Intent(this, PrefsActivity.class), 0);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == 2) {
                setDefaultValues(true);
            }
        }
    }

    @Override // CustomResultReceiver.Receiver
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == 0) {
            Net.showAlert(this, R.string.ok, -1, R.string.app_name, R.string.errorRunServer, null, null);
        }
    }

    protected void setDefaultValues(boolean readAgain) {
        if (readAgain) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().clear().apply();
        }
        PreferenceManager.setDefaultValues(this, R.xml.preference, readAgain);
    }

    public void startClickHandler(String updateWidgetAction) {
        try {
            Context context = WebdavServerApp.getAppContext();
            if (Helper.StartService(context, getClass(), this.mReceiver, updateWidgetAction, false)) {
                bindService(new Intent(context, WebdavService.class), this.mConnection, 0);
            } else {
                Net.showAlert(this, R.string.ok, -1, R.string.app_name, R.string.notConnect, null, null);
            }
        } catch (Exception e) {
            Net.showAlert(this, R.string.ok, -1, R.string.app_name, R.string.errorRunServer, null, null);
        }
    }

    public void stopClickHandler(String updateWidgetAction) {
        setViewsStopped();
        Intent intent = new Intent(this, WebdavService.class);
        stopService(intent);
        WebdavService.updateWidgets(getApplicationContext(), updateWidgetAction, false, true);
    }


    private void setViewsStarted(String connectString) {
        ImageView button = findViewById(R.id.imageView1);
        TextView textStartStop = findViewById(R.id.textView3);
        TextView text2 = findViewById(R.id.textView2);
        text2.setText(connectString);
        button.setImageResource(R.drawable.on);
        textStartStop.setText(R.string.str_stop);
        text2.setVisibility(View.VISIBLE);
    }

    private void setViewsStopped() {
        ImageView button = findViewById(R.id.imageView1);
        TextView textStartStop = findViewById(R.id.textView3);
        TextView text2 = findViewById(R.id.textView2);
        button.setImageResource(R.drawable.off);
        button.setScaleType(ImageView.ScaleType.FIT_XY);
        textStartStop.setText(R.string.str_start);
        text2.setVisibility(View.INVISIBLE);
    }
}
