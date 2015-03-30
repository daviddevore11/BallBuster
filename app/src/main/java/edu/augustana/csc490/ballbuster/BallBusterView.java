package edu.augustana.csc490.ballbuster;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import java.util.Random;

public class BallBusterView extends SurfaceView implements SurfaceHolder.Callback{

    private BallBusterThread ballBusterThread; // controls the game loop
    private Activity activity; // to display Game Over dialog in GUI thread
    private boolean dialogIsDisplayed = false;

    private Paint backgroundPaint = new Paint();
    private Paint curtainPaint = new Paint();
    private Paint colorIndicatorBackPaint = new Paint();
    private Paint colorIndicatorPaint = new Paint();
    private Paint textPaint = new Paint();

    private Ball ballOne;
    private Ball ballTwo;
    private Ball ballThree;

    private int ballSpeed;
    private int screenWidth;
    private int screenHeight;
    private int upperBound;
    private int lowerBound;
    private Random r = new Random();

    private int playerScore;
    private double timeLeft;
    private boolean upwardMovement;

    public BallBusterView(Context context, AttributeSet attrs){
        super(context, attrs);
        activity = (Activity) context; // store reference to MainActivity
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        ballSpeed = 5;
        lowerBound = screenHeight - (screenHeight/5) + (screenWidth/10) + ballSpeed;
        upperBound = screenHeight/4;

        // register SurfaceHolder.Callback listener
        getHolder().addCallback(this);

        // construct Paints for drawings
        backgroundPaint.setColor(Color.LTGRAY);
        curtainPaint.setColor(Color.DKGRAY);
        colorIndicatorBackPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(w/ 20);
        //ballOnePaint = chooseRandomColor(ballOnePaint);
        //ballTwoPaint = chooseRandomColor(ballTwoPaint);
        //ballThreePaint = chooseRandomColor(ballThreePaint);
        colorIndicatorPaint = chooseRandomColor(colorIndicatorPaint);

        // sets up ball parameters
        // parameters for creating a ball are (ball's X, ball's Y, ball's Radius)
        // All balls radius: screenWidth/10
        ballOne = new Ball((screenWidth/4) - screenWidth/10, (screenHeight - (screenHeight/5)) + screenWidth/10, screenWidth/10);
        ballTwo = new Ball(screenWidth/2,(screenHeight - (screenHeight/5)) + screenWidth/10, screenWidth/10);
        ballThree = new Ball((screenWidth - (screenWidth/4)) + screenWidth/10, (screenHeight - (screenHeight/5)) + screenWidth/10, screenWidth/10);

        newGame(); // set up and start a new game
    }

    // reset all the screen elements and start a new game
    public void newGame(){
        timeLeft = 60; // start the countdown at 60 seconds
        playerScore = 0; // sets player score
        upwardMovement = true;
    }

    public void updatePositions(double elapsedTimeMS){
        double interval = elapsedTimeMS / 1000.0;

        // moves ball up and down the screen
        ballOne.moveBall(speed);
        ballTwo.moveBall(speed);
        ballThree.moveBall(speed);

        // check for BallOne
        if (ballOne.getY() <= upperBound){
            ballOne.switchDirection();
        }else if(ballOne.getY() >= lowerBound){
            ballOne.switchDirection();
            ballOne.randomizePaint();
        }
        // check for ballTwo
        if (ballTwo.getY() <= upperBound){
            ballOne.switchDirection();
        }else if(ballTwo.getY() >= lowerBound){
            ballTwo.switchDirection();
            ballTwo.randomizePaint();
        }
        // check for ballThree
        if (ballThree.getY() <= upperBound){
            ballThree.switchDirection();
        }else if(ballThree.getY() >= lowerBound){
            ballThree.switchDirection();
            ballThree.randomizePaint();
        }

        // updates text on screen with timeLeft
        timeLeft = 60 - interval;
    }

    // picks a random color for the desired Paint Object
    public Paint chooseRandomColor(Paint paint){
        int randNum = r.nextInt(3-0)+0;
        if(randNum == 1){
             paint.setColor(Color.RED);
        }else if(randNum == 2){
            paint.setColor(Color.BLUE);
        }else{
            paint.setColor(Color.MAGENTA);
        }
        return paint;
    }

    public void drawGameBackground(Canvas canvas){
        // reset canvas
        canvas.drawRect(0, 0, screenWidth, screenHeight, backgroundPaint);

        // draws color indicator backing
        canvas.drawRect((screenWidth/2)-50, screenHeight/7, ((screenWidth/2)+50), screenHeight/20, colorIndicatorBackPaint);

        // display score
        canvas.drawText("Score: " + playerScore, 30, 50, textPaint);

        // display time
        canvas.drawText(getResources().getString(R.string.time_remaining_format, timeLeft), screenWidth-screenWidth/3, 50, textPaint);

        // draw color indicator
        canvas.drawRect((screenWidth/2)-40, (screenHeight/7)-10, ((screenWidth/2)+40), (screenHeight/20)+10, colorIndicatorPaint);

        // draws circles
        canvas.drawCircle(ballOne.getX(), ballOne.getY(), ballOne.getRadius(), ballOne.getBallPaint());
        canvas.drawCircle(ballTwo.getX(), ballTwo.getY(), ballTwo.getRadius(), ballTwo.getBallPaint());
        canvas.drawCircle(ballThree.getX(), ballThree.getY(), ballThree.getRadius(), ballThree.getBallPaint());

        // draws main curtain
        canvas.drawRect(0, screenHeight - (screenHeight/5), screenWidth, screenHeight, curtainPaint);
    }

    public void checkBallTap(MotionEvent e){
        if(((ballOne.getX() - ballOne.getRadius()) < e.getX() && e.getX() < (ballOne.getX() + ballOne.getRadius())) && ((ballOne.getY() - ballOne.getRadius()) < e.getY() && e.getY() < (ballOne.getY() + ballOne.getRadius()))){
            if (ballOne.getBallPaint().getColor() == colorIndicatorPaint.getColor()){
                playerScore += 1;
                resetBalls(1);
            }else {
                playerScore -= 1;
                resetBalls(1);
            }
        }else if(((ballTwo.getX() - ballTwo.getRadius()) < e.getX() && e.getX() < (ballTwo.getX() + ballTwo.getRadius())) && ((ballTwo.getY() - ballTwo.getRadius()) < e.getY() && e.getY() < (ballTwo.getY() + ballTwo.getRadius()))){
            if (ballTwo.getBallPaint().getColor() == colorIndicatorPaint.getColor()){
                playerScore += 1;
                resetBalls(2);
            }else {
                playerScore -= 1;
                resetBalls(2);
            }
        }else if(((ballThree.getX() - ballThree.getRadius()) < e.getX() && e.getX() < (ballThree.getX() + ballThree.getRadius())) && ((ballThree.getY() - ballThree.getRadius()) < e.getY() && e.getY() < (ballThree.getY() + ballThree.getRadius()))){
            if (ballThree.getBallPaint().getColor() == colorIndicatorPaint.getColor()){
                playerScore += 1;
                resetBalls(3);
            }else {
                playerScore -= 1;
                resetBalls(3);
            }
        }
    }

    public void resetBalls(int ballNum){
        ballBusterThread.setRunning(false);
        if(ballNum == 1){
            ballOne.randomizePaint();
            ballOne.setY((screenHeight - (screenHeight/5)) + ballOne.getRadius());
        }else if(ballNum == 2){
            ballTwo.randomizePaint();
            ballTwo.setY((screenHeight - (screenHeight/5)) + ballOne.getRadius());
        }else if(ballNum == 3){
            ballThree.randomizePaint();
            ballThree.setY((screenHeight - (screenHeight/5)) + ballOne.getRadius());
        }
        ballBusterThread.setRunning(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        // get int representing the type of action which caused this event
        int action = e.getAction();

        if (action == MotionEvent.ACTION_DOWN){
            checkBallTap(e);
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!dialogIsDisplayed){
            ballBusterThread = new BallBusterThread(holder);
            ballBusterThread.setRunning(true); // start game running
            ballBusterThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // ensure that thread terminates properly
        boolean retry = true;
        ballBusterThread.setRunning(false); // terminate ballBusterThread

        while(retry){
            try{
                ballBusterThread.join(); // wait for ballBusterThread to finish
                retry = false;
            }catch(InterruptedException e){
                Log.e("BallBusterView", "Thread interrupted", e);
            }
        }
    }

    public void releaseResources(){

    }

    public class BallBusterThread extends Thread {
        private SurfaceHolder surfaceHolder; // for manipulating the canvas
        private boolean threadIsRunning; // running by default

        // initialize the surface holder
        public BallBusterThread(SurfaceHolder holder){
            surfaceHolder = holder;
            setName("BallBusterThread");
        }

        // changes running state
        public void setRunning(boolean running){
            threadIsRunning = running;
        }

        // controls the game loop
        @Override
        public void run(){
            Canvas canvas = null; // used for drawing
            long previousFrameTime = System.currentTimeMillis();

            while (threadIsRunning){
                try{
                    // get Canvas for exclusive drawing from this thread
                    canvas = surfaceHolder.lockCanvas(null);

                    // lock the surfaceHolder for drawing
                    synchronized (surfaceHolder){
                        long currentTime = System.currentTimeMillis();
                        double elapsedTimeMS = currentTime - previousFrameTime;
                        updatePositions(elapsedTimeMS);
                        drawGameBackground(canvas);

                    }
                }finally{
                    if(canvas!=null){
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

}
