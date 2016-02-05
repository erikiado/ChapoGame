package com.example.plak.chapogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by erikiado on 24-Jan-16.
 */
public class Player extends GameObject{
    private Bitmap spritesheet;
    private int score;
    private double dya;
    private boolean playing;
    private boolean up;
    private Animation animation;
    private long startTime;

    public Player(Bitmap b, int w, int h, int numFrames){
        x = 100;
        y = GamePanel.HEIGHT/2;
        dy = 0;
        score = 0;
        height = h;
        width = w;
        animation = new Animation();

        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = b;

        for (int i =0; i< image.length;i++){
            image[i] = Bitmap.createBitmap(spritesheet,i*width,0,width,height);
        }

        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();

    }

    public void setUp(boolean b){
        up = b;
    }

    public void update(){
        long elapsed = (System.nanoTime() - startTime)/1000000;

        if(elapsed > 100){
            score ++;
            startTime = System.nanoTime();
        }

        animation.update();

        if(up){
            dy -= 1;
        }else{
            dy += 1;
        }

        if(dy>14) dy = 14;
        if(dy<-14) dy = -14;

        y += dy*2;

    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(animation.getImage(),x,y,null);
    }

    public int getScore(){
        return score;
    }

    public boolean getPlaying(){
        return playing;
    }

    public void setPlaying(boolean b){
        playing = b;
    }

    public void resetDYA(){
        dya = 0;
    }

    public void resetScore(){
        score = 0;
    }
}
