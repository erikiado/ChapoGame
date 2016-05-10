package com.example.plak.chapogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by erikiado on 5/9/16.
 */
public class FrontParallax {
    private Bitmap fondo;
    private int  x, y, dx;

    public FrontParallax(Bitmap b,int speed){
        fondo = b;
        dx = speed;
        y = 525;
    }

    public void update(){
        x+=dx;
        if(x < -GamePanel.WIDTH){
            x = 0;
        }
    }

    public void changeSpeed(int speed){
        dx = speed;
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(fondo,x,y,null);
        if(x<0){
            canvas.drawBitmap(fondo,x+GamePanel.WIDTH,y,null);
        }
    }

}
