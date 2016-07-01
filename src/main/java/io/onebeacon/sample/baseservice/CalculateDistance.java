package io.onebeacon.sample.baseservice;

/**
 * Created by paulstone on 2016/7/1.
 */
public class CalculateDistance {

    private float distance;
    private double paraA;
    private double paraB;
    private double paraC;
    private double txPower = -51;
    private double ratio;

    public CalculateDistance (){
        this.paraA = Values.paraA;
        this.paraB = Values.paraB;
        this.paraC = Values.paraC;
    }

    public float getDistance (float rssi){
        if(rssi > -100 && rssi < 0) {
            ratio = rssi * 1.0 / txPower;
            distance = (float)(paraA * Math.pow(ratio, paraB) + paraC);
        }
        else {
            distance = 1;
        }
        return distance;
    }
}
