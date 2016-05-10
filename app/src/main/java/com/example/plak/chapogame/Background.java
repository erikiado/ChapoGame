package com.example.plak.chapogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by erikiado on 24-Jan-16.
 */
public class Background {

    private Bitmap fondo;
    private int  x, y, dx;
    private int width;
    public Background(Bitmap b,int speed, int w){
        fondo = b;
        dx = speed;
        width = w;
    }

    public void update(){
        x+=dx;
        if(x < -width){
            x = 0;
        }
    }

    public void changeSpeed(int speed){
        dx = speed;
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(fondo,x,y,null);
        if(x<0){
            canvas.drawBitmap(fondo,x+width,y,null);
        }
    }

}
