package edu.augustana.csc490.ballbuster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
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

    private Paint backgroundPaint;
    private Paint curtainPaint;
    private Paint colorIndicatorBackPaint;
    private Paint colorIndicatorPaint;
    private Paint textPaint;

    private Ball ballOne;
    private Ball ballTwo;
    private Ball ballThree;
    private int startingY;
    private double startingSpeed;

    private int screenWidth;
    private int screenHeight;
    private Random r = new Random();

    private int playerScore;
    private double timeLeft;
    private boolean gameOver;

    private MediaPlayer player; // plays background music
    private SoundPool soundPool; // plays sound effects
    private SparseIntArray soundMap; // maps IDs to SoundPool
    private static final int BALL_GOOD_POP_ID = 0;
    private static final int BALL_BAD_POP_ID = 1;
    private static final int COLOR_SWITCH_ID = 2;

    public BallBusterView(Context context, AttributeSet attrs){
        super(context, attrs);
        activity = (Activity) context; // store reference to MainActivity

        getHolder().addCallback(this);


        player = MediaPlayer.create(context, R.raw.background);
        player.setLooping(true);
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundMap = new SparseIntArray(3);
        soundMap.put(BALL_GOOD_POP_ID, soundPool.load(context, R.raw.blop, 1));
        soundMap.put(BALL_BAD_POP_ID, soundPool.load(context, R.raw.buzz,1));
        soundMap.put(COLOR_SWITCH_ID, soundPool.load(context, R.raw.sweep,1));

        backgroundPaint = new Paint();
        curtainPaint = new Paint();
        colorIndicatorBackPaint = new Paint();
        colorIndicatorPaint = new Paint();
        textPaint = new Paint();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        startingY = (screenHeight - (screenHeight/5)) + screenWidth/10;
        startingSpeed = 20.0;
        int lowerBound = screenHeight - (screenHeight/5) + (screenWidth/10) + (int)startingSpeed;
        int upperBound = screenHeight/4;

        // construct Paints for drawings
        backgroundPaint.setColor(Color.LTGRAY);
        curtainPaint.setColor(Color.DKGRAY);
        colorIndicatorBackPaint.setColor(Color.DKGRAY);
        textPaint.setTextSize(w/ 20);
        colorIndicatorPaint = chooseRandomColor(colorIndicatorPaint);

        // sets up ball parameters
        // parameters for creating a ball are (X, Y, radius, speed, upper bound, lower bound)
        // All balls radius: screenWidth/10
        ballOne = new Ball((screenWidth/4) - screenWidth/10, startingY, screenWidth/10, startingSpeed, upperBound, lowerBound);
        ballTwo = new Ball(screenWidth/2, startingY, screenWidth/10, startingSpeed, upperBound, lowerBound);
        ballThree = new Ball((screenWidth - (screenWidth/4)) + screenWidth/10, startingY, screenWidth/10, startingSpeed, upperBound, lowerBound);

        showStartUpDialog(R.string.welcome);
    }

    // reset all the screen elements and start a new game
    public void newGame(){
        timeLeft = 60; // start the countdown at 60 seconds
        playerScore = 0; // sets player score

        if(gameOver){
            gameOver = false;

            //reset ball parameters to starting positions
            //re-randomize the paint for every object (balls and color indicator)
            ballOne.setY(startingY);
            ballTwo.setY(startingY);
            ballThree.setY(startingY);
            ballOne.resetSpeed(startingSpeed);
            ballTwo.resetSpeed(startingSpeed);
            ballThree.resetSpeed(startingSpeed);
            ballOne.resetIncrementTracker();
            ballTwo.resetIncrementTracker();
            ballThree.resetIncrementTracker();
            colorIndicatorPaint = chooseRandomColor(colorIndicatorPaint);
            ballOne.randomizePaint();
            ballTwo.randomizePaint();
            ballThree.randomizePaint();

            ballBusterThread = new BallBusterThread(getHolder());
            ballBusterThread.setRunning(true);
            ballBusterThread.start();
        }
    }

    public void updatePositions(double elapsedTimeMS){
        double interval = elapsedTimeMS / 1000.0;

        // moves ball up and down the screen
        ballOne.moveBall();
        ballTwo.moveBall();
        ballThree.moveBall();

        ballOne.increaseSpeed(interval);
        ballTwo.increaseSpeed(interval);
        ballThree.increaseSpeed(interval);

        int randNum = r.nextInt(100-0);
        if(randNum == 10){
            int temp = colorIndicatorPaint.getColor();
            colorIndicatorPaint = chooseRandomColor(colorIndicatorPaint);
            if(temp != colorIndicatorPaint.getColor()){
                soundPool.play(soundMap.get(COLOR_SWITCH_ID), 1, 1, 1, 0, 1f);
            }
        }

        // updates text on screen with timeLeft
        timeLeft = 60 - interval;

        if(timeLeft <= 0.0){
            timeLeft = 0.0;
            gameOver = true; // the game is over
            ballBusterThread.setRunning(false);
            player.stop();
            player.release();
            showGameOverDialog(R.string.over);
        }
    }

    // picks a random color for the desired Paint Object
    public Paint chooseRandomColor(Paint paint){
        int randNum = r.nextInt(3);
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
                soundPool.play(soundMap.get(BALL_GOOD_POP_ID), 1, 1, 1, 0, 1f);
            }else {
                playerScore -= 1;
                soundPool.play(soundMap.get(BALL_BAD_POP_ID), 1, 1, 1, 0, 1f);
                resetBalls(1);
            }
        }else if(((ballTwo.getX() - ballTwo.getRadius()) < e.getX() && e.getX() < (ballTwo.getX() + ballTwo.getRadius())) && ((ballTwo.getY() - ballTwo.getRadius()) < e.getY() && e.getY() < (ballTwo.getY() + ballTwo.getRadius()))){
            if (ballTwo.getBallPaint().getColor() == colorIndicatorPaint.getColor()){
                playerScore += 1;
                resetBalls(2);
                soundPool.play(soundMap.get(BALL_GOOD_POP_ID), 1, 1, 1, 0, 1f);
            }else {
                playerScore -= 1;
                soundPool.play(soundMap.get(BALL_BAD_POP_ID), 1, 1, 1, 0, 1f);
                resetBalls(2);
            }
        }else if(((ballThree.getX() - ballThree.getRadius()) < e.getX() && e.getX() < (ballThree.getX() + ballThree.getRadius())) && ((ballThree.getY() - ballThree.getRadius()) < e.getY() && e.getY() < (ballThree.getY() + ballThree.getRadius()))){
            if (ballThree.getBallPaint().getColor() == colorIndicatorPaint.getColor()){
                playerScore += 1;
                resetBalls(3);
                soundPool.play(soundMap.get(BALL_GOOD_POP_ID), 1, 1, 1, 0, 1f);
            }else {
                playerScore -= 1;
                soundPool.play(soundMap.get(BALL_BAD_POP_ID), 1, 1, 1, 0, 1f);
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

    public void showStartUpDialog(final int messageId){
        final DialogFragment gameStart = new DialogFragment(){
            @Override
            public Dialog onCreateDialog(Bundle bundle){
                // create dialog displaying String resource for messageId
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(messageId));

                // display game description
                builder.setMessage(getResources().getString(R.string.welcome_info));
                builder.setPositiveButton(R.string.start_game, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        ballBusterThread.setRunning(true); // start game running
                        ballBusterThread.start();
                        player.start();
                        newGame();
                    }
                });

                return builder.create();
            }
        };

        // in GUI thread, use FragmentManager to dispaly the DialogFragment
        activity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        gameStart.setCancelable(false); // modal dialog
                        gameStart.show(activity.getFragmentManager(), "start");
                    }
                }
        );
    }

    public void showGameOverDialog(final int messageId){
        final DialogFragment gameResult = new DialogFragment(){
            @Override
            public Dialog onCreateDialog(Bundle bundle){
                // create dialog displaying String resource for messageId
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(messageId));

                // display total number of bulls busted
                builder.setMessage(getResources().getString(R.string.results_format, playerScore));
                builder.setPositiveButton(R.string.reset_game, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogIsDisplayed = false;
                        player = MediaPlayer.create(BallBusterView.this.getContext(), R.raw.background);
                        player.setLooping(true);
                        player.start();
                        newGame();
                    }
                });

                return builder.create(); // return the AlertDialog
            }
        };

        // in GUI thread, use FragmentManager to dispaly the DialogFragment
        activity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        dialogIsDisplayed = true;
                        gameResult.setCancelable(false); // modal dialog
                        gameResult.show(activity.getFragmentManager(), "results");
                    }
                }
        );
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
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

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
        player.release();
        soundPool.release();
    }

    public void stopGame(){
        if (ballBusterThread != null){
            ballBusterThread.setRunning(false); // tell thread to terminate
            releaseResources();
        }
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
