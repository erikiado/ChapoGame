package com.example.plak.chapogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by erikiado on 24-Jan-16.
 */
public class Background {

    private Bitmap fondo;
    private int  x, y, dx;

    public Background(Bitmap b,int speed){
        fondo = b;
        dx = speed;
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
