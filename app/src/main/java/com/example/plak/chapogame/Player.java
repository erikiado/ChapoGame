package com.example.plak.chapogame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by erikiado on 24-Jan-16.
 */
public class Player extends GameObject{
    private Bitmap spritesheet;
    private int score;
    private double dy;
    private boolean playing;
    private boolean jump;
    private boolean onGround;
    private Animation animation;
    private long startTime;
    private int position;
    private boolean win;
    private boolean papaPower;
    private long papaTime;

    public Player(Bitmap b, int w, int h, int numFrames){
        x = 400;
        y = 450;
        dy = 0;
        score = 0;
        position = 2;
        height = h;
        width = w;
        onGround = true;
        jump = false;
        animation = new Animation();
        win = false;
        papaPower = false;

        Bitmap[] image = new Bitmap[numFrames];
        spritesheet = b;

        for (int i =0; i< image.length;i++){
            image[i] = Bitmap.createBitmap(spritesheet,i*width,0,width,height);
        }

        animation.setFrames(image);
        animation.setDelay(10);
        startTime = System.nanoTime();

    }

    public void jump(){
        jump = true;
    }

    public boolean getJump(){
        return jump;
    }


    public boolean onGround(){
        return onGround;
    }
    public void update(){
        long elapsed = (System.nanoTime() - startTime)/1000000;

        if(elapsed > 1000){
            score ++;
            startTime = System.nanoTime();
        }

        animation.update();

        if(onGround){
            if(dy > 0){
                //Este es el cambio de la caida, que tan rapido
                dy*=-0.5;
            }
            if(jump){
                dy = -21;
                jump=false;
                onGround = false;
            }
        }else{
            //Entre mayor sea mas rapido cae, para impedir que llegue al otro bloque
            dy += 1;
        }


        if(y > 450){
            stand();
            y = 450;
        }

        y += (int)(dy*1.1);

        if (papaPower) {
            long elapsedPapa = (System.nanoTime() - papaTime)/1000000;
            if (elapsedPapa < 6000) {
                papaPower = true;
            }else {
                papaPower = false;
            }
        }

    }

    public void rollBack(){
        if (!papaPower) {
            position--;
            y = 450;
            x -= 150;
        }
    }

    public void stand(){
        dy = 0;
        onGround = true;
    }

    public void fall(){
        onGround = false;
    }

    public void stand(GameObject platform){
        dy = 0;
        y = platform.getY()-height;
        onGround = true;
    }

    public boolean onPlatform(){
        if(onGround && y < 390){
            return true;
        }
        return false;
    }

    public void juntarDinero(){
        if (papaPower) {
            score += 100;
        }else {
            score += 10;
        }
    }

    public void juntarTequila(){
        score += 20;
        position++;
        x+=150;
    }

    public void papaPowerUp(long currTime) {
        //que va a hacer este culero cuando lo agarre?
        papaPower = true;
        papaTime = currTime;
    }

    public void colisionPolicia () {
        if (!papaPower) {
            position = 0;
            setPlaying(false);
        }
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

    public int getPosition(){
        return position;
    }

    public void setPlaying(boolean b){
        playing = b;
    }

    public void resetDy(){
        dy = 0;
    }

    public void resetScore(){
        score = 0;
    }

    public boolean getWin() { return win; }

    public void setWin(boolean w) { win = w; }

    public boolean isPowerUp() {return papaPower; }
}
