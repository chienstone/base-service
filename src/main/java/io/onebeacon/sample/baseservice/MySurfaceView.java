package io.onebeacon.sample.baseservice;

import java.text.NumberFormat;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.onebeacon.sample.baseservice.CalculateArg.IndoorLoc;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable{  
	
	public  static 	String 			LOG 	= 	MySurfaceView.class.getSimpleName();
	private static  SurfaceHolder 	holder   =  	null	;
    private	static 	Bitmap 			people 				;
	private static 	Bitmap 			bitmap				;  
    public 	static 	Matrix 			people_matrix		    ;
	private static 	Matrix 			matrix				;   
    private static 	Matrix 			people_savedMatrix  	;
	private static 	Matrix 			savedMatrix			;  
	private static 	int 			Screen_Width	       	;
	private static 	int 			MAX_W	,	MIN_W	;//縮放限制
	private static 	int				tmp 	= 	0 		;//模擬用 累加器
	private static 	float			x	 	= 	5 		;//模擬用 累加器  
	private static 	float 			bitmapWidth			;
	private static 	float 			bitmapHeight		    ;
	
    private int 	DRAG 		= 1;  
    private int 	ZOOM 		= 2;  
    private int 	NONE 		= 3;   
    private int 	flagCount 	= 5;  
    private int 	action 		= NONE;  
    private int 	flag; 
    private Point	startPoint 	= new Point();  
    private Point 	currentPoint= new Point();  
    private Point 	midPoint 	= new Point();  
    private Point 	pointMid;  
    private static 	Paint paint = new Paint();
    
    private float  	scale 	  	= 1;  
    private float  	fontScale  	= 1;    
    private float  	oldDistance;
	private float  	newDistance;
    private static 	Point[]  	textPoint 	= new Point[Values.num]; //iBeacon座標位置 	
    private static  double[] 	oldPeople_distance = {0.0,0.0};  //舊的人位置
    private static 	boolean  	first;
    //private static IndoorLoc	People_Loc =  new CalculateArg.IndoorLoc(0,0,0);
    //private static Node[] iBeacon = new Node[4];//還沒寫好
    
    public MySurfaceView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        
        Initial(context);
        
        holder = getHolder();  
        holder.addCallback(this);  
    }  
    
    private void Initial(Context context) {
		// TODO Auto-generated method stub
    	for(int i=0;i<Values.num;i++)
        {
        	int id = getResources().getIdentifier(   
                    "node" + i,   
                "drawable", context.getPackageName()); 
        	Values.node[i] 				= BitmapFactory.decodeResource(getResources(), id);
        	Values.node_matrix[i] 		= new Matrix();  
        	Values.node_savedMatrix[i] 	= new Matrix();
        	Values.node_matrix[i].setTranslate(0f, 0f); 
        	Values.node_savedMatrix[i].setTranslate(0f, 0f); 
        	
        	/*
        	iBeacon[i] = new Node(BitmapFactory.decodeResource(getResources(), id),new Matrix(),new Matrix());
        	iBeacon[i].getNodeMatrix().setTranslate(0f, 0f);
        	iBeacon[i].getNodesavedMatrix().setTranslate(0f, 0f);
        	Log.e("i="+i,"iBeacon[i]="+iBeacon[i].getNode());
        	*/
        }
    	   
        people 				= BitmapFactory.decodeResource(getResources(), R.drawable.people);
        matrix 				= new Matrix();      
        savedMatrix 		    = new Matrix();
        people_matrix 		= new Matrix();      
        people_savedMatrix 	= new Matrix(); 
        matrix.setTranslate(0f, 0f);
        savedMatrix.setTranslate(0f, 0f);   
        people_matrix.setTranslate(0f, 0f);
        people_savedMatrix.setTranslate(0f, 0f);
        
        first = true;
		
	}

	public static void bm(Bitmap bm){
    	bitmap 		 = 	bm;
    	bitmapWidth  = 	bitmap.getWidth(); 
        bitmapHeight = 	bitmap.getHeight(); 
        Screen_Width = 	bitmap.getWidth();
        MAX_W 		 = 	bitmap.getWidth() * 3;
        MIN_W 		 = 	bitmap.getWidth();
    }   

    //TODO 初始化  
    @Override  
    public void run() {       
        //初始iBeacon位置 *模擬的
        textPoint[0] 	= new Point(0,0);
        textPoint[1] 	= new Point(0,1050);
        textPoint[2] 	= new Point(650,1050);
        textPoint[3]	= new Point(137,425);
        matrix.setTranslate(0f, 0f);
       
        //模擬 people_matrix.setTranslate(275, 546);
        
        Log.e("bitmapWidth="+bitmapWidth,"bitmapHeight"+bitmapHeight);
        for(int i=0;i<Values.num;i++)
        {
        	Values.node_matrix[i].setTranslate(textPoint[i].x,textPoint[i].y); 
        	//iBeacon[i].getNodeMatrix().setTranslate(textPoint[i].x+100,textPoint[i].y);
        }
   
        Canvas_Update();
     
    }  
    
    //TODO 即時更新
    public static int realtime() {
    
        double[] newPeople_distance = new double[2];
        newPeople_distance = Intersect();
        //two = Intersect(1,2);
        Log.e("newPeople_distance="+newPeople_distance[0],"newPeople_distance[1]"+newPeople_distance[1]);
        Log.e("oldPeople_distance[0]="+oldPeople_distance[0],"oldPeople_distance[1]="+oldPeople_distance[1]);
        
        //新位置與舊位置差別只在比例尺之內,以及舊位置等於(0,0)
        if((Math.sqrt(Math.pow(oldPeople_distance[0]-newPeople_distance[0],2)+Math.pow(oldPeople_distance[1]-newPeople_distance[1],2))
           >  55
           || (oldPeople_distance[0] == 0 && oldPeople_distance[1] == 0))
           && newPeople_distance[0] != 0.0 && newPeople_distance[1] != 0.0)
        {
        	Log.e("差異過大","MOVE");
        	people_matrix.setTranslate((float)newPeople_distance[0],(float)newPeople_distance[1]);
    		Canvas_Update();
    		Matrix_Status();  	
    		oldPeople_distance = newPeople_distance;
        }
		
		float[] matrixValues = new float[9];     
     	people_matrix.getValues(matrixValues);   	
    	for(int i = 0 ;i<Values.num;i++)
        {
    		float[] nodeValues = new float[9];     
    		Values.node_matrix[i].getValues(nodeValues);
    		//Log.e("ID="+i+",兩點距離="+Math.sqrt(Math.pow(matrixValues[2]-nodeValues[2],2)+Math.pow(matrixValues[5]-nodeValues[5],2)),"Values.node_range[i]="+Values.node_range[i]);  		
    		//計算兩點直線距離,若小於node範圍
    		if(Math.sqrt(Math.pow(matrixValues[2]-nodeValues[2],2)+Math.pow(matrixValues[5]-nodeValues[5],2)) <= Values.node_range[i])
    		{		
    			return i;
    		}
        } 
    		/*
            if(one != null && two != null)
            {
            	//顯示三圓相交座標
                End : for(int i =0 ; i<4 ; i=i+2)
                {
                	for(int j = 0 ; j<4 ; j=j+2)
                	{
                		if(one[i] == two[j] && one[i+1] == two[j+1])
                    	{
                    		people_matrix.setTranslate((int)one[i], (int)one[i+1]);
                    		Canvas_Update();
                    		first = true;
                    		break End;
                    	} 		
                	}       	   	
                }
            	//找不到相同的..第二階段
            	if(!first)
            	{
            		//如果兩個數只差比例尺*10 就採用平均
                    End : for(int i =0 ; i<4 ; i=i+2)
                    {
                    	for(int j = 0 ; j<4 ; j=j+2)
                    	{
                    		if(Math.abs(one[i] - two[j])<Values.Scale_W*10 && Math.abs(one[i+1] - two[j+1]) < Values.Scale_W*10)
                        	{
                        		people_matrix.setTranslate((int)(one[i]+two[j])/2, (int)(one[i+1]+two[j+1])/2);
                        		Canvas_Update();
                        		first = true;
                        		break End;
                        	} 		
                    	}       	   	
                    }  		
            	}
            }  
    	}
    	if(first)
    	{
    		Matrix_Status();
    		/*
        	tmp+=1;
        	Log.e("tmpe="+tmp,"x="+x);
        	
        	if(tmp >=86)
        		people_matrix.postTranslate(0,0); //暫停
        	else if(tmp >=80)
        		people_matrix.postTranslate(x,0); //往右移動
        	else if(tmp>=53)
        		people_matrix.postTranslate(0,-x); //往上移動
        	else if(tmp >= 23)
        		people_matrix.postTranslate(-x,0); //往左移動
        	else
        		people_matrix.postTranslate(0,-x); //往上移動
       
        	Canvas_Update();
        	
        	float[] matrixValues = new float[9];     
         	people_matrix.getValues(matrixValues);
         	
        	for(int i = 0 ;i<Values.num;i++)
            {
        		float[] nodeValues = new float[9];     
        		Values.node_matrix[i].getValues(nodeValues);
        		Log.e("ID="+i+",兩點距離="+Math.sqrt(Math.pow(matrixValues[2]-nodeValues[2],2)+Math.pow(matrixValues[5]-nodeValues[5],2)),"Values.node_range[i]="+Values.node_range[i]);  		
        		//計算兩點直線距離,若小於node範圍
        		if(Math.sqrt(Math.pow(matrixValues[2]-nodeValues[2],2)+Math.pow(matrixValues[5]-nodeValues[5],2)) <= Values.node_range[i])
        		{		
        			return i;
        		}
            } 
    	}
    	*/
    	return Values.num;
	}

    //判斷手勢
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch(event.getAction() & MotionEvent.ACTION_MASK)    
        {    
            case MotionEvent.ACTION_DOWN:// 第一個Touch  
            {  
                startPoint.x = event.getX();  
                startPoint.y = event.getY();  
                flag =flagCount - 3;  
                action = DRAG;  
                //Log.e("startPoint.x"+startPoint.x,"startPoint.y"+startPoint.y);
                break;  
            }  
            case MotionEvent.ACTION_POINTER_DOWN:// 第二個Touch  
            {            
                  
                oldDistance = getDistance(event);// 計算第二個touch時，兩點之間距離    
                if(oldDistance>10f){  
                    action = ZOOM;  
                    savedMatrix.set(matrix); 
                    for(int i = 0 ;i<Values.num;i++)
                    {
                    	Values.node_savedMatrix[i].set(Values.node_matrix[i]);
                    }
                    midPoint = pointMid(event);  
                }                 
                break;  
            }  
            case MotionEvent.ACTION_UP:    
            {  
                action = NONE;            
                break;  
            }  
            case MotionEvent.ACTION_POINTER_UP:    
            {  
                System.out.println("ACTION_POINTER_UP");  
                flag = 0;  
                action = DRAG;                
                break;  
            }  
            case MotionEvent.ACTION_MOVE:                
            {        
                //System.out.println("move");  
                if(action == DRAG){  
                    onDrag(event);  
                }  
                flag++;  
                if(action == ZOOM){ 
                	newDistance = getDistance(event);
                    onZoom(event);  
                }         
            }     
        }  
        return true;
	}  
    
    //TODO 判斷移動  
    public void onDrag(MotionEvent event){  
    	 
        if(flag > flagCount){ 
        	
        	currentPoint.setXY(event.getX(), event.getY()); 
        	Matrix_Status(); 
            
            float[] matrixValues = new float[9];     
         	matrix.getValues(matrixValues); 
         	
         	//判斷出界 
        	if(matrixValues[2] < 6 && (matrixValues[2] + (currentPoint.x-startPoint.x)) < 6 && (bitmapWidth + matrixValues[2] + (currentPoint.x-startPoint.x)) > Screen_Width - 6
        			|| ((currentPoint.x-startPoint.x) < 0) && matrixValues[2] >= 0)
            {
        		matrix.postTranslate((float)(currentPoint.x-startPoint.x), (float)(currentPoint.y-startPoint.y));    
	            people_matrix.postTranslate((float)(currentPoint.x-startPoint.x), (float)(currentPoint.y-startPoint.y));    
	            for(int i = 0 ;i<Values.num;i++)
	            {
	            	Values.node_matrix[i].postTranslate((float)(currentPoint.x-startPoint.x),(float)(currentPoint.y-startPoint.y));
	            	textPoint[i].setXY(textPoint[i].x+currentPoint.x-startPoint.x, textPoint[i].y+currentPoint.y-startPoint.y);         
	            }
	
	            startPoint.setPoint(currentPoint); 
	            Canvas_Update(); 
            } 	
        }
    }  

	//TODO 判斷縮放
    public void onZoom(MotionEvent event){   
         newDistance = getDistance(event);   // 計算第二個touch時，兩指間的距離    
         if(newDistance>10f)
         {      
        	scale = (oldDistance+((newDistance-oldDistance)/2)) / oldDistance; 
            //判斷最大倍數與最小倍數
            if(bitmapWidth*scale <= MAX_W && bitmapWidth*scale >= MIN_W)
            {
            	Matrix_Status(); 
            	matrix.postScale(scale, scale, midPoint.x, midPoint.y);
            	people_matrix.postScale(scale, scale, midPoint.x, midPoint.y); 
                fontScale *=scale;  
                oldDistance = newDistance;
                paint.setTextSize(10*fontScale);
                for(int i=0;i<Values.num;i++)
                {
                	Values.node_matrix[i].postScale(scale, scale, midPoint.x, midPoint.y);
                	//縮放的中心點和textPoint的距離，縮放過程中保持不變  
                    float distance 	= getXYDistance(midPoint, textPoint[i]);  
                    float sina 		= (textPoint[i].y-midPoint.y)/distance;  
                    float cosa 		= (textPoint[i].x-midPoint.x)/distance;  
                    distance*=scale;  
                    textPoint[i].x = distance*cosa+midPoint.x;  
                    textPoint[i].y = distance*sina+midPoint.y; 
                    Values.node_range[i]*=scale;
                    Log.e("i="+i,"Values.node_range[i]="+Values.node_range[i]);
                }
                Canvas_Update();
                bitmapHeight *= scale; 
                bitmapWidth	 *= scale;
                x *=scale; 
            }             
         }                    
    }
      
    public float getXYDistance(Point p1,Point p2){  
        float x = (p1.x-p2.x) * (p1.x-p2.x);            
        float y = (p1.y-p2.y) * (p1.y-p2.y);  
        return (float) Math.sqrt(x+y);  
    }        
  
    //TODO 計算三點相交座標
    private static double[] Intersect() {
    	double[] intersect = {0,0,0,0};// x1=0 , y1=1 , x2=2 , vy2=3;
    	Double[] r={0.0,0.0,0.0};
    	
    	//第一次計算平均的位置
    	if(first)
    	{ 		
    		r[0] = (double) Values.iBeaconArg[0];
        	r[1] = (double) Values.iBeaconArg[1];
        	r[2] = (double) Values.iBeaconArg[2];
        	Log.e("first="+first,"r1="+r[0]+",r2="+r[1]+",r3="+r[2]);
        	first = false;
    	}
    	//接下來計算最新的位置
    	else
    	{
    		if(MyBeaconsMonitor.tmp == MyBeaconsMonitor.ARG -1)
    		{
            	for(int i=0;i<Values.num-1;i++)
            	{
            		float sum=0;
            		for(int j=0;j<MyBeaconsMonitor.ARG;j++)
            		{
            			sum += Values.iBeaconNew[i][j];
            		}
            		r[i] = (double) sum/MyBeaconsMonitor.ARG;
            	}
            	Log.e("first="+first,"r1="+r[0]+",r2="+r[1]+",r3="+r[2]);
    		}
    	}
    	if(r[0]!=0 && r[1]!=0 && r[2]!=0)
    	{
    		intersect[1] = (Math.pow(r[0], 2) - Math.pow(r[1], 2) + Math.pow(textPoint[1].y, 2)) / (2 * textPoint[1].y);
    		double f1 = (Math.pow(r[0], 2) - Math.pow(r[2], 2) + Math.pow(textPoint[2].x, 2) + Math.pow(textPoint[2].y, 2)) / (2 * textPoint[2].y);
    		double f2 = ((textPoint[2].x) / (textPoint[2].y)) * intersect[1];
    		intersect[0] = f1 - f2;
    	}
    	else
    	{
    		intersect[0] = 0;
    		intersect[1] = 0;
    	}
		
		return intersect;
    	/*
        if(textPoint[one].y!=textPoint[two].y)//兩圓圓心Y值不同時 
        {
        	//m= y=mx+k的x項系數、k= y=mx+k的k項常數、 a、b、c= x=(-b±√(b^2-4ac))/2a的係數 
        	double m=(textPoint[one].x-textPoint[two].x)/(textPoint[two].y-textPoint[one].y);
        	double k=(Math.pow(r1, 2)-Math.pow(r2, 2)+Math.pow(textPoint[two].x, 2)-Math.pow(textPoint[one].x, 2)+Math.pow(textPoint[two].y, 2)-Math.pow(textPoint[one].y, 2))/(2*(textPoint[two].y-textPoint[one].y));
        	double a=1+Math.pow(m,2);
        	double b=2*(k*m-textPoint[two].x-m*textPoint[two].y);
        	double c=Math.pow(textPoint[two].x,2)+Math.pow(textPoint[two].y,2)+Math.pow(k,2)-2*k*textPoint[two].y-Math.pow(r2,2);
        	if(b*b-4*a*c>=0)//有交點時 
            {
        		intersect[0]=Math.round(((-b)+Math.sqrt(b*b-4*a*c))/(2*a));//x=(-b-√(b^2-4ac))/2a
        		intersect[1]=Math.round(m*intersect[0]+k);//y=mx+k
                intersect[2]=Math.round(((-b)-Math.sqrt(b*b-4*a*c))/(2*a));//x=(-b-√(b^2-4ac))/2a
                intersect[3]=Math.round(m*intersect[2]+k);//y=mx+k
                if(b*b-4*a*c>0)//兩交點 
                	Log.e("(一點相交為x1,y1)="+intersect[0]+","+intersect[1],"兩點相交為x2,y2)="+intersect[2]+","+intersect[3]);
                else//一交點 
                	Log.e("(一點相交為x1,y1)="+intersect[0]+","+intersect[1],"OK");
                return intersect;
            }
            else//沒有交點時
            {    
            	System.out.println("No cross points.");
            	return null;
            }	
        }
        else if((textPoint[one].y==textPoint[two].y))//兩圓圓心Y值相同時
        {
        	//x1= 兩交點的x值、 a、b、c= x=(-b±√(b^2-4ac))/2a的係數
        	intersect[0]=-(Math.pow(textPoint[one].x,2)-Math.pow(textPoint[two].x,2)-Math.pow(r1,2)+Math.pow(r2,2))/(2*textPoint[two].x-2*textPoint[one].x);
            double a=1;
            double b=-2*textPoint[one].y;
            double c=Math.pow(intersect[0],2)+Math.pow(textPoint[one].x,2)-2*textPoint[one].x*intersect[0]+Math.pow(textPoint[one].y,2)-Math.pow(r1,2);

            if(b*b-4*a*c>=0)
            {
            	intersect[1]=Math.round(((-b)+Math.sqrt(b*b-4*a*c))/(2*a));//y=(-b-√(b^2-4ac))/2a
            	intersect[3]=Math.round(((-b)-Math.sqrt(b*b-4*a*c))/(2*a));//y=(-b-√(b^2-4ac))/2a
                if(b*b-4*a*c>0)//兩交點
                	Log.e("兩點相交為x1,y1)="+intersect[0]+","+intersect[1],"兩點相交為x2,y2)="+intersect[2]+","+intersect[3]);
                else//一交點
                	Log.e("一點相交為x1,y1)="+intersect[0]+","+intersect[1],"OK");
                return intersect;
            }
            else//沒有交點時
            	System.out.println("No cross points.");
            	return null;
        }
        else
        {
        	return null;
        }
        */
	}
    
    //Matrix狀態
    private static void Matrix_Status() {
		// TODO Auto-generated method stub
    	matrix.set(savedMatrix); 
    	people_matrix.set(people_savedMatrix);
        for(int i = 0 ;i<Values.num;i++)
        {
        	Values.node_matrix[i].set(Values.node_savedMatrix[i]);
        	//iBeacon[i].getNodeMatrix().set(iBeacon[i].getNodesavedMatrix());
        } 
	}
    
    //Canvas畫面更新
    @SuppressLint("ResourceAsColor")
	private static void Canvas_Update() {
		// TODO Auto-generated method stub
    	Canvas c=holder.lockCanvas();  
        c.drawColor(Color.WHITE);  
        savedMatrix.set(matrix);  
        people_savedMatrix.set(people_matrix); 
        c.drawBitmap(bitmap, matrix, paint);  
        c.drawBitmap(people, people_matrix, paint);
        for(int i=0;i<Values.num;i++)
        {
        	Values.node_savedMatrix[i].set(Values.node_matrix[i]);
        	c.drawBitmap(Values.node[i], Values.node_matrix[i], paint); 
        	
        	//iBeacon[i].getNodesavedMatrix().set(iBeacon[i].getNodeMatrix());
        	//c.drawBitmap(iBeacon[i].getNode(), iBeacon[i].getNodeMatrix(), paint); 
        	/* Node文字
        	c.drawText(Values.node_name[i], textPoint[i].x, textPoint[i].y, paint); 
        	*/ 
        }    
        
        holder.unlockCanvasAndPost(c);     
	}
    
    public void onNone(MotionEvent event){  
        System.out.println("NONE");  
    }     
      
    private float getDistance(MotionEvent event)    
    {    
    	float x = event.getX(0) - event.getX(1);    
    	float y = event.getY(0) - event.getY(1);    
    	return (float)Math.sqrt(x * x + y * y);
    }    
	private Point pointMid(MotionEvent event)    
	{    
	    pointMid 	= new Point();    
	    pointMid.x 	= event.getX(0) + event.getX(1);    
	    pointMid.y 	= event.getY(0) + event.getY(1);    
	    pointMid.setXY(pointMid.x /2f, pointMid.y /2f);    
	    return pointMid;    
	}    
  
	//點對象  
    class Point {  
        private float x;  
        private float y;  
        public Point() {  
            super();  
        }            
        public void setPoint(Point point){  
            this.x = point.x;  
            this.y = point.y;  
        }  
        public Point(float x, float y) {  
            super();  
            this.x = x;  
            this.y = y;  
        }   
        public void setXY(float xx,float yy){  
            x=xx;  
            y=yy;  
        }  
        public float getX() {  
            return x;  
        }  
        public void setX(float x) {  
            this.x = x;  
        }  
        public float getY() {  
            return y;  
        }  
        public void setY(float y) {  
            this.y = y;  
        }  
    }    
    
    @Override  
    public void surfaceCreated(SurfaceHolder holder) { 
        new Thread(this).start(); 
    }  
    @Override  
    public void surfaceChanged(SurfaceHolder holder, int format, int width,  
            int height) {     
    }  
    @Override  
    public void surfaceDestroyed(SurfaceHolder holder) {   

    }  
}  
