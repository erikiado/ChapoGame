package com.example.plak.chapogame;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.plak.chapogame.Activities.ActivityGameOver;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by erikiado on 23-Jan-16.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{


    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static int moveSpeed = -5;
    private long trailStartTime;
    private long blockStartTime;

    private HiloPrincipal thread;
    private Background fondo;
    private Player player;
    private ArrayList<SmokeTrail> trails;
    private ArrayList<Block> blocks;
    private ArrayList<Platform> platforms;
    private ArrayList<Money> moneys;
    private ArrayList<Integer> moneysList;
    private ArrayList<Pair> moneyPairs; //se crea el par de bloque y dinero, se esta guardando dos veces el bloque?
    private int moneyCount;
    private Random rand = new Random();
    private Paint hudPaint;
    private SoundPool sp;
    private int soundIds[];
    private Context context;
    private int placesBlock [];

    public GamePanel(Context cxt){
        super(cxt);
        context = cxt;
        //Interceptar eventos
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new HiloPrincipal(holder,this);
        initializeSounds();
        setFocusable(true);
        placesBlock = new int [3];
        placesBlock [0] = 450;
        placesBlock [1] = 320;
        placesBlock [2] = 250;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void initializeSounds(){
        SoundPool.Builder sP = new SoundPool.Builder();
        AudioAttributes attrs = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        sp = sP.setMaxStreams(10).setAudioAttributes(attrs).build();

        soundIds = new int[10];
        //jump sound
        soundIds[0] = sp.load(context,R.raw.jump,1);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //HUD
        hudPaint = new Paint();
        hudPaint.setColor(Color.RED);
        hudPaint.setStyle(Paint.Style.FILL);
        hudPaint.setTextSize(50);

        //Actors
        fondo = new Background(BitmapFactory.decodeResource(getResources(),R.drawable.montana));
        player = new Player(BitmapFactory.decodeResource(getResources(),R.drawable.sprite_player),80,100,5);
        trails = new ArrayList<SmokeTrail>();
        blocks = new ArrayList<Block>();
        platforms = new ArrayList<Platform>();
        moneys = new ArrayList<Money>();
        moneysList = new ArrayList<>();
        moneyPairs = new ArrayList<>();
        moneyCount = 0;

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

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(!player.getPlaying()){
                player.setPlaying(true);
            }
            if(player.onGround()){
                player.jump();
                sp.play(soundIds[0], 1, 1, 1, 0, (float) 1.0);
            }
            return true;
        }

        if(event.getAction() == MotionEvent.ACTION_UP){

            return true;
        }

        return super.onTouchEvent(event);

    }

    public void update() {
        //LOOP PRINCIPAL
        if (player.getPlaying()) {
            //Actualizar/Mover fondo y jugador: Editar esto si quieres que se mueva distinto
            fondo.update();
            player.update();


            //Spawnear bloques dependiendo del tiempo
            long blockElapsed = (System.nanoTime() - blockStartTime)/1000000;
            //Cada 1.8 segundos spawnear
            if(blockElapsed > 1800){//(2000 - player.getScore()/4)){
                if(blocks.size()==0){
                    Block b = new Block(BitmapFactory.decodeResource(getResources(),R.drawable.bloque),WIDTH+10,placesBlock[0],100,100,moveSpeed,1);
                    blocks.add(b);
                    platforms.add(new Platform(BitmapFactory.decodeResource(getResources(),R.drawable.fondoverde),WIDTH+10,450,100,10,1));
                    moneys.add(new Money(BitmapFactory.decodeResource(getResources(), R.drawable.weed), WIDTH + 10, 370, 38, 45,moveSpeed, 1));
                    //moneyPairs.add(new Pair(b,new Money(BitmapFactory.decodeResource(getResources(), R.drawable.weed), WIDTH + 10, 370, 38, 45,moveSpeed, 1)));
                }else{
                    int x = rand.nextInt(3);
                    // to know if weed should appear
                    Block b = new Block(BitmapFactory.decodeResource(getResources(),R.drawable.bloque),WIDTH+10,placesBlock[x],100,100,moveSpeed,1);
                    blocks.add(b);
                    platforms.add(new Platform(BitmapFactory.decodeResource(getResources(),R.drawable.fondoverde),WIDTH+10,placesBlock[x],100,10,1));
                    moneys.add(new Money(BitmapFactory.decodeResource(getResources(),R.drawable.weed),WIDTH+10,placesBlock[x]-80,38,45,moveSpeed,1));
                }
                blockStartTime = System.nanoTime();
            }



            //Actualizar bloques
            for(int i =0; i <blocks.size();i++){
                Block block = blocks.get(i);
                block.update();
                platforms.get(i).update(block);
            }

            for(int i = 0; i <moneys.size();i++){
                moneys.get(i).update();
            }






            for(int i = 0; i <moneys.size();i++){
                Money m = moneys.get(i);
                if(!m.isPicked()){
                    if (colisionBloque(player, m)) {
                        player.juntarDinero();
                        m.pickUp();
                    }
                }
            }

            for(int i = 0; i < blocks.size(); i++){
                Block block = blocks.get(i);
                Platform platform = platforms.get(i);

                if(paso(player, platform)){
                    player.fall();
                }else if(colisionPlatform(player, platform)){
                    player.stand(platform);
                }else if(colisionBloque(player,block)){
                    blocks.remove(i);
                    platforms.remove(i);
                    moneys.remove(i);
                    switch(player.getPosition()){
                        case 0:
                            break;
                        case 1:
                        case 2:
                            player.rollBack();
                            break;
                    }
                    player.setPlaying(false);

                    break;
                }

            }



            checarBorde();


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

            //hacer que el jugador ya puede ganar
            if (player.getScore() > 200) {
                player.setWin(true);
                player.setPlaying(false);
            }

        }else if (player.getWin()) {
            Intent i = new Intent(context, ActivityGameOver.class);
            ((Activity)context).startActivity(i);
            ((Activity)context).finish();
        }

    }//end main loop


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
            for(Platform platform: platforms){
                platform.draw(canvas);
            }
            for(Money money: moneys){
                if(!money.isPicked()){
                    money.draw(canvas);
                }
            }

            //HUD
            canvas.drawText(String.valueOf(player.getScore()*100),1000,100,hudPaint);

            canvas.restoreToCount(savedState);
        }
    }

    public boolean colisionBloque(GameObject player, GameObject block){
        if(player.getX()+player.getWidth() >= block.getX() && player.getX() <= block.getX()+block.getWidth()){
           if(player.getY()+player.getHeight() >= block.getY() && player.getY() <= block.getY()+block.getHeight()){
               return true;
           }
        }
        return false;
    }

    public boolean colisionPlatform(GameObject player, GameObject block){
        if(player.getX()+player.getWidth() > block.getX()+6 && player.getX() <= block.getX()+block.getWidth()){
            if(player.getY()+player.getHeight() >= block.getY() && player.getY() <= block.getY()+block.getHeight()){
                return true;
            }
        }
        return false;
    }


    public boolean paso(GameObject player, GameObject platform){
        if(player.getX()>= platform.getX()+platform.getWidth()-1){
            return true;
        }
        return false;
    }

    public void checarBorde(){
        for(int i = 0; i < blocks.size();i++){
            Block b = blocks.get(i);
            if(b.getX() < -110){
                blocks.remove(i);
                platforms.remove(i);
                break;
            }
        }
        for(int i = 0; i < moneys.size();i++){
            Money m = moneys.get(i);
            if(m.getX() < -110){
                moneys.remove(i);
                break;
            }
        }
    }


}
