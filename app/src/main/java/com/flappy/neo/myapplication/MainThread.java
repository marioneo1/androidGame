package com.flappy.neo.myapplication;


import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    //Thread allows a thread to run that's an offshoot of main thing.
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;
    public static Canvas canvas;
    private int targetFPS = 60;
    private double averageFPS;

    public MainThread(SurfaceHolder surfaceHolder, GameView gameView){
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    public void setRunning(boolean isRunning) {
        running = isRunning;
    }

    @Override
    public void run() {
        long startTime;
        long timeMilliSec;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000/ targetFPS;

        while (running) {
            startTime= System.nanoTime();
            canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized(surfaceHolder) {
                    this.gameView.update();
                    this.gameView.draw(canvas);
                }
            } catch (Exception e) {} finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            timeMilliSec = (System.nanoTime()-startTime)/1000000;
            waitTime = targetTime - timeMilliSec;

            try{
                this.sleep(waitTime);
            }
            catch (Exception e) {}

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == targetFPS) {
                averageFPS = 1000/ ((totalTime/frameCount/1000000));
                frameCount = 0;
                totalTime = 0;
                System.out.println(averageFPS);
            }

        }
    }
}
