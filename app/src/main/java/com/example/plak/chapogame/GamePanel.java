package com.example.plak.chapogame;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
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
    private ArrayList<Platform> platforms;
    private ArrayList<Money> moneys;
    private ArrayList<Integer> moneysList;
    private int moneyCount;
    private Random rand = new Random();
    private Paint hudPaint;
    private SoundPool sp;
    private int soundIds[];
    private Context context;

    public GamePanel(Context context){
        super(context);
        this.context = context;
        //Interceptar eventos
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new HiloPrincipal(holder,this);
        initializeSounds();
        setFocusable(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void initializeSounds(){
        SoundPool.Builder sP = new SoundPool.Builder();
        AudioAttributes attrs = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        sp = sP.setMaxStreams(10).setAudioAttributes(attrs).build();

        soundIds = new int[10];
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
                player.jump();
                sp.play(soundIds[0], 1, 1, 1, 0, (float) 1.0);
            }
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
            if(blockElapsed > (2000 - player.getScore()/4)){
                if(blocks.size()==0){
                    blocks.add(new Block(BitmapFactory.decodeResource(getResources(),R.drawable.bloque),WIDTH+10,450,100,100,player.getScore(),1));
                    platforms.add(new Platform(BitmapFactory.decodeResource(getResources(),R.drawable.fondoverde),WIDTH+10,450,100,10,1));
                    moneys.add(new Money(BitmapFactory.decodeResource(getResources(), R.drawable.fondoverde), WIDTH + 10, 370, 20, 20, 1));
                    moneysList.add(moneyCount);
                    moneyCount++;
                }else{
                    int neuY = ((int)(rand.nextDouble()*(HEIGHT-250)))+150;
                    if(neuY>600){
                        neuY -= 150;
                    }
                    blocks.add(new Block(BitmapFactory.decodeResource(getResources(),R.drawable.bloque),WIDTH+10,neuY,100,100,player.getScore(),1));
                    platforms.add(new Platform(BitmapFactory.decodeResource(getResources(),R.drawable.fondoverde),WIDTH+10,neuY,100,10,1));
                    moneys.add(new Money(BitmapFactory.decodeResource(getResources(),R.drawable.fondoverde),WIDTH+10,neuY-80,20,20,1));
                    moneysList.add(moneyCount);
                    moneyCount++;
                }
                blockStartTime = System.nanoTime();
            }

            //Actualizar bloques
            for(int i =0; i <blocks.size();i++){
                Block block = blocks.get(i);
                block.update();
                platforms.get(i).update(block);
            }

            int contBlock = 0;
            for(int i = 0; i < moneysList.size();i++){
                if(moneysList.get(i) >= 0){
                    contBlock = moneysList.get(i);
                    int dif = moneysList.size() - moneys.size();
                    moneys.get(contBlock).update(blocks.get(contBlock+dif));
                }
            }





            //Revisar colision por bloque
            for(int i = 0; i < moneysList.size();i++){
                if(moneysList.get(i) >= 0){
                    if(colisionBloque(player, moneys.get(moneysList.get(i)))){
                        player.juntarDinero();
                        int k = moneysList.get(i);
                        moneys.remove(k);
                        moneysList.set(i,-1);
                        moneyCount--;
                        for(int j = i+1;j<moneysList.size();j++){
                            moneysList.set(j,moneysList.get(j)-1);
                        }
                        break;
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
                    switch(player.getPosition()){
                        case 0:
                            break;
                        case 1:
                        case 2:
                            player.rollBack();
                            break;
                    }
                    moneysList.remove(0);
                    player.setPlaying(false);
                    break;
                }

                if(block.getX() < -110){
                    blocks.remove(i);
                    platforms.remove(i);
                    if (moneysList.get(i) >= 0){
                        moneys.remove(moneysList.get(i));
                        moneysList.set(i,-1);
                        moneyCount--;
                        for(int j = i+1;j<moneysList.size();j++){
                            moneysList.set(j,moneysList.get(j)-1);
                        }
                    }
                    moneysList.remove(0);
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
                money.draw(canvas);
            }

            //HUD
            canvas.drawText(String.valueOf(player.getScore()),1000,100,hudPaint);

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
}
