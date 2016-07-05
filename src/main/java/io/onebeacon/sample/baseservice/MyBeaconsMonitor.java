package io.onebeacon.sample.baseservice;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import io.onebeacon.api.Beacon;
import io.onebeacon.api.BeaconsMonitor;
import io.onebeacon.api.Rangeable;
import io.onebeacon.api.spec.Apple_iBeacon;

/** Example subclass for a BeaconsMonitor **/
class MyBeaconsMonitor extends BeaconsMonitor {
    private static final String		TAG = MyBeaconsMonitor.class.getSimpleName();
    private Context myBeaconsMonitor;

    private CalculateDistance calculateDistance = new CalculateDistance();
    private CalculateArg.MyMap[] 	iBeacon 	= new CalculateArg.MyMap[4];//iBeacon距離
    private int 					FREQUENCY  	= Values.num;//多久結束調適
    private int 					RSSI 	   	= -100 	;//只接收大於多少RSSI的數值
    public  static  int 			ARG  		= 10	;//接收幾次值算一次定位
    public  static	int 			tmp			= 0		;//BeaconNew接收記次記錄
    private	boolean 				key			= false	;//確認是否所有beacon都偵測超過FREQUENCY的數量
    private	boolean 				start		= false	;
    public 	float[]					TmpNew 		= new float[3];
    private ProgressDialog 			pd;
    public  static  int[] 		    EachARG  	= new int[Values.num];

    public MyBeaconsMonitor(Context context) {
        super(context);
        myBeaconsMonitor = context;
        Initial();
    }

    @Override
    protected void onBeaconChangedRssi(Beacon beacon) {
        super.onBeaconChangedRssi(beacon);

        if (beacon.getType() == Beacon.Type.IBEACON) {
            //Apple_iBeacon xBeacon = (Apple_iBeacon) beacon;
            Collection<Beacon> onyxBeacons = filterBeacons(this.getBeacons());
            Log.e(TAG, "[0]=" + iBeacon[0].count + " [1]=" + iBeacon[1].count + " [2]=" + iBeacon[2].count);
            //log(String.format("Beacon %s Rssi changed to %s", xBeacon.getMinor(), beacon.getAverageRssi()));

            mainProcess(onyxBeacons);
        }
    }

    @Override
    protected void onBeaconAdded(Beacon beacon) {
        super.onBeaconAdded(beacon);
        if (beacon.getType() == Beacon.Type.IBEACON) {
            // example usage for an iBeacon
            Apple_iBeacon iBeacon = (Apple_iBeacon) beacon;
            //UUID uuid = iBeacon.getUUID();
            //int maj = iBeacon.getMajor();
            int min = iBeacon.getMinor();

            for(int i = 0 ;i<Values.num;i++) {
                if(iBeacon.getPrettyAddress().equals(Values.iBeaconMAC[i]))
                    Values.nodeInRange[i] = true;

                //EachARG[i] = 0;
            }

            Beacon_Identify(min);
        }

        // see Beacon.Type.* for more types, and io.onebeacon.api.spec.* for beacon type interfaces
    }

    //TODO 宣告 模擬數據
    private void Initial() {
        //Dialog樣式
        MainActivity.DialogData 		= new Dialog(myBeaconsMonitor,R.style.MyDialog);
        MainActivity.Dialog     		= new Dialog(myBeaconsMonitor,R.style.MyDialog);
        MainActivity.DialogWeb 		= new Dialog(myBeaconsMonitor,R.style.MyDialog);
        pd = new ProgressDialog(myBeaconsMonitor);
        //iBeacon_distance = new HashMap<Integer, Double>();
        iBeacon[0] = new CalculateArg.MyMap();
        iBeacon[1] = new CalculateArg.MyMap();
        iBeacon[2] = new CalculateArg.MyMap();
        iBeacon[3] = new CalculateArg.MyMap();

        for(int i = 0 ; i<TmpNew.length;i++) {
            TmpNew[i] = 0;
            Values.iBeaconRS2[i] = 0;
        }
    }

    //TODO 取得Beacon資訊
    private void mainProcess(Collection<Beacon> onyxBeacons) {
        if(key && onyxBeacons.size() >= Values.num - 1) {
            log("mainProcess:step_3");
            //如果偵測次數大於FREQUENCY,且偵測到三個beacon
            //計算所有Beacon距離的平均
            Beacon_Arg();
            Beacon_Gc();
            //計算所有Beacon距離的標準差 目前沒用
            //Beacon_SD();

            int num = MySurfaceView.realtime();
            Data_Show();
            //抵達beacon觸發
            //TODO 需添加強訊號無條件驅動
            if(num != Values.num) {
                //如果沒有顯示webview，在跳Dilaog
                if(!MainActivity.DialogWeb.isShowing()) {
                    if(!MainActivity.Dialog.isShowing())
                        Dialog_Show(num);
                }
            }
            else
                MainActivity.Dialog.cancel();

        }
        else if (start) { //check data size
            log("mainProcess:step_2");
            //確認是否所有beacon都偵測超過FREQUENCY的數量
            key = Check_Beacon_Num();
            //Log.e("key="+key,"tmp="+tmp);

            if(!key) {
                if(!pd.isShowing())
                    log("調適");
                pd = ProgressDialog.show(myBeaconsMonitor, "調適", "調適中，請稍後……");
            }
            else
                pd.dismiss();// 关闭ProgressDialog

        }
        else if(!start && onyxBeacons.size()>=3 && !MainActivity.Dialog.isShowing()) { //check iBeacon count
            log("mainProcess:step_1");
            DialogAdj_Show();
        }
    }

    // checkout the other available callbacks in the BeaconsManager base class
    //TODO 取得Beacon資訊
    private HashSet<Beacon> filterBeacons(Collection<Beacon> beacons) {
        HashSet<Beacon> filteredBeacons = new HashSet<Beacon>(beacons.size());
        //log("filteredBeacons trigger");

        if(beacons.size()>=3)
        {
            for (Beacon beacon : beacons) {
                if (beacon.getType() == Beacon.Type.IBEACON) {
                    Apple_iBeacon xBeacon = (Apple_iBeacon) beacon;

                    if (xBeacon.getUUID().toString().equalsIgnoreCase(Values.ONYX_BEACON_PROXIMITY_UUID)) {
                        filteredBeacons.add(xBeacon);
                        //System.out.println("MAC: %s " +  xBeacon.getPrettyAddress());

                        for (int i = 0; i < Values.num; i++) {
                            //log("MAC:" + xBeacon.getPrettyAddress());
                            //配對偵測到的數據是屬於哪個Beacon
                            if (xBeacon.getPrettyAddress().equals(Values.iBeaconMAC[i])) {
                                float Rssi = xBeacon.getAverageRssi();

                                //RSSI強度是否大於門檻值
                                if ( Rssi >= RSSI) {
                                   //float Distance = xBeacon.getEstimatedDistance();
                                 float distance = calculateDistance.getDistance(Rssi);
                                 //float calculateDis = calculateDistance.getDistance(Rssi);
                                 //log("Distance[" + xBeacon.getMinor() + "]-" + calculateDis);
                                 log("Distance[" + xBeacon.getMinor() + "]-" + distance);
                                 //Values.iBeaconMeter[i] = Distance;

                                    /*
                                    //測RSSI值
                                    if (EachARG[i] < 100) {
                                        Values.iBeaconRS[i][EachARG[i]] = Rssi;
                                        EachARG[i]++;
                                        //Log.d("EachARG",String.valueOf(EachARG[i]));
                                    } else {
                                        for (int j = 9; j < 80; j++) {
                                            Arrays.sort(Values.iBeaconRS[i]);
                                            Values.iBeaconRS2[i] += Values.iBeaconRS[i][j];
                                        }
                                        Values.iBeaconRS2[i] = Values.iBeaconRS2[i] / 80;
                                        Log.d("RS2", String.valueOf("1 [" + Values.iBeaconRS2[0] + "]    2[" + Values.iBeaconRS2[1] + "]    3[" + Values.iBeaconRS2[2] + "]"));
                                    }*/

                                    //第一層 距離小於地圖最大長度才增加
                                    if (distance * 100 < Math.sqrt(Math.pow(Values.Map_W, 2) + Math.pow(Values.Map_H, 2))) {
                                        //儲存所有數據
                                        //log("Distance=" + String.valueOf(distance) +"iBeacon["+ i +"]," + "MAC:" + Values.iBeaconMAC[i]);
                                        iBeacon[i].addValue(distance);

                                        //紀錄距離資訊
                                        if (key) {
                                            TmpNew[i] = distance * Values.Scale_W * 100;
                                            Check_Beacon_Tmp();
                                        }
                                    }//第一層 距離小於地圖最大長度才增加 End
                                    else {
                                        log("abnormal distance");
                                        break;
                                    }
                                }
                            }
                        }//for End
                    }
                }
            }
        }//beacons.size()>=3 End

        return filteredBeacons;
    }

    //TODO 確認Beacon獲得的數據大於FREQUENCY
    private boolean Check_Beacon_Num() {
        boolean key = true;

        //暫時-1 因為只有三顆beacon
        for(int i = 0; i<Values.num - 1; i++) {
            if(iBeacon[i].count < FREQUENCY)
                key = false;
        }
        log("Check_Beacon_Num:" + key);
        return key;
    }

    //TODO 確認是否所有Beacon都有接收到值,若都收到值就放入iBeaconNew
    private void Check_Beacon_Tmp() {
        boolean key = true;
        //暫時-1 因為只有三顆beacon
        for(int i = 0 ; i<Values.num - 1 ; i++) {
            if(TmpNew[i] == 0) {
                key = false;
                break;
            }
        }

        if(key) {
            for(int i = 0 ; i<Values.num - 1 ; i++) {
                log("TmpNew[" + i + "]"+ TmpNew[i]);
                Values.iBeaconNew[i][tmp] = TmpNew[i];
                TmpNew[i] = 0;
            }

            //矩陣參數循環操作
            if(tmp >= ARG - 1) tmp = 0;
            else tmp++;
        }
    }

    //TODO 計算平均數據
    private void Beacon_Arg() {
        for(int i = 0 ;i<Values.num - 1;i++)
        {
            if(iBeacon[i].count % 10 == 0)
                Values.iBeaconArg[i] = CalculateArg.cutMaxMinAverage(iBeacon[i]);
            //iBeaconArg單位是像度距離,log顯示為meter
            Log.e(TAG,"iBeaconArg["+i+"] ="+((Values.iBeaconArg[i]) / 100 / Values.Scale_W) );
        }
    }

    //清理累積數據
    private void Beacon_Gc() {
        for(int i = 0 ;i<Values.num - 1;i++) {
            if(CalculateArg.dataAmount(iBeacon[i])) {
                iBeacon[i].map.clear();
            }
            //Log.e(TAG,"iBeaconArg["+i+"] ="+((Values.iBeaconArg[i]) / 100) );
        }
    }

    private void Beacon_Identify(int min) {
        int beaconnum = 0;
        switch (min){
            case 283:
                beaconnum = 1;
                break;
            case 595:
                beaconnum = 2;
                break;
            case 600:
                beaconnum = 3;
                break;
            case 578:
                beaconnum = 4;
                break;
            case 11664:
                beaconnum = 5;
                break;
            case 11722:
                beaconnum = 6;
                break;
            default:
                break;
        }
        log(String.format("%d new iBeacon found:%d", min, beaconnum));
    }

    //右上角模擬數據
    private void Data_Show() {

        //取到小數點第一位
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);

        float[] matrixValues = new float[9];
        MySurfaceView.people_matrix.getValues(matrixValues);
        for(int i = 0 ;i<Values.num-1;i++)
        {
            float[] nodeValues = new float[9];
            Values.node_matrix[i].getValues(nodeValues);
            //計算直線距離
            //MainActivity.distance[i].setText(String.valueOf( nf.format( (Math.sqrt(Math.pow(matrixValues[2] - nodeValues[2], 2) + Math.pow(matrixValues[5] - nodeValues[5], 2))) * (Values.Scale_W)/100 )));
            MainActivity.distance[i].setText(String.valueOf(nf.format(Values.iBeaconArg[i] / 100 / Values.Scale_W)));
            //Log.e("ID="+i,"兩點距離="+distance[i].getText());

        }
        MainActivity.distance[3].setText(String.valueOf(nf.format(MySurfaceView.oldPeople_distance[0]) + "," + nf.format(MySurfaceView.oldPeople_distance[1])));

    }

    //TODO 是否進行調適
    public void DialogAdj_Show()
    {
        log("dialog_show_1");
        MainActivity. Dialog.setContentView(R.layout.dialog_adj);//指定自定義layout
        MainActivity.Dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = MainActivity.Dialog.getWindow();
        //顯示的初始位置為左上角,默認為畫面中間
        dialogWindow.setGravity(Gravity.CENTER | Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = Values.window_width/2; // 寬度
        lp.height = Values.window_height/2; // 高度
        //新增自定義按鈕點擊監聽
        Button btn = (Button)MainActivity.Dialog.findViewById(R.id.dialog_button_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                log("dialog_show_2");
                MainActivity.Dialog.dismiss();
                start = true;
            }
        });
        //顯示dialog
        MainActivity.Dialog.show();
        log("dialog_show_3");
    };

    //TODO 進入iBeacon範圍
    public void Dialog_Show(final int num)
    {
        MainActivity.Dialog.setContentView(R.layout.dialog);//指定自定義layout
        MainActivity.Dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = MainActivity.Dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        //顯示的初始位置為左上角,默認為畫面中間
        dialogWindow.setGravity(Gravity.TOP | Gravity.LEFT);
        float[] nodeValues = new float[9];
        Values.node_matrix[num].getValues(nodeValues);
        lp.x = (int) nodeValues[2]; // 新位置X坐標
        lp.y = (int) nodeValues[5]; // 新位置Y坐標
        //lp.width = 100; // 寬度
        //lp.height = 100; // 高度
        lp.alpha = 0.7f; // 透明度
        //新增自定義按鈕點擊監聽
        Button btn = (Button)MainActivity.Dialog.findViewById(R.id.dialog_button_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //WebView_Show(num);
                log("Dialog_Show");
            }
        });
        //顯示dialog
        MainActivity.Dialog.show();
    };

    /*//TODO 顯示廠商資訊
    @SuppressLint("SetJavaScriptEnabled")
    private void WebView_Show(int num)
    {
        Dialog.cancel();
        DialogWeb.setContentView(R.layout.webview);//指定自定義layout
        Window dialogWindow = DialogWeb.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.TOP | Gravity.LEFT);
        lp.width = window_width; // 寬度
        lp.height = window_height-40; // 高度
        lp.y = 0;
        wb = (WebView)DialogWeb.findViewById(R.id.myview);
        WebSettings websettings = wb.getSettings();
        // 支援JavaScript
        websettings.setJavaScriptEnabled(true);
        // 支援Zoom
        websettings.setSupportZoom(true);
        websettings.setBuiltInZoomControls(true);
        websettings.setAllowFileAccess(true);
        //websettings.setPluginsEnabled(true);
        websettings.setJavaScriptCanOpenWindowsAutomatically(true);
        websettings.setPluginState(WebSettings.PluginState.ON);
        wb.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        wb.setWebViewClient(new WebViewClient());
        wb.setWebChromeClient(m_chromeClient);

        wb.loadUrl("file:///android_asset/index"+num+".html");
        //wb.loadUrl("http://html5demos.com/video");
        //顯示dialog
        DialogWeb.show();

    }*/

    private WebChromeClient m_chromeClient = new WebChromeClient(){
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // TODO Auto-generated method stub
        }
    };

    private void log(String msg) {
        Log.d("MonitorService", msg);
    }
}