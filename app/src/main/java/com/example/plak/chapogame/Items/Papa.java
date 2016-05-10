package com.example.plak.chapogame.Items;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.plak.chapogame.Animation;
import com.example.plak.chapogame.GameObject;

/**
 * Created by Ral15 on 5/9/16.
 */
public class Papa extends GameObject {

    private Animation animation = new Animation();
    private Bitmap spritesheet;
    private int speed;
    private boolean picked;

    public Papa(Bitmap res, int x, int y, int w, int h, int s, int numFrames) {
        super.x = x;
        super.y = y - 5;
        width = w;
        height = h;
        picked = false;
        speed = s;

        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;

        for(int i = 0; i < image.length; i++){
            //suponiendo que la serie de sprites esta verticcal
            image[i] = Bitmap.createBitmap(spritesheet,0,i*height,width,height);
        }

        animation.setFrames(image);
        animation.setDelay(100);
    }


    public void update(){
        x += speed;
    }

    public void draw(Canvas canvas){
        try{
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }catch (Exception e){

        }
    }

    public void pickUp(){
        picked = true;
    }

    public boolean isPicked(){
        return picked;
    }

    public void changeSpeed(int speed){
        this.speed = speed;
    }
}
