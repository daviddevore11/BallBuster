package edu.augustana.csc490.ballbuster;


import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

public class Ball {

    private int ballX;
    private int ballY;
    private int ballRadius;
    private int upperBound;
    private int lowerBound;
    private int height;
    private boolean upwardMovement;
    private double ballVelocity;
    private double previousIncrement;
    private Paint ballPaint = new Paint();
    private Random r = new Random();

    public Ball(int x, int y, int radius, double velocity, int upper, int lower){
        upwardMovement = true;
        ballX = x;
        ballY = y;
        ballRadius = radius;
        ballVelocity = velocity;
        upperBound = upper;
        lowerBound = lower;
        previousIncrement = 10;
        height = r.nextInt((lowerBound-(ballRadius*5))-upperBound)+upperBound;
        this.randomizePaint();
        Log.d("SPEED", "Starting Speed: " + ballVelocity);
    }

    public int getRadius(){
        return ballRadius;
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
        if (ballY <= height){
            upwardMovement = false;
        }else if(ballY >= lowerBound){
            upwardMovement = true;
            this.randomizePaint();
            height = r.nextInt((lowerBound-(ballRadius*5))-upperBound)+upperBound;
        }
    }

    public void setY(int y){
        ballY = y;
    }

    public int getY(){
        return ballY;
    }

    public void randomizePaint(){
        int randNum = r.nextInt(3-0);

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

    public void resetSpeed(double speed){
        ballVelocity = speed;
    }

    public void resetIncrementTracker(){
        previousIncrement = 10;
    }

    public void increaseSpeed(double increment){
        if(increment >= 10){
            double tempIncrement = (increment-previousIncrement);
            Log.d("SPEED", "" + tempIncrement);
            Log.d("SPEED", "" + ballVelocity);
            ballVelocity += tempIncrement;
            previousIncrement = increment;
            Log.d("SPEED", "" + ballVelocity);
        }
    }
}
