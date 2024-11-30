package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.AngryBirdsGame.AngryBirds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Level3 extends Level implements Screen{
    private AngryBirds game;
    private boolean snd = true;
    private SpriteBatch batch;
    private Sprite bgSprite, pauseGame, catapult, baseblock;
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
    protected static final float PIXELS_TO_METERS = 100f;
    private Bird currentBird;
    private Queue<Bird> birdQueue;
    private float[] initialBirdX = new float[5];
    private float[] initialBirdY = new float[5];
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
    private ArrayList<Bird> launchedBirds;
    private static final float BIRD_CLEANUP_Y = 0;
    private boolean gameStarted = false;
    private boolean allBirdsLaunched=false;
    public Array<Vector2> trajectoryPoints;
    private boolean birdLaunched = false;
    private Stage stage;
    private Skin skin;
    private float losePageTimer = 0f;
    private static final float LOSE_PAGE_DELAY = 2f; // 2 seconds delay


    public Level3(AngryBirds game){
        super();
        game.setCurrentLevel(3);
        this.game=game;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0,-9.8f),true);
        pigDestroyed = new boolean[3];
        blockDestroyed = new boolean[30];
        activeAnimations = new Array<>();
        objectsToDestroy = new ArrayList<>();
        contactListener = new CollisionContactListener(this);
        world.setContactListener(contactListener);
        blocks = new ArrayList<>();
        pigs = new ArrayList<>();

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Create ground body
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(0, 2/ PIXELS_TO_METERS);// Position at bottom of the screen
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        Body groundBody = world.createBody(groundBodyDef);

        // Create ground fixture
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(camera.viewportWidth / 2, 1); // Width of screen, very thin height

        FixtureDef groundFixtureDef = new FixtureDef();
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.friction = 0.5f;
        groundBody.createFixture(groundFixtureDef);

        groundShape.dispose();
        createWorldBoundaries();

        Texture bgtexture=new Texture(Gdx.files.internal("Images/Level1-bg.jpg"));
        Texture pause=new Texture(Gdx.files.internal("Images/Pause.png"));

        // Texture Baseblock=new Texture(Gdx.files.internal("Images/baseBlock.png"));

        bird1=new RedBird(world, "Images/redBird.png",  0.12f, 140, 100);
//        bird2=new YellowBird(world, "Images/yellowBird.png", 0.12f, 140, 100);
        bird3=new YellowBird(world, "Images/yellowBird.png",  0.12f, 100, 100);
        bird4=new BlackBird(world, "Images/bombBird.png",  0.12f, 65, 100);
        bird5=new BlackBird(world, "Images/bombBird.png",  0.12f, 30, 100);
//        block1 = new Block(world, "Images/baseBlock.png","BlockSetup",0.4f,105,50,game);
        block2 = new WoodBlock(world,"Images/vWNew.png",0.3f,420,305,game);
        block3 = new WoodBlock(world,"Images/vWNew.png",0.3f,540,305,game);
        block4 = new WoodBlock(world,"Images/glassbvertical.png",0.2f,435,672,game);
        block5 = new WoodBlock(world,"Images/glassbvertical.png",0.2f,525,672,game);
        //block6 = new WoodBlock(world,"Images/vWNew.png",0.15f,407,14,game);
//        block7 = new WoodBlock(world,"Images/woodsmallblock.png",0.15f,407,32,game);
//        block8 = new WoodBlock(world,"Images/woodsquare.png",0.17f,375,52,game);
//        block9 = new WoodBlock(world,"Images/woodsquare.png",0.17f,479,52,game);
//        block10 = new WoodBlock(world,"Images/woodsmallblock.png",0.15f,307,72,game);
//        block11 = new WoodBlock(world,"Images/woodsmallblock.png",0.15f,407,72,game);
//        block12 = new WoodBlock(world,"Images/woodsmallblock.png",0.15f,337,90,game);
//        block13 = new WoodBlock(world,"Images/woodsmallblock.png",0.15f,377,90,game);
        //  block14 = new WoodBlock(world,"Images/h.png",0.4f,382,109,game);
        block15 = new WoodBlock(world,"Images/hWNew.png",0.45f,480,505,game);
        block16 = new WoodBlock(world,"Images/hWNew.png",0.35f,480,805,game);
        block17 = new GlassBlock(world, "Images/sTri.png",0.45f,430,910,game);
        block18 = new GlassBlock(world, "Images/sTri.png",0.45f,510,910,game);
//        block19 = new GlassBlock(world, "Images/vWNew.png",0.3f,403,48,game);
        // block20 = new GlassBlock(world, "Images/glasshori.png",0.2f,155,176,game);
//        block21 = new GlassBlock(world, "Images/v.png",0.3f,385,70,game);
//        block22 = new GlassBlock(world, "Images/v.png",0.3f,465,70,game);
//        block23 = new GlassBlock(world, "Images/v.png",0.3f,290,-78,game);
//        block24 = new GlassBlock(world, "Images/v.png",0.3f,560,-78,game);
        // block25 = new GlassBlock(world, "Images/glasshori.png",0.2f,155,296,game);
//        block26 = new WoodBlock(world,"Images/woodsmallblock.png",0.15f,357,160,game);
//        block27 = new WoodBlock(world,"Images/woodenbvertical.png",0.15f,427,118,game);
//        block28 = new SteelBlock(world,"Images/steelsquare.png",0.22f,429,224,game);
//        block29 = new GlassBlock(world, "Images/glasssmallsq.png",0.22f,390,287,game);
//        block30 = new GlassBlock(world, "Images/glasssmallsq.png",0.22f,429,287,game);
//        block31 = new GlassBlock(world, "Images/glasssmallsq.png",0.22f,468,287,game);
        pig1 = new Pig3(world, "Images/pig3.png",0.1f,465,20,game);
        pig2 = new Pig1(world, "Images/pig1.png",0.11f,477,750,game);
        pig3 = new Pig1(world, "Images/pig2.png",0.13f,458,975,game);


        Texture Catapult=new Texture(Gdx.files.internal("Images/Catapultimg.png"));

        birdQueue = new LinkedList<>();
        birdQueue.add(bird1);
//        birdQueue.add(bird2);
        birdQueue.add(bird3);
        birdQueue.add(bird4);
        birdQueue.add(bird5);
        for (Bird b:birdQueue){
            gameObjects.add(b);
        }
        pigs.add(pig1);
        pigs.add(pig2);
        pigs.add(pig3);
        for (Pig p:pigs){
            gameObjects.add(p);
        }
        blocks.add(block2);
        blocks.add(block3);
        blocks.add(block4);
        blocks.add(block5);
//        blocks.add(block6);
//        blocks.add(block7);
//        blocks.add(block8);
//        blocks.add(block9);
//        blocks.add(block10);
//        blocks.add(block11);
//        blocks.add(block12);
//        blocks.add(block13);
//        blocks.add(block14);
        blocks.add(block15);
        blocks.add(block16);
        blocks.add(block17);
        blocks.add(block18);
//        blocks.add(block19);
//       // blocks.add(block20);
//        blocks.add(block21);
//        blocks.add(block22);
//        blocks.add(block23);
//        blocks.add(block24);
//       // blocks.add(block25);
//        blocks.add(block26);
//        blocks.add(block27);
//        blocks.add(block28);
//        blocks.add(block29);
        // blocks.add(block30);
        for (Block b:blocks){
            gameObjects.add(b);
        }

        int i = 0;
        for (Bird b : birdQueue) {
            // Store initial positions
            initialBirdX[i] = b.getX();
            initialBirdY[i] = b.getY();
            i++;
        }

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
        Gdx.input.setInputProcessor(stage);

        // Initialize the skin (UI assets)
        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        // Initialize trajectory points
        trajectoryPoints = new Array<>();

        music_buff = Gdx.audio.newMusic(Gdx.files.internal("Sounds/gamePlay.mp3"));
        icon_sound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/tap.mp3"));

        music_buff.setLooping(true);
        if (!music_buff.isPlaying()) music_buff.play();

        bgSprite=new Sprite(bgtexture);
        pauseGame=new Sprite(pause);
        catapult=new Sprite(Catapult);
        //baseblock = new Sprite(Baseblock);

        pauseGame.setScale(0.2f);
        catapult.setScale(0.18f);
        //baseblock.setScale(0.4f);

        pauseGame.setPosition(-150,270);
        catapult.setPosition(0,-100);
        //baseblock.setPosition(105,50);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
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
        // Left boundary
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

        // Right boundary (similar to left, but positioned at screen width)
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

                // Screen width and height in meters
                float screenWidthMeters = Gdx.graphics.getWidth() / PIXELS_TO_METERS;
                float screenHeightMeters = Gdx.graphics.getHeight() / PIXELS_TO_METERS;

                // Correct position if out of bounds
                if (position.x < 0) blockBody.setTransform(0, position.y, blockBody.getAngle());
                if (position.x > screenWidthMeters) blockBody.setTransform(screenWidthMeters, position.y, blockBody.getAngle());
            }
        }
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
    protected void restoreGameObjects(SaveData loadedGameState) {

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
            float minBirdY = 100f; // Adjust based on your screen layout
            if (currentBird.getBirdBody().getPosition().y * PIXELS_TO_METERS < minBirdY) {
                currentBird.getBirdBody().setTransform(
                    currentBird.getBirdBody().getPosition().x,
                    minBirdY / PIXELS_TO_METERS,
                    0
                );
            }
        } else if (isDragging) {
            // Mouse/touch released, launch the bird
            isDragging = false;

            // Calculate launch velocity based on drag vector
            float dragX = catapultX-dragCurrent.x;
            float dragY = catapultY-dragCurrent.y;

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
            //checkGameStatus();
            currentBird = null;
//            if(getRemPigs().size==0){
//                game.setScreen(new WinPage(game));
//            }
//            else{
//                game.setScreen(new LosePage(game));
//            }
        }
    }

    private void positionBirdOnCatapult(Bird bird) {
        if (bird == null){
            // checkGameStatus();
            return;
        }

        bird.getBirdBody().setType(BodyDef.BodyType.KinematicBody);
        bird.setPosition(catapultX, catapultY+35f);
        bird.getBirdBody().setLinearVelocity(0, 0);
        bird.getBirdBody().setAngularVelocity(0);
        bird.getBirdBody().setGravityScale(0);
    }



    @Override
    public void addDestructionAnimation(float x, float y, float scale) {
        activeAnimations.add(new CollisionDestruction(x, y, scale));
    }

    @Override
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
        // Handle object destruction
        Iterator<GameObject> iterator = objectsToDestroy.iterator();
        while (iterator.hasNext()) {
            GameObject obj = iterator.next();

            // Only process objects that are still alive
            if (obj.isAlive()) {
                // Set alive status to false
                obj.setAlive(false);


                // Clean up physics body
                if (obj instanceof PhysicsBody) {
                    ((PhysicsBody) obj).destroyBody();
                }
            }
            iterator.remove();
        }
        //checkGameStatus();
    }

    public void update(float deltaTime) {
        // Defer body updates and removals
        for (int i = blocks.size() - 1; i >= 0; i--) {
            Block block = blocks.get(i);
            if (block == null || block.getBlockBody() == null) {
                blocks.remove(i);
            }
        }

        for (int i = pigs.size() - 1; i >= 0; i--) {
            Pig pig = pigs.get(i);
            if (pig == null || pig.getPigBody() == null) {
                pigs.remove(i);
            }
        }
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        super.update(v);
        updateObjectPos();
        world.step(1/60f,6,2);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        enforceScreenBoundaries();

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        handleDragInput();

        batch.begin();

        batch.draw(bgSprite, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        pauseGame.draw(batch);
        catapult.draw(batch);
        // baseblock.draw(batch);
        batch.end();

        if (isDragging && currentBird!=null){
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

        //updateDestructions(v);

        batch.begin();
        bird1.draw(batch);
//        bird2.draw(batch);
        bird3.draw(batch);
        bird4.draw(batch);
        bird5.draw(batch);
//        block1.draw(batch);
        block2.draw(batch);
        block3.draw(batch);
        block4.draw(batch);
        block5.draw(batch);
//        block6.draw(batch);
//        block7.draw(batch);
//        block8.draw(batch);
//        block9.draw(batch);
//        block10.draw(batch);
//        block11.draw(batch);
//        block12.draw(batch);
//        block13.draw(batch);
//        block14.draw(batch);
        block15.draw(batch);
        block16.draw(batch);
        block17.draw(batch);
        block18.draw(batch);
//        block19.draw(batch);
        // block20.draw(batch);
//        block21.draw(batch);
//        block22.draw(batch);
//        block23.draw(batch);
//        block24.draw(batch);
//       // block25.draw(batch);
//        block26.draw(batch);
//        block27.draw(batch);
//        block28.draw(batch);
//        block29.draw(batch);
//        block30.draw(batch);
//        block31.draw(batch);
        pig1.draw(batch);
        pig2.draw(batch);
        pig3.draw(batch);

        for (CollisionDestruction anim : activeAnimations){
            anim.render(batch);
        }

        batch.end();

        checkGameStatus();

//        float touchX = Gdx.input.getX();
//        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Handle touch input for bird selection
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

    private void updateObjectPos() {
        // Safe updates before physics step
        for (GameObject obj : gameObjects) {
            obj.update();
        }
    }



//    private void updateObjectPos() {
//        // Safe updates before physics step
//        for (GameObject obj : gameObjects) {
//            obj.update();
//        }
//    }

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
        super.dispose();
        batch.dispose();
        icon_sound.dispose();
        music_buff.dispose();
        bgSprite.getTexture().dispose();
        pauseGame.getTexture().dispose();
        catapult.getTexture().dispose();
        // baseblock.getTexture().dispose();
        bird1.dispose();
//        bird2.dispose();
        bird3.dispose();
        bird4.dispose();
        bird5.dispose();
//        block1.dispose();
        block2.dispose();
        block3.dispose();
        block4.dispose();
        block5.dispose();
//        block6.dispose();
//        block7.dispose();
//        block8.dispose();
//        block9.dispose();
//        block10.dispose();
//        block11.dispose();
//        block12.dispose();
//        block13.dispose();
//        block14.dispose();
        block15.dispose();
        block16.dispose();
        block17.dispose();
        block18.dispose();
//        block19.dispose();
        // block20.dispose();
//        block21.dispose();
//        block22.dispose();
//        block23.dispose();
//        block24.dispose();
//      //  block25.dispose();
//        block26.dispose();
//        block27.dispose();
//        block28.dispose();
//        block29.dispose();
//        block30.dispose();
//        block31.dispose();
        pig1.dispose();
        pig2.dispose();
        pig3.dispose();
        for (CollisionDestruction anim : activeAnimations) {
            anim.dispose();
        }
        activeAnimations.clear();



    }
}
