package io.onebeacon.sample.baseservice;
/*
 * 
 * �٨S�g�n
 * 
 */
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;

public class Node {
	
	public static  Bitmap node;
	public static  Matrix node_matrix;  
	public static  Matrix node_savedMatrix; 
	public static  Point  p; 
	
	// �L�Ѽƫغc��k 
    public Node() { 
        this(null,null,null);
    }
	
    // ���Ѽƫغc��k 
    public Node(Bitmap node, Matrix node_matrix, Matrix node_savedMatrix) {  
        Node.node = node; 
        Node.node_matrix = node_matrix; 
        Node.node_savedMatrix = node_savedMatrix;
    }
    
    public Bitmap getNode() { 
        return node; 
    }

    public Matrix getNodeMatrix() { 
        return node_matrix; 
    }
    
    public Matrix getNodesavedMatrix() { 
        return node_savedMatrix; 
    }

    public void setNode(Bitmap node) {
        Node.node = node;
    }
    
    public void setNode_Matrix(Matrix node_matrix) {
        Node.node_matrix = node_matrix;
    }
    
    public void setNode_savedMatrix(Matrix node_savedMatrix) {
        Node.node_savedMatrix = node_savedMatrix;
    }
}
