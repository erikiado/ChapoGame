package com.example.plak.chapogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

/**
 * Created by erikiado on 3/27/16.
 */
public class Block extends GameObject{
    private int score;
    private int speed;
    private Random rand = new Random();
    private Animation animation = new Animation();
    private Bitmap spritesheet;

    public Block(Bitmap res, int x, int y, int w, int h, int s, int numFrames){
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        score = s;

        speed = 7 + (int)(rand.nextDouble()*score/30);
        if(speed > 40){
            speed = 40;
        }

        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = res;

        for(int i = 0; i < image.length; i++){
            //suponiendo que la serie de sprites esta verticcal
            image[i] = Bitmap.createBitmap(spritesheet,0,i*height,width,height);
        }

        animation.setFrames(image);
        animation.setDelay(100-speed);
    }

    public void update(){
        x -= speed;
        animation.update();
    }

    public void draw(Canvas canvas){
        try{
           canvas.drawBitmap(animation.getImage(),x,y,null);
        }catch (Exception e){

        }
    }

    public boolean colisionPlayer(GameObject player){
        if(y <= player.getY()+player.getHeight() && y+height > player.getY()){
            if(x > player.getX() && x <= player.getX()+player.getWidth()){
                return true;
            }
        }
        return false;
    }

}
