package io.github.AngryBirdsGame.Pages;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.AngryBirdsGame.AngryBirds;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.AngryBirdsGame.Pages.CollisionDestruction;

import java.util.*;

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
    private boolean isDragging = false;
    private Vector2 dragStart = new Vector2();
    private Vector2 dragCurrent = new Vector2();
    private static final float MAX_DRAG_DISTANCE = 60f;
    private static final float LAUNCH_FORCE_MULTIPLIER = 0.5f;
    private static final float COLLISION_IMPULSE_THRESHOLD = 5.0f;
    private static final float SLINGSHOT_LEFT_X = 165f;  // Left band anchor point
    private static final float SLINGSHOT_RIGHT_X = 175f; // Right band anchor point
    private static final float SLINGSHOT_Y = 250f;      // Band anchor Y position
    private ShapeRenderer shapeRenderer;                // For drawing the bands
    private static final Color BAND_COLOR = new Color(0.4f, 0.2f, 0.1f, 1f);// Brown rubber band color
    private boolean[] pigDestroyed;
    private boolean[] blockDestroyed;
    private Array<CollisionDestruction> activeDestructions;
    // Add fields to track destruction state
    private Array<GameObject> objectsToDestroy;
    private boolean isDestroying;


    public Level1(AngryBirds game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0, -9.8f), true);
        pigDestroyed = new boolean[2];
        blockDestroyed = new boolean[8];
        activeDestructions = new Array<>();
        objectsToDestroy=new Array<>();
        isDestroying=false;


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

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        //  world.setContactListener(new CollisionContactListener());
    }




    @Override
    public void show() {
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

    private boolean isCollisionBetween(Class<?> class1, Class<?> class2, Object obj1, Object obj2) {
        return (class1.isInstance(obj1) && class2.isInstance(obj2)) ||
            (class1.isInstance(obj2) && class2.isInstance(obj1));
    }

    private void handleBirdSelection(float touchX, float touchY) {
        // Check if we're touching any bird in the queue
        Iterator<Bird> iterator = birdQueue.iterator();
        while (iterator.hasNext()) {
            Bird queuedBird = iterator.next();
            if (isBirdTouched(queuedBird, touchX, touchY)) {
                if (currentBird == null) {
                    // No bird on catapult - place the touched bird
                    currentBird = queuedBird;
                    iterator.remove();
                    positionBirdOnCatapult(currentBird);
                } else {
                    // Bird already on catapult - swap them
                    Vector2 tempPos = new Vector2(queuedBird.getBirdBody().getPosition().x * PIXELS_TO_METERS,
                        queuedBird.getBirdBody().getPosition().y * PIXELS_TO_METERS);

                    // Move queued bird to catapult
                    iterator.remove();
                    positionBirdOnCatapult(queuedBird);

                    // Move current bird back to queue position
                    currentBird.getBirdBody().setType(BodyDef.BodyType.KinematicBody);
                    currentBird.setPosition(tempPos.x, tempPos.y);
                    birdQueue.add(currentBird);

                    // Update current bird reference
                    currentBird = queuedBird;
                }
                return;
            }
        }
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
        shapeRenderer.setProjectionMatrix(camera.combined);
        handleDragInput();

        batch.begin();
        bgSprite.draw(batch);
        pauseGame.draw(batch);

        // Draw catapult base behind the bands
        catapult.draw(batch);
        batch.end();

        // Draw rubber bands if dragging
        if (isDragging && currentBird != null) {
            drawRubberBands();
        }

        batch.begin();
        bird1.draw(batch);
        bird2.draw(batch);
        bird3.draw(batch);
        pig1.draw(batch);
        pig2.draw(batch);
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

            handleBirdSelection(touchPos.x, touchPos.y);

            // Bird selection and launching logic
            if (currentBird != null && isBirdTouched(currentBird, touchPos.x, touchPos.y)) {
                isDragging = true;
                dragStart.set(touchPos.x, touchPos.y);
            }

            if (currentBird != null) {
                handleDragInput();
            }
        }

    }



    private void handleDragInput() {
        if (currentBird == null) return;

        // Get mouse/touch position and convert to world coordinates
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);

        if (Gdx.input.isTouched()) {
            // Check if we're starting a new drag
            if (!isDragging) {
                // Only start dragging if touching near the bird
                float birdX = currentBird.getBirdBody().getPosition().x * PIXELS_TO_METERS;
                float birdY = currentBird.getBirdBody().getPosition().y * PIXELS_TO_METERS;

                if (Vector2.dst(birdX, birdY, touchPos.x, touchPos.y) < 50) {
                    isDragging = true;
                    dragStart.set(touchPos.x, touchPos.y);
                }
            }

            if (isDragging) {
                dragCurrent.set(touchPos.x, touchPos.y);

                // Calculate the drag vector from the catapult position
                float dragX = catapultX - touchPos.x;
                float dragY = catapultY - touchPos.y;

                // Limit the drag distance
                float dragDistance = (float) Math.sqrt(dragX * dragX + dragY * dragY);
                if (dragDistance > MAX_DRAG_DISTANCE) {
                    float scale = MAX_DRAG_DISTANCE / dragDistance;
                    dragX *= scale;
                    dragY *= scale;
                }

                // Update bird position
                float newX = catapultX - dragX;
                float newY = catapultY - dragY;

                // Update bird body position
                currentBird.getBirdBody().setTransform(
                    newX / PIXELS_TO_METERS,
                    newY / PIXELS_TO_METERS,
                    0
                );

                // Update sprite position to match physics body
                currentBird.objectSprite.setPosition(
                    currentBird.getBirdBody().getPosition().x * PIXELS_TO_METERS,
                    currentBird.getBirdBody().getPosition().y * PIXELS_TO_METERS
                );
            }
        } else if (isDragging) {
            // Mouse/touch released, launch the bird
            isDragging = false;

            // Calculate launch velocity based on drag vector
            float dragX = catapultX - dragCurrent.x;
            float dragY = catapultY - dragCurrent.y;

            // Scale the launch force
            Vector2 launchVelocity = new Vector2(
                dragX * LAUNCH_FORCE_MULTIPLIER,
                dragY * LAUNCH_FORCE_MULTIPLIER
            );

            // Launch the bird
            currentBird.getBirdBody().setGravityScale(1);
            currentBird.getBirdBody().setLinearVelocity(
                launchVelocity.x,
                launchVelocity.y
            );

            // Reset current bird
            currentBird = null;

            // Select next bird if available
            selectBird();
        }
    }

    private void drawRubberBands() {
        // Enable blending for smooth lines
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Get current bird position
        float birdX = currentBird.getBirdBody().getPosition().x * Bird.PIXELS_TO_METERS;
        float birdY = currentBird.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS;

        // Calculate band width based on stretch (makes bands thinner when stretched)
        float stretchDistance = Vector2.dst(SLINGSHOT_LEFT_X, SLINGSHOT_Y, birdX, birdY);
        float bandWidth = Math.max(1f, 3f - (stretchDistance / MAX_DRAG_DISTANCE) * 2f);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BAND_COLOR);

        // Draw left band
        drawBand(SLINGSHOT_LEFT_X, SLINGSHOT_Y, birdX, birdY, bandWidth);

        // Draw right band
        drawBand(SLINGSHOT_RIGHT_X, SLINGSHOT_Y, birdX, birdY, bandWidth);

        shapeRenderer.end();

        // Optional: Add band shine effect
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, 0.2f); // Slight white shine

        // Draw thinner shine lines on both bands
        drawBand(SLINGSHOT_LEFT_X, SLINGSHOT_Y, birdX, birdY, bandWidth * 0.3f);
        drawBand(SLINGSHOT_RIGHT_X, SLINGSHOT_Y, birdX, birdY, bandWidth * 0.3f);

        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawBand(float x1, float y1, float x2, float y2, float width) {
        // Calculate perpendicular vector for band thickness
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length > 0) {
            // Normalize and rotate 90 degrees for perpendicular vector
            float perpX = -dy * width / length;
            float perpY = dx * width / length;

            // Draw band as a quad
            shapeRenderer.triangle(
                x1 + perpX, y1 + perpY,
                x1 - perpX, y1 - perpY,
                x2 + perpX, y2 + perpY
            );
            shapeRenderer.triangle(
                x2 + perpX, y2 + perpY,
                x2 - perpX, y2 - perpY,
                x1 - perpX, y1 - perpY
            );
        }
    }


    private boolean isBirdTouched(Bird bird, float touchX, float touchY) {
        if (bird == null || bird.objectSprite == null) return false;

        float birdCenterX = bird.getBirdBody().getPosition().x * PIXELS_TO_METERS;
        float birdCenterY = bird.getBirdBody().getPosition().y * PIXELS_TO_METERS;

        // Use a circular touch area for better touch detection
        float touchRadius = 30f; // Adjust this value as needed
        float distanceSquared = (touchX - birdCenterX) * (touchX - birdCenterX) +
            (touchY - birdCenterY) * (touchY - birdCenterY);

        return distanceSquared <= touchRadius * touchRadius;
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
        if (bird == null) return;

        bird.getBirdBody().setType(BodyDef.BodyType.KinematicBody);
        bird.setPosition(catapultX, catapultY);
        bird.getBirdBody().setLinearVelocity(0, 0);
        bird.getBirdBody().setAngularVelocity(0);
        bird.getBirdBody().setGravityScale(0);
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
//        for (CollisionDestruction destruction : activeDestructions) {
//            destruction.dispose();
//        }
//        activeDestructions.clear();
    }
}
