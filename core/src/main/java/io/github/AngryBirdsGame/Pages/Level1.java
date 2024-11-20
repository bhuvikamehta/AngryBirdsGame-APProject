package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import io.github.AngryBirdsGame.AngryBirds;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Level1 implements Screen {
    private AngryBirds game;
    private boolean snd = true;
    private SpriteBatch batch;
    private Sprite bgSprite, pauseGame, exitGame,catapult, winPage, losePage, lockedGame;
    //private GameObject catapult;
    private Bird bird1, bird2, bird3;
    private Pig pig1, pig2;
    private Block block1, block2, block3, block4, block5, block6, block7, block8;
    private Music music_buff, icon_sound, shoot_sound, hit_sound;
    private OrthographicCamera camera;
    private World world;
    protected static final float PIXELS_TO_METERS = 100f;
    private Bird currentBird;
    private Bird[] birdQueue;
    private int currentBirdIndex=-1;
    private float[] initialBirdX = new float[3];
    private float[] initialBirdY = new float[3];
    private float catapultX = 170;
    private float catapultY = 228;


    public Level1(AngryBirds game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.world=new World(new Vector2(0, -9.8f), true);

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


// Create ground body
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(0, 45/ PIXELS_TO_METERS); // Position at bottom of the screen
        Body groundBody = world.createBody(groundBodyDef);
        // Create ground fixture
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(camera.viewportWidth / 2, 1); // Width of screen, very thin height

        FixtureDef groundFixtureDef = new FixtureDef();
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.friction = 0.5f;
        groundBody.createFixture(groundFixtureDef);


        groundShape.dispose();

        Texture bgtexture=new Texture(Gdx.files.internal("Images/Level1-bg.jpg"));
        bird1=new Bird(world, "Images/redBird.png", "RedBird", 0.15f, 130, 220);
//        bird2=new Bird(world, "Images/yellowBird.png", "YellowBird", 0.15f, 90, 250);
//        bird3=new Bird(world, "Images/bombBird.png", "BombBird", 0.15f, 50, 260);
        bird2=new Bird(world, "Images/redBird.png", "RedBird", 0.15f, 110, 220);

        bird3=new Bird(world, "Images/redBird.png", "RedBird", 0.15f, 90, 220);

        pig1=new Pig(world, "Images/pig1.png", "Pig1", 0.12f, 357, 200);
        pig2=new Pig(world, "Images/pig2.png", "Pig2", 0.12f, 358, 128);
        block1=new Block(world, "Images/blockhorizontal.png", "BlockSetup", 0.35f,  437, 330);
        block2=new Block(world, "Images/blockvertical.png", "BlockSetup", 0.35f,  480, 180);
        block3=new Block(world, "Images/blockvertical.png", "BlockSetup", 0.35f,  520, 180);
        block4=new Block(world, "Images/v.png", "BlockSetup", 0.3f,  430, 10);
        block5=new Block(world, "Images/v.png", "BlockSetup", 0.3f,  470, 10);
        block6=new Block(world, "Images/h.png", "BlockSetup", 0.4f, 415, 250);
        block7=new Block(world, "Images/blockvertical.png", "BlockSetup", 0.48f, 460, 90);
        block8=new Block(world, "Images/blockvertical.png", "BlockSetup", 0.48f, 550, 90);
        Texture Catapult=new Texture(Gdx.files.internal("Images/Catapultimg.png"));
        Texture pauseG=new Texture(Gdx.files.internal("Images/Pause.png"));
        // Texture exit=new Texture(Gdx.files.internal("Images/exitgameICON.png"));
        Texture winIcon=new Texture(Gdx.files.internal("Images/win.png"));
        Texture loseIcon=new Texture(Gdx.files.internal("Images/lose.png"));

        birdQueue = new Bird[]{bird1, bird2, bird3};
        // Initialize bird queue and store initial positions

        for (int i = 0; i < birdQueue.length; i++) {
            // Store initial positions
            initialBirdX[i] = birdQueue[i].getX();
            initialBirdY[i] = birdQueue[i].getY();

            // Restore default gravity
//            birdQueue[i].getBirdBody().setGravityScale(1.5f);
        }


//        //playing gamesound before shoot
        music_buff = Gdx.audio.newMusic(Gdx.files.internal("Sounds/gamePlay.mp3"));
        icon_sound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/tap.mp3"));
//        shoot_sound=Gdx.audio.newMusic(Gdx.files.internal());
//        hit_sound=Gdx.audio.newMusic(Gdx.files.internal());

        music_buff.setLooping(true);
        if (!music_buff.isPlaying()) music_buff.play();

        bgSprite=new Sprite(bgtexture);
        pauseGame=new Sprite(pauseG);


        // pig3=new Sprite(Pig3);
        catapult=new Sprite(Catapult);
        //  exitGame=new Sprite(exit);
        winPage=new Sprite(winIcon);
        losePage=new Sprite(loseIcon);



        pauseGame.setScale(0.2f);
        // exitGame.setScale(0.2f);
//        bird1.setScale(0.2f);
//        bird2.setScale(0.2f);
//        bird3.setScale(0.2f);


        // pig3.setScale(0.08f);
        catapult.setScale(0.2f);
        // exitGame.setScale(0.2f);
        winPage.setScale(0.15f);
        losePage.setScale(0.15f);

        //  pauseGame.setPosition(-150, 270);
        //     exitGame.setPosition(-150, 220);
        catapult.setPosition(0, -48);
//        bird1.setPosition(-10, 30);
//        bird2.setPosition(-20, 73);
//        bird3.setPosition(-40, 55);

        // pig3.setPosition(175, 60);
//        blocks.setPosition(360, 40);
//        winPage.setPosition(400, 270);
//        losePage.setPosition(400, 260);
        // Update sprite positions to use absolute coordinates
//        pauseGame.setPosition(50, Gdx.graphics.getHeight() - 100);  // Adjusted for visibility
        pauseGame.setPosition(-150,270);
        //   exitGame.setPosition(50, Gdx.graphics.getHeight() - 150);   // Positioned below pause
//        winPage.setPosition(Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 100);
//        losePage.setPosition(Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 150);
        winPage.setPosition(360,275);
        losePage.setPosition(410,285);

        // Rest of your initialization code remains the same
    }

    @Override
    public void show() {

    }
    private void stopBirdIfAtGround(Bird bird) {
        if (bird.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS < 45) { // Adjust ground level as needed
            bird.getBirdBody().setLinearVelocity(0, 0);
            bird.getBirdBody().setAngularVelocity(0);
        }
    }

    private void selectBird(int index) {
        // If a bird is currently on the catapult, return it to queue
        if (currentBirdIndex != -1) {
            resetBirdToQueuePosition(birdQueue[currentBirdIndex]);
        }

        // Select new bird
        currentBird = birdQueue[index];
        currentBirdIndex = index;

        // Position bird on catapult
        positionBirdOnCatapult(currentBird);
    }

    private void positionBirdOnCatapult(Bird bird) {
        // Disable gravity and set precise position
        bird.getBirdBody().setGravityScale(0);
        bird.getBirdBody().setTransform(
            catapultX / Bird.PIXELS_TO_METERS,
            catapultY / Bird.PIXELS_TO_METERS,
            0
        );
        bird.getBirdBody().setLinearVelocity(0, 0);
        bird.getBirdBody().setAngularVelocity(0);

        // Update sprite position to match
        bird.objectSprite.setPosition(catapultX, catapultY);
    }



    private void resetBirdToQueuePosition(Bird bird) {
        // Find the bird's original index
        int index = -1;
        for (int i = 0; i < birdQueue.length; i++) {
            if (birdQueue[i] == bird) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            // Always reset to the original initial position
            float originalX = initialBirdX[index];
            float originalY = initialBirdY[index];

            // Force the bird back to its original position
            bird.setPosition(originalX, originalY);

            // Restore original gravity
            bird.getBirdBody().setGravityScale(1.5f);

            // Ensure the bird is completely stopped
            bird.stopMovement();
        }

        // Reset current bird tracking
        currentBird = null;
        currentBirdIndex = -1;
    }
    @Override
    public void render(float delta) {
        world.step(delta, 6, 2);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        bgSprite.draw(batch);
        pauseGame.draw(batch);
        //  exitGame.draw(batch);
        bird1.draw(batch);
        bird2.draw(batch);
        bird3.draw(batch);
        pig1.draw(batch);
        pig2.draw(batch);
        // pig3.draw(batch);
        catapult.draw(batch);
        winPage.draw(batch);
        losePage.draw(batch);
        block1.draw(batch);
        block2.draw(batch);
        block3.draw(batch);
        block4.draw(batch);
        block5.draw(batch);
        block6.draw(batch);
        block7.draw(batch);
        block8.draw(batch);
        stopBirdIfAtGround(bird1);
        stopBirdIfAtGround(bird2);
        stopBirdIfAtGround(bird3);

        batch.end();
        // Handle touch input for bird selection
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            // Check if a bird is touched
            for (int i = 0; i < birdQueue.length; i++) {
                Bird bird = birdQueue[i];
                if (bird.objectSprite.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                    selectBird(i);
                    break;
                }
            }
        }

        // Handle touch input
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            // Check each sprite's bounds
            if (pauseGame.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                music_buff.stop();
                playSound();
                game.setScreen(new PauseGamePage(game));
            } else if (winPage.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                music_buff.stop();
                playSound();
                game.setScreen(new WinPage(game));
            } else if (losePage.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                music_buff.stop();
                playSound();
                game.setScreen(new LosePage(game));
            }
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

    private void playSound() {
        if (icon_sound != null) {
            icon_sound.play();
        }
    }



    @Override
    public void dispose() {
        batch.dispose();
        bgSprite.getTexture().dispose();
        icon_sound.dispose();
        music_buff.dispose();
        pauseGame.getTexture().dispose();
        bird1.dispose();
        bird2.dispose();
        bird3.dispose();
        pig1.dispose();
        pig2.dispose();
        block1.dispose();
        block2.dispose();
        block3.dispose();
        block4.dispose();
        block5.dispose();
        block6.dispose();
        block7.dispose();
        block8.dispose();
        catapult.getTexture().dispose();
        winPage.getTexture().dispose();
        losePage.getTexture().dispose();




    }
}
