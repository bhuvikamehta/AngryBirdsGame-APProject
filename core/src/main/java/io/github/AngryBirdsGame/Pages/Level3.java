package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import io.github.AngryBirdsGame.AngryBirds;

public class Level3 implements Screen{
    private AngryBirds game;
    private boolean snd = true;
    private SpriteBatch batch;
    private Sprite bgSprite, pauseGame, catapult;
    private Music music_buff, icon_sound, shoot_sound, hit_sound;
    private Preferences preferences;
    private float touchCooldown = 0.5f;  // 0.5 seconds cooldown
    private float lastTouchTime = 0;
    private World world;
    private OrthographicCamera camera;
    private Bird bird1, bird2, bird3, bird4, bird5;
    private Block block1, block2, block3, block4, block5, block6, block7, block8, block9, block10;
    private Block block11, block12, block13, block14, block15, block16, block17, block18, block19, block20;
    private Block block21, block22, block23, block24, block25, block26, block27, block28, block29, block30;
    private Block block31;
    private Pig pig1, pig2, pig3, pig4;



    public Level3(AngryBirds game){
        this.game=game;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0,-9.8f),true);

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Texture bgtexture=new Texture(Gdx.files.internal("Images/Level-3_bg.png"));
        Texture pause=new Texture(Gdx.files.internal("Images/Pause.png"));

        bird1=new Bird(world, "Images/redBird.png", "RedBird", 0.15f, 180, 75);
        bird2=new Bird(world, "Images/yellowBird.png", "YellowBird", 0.17f, 140, 78);
        bird3=new Bird(world, "Images/yellowBird.png", "YellowBird", 0.17f, 100, 78);
        bird4=new Bird(world, "Images/bombBird.png", "BombBird", 0.17f, 65, 81);
        bird5=new Bird(world, "Images/bombBird.png", "BombBird", 0.17f, 30, 81);
        block1 = new Block(world, "Images/baseBlock.png","BlockSetup",0.4f,105,50);
        block2 = new Block(world,"Images/blockvertical.png","BlockSetup",0.4f,390,-4);
        block3 = new Block(world,"Images/blockvertical.png","BlockSetup",0.4f,560,-4);
        block4 = new Block(world,"Images/woodsmallblock.png","BlockSetup",0.15f,307,14);
        block5 = new Block(world,"Images/woodsmallblock.png","BlockSetup",0.15f,307,32);
        block6 = new Block(world,"Images/woodsmallblock.png","BlockSetup",0.15f,407,14);
        block7 = new Block(world,"Images/woodsmallblock.png","BlockSetup",0.15f,407,32);
        block8 = new Block(world,"Images/woodsquare.png","BlockSetup",0.17f,375,52);
        block9 = new Block(world,"Images/woodsquare.png","BlockSetup",0.17f,479,52);
        block10 = new Block(world,"Images/woodsmallblock.png","BlockSetup",0.15f,307,72);
        block11 = new Block(world,"Images/woodsmallblock.png","BlockSetup",0.15f,407,72);
        block12 = new Block(world,"Images/woodsmallblock.png","BlockSetup",0.15f,337,90);
        block13 = new Block(world,"Images/woodsmallblock.png","BlockSetup",0.15f,377,90);
        block14 = new Block(world,"Images/h.png","BlockSetup",0.4f,382,109);
        block15 = new Block(world,"Images/woodenbvertical.png","BlockSetup",0.15f,427,48);
        block16 = new Block(world, "Images/glassbvertical.png","BlockSetup",0.15f,370,30);
        block17 = new Block(world, "Images/glassbvertical.png","BlockSetup",0.15f,480,30);
        block18 = new Block(world, "Images/glassbvertical.png","BlockSetup",0.15f,445,48);
        block19 = new Block(world, "Images/glassbvertical.png","BlockSetup",0.15f,403,48);
        block20 = new Block(world, "Images/glasshori.png","BlockSetup",0.2f,155,176);
        block21 = new Block(world, "Images/v.png","BlockSetup",0.3f,385,70);
        block22 = new Block(world, "Images/v.png","BlockSetup",0.3f,465,70);
        block23 = new Block(world, "Images/v.png","BlockSetup",0.3f,290,-78);
        block24 = new Block(world, "Images/v.png","BlockSetup",0.3f,560,-78);
        block25 = new Block(world, "Images/glasshori.png","BlockSetup",0.2f,155,296);
        block26 = new Block(world,"Images/woodsmallblock.png","BlockSetup",0.15f,357,160);
        block27 = new Block(world,"Images/woodenbvertical.png","BlockSetup",0.15f,427,118);
        block28 = new Block(world,"Images/steelsquare.png","BlockSetup",0.22f,429,224);
        block29 = new Block(world, "Images/glasssmallsq.png","BlockSetup",0.22f,390,287);
        block30 = new Block(world, "Images/glasssmallsq.png","BlockSetup",0.22f,429,287);
        block31 = new Block(world, "Images/glasssmallsq.png","BlockSetup",0.22f,468,287);
        pig1 = new Pig(world, "Images/pig3.png","Pig3",0.12f,490,85);
        pig2 = new Pig(world, "Images/pig1.png","Pig1",0.12f,382,85);
        pig3 = new Pig(world, "Images/pig1.png","Pig1",0.12f,600,85);







        Texture Catapult=new Texture(Gdx.files.internal("Images/Catapultimg.png"));


        music_buff = Gdx.audio.newMusic(Gdx.files.internal("Sounds/gamePlay.mp3"));
        icon_sound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/tap.mp3"));

        music_buff.setLooping(true);
        if (!music_buff.isPlaying()) music_buff.play();

        bgSprite=new Sprite(bgtexture);
        pauseGame=new Sprite(pause);
        catapult=new Sprite(Catapult);

        pauseGame.setScale(0.2f);
        catapult.setScale(0.18f);

        pauseGame.setPosition(-150,270);
        catapult.setPosition(30,-148);
    }



    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(bgSprite, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        pauseGame.draw(batch);
        catapult.draw(batch);
        bird1.draw(batch);
        bird2.draw(batch);
        bird3.draw(batch);
        bird4.draw(batch);
        bird5.draw(batch);
        block1.draw(batch);
        block2.draw(batch);
        block3.draw(batch);
        block4.draw(batch);
        block5.draw(batch);
        block6.draw(batch);
        block7.draw(batch);
        block8.draw(batch);
        block9.draw(batch);
        block10.draw(batch);
        block11.draw(batch);
        block12.draw(batch);
        block13.draw(batch);
        block14.draw(batch);
        block15.draw(batch);
        block16.draw(batch);
        block17.draw(batch);
        block18.draw(batch);
        block19.draw(batch);
        block20.draw(batch);
        block21.draw(batch);
        block22.draw(batch);
        block23.draw(batch);
        block24.draw(batch);
        block25.draw(batch);
        block26.draw(batch);
        block27.draw(batch);
        block28.draw(batch);
        block29.draw(batch);
        block30.draw(batch);
        block31.draw(batch);
        pig1.draw(batch);
        pig2.draw(batch);
        pig3.draw(batch);

        batch.end();

        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            // Check each sprite's bounds
            if (pauseGame.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                music_buff.stop();
                playSound();
                game.setScreen(new PauseGamePage(game));
            }
        }
    }
    private void playSound() {
        if (icon_sound != null) {
            icon_sound.play();
        }
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        icon_sound.dispose();
        music_buff.dispose();
        bgSprite.getTexture().dispose();
        pauseGame.getTexture().dispose();
        catapult.getTexture().dispose();
        bird1.dispose();
        bird2.dispose();
        bird3.dispose();
        bird4.dispose();
        bird5.dispose();
        block1.dispose();
        block2.dispose();
        block3.dispose();
        block4.dispose();
        block5.dispose();
        block6.dispose();
        block7.dispose();
        block8.dispose();
        block9.dispose();
        block10.dispose();
        block11.dispose();
        block12.dispose();
        block13.dispose();
        block14.dispose();
        block15.dispose();
        block16.dispose();
        block17.dispose();
        block18.dispose();
        block19.dispose();
        block20.dispose();
        block21.dispose();
        block22.dispose();
        block23.dispose();
        block24.dispose();
        block25.dispose();
        block26.dispose();
        block27.dispose();
        block28.dispose();
        block29.dispose();
        block30.dispose();
        block31.dispose();
        pig1.dispose();
        pig2.dispose();
        pig3.dispose();


    }
}
