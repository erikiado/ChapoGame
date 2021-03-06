package com.example.plak.chapogame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by erikiado on 3/15/16.
 */
public class SmokeTrail extends GameObject {

    public int r;
    private int alpha;
    public SmokeTrail(int x, int y ){
        r = 5;
        super.x = x;
        super.y = y;
        alpha = 15;
    }

    public void update(){
        x -=10;
        r++;
        alpha+=2;
    }

    public void draw(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        //paint.setAlpha(60);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(alpha);
        canvas.drawCircle(x - r, y - r, r, paint);
        canvas.drawCircle(x-r+2,y-r-2,r,paint);
        canvas.drawCircle(x-r+4,y-r+1,r,paint);

    }

}
