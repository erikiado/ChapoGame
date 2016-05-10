package com.example.plak.chapogame.Buttons;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.plak.chapogame.GameObject;

/**
 * Created by erikiado on 5/9/16.
 */
public class Button extends GameObject {
    private Bitmap image;

    public Button(Bitmap res, int x, int y, int w, int h){
        super.x = x;
        super.y = y;
        width = w;
        height = h;


        image = Bitmap.createBitmap(res,0,0,width,height);
    }

    public void draw(Canvas canvas){
        try{
            canvas.drawBitmap(image,x,y,null);
        }catch (Exception e){

        }
    }



}
