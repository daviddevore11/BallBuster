package edu.augustana.csc490.ballbuster;


import android.graphics.Paint;

public class Ball {

    private int ballX;
    private int ballY;
    private boolean upwardMovement;
    private Paint ballPaint = new Paint();

    public Ball(int x, int y){
        upwardMovement = true;
        ballX = x;
        ballY = y;
    }

    public void setX(int x){
        ballX = x;
    }

    public int getX(){
        return ballX;
    }

    public void setY(int y){

    }

    public int getY(){
        return ballY;
    }

    public void randomizePaint(){

    }

    public Paint getBallPaint(){
        return ballPaint;
    }
}
