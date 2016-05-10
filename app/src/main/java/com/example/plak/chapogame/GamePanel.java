package com.example.plak.chapogame;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.plak.chapogame.Activities.ActivityGameOver;
import com.example.plak.chapogame.Activities.ActivityScore;
import com.example.plak.chapogame.Buttons.Button;
import com.example.plak.chapogame.Items.Money;
import com.example.plak.chapogame.Items.Tequila;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by erikiado on 23-Jan-16.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{


    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public int moveSpeed;
    private long trailStartTime;
    private long blockStartTime;

    private HiloPrincipal thread;
    private Background fondo;
    private Player player;
    private ArrayList<SmokeTrail> trails;
    private ArrayList<Block> blocks;
    private ArrayList<Platform> platforms;
    private ArrayList<Money> moneys;
    private ArrayList<Tequila> tequilas;
    private Random rand = new Random();
    private Paint hudPaint;
    private SoundPool sp;
    private int soundIds[];
    private Context context;
    private int placesBlock [];
    private int totalBloques, numBloqueCambio;
    private int neuBlockPosition, oldBlockPosition;
    private SharedPreferences sharedPreferences;
    private int level;
    private boolean soundOn;
    private FrontParallax parallax;
    private Button pause,playButton;
    private boolean pauseOn;

    public GamePanel(Context cxt, int level){
        super(cxt);
        context = cxt;
        //Interceptar eventos
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        this.level = level;
        thread = new HiloPrincipal(holder,this);
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
        soundOn = sharedPreferences.getBoolean("switch_sound", true);
        initializeSounds();
        setFocusable(true);
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

        switch(level){
            case 1:
                moveSpeed = -5;
                fondo = new Background(BitmapFactory.decodeResource(getResources(),R.drawable.montana),moveSpeed);
                parallax = new FrontParallax(BitmapFactory.decodeResource(getResources(),R.drawable.fondoverde),moveSpeed-2);
                break;
            case 2:
                moveSpeed = -8;
                fondo = new Background(BitmapFactory.decodeResource(getResources(),R.drawable.fondoverde),moveSpeed);
                parallax = new FrontParallax(BitmapFactory.decodeResource(getResources(),R.drawable.fondoverde),moveSpeed-2);
                break;
            case 3:
                moveSpeed = -10;
                fondo = new Background(BitmapFactory.decodeResource(getResources(),R.drawable.fondocarcel),moveSpeed);
                parallax = new FrontParallax(BitmapFactory.decodeResource(getResources(),R.drawable.fondoverde),moveSpeed-2);
                break;
            default:
                moveSpeed = -5;
                fondo = new Background(BitmapFactory.decodeResource(getResources(),R.drawable.montana),moveSpeed);
                parallax = new FrontParallax(BitmapFactory.decodeResource(getResources(),R.drawable.fondoverde),moveSpeed-2);
                break;
        }
        player = new Player(BitmapFactory.decodeResource(getResources(),R.drawable.sprite_player),80,100,5);
        trails = new ArrayList<SmokeTrail>();
        blocks = new ArrayList<Block>();
        platforms = new ArrayList<Platform>();
        moneys = new ArrayList<Money>();
        tequilas = new ArrayList<Tequila>();
        trailStartTime = System.nanoTime();
        blockStartTime = System.nanoTime();
        pause = new Button(BitmapFactory.decodeResource(getResources(),R.drawable.bloque),50,50,80,80);
        playButton = new Button(BitmapFactory.decodeResource(getResources(),R.drawable.bloque),600,250,80,80);

        pauseOn = false;

        placesBlock = new int [3];
        placesBlock [0] = 450;
        placesBlock [1] = 320;
        placesBlock [2] = 250;

        totalBloques = 0;
        numBloqueCambio = 0;
        neuBlockPosition = 0;
        oldBlockPosition = 0;

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

        float posX = event.getX();
        float posY = event.getY();

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(pauseOn) {
                if(posX > 600 && posX < 680 && posY > 250 && posY <330){
                    player.setPlaying(true);
                    pauseOn = false;
                    //Resto de la pantalla
                }

            //Si el jugador NO esta jugando, jugar
            }else if(!player.getPlaying()){
                player.setPlaying(true);
            //Mientras se juega
            }else{
                //Click Boton Pausa
                if(posX > 50 && posX < 130 && posY > 50 && posY <130){
                    player.setPlaying(false);
                    pauseOn = true;
                //Resto de la pantalla
                }else{
                    //Si estoy en tocando, brinco
                    if(player.onGround()){
                        player.jump();
                        if(soundOn){
                            sp.play(soundIds[0], 1, 1, 1, 0, (float) 1.0);
                        }
                    }
                }

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
            parallax.update();
            player.update();


            //Spawnear bloques dependiendo del tiempo
            long blockElapsed = (System.nanoTime() - blockStartTime) / 1000000;
            //Cada 1.8 segundos spawnear
            if (blockElapsed > 1800) {//(2000 - player.getScore()/4)){
                if (blocks.size() == 0) {
                    Block b = new Block(BitmapFactory.decodeResource(getResources(), R.drawable.blocknara), WIDTH + 10, placesBlock[0], 100, 40, moveSpeed, 1);
                    blocks.add(b);
                    platforms.add(new Platform(BitmapFactory.decodeResource(getResources(), R.drawable.fondoverde), WIDTH + 10, 450, 100, 10, 1));
                    moneys.add(new Money(BitmapFactory.decodeResource(getResources(), R.drawable.weed), WIDTH + 40, 370, 38, 45, moveSpeed, 1));
                } else {
                    if (neuBlockPosition == 3) {
                        neuBlockPosition = 0;
                    } else if (neuBlockPosition == 0) {
                        neuBlockPosition = rand.nextInt(4);
                        if (neuBlockPosition == 2) {
                            neuBlockPosition = rand.nextInt(2);
                        }
                    } else {
                        neuBlockPosition = rand.nextInt(3) + 1;
                    }

                    if (neuBlockPosition != 3) {
                        // to know if weed should appear
                        Block b = new Block(BitmapFactory.decodeResource(getResources(), R.drawable.blocknara), WIDTH + 10, placesBlock[neuBlockPosition], 100, 40, moveSpeed, 1);
                        blocks.add(b);
                        platforms.add(new Platform(BitmapFactory.decodeResource(getResources(), R.drawable.fondoverde), WIDTH + 10, placesBlock[neuBlockPosition], 100, 10, 1));
                        if (player.getPosition() == 2) {
                            moneys.add(new Money(BitmapFactory.decodeResource(getResources(), R.drawable.weed), WIDTH + 40, placesBlock[neuBlockPosition] - 80, 38, 45, moveSpeed, 1));
                        } else {
                            if (totalBloques % 20 == 0) {
                                tequilas.add(new Tequila(BitmapFactory.decodeResource(getResources(), R.drawable.rsz_1tequikate), WIDTH + 40, placesBlock[neuBlockPosition] - 80, 35, 80, moveSpeed, 1));
                            } else {
                                moneys.add(new Money(BitmapFactory.decodeResource(getResources(), R.drawable.weed), WIDTH + 40, placesBlock[neuBlockPosition] - 80, 38, 45, moveSpeed, 1));
                            }
                        }
                    }

                }
                blockStartTime = System.nanoTime();
                if (neuBlockPosition != 3) {
                    totalBloques++;
                }
            }


            //Actualizar bloques
            for (int i = 0; i < blocks.size(); i++) {
                Block block = blocks.get(i);
                block.update();
                platforms.get(i).update(block);
            }

            for (int i = 0; i < moneys.size(); i++) {
                moneys.get(i).update();
            }

            for (int i = 0; i < tequilas.size(); i++) {
                tequilas.get(i).update();
            }


            for (int i = 0; i < moneys.size(); i++) {
                Money m = moneys.get(i);
                if (!m.isPicked()) {
                    if (colisionBloque(player, m)) {
                        player.juntarDinero();
                        m.pickUp();
                    }
                }
            }

            for (int i = 0; i < tequilas.size(); i++) {
                Tequila t = tequilas.get(i);
                if (!t.isPicked()) {
                    if (colisionBloque(player, t)) {
                        player.juntarTequila();
                        t.pickUp();
                    }
                }
            }

            for (int i = 0; i < blocks.size() && i < 3; i++) {
                Block block = blocks.get(i);
                Platform platform = platforms.get(i);

                if (paso(player, platform)) {
                    if (player.onPlatform()) {
                        player.fall();
                    }
                } else if (colisionPlatform(player, platform)) {
                    player.stand(platform);
                } else if (colisionBloque(player, block)) {
                    blocks.remove(i);
                    platforms.remove(i);
                    switch (player.getPosition()) {
                        case 0:
                            player.setPlaying(false);
                            Intent intent;
                            if(player.getWin()){
                                intent = new Intent(context, ActivityScore.class);
                            }else{
                                intent = new Intent(context, ActivityGameOver.class);
                            }
                            intent.putExtra("cur_level", level);
                            intent.putExtra("score", player.getScore());
//                                ArrayList scores = new ArrayList();
//                                scores.add(player.getScore());
                            context.startActivity(intent);
                            ((Activity) context).finish();
                            break;
                        case 1:
                        case 2:
                            player.rollBack();
                            break;
                    }

                    break;
                }

            }


            checarBorde();



            long elapsed = (System.nanoTime() - trailStartTime) / 1000000;
            if (elapsed > 120) {
                trails.add(new SmokeTrail(player.getX(), player.getY() + 100));
                trailStartTime = System.nanoTime();
            }

            for (int i = 0; i < trails.size(); i++) {
                trails.get(i).update();
                if (trails.get(i).getX() < -10) {
                    trails.remove(i);
                }
            }



            if(totalBloques%15 == 14 && totalBloques > numBloqueCambio){
                moveSpeed --;
                numBloqueCambio = totalBloques;
            }

            //hacer que el jugador ya puede ganar
//            if(player.getScore()>250){
//                moveSpeed = -10;
//            }else if(player.getScore() > 200){
//                moveSpeed = -9;
//            }else if(player.getScore() > 150){
//                moveSpeed = -8;
//            }else if(player.getScore() > 100){
//                moveSpeed = -7;
//            }else if(player.getScore() > 50){
//                moveSpeed = -6;
//            }else{
//                moveSpeed = -5;
//            }

            fondo.changeSpeed(moveSpeed);
            parallax.changeSpeed(moveSpeed-2);


            if (player.getScore() > 200) {
                player.setWin(true);
            }

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
            parallax.draw(canvas);
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

            for(Tequila tequila: tequilas){
                if(!tequila.isPicked()){
                    tequila.draw(canvas);
                }
            }

            if (player.getWin()) {
                canvas.drawText("Ganaste", (WIDTH/2)-50, 200, hudPaint);
            }

            //HUD
            if(pauseOn){
                playButton.draw(canvas);
            }else{
                canvas.drawText(String.valueOf(player.getScore()*12),1000,100,hudPaint);
                pause.draw(canvas);
            }
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
        for(int i = 0; i < tequilas.size();i++){
            Tequila t = tequilas.get(i);
            if(t.getX() < 5){
                tequilas.remove(i);
                break;
            }
        }
    }


}
