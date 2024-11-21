package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import io.github.AngryBirdsGame.AngryBirds;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.math.Rectangle;

public class Level1 implements Screen {
    private AngryBirds game;
    private boolean snd = true;
    private SpriteBatch batch;
    private Sprite bgSprite, pauseGame, exitGame, catapult, winPage, losePage, lockedGame;
    private Bird bird1, bird2, bird3;
    private Pig pig1, pig2;
    private Block block1, block2, block3, block4, block5, block6, block7, block8;
    private Music music_buff, icon_sound, shoot_sound, hit_sound;
    private OrthographicCamera camera;
    private World world;
    protected static final float PIXELS_TO_METERS = 100f;
    private Bird currentBird;
    private Queue<Bird> birdQueue;
    private int currentBirdIndex = -1;
    private float[] initialBirdX = new float[3];
    private float[] initialBirdY = new float[3];
    private float catapultX = 170;
    private float catapultY = 228;

    public Level1(AngryBirds game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0, -9.8f), true);

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Create ground body
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(0, 45 / PIXELS_TO_METERS); // Position at bottom of the screen
        Body groundBody = world.createBody(groundBodyDef);

        // Create ground fixture
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(camera.viewportWidth / 2, 1); // Width of screen, very thin height

        FixtureDef groundFixtureDef = new FixtureDef();
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.friction = 0.5f;
        groundBody.createFixture(groundFixtureDef);

        groundShape.dispose();

        Texture bgtexture = new Texture(Gdx.files.internal("Images/Level1-bg.jpg"));
        bird1 = new Bird(world, "Images/redBird.png", "RedBird", 0.15f, 130, 220);
        bird2 = new Bird(world, "Images/redBird.png", "RedBird", 0.15f, 110, 220);
        bird3 = new Bird(world, "Images/redBird.png", "RedBird", 0.15f, 90, 220);

        pig1 = new Pig(world, "Images/pig1.png", "Pig1", 0.12f, 520, 360);
        pig2 = new Pig(world, "Images/pig2.png", "Pig2", 0.12f, 520, 290);

        block1 = new Block(world, "Images/blockhorizontal.png", "BlockSetup", 0.35f, 437, 330);
        block2 = new Block(world, "Images/blockvertical.png", "BlockSetup", 0.35f, 480, 180);
        block3 = new Block(world, "Images/blockvertical.png", "BlockSetup", 0.35f, 520, 180);
        block4 = new Block(world, "Images/v.png", "BlockSetup", 0.3f, 430, 10);
        block5 = new Block(world, "Images/v.png", "BlockSetup", 0.3f, 470, 10);
        block6 = new Block(world, "Images/h.png", "BlockSetup", 0.4f, 415, 250);
        block7 = new Block(world, "Images/blockvertical.png", "BlockSetup", 0.48f, 460, 90);
        block8 = new Block(world, "Images/blockvertical.png", "BlockSetup", 0.48f, 550, 90);

        Texture Catapult = new Texture(Gdx.files.internal("Images/Catapultimg.png"));
        Texture pauseG = new Texture(Gdx.files.internal("Images/Pause.png"));
        Texture winIcon = new Texture(Gdx.files.internal("Images/win.png"));
        Texture loseIcon = new Texture(Gdx.files.internal("Images/lose.png"));

        birdQueue = new LinkedList<>();
        birdQueue.add(bird1);
        birdQueue.add(bird2);
        birdQueue.add(bird3);

        // Initialize bird queue and store initial positions
        int i = 0;
        for (Bird b : birdQueue) {
            // Store initial positions
            initialBirdX[i] = b.getX();
            initialBirdY[i] = b.getY();
            i++;
        }

        // Playing game sound
        music_buff = Gdx.audio.newMusic(Gdx.files.internal("Sounds/gamePlay.mp3"));
        icon_sound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/tap.mp3"));

        music_buff.setLooping(true);
        if (!music_buff.isPlaying()) music_buff.play();

        bgSprite = new Sprite(bgtexture);
        pauseGame = new Sprite(pauseG);
        catapult = new Sprite(Catapult);
        winPage = new Sprite(winIcon);
        losePage = new Sprite(loseIcon);

        pauseGame.setScale(0.2f);
        catapult.setScale(0.2f);
        winPage.setScale(0.15f);
        losePage.setScale(0.15f);

        catapult.setPosition(0, -48);
        pauseGame.setPosition(-150, 270);
        winPage.setPosition(360, 275);
        losePage.setPosition(410, 285);
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

    private void swapBirdsOnCatapult(Bird currentBird, Bird newBird) {
        if (currentBird == null || newBird == null) return;

        // Store current bird's initial position and body properties
        float currentBirdX = currentBird.getBirdBody().getPosition().x * Bird.PIXELS_TO_METERS;
        float currentBirdY = currentBird.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS;
        float currentBirdAngle = currentBird.getBirdBody().getAngle();
        Vector2 currentBirdVelocity = currentBird.getBirdBody().getLinearVelocity();
        float currentBirdAngularVelocity = currentBird.getBirdBody().getAngularVelocity();

        // Store new bird's initial position and body properties
        float newBirdX = newBird.getBirdBody().getPosition().x * Bird.PIXELS_TO_METERS;
        float newBirdY = newBird.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS;
        float newBirdAngle = newBird.getBirdBody().getAngle();
        Vector2 newBirdVelocity = newBird.getBirdBody().getLinearVelocity();
        float newBirdAngularVelocity = newBird.getBirdBody().getAngularVelocity();

        // Swap body positions and properties
        currentBird.getBirdBody().setTransform(
            newBird.getBirdBody().getPosition().x,
            newBird.getBirdBody().getPosition().y,
            newBird.getBirdBody().getAngle()
//            newBirdX / Bird.PIXELS_TO_METERS,
//            newBirdY / Bird.PIXELS_TO_METERS,
//            newBirdAngle
        );
        currentBird.getBirdBody().setLinearVelocity(newBird.getBirdBody().getLinearVelocity());
        currentBird.getBirdBody().setAngularVelocity(newBird.getBirdBody().getAngularVelocity());
        currentBird.getBirdBody().setGravityScale(0);

        newBird.getBirdBody().setTransform(
            currentBirdX / Bird.PIXELS_TO_METERS,
            currentBirdY / Bird.PIXELS_TO_METERS,
            currentBirdAngle
        );
        newBird.getBirdBody().setLinearVelocity(currentBirdVelocity);
        newBird.getBirdBody().setAngularVelocity(currentBirdAngularVelocity);
        newBird.getBirdBody().setGravityScale(1);

        // Update sprite positions to match new body positions
        currentBird.objectSprite.setPosition(newBird.getBirdBody().getPosition().x * Bird.PIXELS_TO_METERS,
            newBird.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS);
        newBird.objectSprite.setPosition(currentBirdX, currentBirdY);

        // Remove the new bird from the queue
        birdQueue.remove(newBird);

        // Set the new bird as the current bird
        this.currentBird = newBird;
    }


    @Override
    public void render(float delta) {
        world.step(delta, 6, 2);
        pig1.lockPosition();
        pig2.lockPosition();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        bgSprite.draw(batch);
        pauseGame.draw(batch);
        bird1.draw(batch);
        bird2.draw(batch);
        bird3.draw(batch);
        pig1.draw(batch);
        pig2.draw(batch);
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

        if (bird1.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS < 45) resetBirdCompletely(bird1, 0);
        if (bird2.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS < 45) resetBirdCompletely(bird2, 1);
        if (bird3.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS < 45) resetBirdCompletely(bird3, 2);

        batch.end();

        // Handle touch input for bird selection and launching
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            // Check pause game button
            if (pauseGame.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                music_buff.stop();
                playSound();
                game.setScreen(new PauseGamePage(game));
                return;
            }

            // Check win page button
            if (winPage.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                music_buff.stop();
                playSound();
                game.setScreen(new WinPage(game));
                return;
            }

            // Check lose page button
            if (losePage.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                music_buff.stop();
                playSound();
                game.setScreen(new LosePage(game));
                return;
            }

            //If any bird in queue has been touched until now
            List<Bird> birdList = Arrays.asList(bird1, bird2, bird3);
            for (Bird queuedBird : birdList) {
                // Expanded touch detection to include a larger area around the bird
                if (isBirdTouched(queuedBird, touchPos.x, touchPos.y) && birdQueue.contains(queuedBird)) {
                    // If a bird is currently on the catapult
                    if (currentBird != null) {
                        // Swap the birds, keeping their visual and physical properties
                        swapBirdsOnCatapult(currentBird, queuedBird);
                        currentBird = null;
                    } else {
                        // If no bird is on the catapult, just position the new bird
                        currentBird = queuedBird;
                        birdQueue.remove(queuedBird);
                        positionBirdOnCatapult(currentBird);
                    }
                    return;
                }
            }

            // Bird selection and launching logic
            if (currentBird != null ) {
                if (isBirdTouched(currentBird, touchPos.x, touchPos.y)) {
                    if (currentBird.isReadyForLaunch()&&currentBird!=null) {
                        float launchForceX = (catapultX - touchPos.x) * 0.5f;
                        float launchForceY = (catapultY - touchPos.y) * 0.5f;

                        Vector2 launchVelocity = new Vector2(launchForceX, launchForceY);
                        currentBird.launch(launchVelocity);

                        selectBird();
                    }
                }
            }
        }
    }

    private boolean isBirdTouched(Bird bird, float touchX, float touchY) {
        if (bird == null || bird.objectSprite == null) return false;

        // Expand the touch area around the bird sprite
        float expandedWidth = bird.objectSprite.getWidth() * 2;
        float expandedHeight = bird.objectSprite.getHeight() * 2;

        // Calculate the expanded bounding rectangle
        float minX = bird.objectSprite.getX() - (expandedWidth - bird.objectSprite.getWidth()) / 2;
        float minY = bird.objectSprite.getY() - (expandedHeight - bird.objectSprite.getHeight()) / 2;

        // Create a rectangle with expanded boundaries
        Rectangle expandedBounds = new Rectangle(
            minX,
            minY,
            expandedWidth,
            expandedHeight
        );

        // Check if the touch point is within the expanded bounds
        return expandedBounds.contains(touchX, touchY);
    }

    private void placeBirdAtGroundPosition(Bird bird) {
        if (bird != null) {
            // Find the index of the reference bird
            int index = -1;
            if (bird == bird1) index = 0;
            else if (bird == bird2) index = 1;
            else if (bird == bird3) index = 2;

            if (index != -1) {
                // Remove the bird from the world to reset its physics state
                world.destroyBody(bird.getBirdBody());

                // Recreate the bird with its original parameters
                Bird newBird = new Bird(world, "Images/redBird.png", "RedBird", 0.15f, initialBirdX[index], initialBirdY[index]);

                // Update the corresponding bird reference
                if (index == 0) bird1 = newBird;
                else if (index == 1) bird2 = newBird;
                else bird3 = newBird;

                // Add the new bird to the queue if it's not already there
                if (!birdQueue.contains(newBird)) {
                    birdQueue.add(newBird);
                }

                // Dispose of the old bird to prevent memory leaks
                bird.dispose();
            }

//            // Place the bird to be replaced at the ground position
//            float groundX = initialBirdX[referenceIndex];
//            float groundY = initialBirdY[referenceIndex];

//            // Reset bird's physics body
//            birdToReplace.getBirdBody().setGravityScale(1);
//            birdToReplace.getBirdBody().setTransform(
//                groundX / Bird.PIXELS_TO_METERS,
//                groundY / Bird.PIXELS_TO_METERS,
//                0
//            );
//            birdToReplace.getBirdBody().setLinearVelocity(0, 0);
//            birdToReplace.getBirdBody().setAngularVelocity(0);
//
//            // Update sprite position
//            birdToReplace.objectSprite.setPosition(groundX, groundY);
//
//            // Add back to the queue
//            birdQueue.add(birdToReplace);
        }
    }

    private void resetBirdCompletely(Bird bird, int index) {
        // Remove the bird from the world
        world.destroyBody(bird.getBirdBody());

        // Recreate the bird with its original parameters
        Bird newBird = new Bird(world, "Images/redBird.png", "RedBird", 0.15f, initialBirdX[index], initialBirdY[index]);

        // Update the corresponding bird reference
        if (index == 0) bird1 = newBird;
        else if (index == 1) bird2 = newBird;
        else bird3 = newBird;

        // Add back to the queue if not already in it
        if (!birdQueue.contains(newBird)) {
            birdQueue.add(newBird);
        }

        // Dispose of the old bird to prevent memory leaks
        bird.dispose();
    }

    private void selectBird() {
        // If there are birds in the queue, select the next one
        if (!birdQueue.isEmpty()) {
            currentBird = birdQueue.poll();
            positionBirdOnCatapult(currentBird);
        } else {
            // No more birds left
            currentBird = null;
        }
    }

    private void positionBirdOnCatapult(Bird bird) {
        if (bird != null) {
            float catapultX = 170; // Your original catapult X position
            float catapultY = 228; // Your original catapult Y position

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
    }

    private void resetBirdToQueuePosition(Bird bird) {
        if (bird != null) {
            // Put the bird back into the queue
            birdQueue.add(bird);

            // Reset bird to its initial position
            bird.setPosition(
                initialBirdX[birdQueue.size() - 1],
                initialBirdY[birdQueue.size() - 1]
            );

            // Stop the bird's movement
            bird.stopMovement();
        }
    }

    @Override
    public void resize(int width, int height) {
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
