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
        previousIncrement = 0;
        height = r.nextInt((lowerBound-(ballRadius*5))-upperBound)+upperBound;
        this.randomizePaint();
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
            height = r.nextInt((lowerBound-(ballRadius*6))-upperBound)+upperBound;
        }
    }

    public void setY(int y){
        ballY = y;
    }

    public int getY(){
        return ballY;
    }

    public void randomizePaint(){
        int randNum = r.nextInt(3);

        if(randNum == 0 && (ballPaint.getColor() != Color.RED)){
            ballPaint.setColor(Color.RED);
        }else if(randNum == 1 && (ballPaint.getColor() != Color.BLUE)){
            ballPaint.setColor(Color.BLUE);
        }else if(randNum == 2 && (ballPaint.getColor() != Color.MAGENTA)){
            ballPaint.setColor(Color.MAGENTA);
        }else{
            this.randomizePaint();
        }
    }

    public Paint getBallPaint(){
        return ballPaint;
    }

    public double getSpeed(){
        return ballVelocity;
    }

    public void resetSpeed(double speed){
        ballVelocity = speed;
    }

    public void resetIncrementTracker(){
        previousIncrement = 0;
    }

    public void increaseSpeed(double increment){
        double tempIncrement = (increment-previousIncrement);
        tempIncrement = tempIncrement/6;
        ballVelocity += tempIncrement;
        previousIncrement = increment;
        Log.d("SPEED", "Current Speed: " + ballVelocity);
    }
}
