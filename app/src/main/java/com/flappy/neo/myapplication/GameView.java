package com.flappy.neo.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.InputStream;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.flappy.neo.myapplication.MainThread.canvas;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread thread;
    public static int spaceHeight = 500;
    public static int velocity = 15;
    public PipeSprite pipe1, pipe2, pipe3;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private CharacterSprite characterSprite;


    public GameView(Context context) {
        super(context);

        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawRGB(53, 182, 236);
//            Paint paint = new Paint();
//            paint.setColor(Color.rgb(250, 0, 0));
//            canvas.drawRect(100, 100, 200, 200, paint);
            characterSprite.draw(canvas);
            pipe1.draw(canvas);
            pipe2.draw(canvas);
            pipe3.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        characterSprite.y = characterSprite.y - (characterSprite.yVel * 10);
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        characterSprite = new CharacterSprite(getResizedBitmap(BitmapFactory.decodeResource(getContext().getResources(),R.drawable.flappy_bird), 300 , 240));
        makeLevel();
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

    }

    public void logic() {
        List<PipeSprite> pipes = new ArrayList<>();
        pipes.add(pipe1);
        pipes.add(pipe2);
        pipes.add(pipe3);

        for (int i=0; i< pipes.size(); i++){
//            If bird touches pipes
            if (characterSprite.y < pipes.get(i).pipeY + (screenHeight/2) - (spaceHeight/2)
                    && characterSprite.x + 300 > pipes.get(i).pipeX +500)
            {
                resetLevel();
            }
            else if (characterSprite.y + 240 > (screenHeight/2) + (spaceHeight/2) + pipes.get(i).pipeY
                    && characterSprite.x + 300 > pipes.get(i).pipeX
                    && characterSprite.x < pipes.get(i).pipeX + 500){
                resetLevel();
            }

            //if pipe goes too far left, put it back infront.
            if (pipes.get(i).pipeX + 500 < 0) {
                Random r = new Random();
                int value1 = r.nextInt(500);
                int value2 = r.nextInt(500);
                pipes.get(i).pipeX = screenWidth + value1 + 1000;
                pipes.get(i).pipeY = value2 - 250;
            }
        }


        //If pipe touches bottom or top of screen
        if (characterSprite.y + 240 < 0) {
            resetLevel();
        }
        if (characterSprite.y > screenHeight) {
            resetLevel();
        }
    }

    public void resetLevel(){
        characterSprite.y = 100;
        pipe1.pipeX = 2000;
        pipe1.pipeY = 0;
        pipe2.pipeX = 4500;
        pipe2.pipeY = 200;
        pipe2.pipeX = 3200;
        pipe2.pipeY = 250;
//ADD A GAME OVER SCREEN INSTEAD

    }

    public void update() {
        logic();
        characterSprite.update();
        pipe1.update();
        pipe2.update();
        pipe3.update();
    }

    private void makeLevel() {
        characterSprite = new CharacterSprite(getResizedBitmap(BitmapFactory.decodeResource(getContext().getResources(),R.drawable.flappy_bird), 300 , 240));
        Bitmap bmp, bmp2;
        int x, y;
        bmp = getResizedBitmap(BitmapFactory.decodeResource
                (getContext().getResources(),R.drawable.pipe_down), 500, Resources.getSystem().getDisplayMetrics().heightPixels/2);
        bmp2 = getResizedBitmap(BitmapFactory.decodeResource
                (getContext().getResources(),R.drawable.pipe_up), 500, Resources.getSystem().getDisplayMetrics().heightPixels/2);
        pipe1 = new PipeSprite(bmp, bmp2, 2000, 100);
        pipe2 = new PipeSprite(bmp, bmp2, 4500, 100);
        pipe3 = new PipeSprite(bmp, bmp2, 3200, 100);



    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap =
                Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
