package io.onebeacon.sample.baseservice;
/*
 * 
 * �ȮɨS�Ψ�
 * 
 */

import java.util.HashMap;
import java.util.List;

import java.util.Collection;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

import java.util.Set;


import android.util.Log;

public class CalculateArg {

	public static final float MAX_X = 3.3f;
	public static final float MAX_Y = 4f;
	public final static String LOG = CalculateArg.class.getSimpleName();
	public final IndoorLoc in1 = new IndoorLoc(0, 0, 0); // Blue   DC:3B:A0:38:3B:33
	public IndoorLoc in2 = new IndoorLoc(0, 4.0, 0); 	 // Purple FA:D1:3D:2E:D0:AB
	public IndoorLoc in3 = new IndoorLoc(3.3, 2.0, 0);   // Green  C5:97:21:39:F8:78
	public static int num = 4;//�����X�ӭȫ�,�N���A�W�[�Ӥj�ΤӤp����

	public static class MyMap {

    	public Map<Float, Integer> map;	
    	public Float max = 0f;	
    	public Float min = 0f;
    	public int count = 0;
    	public Float avg = 0f;

    	public MyMap() {
    		map = new HashMap<Float, Integer>();
    	}
    	public void addValue(Float f) {
    		f *= Values.Scale_W*100; //�Z�� * ��Ҥ� * ���⦨���� 
    		count++;
	    	if ((max == 0f || f > max) && map.size() < num) {
	
	    		max = f;		
	    	}		
	    	if ((min == 0f || f < min) && map.size() < num) {	
	    		min = f;		
	    	}
	    	if( map.containsKey(f)){		
		    	int count = map.get(f);			
		    	count++;			
		    	map.put(f, count);	
	    	}	
	    	else{	
	    		map.put(f, 1);
	    	}
    	}
    	
    	public void addValueSecond(Float f) {
    		f *= Values.Scale_W*100; //�Z�� * ��Ҥ� * ���⦨���� 
    		count++;
	    	if ((max == 0f || f > max) && map.size() < num) {
	
	    		max = f;		
	    	}		
	    	if ((min == 0f || f < min) && map.size() < num) {	
	    		min = f;		
	    	}
	    	if( map.containsKey(f)){		
		    	int count = map.get(f);			
		    	count++;			
		    	map.put(f, count);	
	    	}	
	    	else{	
	    		map.put(f, 1);
	    	}
    	}
	}
	
	public static class IndoorLoc {

		public double x;
		public double y;
		public double z;

		public IndoorLoc(double _x, double _y, double _z) {
    		x = _x;
    		y = _y;
    		z = _z;
		}
	}
	
	public static Float cutMaxMinAverage(MyMap myMap) {
		int allCount = 0;
		float allSum = 0f;
		Set<Float> keys = myMap.map.keySet();
		
		for( Float key : keys){
    		if(key == myMap.max || key == myMap.min){
    			continue;
    		}
    		int count = myMap.map.get(key);
    		allSum += key*count;
    		allCount += count;
		}
		Log.i("myMap="+myMap,"myMap.max="+myMap.max+",myMap.min="+myMap.min);
		Log.i(LOG,"all Count = "+ allCount + ", key size = " + myMap.map.size());
		myMap.avg = allSum/allCount;
		return myMap.avg;
	}
	
	public static Float SD(MyMap myMap) {
		int allCount = 0;
		float allSum = 0f;
		Set<Float> keys = myMap.map.keySet();
		
		for( Float key : keys){
    		if(key == myMap.max || key == myMap.min){
    			continue;
    		}
    		allSum += (float) Math.pow(key - myMap.avg,2);
    		int count = myMap.map.get(key);
    		allCount += count;
		}
		Log.i(LOG,"SD = "+ Math.sqrt(allSum/allCount));
		float res = (float) Math.sqrt(allSum/allCount);
		return res;
	}


	public Double rollingAverage(List<Double> list) {
		Double acum = 0.0;
		for (Double d : list) {
			acum += d;
		}
		Double result = acum / list.size();
		return result;
	}


	public IndoorLoc trianLocArg(Map<Integer, Float> map) {
		IndoorLoc loc = new IndoorLoc(0, 0, 0);
		if (map.size() == 3) {
    		Float r1 = map.get(0);
    		Float r2 = map.get(1);
    		Float r3 = map.get(2);
    		Log.i(LOG,"r1 = " + r1 + ", r2 = " + r2 + ", r3 = " + r3);
    		loc.y = (Math.pow(r1, 2) - Math.pow(r2, 2) + Math.pow(in2.y, 2)) / (2 * in2.y);
    		double f1 = (Math.pow(r1, 2) - Math.pow(r3, 2) + Math.pow(in3.x, 2) + Math.pow(in3.y, 2)) / (2 * in3.y);
    		double f2 = ((in3.x) / (in3.y)) * loc.y;
    		loc.x = f1 - f2;
		}
		return loc;
	}
}
