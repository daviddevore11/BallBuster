package edu.augustana.csc490.ballbuster;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements SurfaceHolder.Callback{

    private Button startButton;
    private MainMenuThread mainMenuThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        addListenerOnButton();
    }

    public void addListenerOnButton(){
        startButton = (Button) findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_main);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void drawBackgroundAnimation(Canvas canvas){
        // draws circles
        //canvas.drawCircle(ballOne.getX(), ballOne.getY(), ballOne.getRadius(), ballOne.getBallPaint());
        //canvas.drawCircle(ballTwo.getX(), ballTwo.getY(), ballTwo.getRadius(), ballTwo.getBallPaint());
        //canvas.drawCircle(ballThree.getX(), ballThree.getY(), ballThree.getRadius(), ballThree.getBallPaint());

        // draws main curtain
        //canvas.drawRect(0, screenHeight - (screenHeight/5), screenWidth, screenHeight, curtainPaint);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //mainMenuThread = new MainMenuThread(holder);
       // mainMenuThread.setRunning(true);
        //mainMenuThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public class MainMenuThread extends Thread {
        private SurfaceHolder surfaceHolder; // for manipulating the canvas
        private boolean threadIsRunning; // running by default

        // initialize the surface holder
        public MainMenuThread(SurfaceHolder holder){
            surfaceHolder = holder;
            setName("MainMenuThread");
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
                        //updatePositions(elapsedTimeMS);
                        drawBackgroundAnimation(canvas);
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
