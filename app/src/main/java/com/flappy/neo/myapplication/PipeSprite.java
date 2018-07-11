package com.flappy.neo.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PipeSprite {
    private Bitmap image;
    private Bitmap image2;
    public int pipeX, pipeY;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;


    public PipeSprite(Bitmap bmp, Bitmap bmp2, int x, int y) {
        image = bmp;
        image2 = bmp2;
        pipeY = y;
        pipeX = x;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, pipeX, -(GameView.spaceHeight/2)+ pipeY, null);
        canvas.drawBitmap(image2, pipeX, ((screenHeight/2)+(GameView.spaceHeight/2)) + pipeY, null);
    }

    public void update() {
        pipeX -= GameView.velocity;
    }
}
