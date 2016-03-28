package com.example.plak.chapogame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.test.suitebuilder.annotation.Smoke;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by erikiado on 23-Jan-16.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{


    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final int moveSpeed = -5;
    private long trailStartTime;
    private long blockStartTime;

    private HiloPrincipal thread;
    private Background fondo;
    private Player player;
    private ArrayList<SmokeTrail> trails;
    private ArrayList<Block> blocks;
    private Random rand = new Random();

    public GamePanel(Context context){
        super(context);

        //Interceptar eventos
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new HiloPrincipal(holder,this);

        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        fondo = new Background(BitmapFactory.decodeResource(getResources(),R.drawable.montana));
        player = new Player(BitmapFactory.decodeResource(getResources(),R.drawable.sprite_player),80,100,5);
        trails = new ArrayList<SmokeTrail>();
        blocks = new ArrayList<Block>();

        trailStartTime = System.nanoTime();
        blockStartTime = System.nanoTime();

        thread.setRunning(true);
        thread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int cont = 0;
        while(retry && cont < 1000){
            cont++;
            try{
                thread.setRunning(false);
                thread.join();
                retry = false;
            }catch (InterruptedException e){
                e.printStackTrace();
            }


        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(!player.getPlaying()){
                player.setPlaying(true);
            }else{
                //player.setUp(true);
            }
            return true;
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            if(player.onGround()){
                player.setJump(true);
            }
            return true;
        }

        return super.onTouchEvent(event);

    }

    public void update() {
        if (player.getPlaying()) {
            fondo.update();
            player.update();

            long blockElapsed = (System.nanoTime() - blockStartTime)/1000000;

            if(blockElapsed > (2000 - player.getScore()/4)){
                System.out.println("Creando bloque");
                if(blocks.size()==0){
                    blocks.add(new Block(BitmapFactory.decodeResource(getResources(),R.drawable.bloque),WIDTH+10,450,100,100,player.getScore(),1));
                }else{
                    blocks.add(new Block(BitmapFactory.decodeResource(getResources(),R.drawable.bloque),WIDTH+10,((int)(rand.nextDouble()*(HEIGHT-250)))+150,100,100,player.getScore(),1));
                }
                blockStartTime = System.nanoTime();
            }

            for(int i =0; i <blocks.size();i++){
                Block b = blocks.get(i);
                b.update();
                //fallInBlock(player,b);
                if(b.colisionPlayer(player)){
                    player.setPlaying(false);
                    break;
                }
                if(b.getX() < -110){
                    blocks.remove(i);
                    break;
                }
            }

            long elapsed = (System.nanoTime() - trailStartTime)/1000000;
            if(elapsed > 120) {
                trails.add(new SmokeTrail(player.getX(), player.getY() + 100));
                trailStartTime = System.nanoTime();
            }

            for(int i = 0; i < trails.size();i++){
                trails.get(i).update();
                if(trails.get(i).getX() < -10){
                    trails.remove(i);
                }
            }

        }
    }

    public void fallInBlock(GameObject player, GameObject b){
        if(player.getX()+player.getWidth()>b.getX() && player.getX()<b.getX()+b.getWidth()){
            if(player.getY()+player.getHeight()==b.getY()){
                ((Player)player).resetDy();
            }
        }
    }

    @Override
    public void draw(Canvas canvas){
        final float scaleX = getWidth()/(WIDTH*1.f);
        final float scaleY = getHeight()/(HEIGHT*1.f);
        if (canvas != null){
            final int savedState = canvas.save();
            canvas.scale(scaleX, scaleY);
            fondo.draw(canvas);
            player.draw(canvas);
            for(SmokeTrail trail: trails){
                trail.draw(canvas);
            }
            for(Block block: blocks) {
                block.draw(canvas);
            }

            canvas.restoreToCount(savedState);
        }
    }
}
