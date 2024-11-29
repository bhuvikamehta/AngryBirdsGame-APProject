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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.AngryBirdsGame.AngryBirds;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.AngryBirdsGame.Pages.CollisionDestruction;

import java.util.*;

public class Level1 extends Level implements Screen {
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
    private float[] initialBirdX = new float[3];
    private float[] initialBirdY = new float[3];
    private float catapultX = 170;
    private float catapultY = 150;
    private boolean isDragging = false;
    private Vector2 dragStart = new Vector2();
    private Vector2 dragCurrent = new Vector2();
    private static final float MAX_DRAG_DISTANCE = 60f;
    private static final float LAUNCH_FORCE_MULTIPLIER = 0.1f;
    private static final float SLINGSHOT_LEFT_X = 190f;
    private static final float SLINGSHOT_RIGHT_X = 170f;
    private static final float SLINGSHOT_Y = 194f;
    private ShapeRenderer shapeRenderer;
    private static final Color BAND_COLOR = new Color(0.4f, 0.2f, 0.1f, 1f);// Brown rubber band color
    private boolean[] pigDestroyed;
    private boolean[] blockDestroyed;
    private Array<CollisionDestruction> activeAnimations;
    private CollisionContactListener contactListener;
    private ArrayList<GameObject> objectsToDestroy;
    private ArrayList<Block> blocks;
    private ArrayList<Pig> pigs;
    private boolean gameStarted = false;
    private boolean allBirdsLaunched=false;
    public Array<Vector2> trajectoryPoints;
    private boolean birdLaunched = false;
    private Stage stage;
    private Skin skin;
    private float losePageTimer = 0f;
    private static final float LOSE_PAGE_DELAY = 2f; // 2 seconds delay

    public Level1(AngryBirds game) {
        super();
        game.setCurrentLevel(1);
        this.game = game;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0, -9.8f), true);
        pigDestroyed = new boolean[2];
        blockDestroyed = new boolean[8];
        activeAnimations = new Array<>();
        objectsToDestroy=new ArrayList<>();
        contactListener = new CollisionContactListener(this);
        world.setContactListener(contactListener);
        blocks = new ArrayList<>();
        pigs = new ArrayList<>();


        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(0, 2 / PIXELS_TO_METERS); // Position at bottom of the screen
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        Body groundBody = world.createBody(groundBodyDef);

        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(camera.viewportWidth / 2, 1); // Width of screen, very thin height

        FixtureDef groundFixtureDef = new FixtureDef();
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.friction = 0.5f;
        groundBody.createFixture(groundFixtureDef);

        groundShape.dispose();
        createWorldBoundaries();

        Texture bgtexture = new Texture(Gdx.files.internal("Images/Level1-bg.jpg"));
        bird1 = new RedBird(world, "Images/redBird.png",  0.15f, 130, 100);
        bird2 = new RedBird(world, "Images/redBird.png",  0.15f, 110, 100);
        bird3 = new RedBird(world, "Images/redBird.png",  0.15f, 90, 100);

        pig1 = new Pig1(world, "Images/pig1.png",  0.12f, 438, 405,game);
        pigs.add(pig1);
        block1 = new WoodBlock(world, "Images/vWNew.png",  0.3f, 400, 305,game);
        block2 = new WoodBlock(world, "Images/vWNew.png",  0.3f, 480, 305,game);
        block3 = new WoodBlock(world, "Images/hWNew.png",  0.3f, 445, 395,game);
        block4 = new WoodBlock(world, "Images/wTri.png",  0.3f, 400, 405,game);
        block5 = new WoodBlock(world, "Images/wTri.png", 0.3f, 480, 405,game);
        blocks.add(block1);
        blocks.add(block2);
        blocks.add(block3);
        blocks.add(block4);
        blocks.add(block5);
        Texture Catapult = new Texture(Gdx.files.internal("Images/Catapultimg.png"));
        Texture pauseG = new Texture(Gdx.files.internal("Images/Pause.png"));
        Texture winIcon = new Texture(Gdx.files.internal("Images/win.png"));
        Texture loseIcon = new Texture(Gdx.files.internal("Images/lose.png"));

        birdQueue = new LinkedList<>();
        birdQueue.add(bird1);
        birdQueue.add(bird2);
        birdQueue.add(bird3);
        for(Bird b:birdQueue){
            gameObjects.add(b);
//            objectsToDestroy.add(b);
        }
        for(Block bl:blocks){
            gameObjects.add(bl);
//            objectsToDestroy.add(bl);
        }
        for(Pig piggie:pigs){
            gameObjects.add(piggie);
//            objectsToDestroy.add(piggie);
        }

        // Initialize bird queue and store initial positions
        int i = 0;
        for (Bird b : birdQueue) {
            // Store initial positions
            initialBirdX[i] = b.getX();
            initialBirdY[i] = b.getY();
            i++;
        }
//yaha pe ui skin wala daal na hai

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
        Gdx.input.setInputProcessor(stage);

        // Initialize the skin (UI assets)
        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        // Initialize trajectory points
        trajectoryPoints = new Array<>();

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

        catapult.setPosition(0, -100);
        pauseGame.setPosition(-150, 270);
        winPage.setPosition(360, 275);
        losePage.setPosition(410, 285);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        //  world.setContactListener(new CollisionContactListener());
    }

    public void calculateTrajectory(Vector2 start, Vector2 velocity, int steps) {
        trajectoryPoints.clear();
        velocity.scl(0.8f); // Scale velocity down to shorten trajectory

        float timeStep = 1 / 60f; // Time step for physics simulation
        Vector2 gravity = world.getGravity(); // Gravity vector

        for (int i = 0; i < steps; i++) {
            float t = i * timeStep;
            float x = start.x + velocity.x * t;
            float y = start.y + velocity.y * t + 0.5f * gravity.y * t * t;
            trajectoryPoints.add(new Vector2(x, y));
        }
    }

    private void renderTrajectory() {
        if (birdLaunched || currentBird == null) {
            return; // Don't draw trajectory if the bird has already been launched
        }

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        for (int i = 1; i < trajectoryPoints.size; i++) {
            Vector2 p1 = trajectoryPoints.get(i - 1);
            Vector2 p2 = trajectoryPoints.get(i);
            shapeRenderer.line(p1.x * PIXELS_TO_METERS, p1.y * PIXELS_TO_METERS,
                p2.x * PIXELS_TO_METERS, p2.y * PIXELS_TO_METERS);
        }

        shapeRenderer.end();
    }

    private void createWorldBoundaries() {
        BodyDef leftWallDef = new BodyDef();
        leftWallDef.position.set(0 / PIXELS_TO_METERS, Gdx.graphics.getHeight() / (2 * PIXELS_TO_METERS));
        Body leftWall = world.createBody(leftWallDef);

        PolygonShape leftWallShape = new PolygonShape();
        leftWallShape.setAsBox(1 / PIXELS_TO_METERS, Gdx.graphics.getHeight() / (2 * PIXELS_TO_METERS));

        FixtureDef leftWallFixture = new FixtureDef();
        leftWallFixture.shape = leftWallShape;
        leftWallFixture.friction = 0.5f;
        leftWall.createFixture(leftWallFixture);
        leftWallShape.dispose();


        BodyDef rightWallDef = new BodyDef();
        rightWallDef.position.set(Gdx.graphics.getWidth() / PIXELS_TO_METERS, Gdx.graphics.getHeight() / (2 * PIXELS_TO_METERS));
        Body rightWall = world.createBody(rightWallDef);

        PolygonShape rightWallShape = new PolygonShape();
        rightWallShape.setAsBox(1 / PIXELS_TO_METERS, Gdx.graphics.getHeight() / (2 * PIXELS_TO_METERS));

        FixtureDef rightWallFixture = new FixtureDef();
        rightWallFixture.shape = rightWallShape;
        rightWallFixture.friction = 0.5f;
        rightWall.createFixture(rightWallFixture);
        rightWallShape.dispose();
    }

    private void enforceScreenBoundaries() {
        for (Block block : blocks) {
            Body blockBody = block.getBlockBody();
            if (blockBody != null) {
                Vector2 position = blockBody.getPosition();

                float screenWidthMeters = Gdx.graphics.getWidth() / PIXELS_TO_METERS;
                float screenHeightMeters = Gdx.graphics.getHeight() / PIXELS_TO_METERS;

                if (position.x < 0) blockBody.setTransform(0, position.y, blockBody.getAngle());
                if (position.x > screenWidthMeters) blockBody.setTransform(screenWidthMeters, position.y, blockBody.getAngle());
            }
        }
    }


    @Override
    public void show() {
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
                    currentBird.getBirdBody().setType(BodyDef.BodyType.DynamicBody);
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
        super.update(delta);
        updateObjectPos();
        world.step(delta, 6, 2);
//        pig1.lockPosition();
//        pig2.lockPosition();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        enforceScreenBoundaries();
        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        handleDragInput();

        batch.begin();
        batch.draw(bgSprite, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        pauseGame.draw(batch);

        // Draw catapult base behind the bands
        catapult.draw(batch);
        batch.end();

        // Draw rubber bands if dragging
        if (isDragging && currentBird != null) {
            drawRubberBands();
            Vector2 start = new Vector2(
                currentBird.getBirdBody().getPosition().x,
                currentBird.getBirdBody().getPosition().y
            );
            Vector2 velocity = new Vector2(
                 catapultX - dragCurrent.x,
                  catapultY-dragCurrent.y
            ).scl(LAUNCH_FORCE_MULTIPLIER);

            calculateTrajectory(start, velocity, 15);
            renderTrajectory();
        }

        //updateDestructions(delta);

        batch.begin();
        bird1.draw(batch);
        bird2.draw(batch);
        bird3.draw(batch);
        pig1.draw(batch);
//        pig2.draw(batch);
        winPage.draw(batch);
        losePage.draw(batch);
        block1.draw(batch);
        block2.draw(batch);
        block3.draw(batch);
        block4.draw(batch);
        block5.draw(batch);
//        block6.draw(batch);
//        block7.draw(batch);
//        block8.draw(batch);

//        if (bird1.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS < 45) resetBirdCompletely(bird1, 0);
//        if (bird2.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS < 45) resetBirdCompletely(bird2, 1);
//        if (bird3.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS < 45) resetBirdCompletely(bird3, 2);

        for (CollisionDestruction anim : activeAnimations) {
            anim.render(batch);
        }

        batch.end();
        checkGameStatus();

        // Handle touch input for bird selection and launching
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            handleBirdSelection(touchPos.x, touchPos.y);

            if (pauseGame.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                music_buff.stop();
                playSound();
                game.setScreen(new PauseGamePage(game));
                return;
            }

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
    private void checkGameStatus() {
        // Win page logic remains the same as previous version
        boolean allPigsDead = true;
        for (Pig pig : pigs) {
            if (pig.getHealth() > 0) {
                allPigsDead = false;
                break;
            }
        }

        if (allPigsDead) {
            music_buff.stop();
            game.setScreen(new WinPage(game));
            return;
        }

        // Lose page logic with timer
        if (currentBird == null && birdQueue.isEmpty()) {
            allBirdsLaunched = true;
        }

        // If all birds launched, start timer
        if (allBirdsLaunched) {
            losePageTimer += Gdx.graphics.getDeltaTime();

            // After delay, check pig health
            if (losePageTimer >= LOSE_PAGE_DELAY) {
                // Recheck pig health after delay
                boolean anyPigAlive = false;
                for (Pig pig : pigs) {
                    if (pig.getHealth() > 0) {
                        anyPigAlive = true;
                        break;
                    }
                }

                if (anyPigAlive) {
                    music_buff.stop();
                    game.setScreen(new LosePage(game));
                }
            }
        }
    }

    private boolean areAllObjectsStationary() {
        for (Pig pig : pigs) {
            if (pig.getPigBody().getLinearVelocity().len() > 0.1f) return false;
        }

        for (Block block : blocks) {
            if (block.getBlockBody().getLinearVelocity().len() > 0.1f) return false;
        }

        return true;
    }

    private void updateObjectPos() {
        // Safe updates before physics step
        for (GameObject obj : gameObjects) {
            obj.update();
        }
    }

    public void addDestructionAnimation(float x, float y, float scale) {
        activeAnimations.add(new CollisionDestruction(x, y, scale));
    }

    public void markForDestruction(GameObject object) {
        if (object.isAlive()) {
            objectsToDestroy.add(object);
            addDestructionAnimation(object.getX(), object.getY(), object.getSize());
        }
    }

    private void updateDestructions(float delta) {
        // Update and remove finished animations
        for (int i = activeAnimations.size - 1; i >= 0; i--) {
            CollisionDestruction anim = activeAnimations.get(i);
            anim.update(delta);
            if (anim.isFinished()) {
                anim.dispose();
                activeAnimations.removeIndex(i);
            }
        }

        // Handle object destruction
        for (GameObject obj : objectsToDestroy) {
            if (obj instanceof Pig) {
                world.destroyBody(((Pig)obj).getPigBody());
            } else if (obj instanceof Block) {
                world.destroyBody(((Block)obj).getBody());
            }
        }
        objectsToDestroy.clear();
    }

    @Override
    protected void restoreGameObjects(SaveData loadedGameState) {

    }

    public void update(float deltaTime) {
        // Defer body updates and removals
        for (int i = blocks.size() - 1; i >= 0; i--) {
            Block block = blocks.get(i);
            if (block == null || block.getBlockBody() == null) {
                blocks.remove(block);
            }
        }

        for (int i = pigs.size() - 1; i >= 0; i--) {
            Pig pig = pigs.get(i);
            if (pig == null || pig.getPigBody() == null) {
                pigs.remove(pig);
            }
        }
    }



    private void handleDragInput() {
        if (currentBird == null) return;
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);

        if (Gdx.input.isTouched()) {
            // Check if we're starting a new drag
            if (!isDragging) {
                // Only start dragging if touching near the bird
                float birdX = currentBird.getBirdBody().getPosition().x * PIXELS_TO_METERS;
                float birdY = currentBird.getBirdBody().getPosition().y * PIXELS_TO_METERS;
                float touchRadius = 50f;


                if (Vector2.dst(birdX, birdY, touchPos.x, touchPos.y) < touchRadius) {
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
            float minBirdY = 100f; // Adjust based on your screen layout
            if (currentBird.getBirdBody().getPosition().y * PIXELS_TO_METERS < minBirdY) {
                currentBird.getBirdBody().setTransform(
                    currentBird.getBirdBody().getPosition().x,
                    minBirdY / PIXELS_TO_METERS,
                    0
                );
            }
        } else if (isDragging) {
            gameStarted=true;
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
        bird.setPosition(catapultX, catapultY+30f);
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
        block1.dispose();
        block2.dispose();
        block3.dispose();
        block4.dispose();
        block5.dispose();
        catapult.getTexture().dispose();
        winPage.getTexture().dispose();
        losePage.getTexture().dispose();
        for (CollisionDestruction anim : activeAnimations) {
            anim.dispose();
        }
        activeAnimations.clear();
//        for (CollisionDestruction destruction : activeDestructions) {
//            destruction.dispose();
//        }
//        activeDestructions.clear();
    }
}
