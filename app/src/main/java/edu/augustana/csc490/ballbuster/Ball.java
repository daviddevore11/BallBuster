package edu.augustana.csc490.ballbuster;


import android.graphics.Color;
import android.graphics.Paint;

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
        // creates the actual properties for each ball
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

    // method to return the radius of ball object
    public int getRadius(){
        return ballRadius;
    }

    // method to return ball's current x position
    public int getX(){
        return ballX;
    }

    // method that moves the ball in the correct y direction
    public void moveBall(){
        if(upwardMovement){
            ballY -= ballVelocity;
        }else{
            ballY += ballVelocity;
        }
        checkBallPosition();
    }

    // method will determine if the ball is too move up or down.  If the ball has reached the
    // lower bound then the method will randomize the ball's paint and height
    public void checkBallPosition(){
        if (ballY <= height){
            upwardMovement = false;
        }else if(ballY >= lowerBound){
            upwardMovement = true;
            this.randomizePaint();
            height = r.nextInt((lowerBound-(ballRadius*6))-upperBound)+upperBound;
        }
    }

    // method will set the y position based upon user input
    public void setY(int y){
        ballY = y;
    }

    // method will return the ball's current y position
    public int getY(){
        return ballY;
    }

    // method will randomize the paint of the current ball
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

    // method returns the ball's current paint
    public Paint getBallPaint(){
        return ballPaint;
    }

    // method returns the ball's current speed
    public double getSpeed(){
        return ballVelocity;
    }

    // method will reset the ball's speed, used when resetting the game
    public void resetSpeed(double speed){
        ballVelocity = speed;
    }

    // method will reset the previousIncrement variable which is used for increasing the speed of
    // the ball.  Used when resetting the game
    public void resetIncrementTracker(){
        previousIncrement = 0;
    }

    // method will slowly increase the ball's traveling speed.  The amount that the speed is
    // increased by is the difference between the current time and the previous time.  All of that
    // is divided by 6 which creates an increase that over 60 secs (the game length) fairly increase
    // the balls speed.
    public void increaseSpeed(double increment){
        double tempIncrement = (increment-previousIncrement);
        tempIncrement = tempIncrement/6;
        ballVelocity += tempIncrement;
        previousIncrement = increment;
    }
}
