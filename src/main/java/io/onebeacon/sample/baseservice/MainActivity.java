package io.onebeacon.sample.baseservice;

import android.app.AlertDialog;
import android.app.Dialog;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import io.onebeacon.api.OneBeacon;
import io.onebeacon.api.ScanStrategy;

public class MainActivity extends ActionBarActivity implements ServiceConnection {
   /* private static MainActivity sInstance;
    public static MainActivity getInstance(Context context) {
        if (sInstance == null) {
            //Always pass in the Application Context
            sInstance = new MainActivity(context.getApplicationContext());
        }

        return sInstance;
    }

    private Context mContext;

    private MainActivity(Context context) {
        mContext = context;
    }*/

    private MonitorService mService = null;

    protected MySurfaceView 			dragImageView;
    public static int 					window_width, window_height;
    public static Dialog 				Dialog,DialogWeb,DialogData;
    public static TextView[] 			distance = new TextView[4];
    protected TextView 				    node_a,node_b,node_c,node_d;
    protected WebView wb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Initial();
        /** 螢幕 寬、高 **/
        WindowManager manager = getWindowManager();
        window_width 	= manager.getDefaultDisplay().getWidth();
        window_height 	= manager.getDefaultDisplay().getHeight();
        dragImageView 	= (MySurfaceView) findViewById(R.id.div_main);

        //讓圖片 = 螢幕寬高
        Bitmap bmp = BitmapUtil.ReadBitmapById(this, R.drawable.lab,
                window_width, window_height);


        if (!bindService(new Intent(this, MonitorService.class), this, BIND_AUTO_CREATE)) {
            setTitle("Bind failed! Manifest?");
        }

        MySurfaceView.bm(bmp);
    }

    //TODO 宣告 模擬數據
    private void Initial() {
        node_a 			= (TextView)findViewById(R.id.node_a);
        node_b 			= (TextView)findViewById(R.id.node_b);
        node_c 			= (TextView)findViewById(R.id.node_c);
        node_d 			= (TextView)findViewById(R.id.node_d);
        distance[0] 	= (TextView)findViewById(R.id.distance_a);
        distance[1] 	= (TextView)findViewById(R.id.distance_b);
        distance[2] 	= (TextView)findViewById(R.id.distance_c);
        distance[3] 	= (TextView)findViewById(R.id.distance_d);
        node_a.setText(Values.node_name[0]);
        node_b.setText(Values.node_name[1]);
        node_c.setText(Values.node_name[2]);
        node_d.setText(Values.node_name[3]);
        //Dialog樣式
        DialogData 		= new Dialog(MainActivity.this,R.style.MyDialog);
        Dialog 			= new Dialog(MainActivity.this,R.style.MyDialog);
        DialogWeb 		= new Dialog(MainActivity.this,R.style.MyDialog);

    }

    @Override
    protected void onDestroy() {
        // Activity is gone, set scan mode to use lowest possible power usage
        OneBeacon.setScanStrategy(ScanStrategy.LOW_POWER);
        if (null != mService) {
            // optionally stop the service if running in background is not desired
//            stopService(new Intent(this, MonitorService.class));
            unbindService(this);
            mService = null;
        }
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = ((MonitorService.LocalServiceBinder) service).getService();
        setTitle("Service connected");

        if (!mService.mServiceStarted) {
            if (null == mService.mBeaconsMonitor) {
                // create and start a new beacons monitor, subclassing a few callbacks
                mService.mBeaconsMonitor = new MyBeaconsMonitor(this);
            }
            mService.mServiceStarted = true;
        }

        // make the service to stick around by actually starting it
        startService(new Intent(this, MonitorService.class));

        log("beacons count" + String.valueOf(mService.getBeacons().size()));
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
        setTitle("Service disconnected");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity is visible, scan with most reliable results
        OneBeacon.setScanStrategy(ScanStrategy.LOW_LATENCY);
    }

    @Override
    protected void onPause() {
        // Activity is not in foreground, make a trade-off between battery usage and scan latency
        OneBeacon.setScanStrategy(ScanStrategy.BALANCED);
        super.onPause();
    }

    private void log(String msg) {
        Log.d("MainActivity", msg);
    }





}