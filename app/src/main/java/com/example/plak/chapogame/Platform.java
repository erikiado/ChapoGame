package com.example.plak.chapogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by erikiado on 4/2/16.
 */
public class Platform extends GameObject {
    private boolean choco;
    private Random rand = new Random();
    private Animation animation = new Animation();
    private Bitmap spritesheet;

    public Platform(Bitmap res, int x, int y, int w, int h, int numFrames){
        super.x = x;
        super.y = y-5;
        width = w;
        height = h;
        choco = false;


        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;

        for(int i = 0; i < image.length; i++){
            //suponiendo que la serie de sprites esta verticcal
            image[i] = Bitmap.createBitmap(spritesheet,0,i*height,width,height);
        }

        animation.setFrames(image);
        animation.setDelay(100);
    }

    public void update(GameObject block){
        x = block.getX();
    }

    public void draw(Canvas canvas){
        try{
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }catch (Exception e){

        }
    }

}
