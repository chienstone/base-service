package io.onebeacon.sample.baseservice;

import android.graphics.Bitmap;
import android.graphics.Matrix;


public class Values {
	
	public static 	int 		num 			 = 4;//node數量
	public static   int 		window_width, window_height;
	public static	int			Map_W			 =1290;//真實地圖的寬(cm)
	public static	int			Map_H			 =1880;
    public static 	int 		Scale_W 			 = Map_W /  1000 ;//手機畫面與真實地圖的比例尺
    //public static 	int 		Scale_H 		 	 = 1880  /1880;
	public static  	Bitmap[] 	node 			 = new Bitmap[num];
	public static  	Matrix[] 	node_matrix 	 	 = new Matrix[num];  //iBeacon物件
	public static  	Matrix[] 	node_savedMatrix 	 = new Matrix[num];
	public static  	float[]		iBeaconArg 		 = new float[num];//計算node距離
	//public static  	float[]		iBeaconArgSecond  = new float[num];//還沒用到
	//public static  	float[]		iBeaconSd 		 = new float[num];//計算每個beacon距離標準差
	public static  	float[][]	iBeaconNew 		 = new float[num][MyBeaconsMonitor.ARG];
	public static	String[]	iBeaconMAC		 = {"20:C3:8F:D5:02:42","20:C3:8F:D5:2D:90","20:C3:8F:D5:2D:CA","20:91:48:35:94:B7","98:7B:F3:57:E7:A9","98:7B:F3:57:E9:36"};
	//public static	int[]		iBeaconMinor		 = {283,595,600,578,11664,11722};
	public static   float[]		iBeaconRS2		= new float[num];

	//distance calculate parameter
    public static double        paraA           = 0.828684609;
    public static double        paraB           = 5.512875746;
    public static double        paraC           = 0.273255852;
            ;
	//iBeacon
	//public static final String 	EXTRAS_TARGET_ACTIVITY 			= "extrasTargetActivity";
	//public static final String 	EXTRAS_BEACON 		  			= "extrasBeacon";
	public static final int 	REQUEST_ENABLE_BT 				= 1000;
	public static final String 	ONYX_BEACON_PROXIMITY_UUID 	= "20cae8a0-a9cf-11e3-a5e2-0800200c9a66";
	public static boolean nodeInRange[] = new boolean[Values.num];

	//iBeacon名稱
	public static String[] node_name = new String[] {
		 "ADIDAS", "GNC", "iBeacon", "CHANEL"
	};
	
	//iBeacon範圍
	public static float[] node_range = new float[] {
		 100, 100, 50, 40
	};
	
	//iBeacon模擬距離
	/*public static double[] node_distance = new double[] {
		 358.201, 265.731, 71.281, 179.805
	};*/
}
