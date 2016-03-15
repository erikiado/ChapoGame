package com.example.plak.chapogame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by erikiado on 23-Jan-16.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{

    private HiloPrincipal thread;
    private Background fondo;
    private Player player;
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 576;
    public static final int moveSpeed = -5;

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
        fondo = new Background(BitmapFactory.decodeResource(getResources(),R.drawable.fondoverde));
        player = new Player(BitmapFactory.decodeResource(getResources(),R.drawable.player),512/4,512/4,4);

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
                player.setUp(true);
            }
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);

    }

    public void update() {
        if (player.getPlaying()) {
            fondo.update();
            player.update();
        }
    }

    @Override
    public void draw(Canvas canvas){
        final float scaleX = getWidth()/(WIDTH*1.f);
        final float scaleY = getHeight()/(HEIGHT*1.f);
        if (canvas != null){
            final int savedState = canvas.save();
            canvas.scale(scaleX,scaleY);
            fondo.draw(canvas);
            player.draw(canvas);
            canvas.restoreToCount(savedState);
        }
    }
}
