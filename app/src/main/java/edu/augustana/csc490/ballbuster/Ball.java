package edu.augustana.csc490.ballbuster;


import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class Ball {

    private int ballX;
    private int ballY;
    private int ballRadius;
    private boolean upwardMovement;
    private int ballVelocity;
    private Paint ballPaint = new Paint();
    private Random r = new Random();

    public Ball(int x, int y, int radius, int velocity){
        upwardMovement = true;
        ballX = x;
        ballY = y;
        ballRadius = radius;
        ballVelocity = velocity;
        this.randomizePaint();
    }

    public int getRadius(){
        return ballRadius;
    }

    public void setX(int x){
        ballX = x;
    }

    public int getX(){
        return ballX;
    }

    public void moveBall(){
        if(upwardMovement){
            ballY -= ballVelocity;
        }else{
            ballY += ballVelocity;
        }
        checkBallPosition();
    }

    public void checkBallPosition(){

    }

    public void switchDirection(){
        if(upwardMovement){
            upwardMovement = false;
        }else{
            upwardMovement = true;
        }
    }

    public void setY(int y){
        ballY = y;
    }

    public int getY(){
        return ballY;
    }

    public void randomizePaint(){
        int randNum = r.nextInt(3-0)+0;

        if(randNum == 1){
            ballPaint.setColor(Color.RED);
        }else if(randNum == 2){
            ballPaint.setColor(Color.BLUE);
        }else{
            ballPaint.setColor(Color.MAGENTA);
        }
    }

    public Paint getBallPaint(){
        return ballPaint;
    }
}
