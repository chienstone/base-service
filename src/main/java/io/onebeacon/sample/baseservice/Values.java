package io.onebeacon.sample.baseservice;

import android.graphics.Bitmap;
import android.graphics.Matrix;


public class Values {
	
	public static 	int 		num 			 = 4;//node數量
	public static 	int 		Scale_W 		 = (1290 * 25) /720;//手機畫面與真實地圖的比例尺
	public static 	int 		Scale_H 		 = (1880 * 25) /1020;
	public static	int			Map_W			 =1290;//真實地圖的寬
	public static	int			Map_H			 =1880;
	public static  	Bitmap[] 	node 			 = new Bitmap[num];
	public static  	Matrix[] 	node_matrix 	 = new Matrix[num];  //iBeacon物件
	public static  	Matrix[] 	node_savedMatrix = new Matrix[num]; 
	public static  	float[]		iBeaconArg 		 = new float[num];
	public static  	float[]		iBeaconMeter = new float[num];//還沒用到
	public static  	float[]		iBeaconArgSecond = new float[num];//還沒用到
	public static  	float[]		iBeaconSd 		 = new float[num];//計算每個beacon距離標準差
	public static  	float[][]	iBeaconNew 		 = new float[num][10 ];
	public static	String[]	iBeaconMAC		 = {"20:C3:8F:D5:02:42","20:C3:8F:D5:2D:90","20:C3:8F:D5:2D:CA",null};
	//iBeacon
	public static final String 	EXTRAS_TARGET_ACTIVITY 			= "extrasTargetActivity";
	public static final String 	EXTRAS_BEACON 		  			= "extrasBeacon";
	public static final int 	REQUEST_ENABLE_BT 				= 1000;
	public static final String 	ONYX_BEACON_PROXIMITY_UUID 	= "20cae8a0-a9cf-11e3-a5e2-0800200c9a66";
	public static boolean nodeInRange[] = new boolean[Values.num];
	//public static final String 	ESTIMOTE_IOS_PROXIMITY_UUID 	= "8492E75F-4FD6-469D-B132-043FE94921D8";
	//public static final Region 	ALL_ESTIMOTE_BEACONS_REGION 	= new Region("rid", null, null, null);
	//public static BeaconManager beaconManager;

	//iBeacon名稱
	public static String[] node_name = new String[] {
		 "ADIDAS", "GNC", "iBeacon", "CHANEL"
	};
	
	//iBeacon範圍
	public static float[] node_range = new float[] {
		 100, 100, 50, 40
	};
	
	//iBeacon模擬距離
	public static double[] node_distance = new double[] {
		 358.201, 265.731, 71.281, 179.805
	};
	
	
}
