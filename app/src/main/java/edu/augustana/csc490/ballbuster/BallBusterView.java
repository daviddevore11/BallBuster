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
    private SurfaceView surfaceView;

    private Paint backgroundPaint = new Paint();
    private Paint curtainPaint = new Paint();
    private Paint colorIndicatorBackPaint = new Paint();
    private Paint colorIndicatorPaint = new Paint();
    private Paint textPaint = new Paint();

    private int ballRadius;
    private Point ballOne = new Point();
    private Paint ballOnePaint = new Paint();
    private Point ballTwo = new Point();
    private Paint ballTwoPaint = new Paint();
    private Point ballThree = new Point();
    private Paint ballThreePaint = new Paint();

    private int screenWidth;
    private int screenHeight;
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

        // register SurfaceHolder.Callback listener
        getHolder().addCallback(this);

        // construct Paints for drawings
        backgroundPaint.setColor(Color.LTGRAY);
        curtainPaint.setColor(Color.DKGRAY);
        colorIndicatorBackPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(w/ 20);
        ballOnePaint = chooseRandomColor(ballOnePaint);
        ballTwoPaint = chooseRandomColor(ballTwoPaint);
        ballThreePaint = chooseRandomColor(ballThreePaint);
        colorIndicatorPaint = chooseRandomColor(colorIndicatorPaint);

        // sets up ball parameters
        ballRadius = screenWidth/10;
        ballOne.x = (screenWidth/4) - ballRadius;
        ballOne.y = (screenHeight - (screenHeight/5)) + ballRadius;
        ballTwo.x = (screenWidth/2);
        ballTwo.y = (screenHeight - (screenHeight/5)) + ballRadius;
        ballThree.x = (screenWidth - (screenWidth/4)) + ballRadius;
        ballThree.y = (screenHeight - (screenHeight/5)) + ballRadius;

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
        int speed = 5;

        // moves ball up and down the screen
        if(upwardMovement){
            ballOne.y = ballOne.y - speed;
            ballTwo.y = ballTwo.y - speed;
            ballThree.y = ballThree.y - speed;
        }else{
            ballOne.y = ballOne.y + speed;
            ballTwo.y = ballTwo.y + speed;
            ballThree.y = ballThree.y + speed;
        }

        /*if((ballOne.y <= (screenHeight/4)) || (ballTwo.y <= (screenHeight/4)) || (ballThree.y <= (screenHeight/4))){
            upwardMovement = false;
        }*/
        if (ballOne.y <= (screenHeight/4)){
            upwardMovement = false;
        }else if(ballOne.y >= (screenHeight - (screenHeight/5))+ballRadius+speed){
            upwardMovement = true;
            ballOnePaint = chooseRandomColor(ballOnePaint);
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
        canvas.drawCircle(ballOne.x, ballOne.y, ballRadius, ballOnePaint);
        canvas.drawCircle(ballTwo.x, ballTwo.y, ballRadius, ballTwoPaint);
        canvas.drawCircle(ballThree.x, ballThree.y, ballRadius, ballThreePaint);

        // draws main curtain
        canvas.drawRect(0, screenHeight - (screenHeight/5), screenWidth, screenHeight, curtainPaint);
    }

    public void checkBallTap(MotionEvent e){
        if(((ballOne.x - ballRadius) < e.getX() && e.getX() < (ballOne.x + ballRadius)) && ((ballOne.y - ballRadius) < e.getY() && e.getY() < (ballOne.y + ballRadius))){
            if (ballOnePaint.getColor() == colorIndicatorPaint.getColor()){
                playerScore += 1;
                resetBalls(1);
            }else {
                playerScore -= 1;
                resetBalls(1);
            }
        }else if(((ballTwo.x - ballRadius) < e.getX() && e.getX() < (ballTwo.x + ballRadius)) && ((ballTwo.y - ballRadius) < e.getY() && e.getY() < (ballTwo.y + ballRadius))){
            if (ballTwoPaint.getColor() == colorIndicatorPaint.getColor()){
                playerScore += 1;
                resetBalls(2);
            }else {
                playerScore -= 1;
                resetBalls(2);
            }
        }else if(((ballThree.x - ballRadius) < e.getX() && e.getX() < (ballThree.x + ballRadius)) && ((ballThree.y - ballRadius) < e.getY() && e.getY() < (ballThree.y + ballRadius))){
            if (ballThreePaint.getColor() == colorIndicatorPaint.getColor()){
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
            ballOnePaint = chooseRandomColor(ballOnePaint);
            ballOne.x = (screenWidth/4) - ballRadius;
            ballOne.y = (screenHeight - (screenHeight/5)) + ballRadius;
        }else if(ballNum == 2){
            ballTwoPaint = chooseRandomColor(ballTwoPaint);
            ballTwo.x = (screenWidth/2);
            ballTwo.y = (screenHeight - (screenHeight/5)) + ballRadius;
        }else if(ballNum == 3){
            ballThreePaint = chooseRandomColor(ballThreePaint);
            ballThree.x = (screenWidth - (screenWidth/4)) + ballRadius;
            ballThree.y = (screenHeight - (screenHeight/5)) + ballRadius;
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
