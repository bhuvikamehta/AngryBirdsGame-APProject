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

public class Level2 implements Screen{
    private AngryBirds game;
    private boolean snd = true;
    private SpriteBatch batch;
    private Sprite bgSprite, pauseGame, exitGame, catapult;
    private Music music_buff, icon_sound, shoot_sound, hit_sound;
    private Preferences preferences;
    private float touchCooldown = 0.5f;  // 0.5 seconds cooldown
    private float lastTouchTime = 0;
    private World world;
    private OrthographicCamera camera;
    private Bird bird1, bird2, bird3, bird4, bird5;
    private Block block1,block2,block3,block4,block5,block6,block7,block8, block9,block10,block11,block12,block13,block14,block15,block16,block17,block18;
    private Pig pig1,pig2,pig3,pig4;

    public Level2(AngryBirds game){
        this.game=game;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0,-9.8f),true);

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Texture bgtexture=new Texture(Gdx.files.internal("Images/Level2-bg.png"));
        Texture pause=new Texture(Gdx.files.internal("Images/Pause.png"));

        bird1=new Bird(world, "Images/redBird.png", "RedBird", 0.15f, 195, 172);
        bird2=new Bird(world, "Images/redBird.png", "RedBird", 0.15f, 155, 172);
        bird3=new Bird(world, "Images/redBird.png", "RedBird", 0.15f, 115, 172);
        bird4=new Bird(world, "Images/yellowBird.png", "YellowBird", 0.17f, 75, 172);
        bird5=new Bird(world, "Images/yellowBird.png", "YellowBird", 0.17f, 35, 172);
        block1 = new Block(world, "Images/baseBlock.png","BlockSetup", 0.3f, 150, 137);
        block2 = new Block(world, "Images/blockvertical.png","BlockSetup",0.4f,410,82);
        block3 = new Block(world, "Images/blockvertical.png","BlockSetup",0.4f,430,82);
        block4 = new Block(world, "Images/blockvertical.png","BlockSetup",0.4f,450,82);
        block5 = new Block(world, "Images/blockvertical.png","BlockSetup",0.4f,575,82);
        block6 = new Block(world, "Images/blockvertical.png","BlockSetup",0.4f,595,82);
        block7 = new Block(world, "Images/blockvertical.png","BlockSetup",0.4f,615,82);
        block8 = new Block(world, "Images/h.png","BlockSetup",0.4f,350,240);
        block9 = new Block(world, "Images/h.png","BlockSetup",0.4f,490,240);
        block10 = new Block(world, "Images/blockvertical.png","BlockSetup",0.4f,470,185);
        block11 = new Block(world, "Images/blockvertical.png","BlockSetup",0.4f,555,185);
        block12 = new Block(world, "Images/h.png","BlockSetup",0.4f,420,340);
        block13= new Block(world, "Images/steelsq.png","BlockSetup", 0.23f,333,170);
        block14= new Block(world, "Images/steelsq.png","BlockSetup", 0.23f,493,170);
        block15 = new Block(world, "Images/steelcircle.png","BlockSetup",0.2f,345,230);
        block16 = new Block(world, "Images/steelcircle.png","BlockSetup",0.2f,505,230);
        block17 = new Block(world, "Images/glasstriangle.png","Blocksetup",0.16f,390,260);
        block18 = new Block(world, "Images/glasstriangle.png","Blocksetup",0.16f,430,260);
        pig1 = new Pig(world, "Images/pig3.png","Pig3",0.12f,183,220);
        pig2 = new Pig(world, "Images/pig2.png","Pig2",0.17f,330,20);
        pig3 = new Pig(world, "Images/pig3.png","Pig3",0.12f,181,-9);
        pig4 = new Pig(world, "Images/pig1.png","Pig1",0.12f,405,12);




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
        catapult.setPosition(50,-50);

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
        bird1.draw(batch);
        bird2.draw(batch);
        bird3.draw(batch);
        bird4.draw(batch);
        bird5.draw(batch);
        catapult.draw(batch);
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
        pig1.draw(batch);
        pig2.draw(batch);
        pig3.draw(batch);
        pig4.draw(batch);

        batch.end();

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
        bird1.dispose();
        bird2.dispose();
        bird3.dispose();
        bird4.dispose();
        bird5.dispose();
        catapult.getTexture().dispose();
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
        pig1.dispose();
        pig2.dispose();
        pig3.dispose();
        pig4.dispose();

    }
}
