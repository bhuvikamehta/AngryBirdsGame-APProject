package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import io.github.AngryBirdsGame.AngryBirds;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Level2 extends Level implements Screen{
    private static final short BIRD_CATEGORY = 0x0002;
    private static final short OBSTACLE_CATEGORY= 0x0004;
    private AngryBirds game;
    private boolean snd = true;
    private SpriteBatch batch;
    private Sprite bgSprite, pauseGame, exitGame, catapult;
    private Bird bird1, bird2, bird3, bird4, bird5;
    private Pig pig1,pig2,pig3,pig4;
    private Block block1,block2,block3,block4,block5,block6,block7,block8, block9,block10,block11,block12,block13,block14,block15,block16,block17,block18;
    private Music music_buff, icon_sound, shoot_sound, hit_sound;
    private OrthographicCamera camera;
    private Preferences preferences;
    private float touchCooldown = 0.5f;  // 0.5 seconds cooldown
    private float lastTouchTime = 0;
    private World world;
    protected static final float PIXELS_TO_METERS = 100f;
    private Bird currentBird;
    private Queue<Bird> birdQueue;
    private float[] initialBirdX = new float[5];
    private float[] initialBirdY = new float[5];
    private float catapultX = 170f;
    private float catapultY = 220f;
    private boolean isDragging = false;
    private Vector2 dragStart = new Vector2();
    private Vector2 dragCurrent = new Vector2();
    private static final float MAX_DRAG_DISTANCE = 25f;
    private static final float LAUNCH_FORCE_MULTIPLIER = 0.2f;
    private static final float SLINGSHOT_LEFT_X = 230f;
    private static final float SLINGSHOT_RIGHT_X = 220f;
    private static final float SLINGSHOT_Y = 235f;
    private ShapeRenderer shapeRenderer;
    private static final Color BAND_COLOR = new Color(0.4f, 0.2f, 0.1f, 1f);// Brown rubber band color
    private boolean[] pigDestroyed;
    private boolean[] blockDestroyed;
    private Array<CollisionDestruction> activeAnimations;
    private CollisionContactListener contactListener;
    private Array<GameObject> objectsToDestroy;
    private Array<Block> blocks;
    private Array<Pig> pigs;
    private Array<Bird> launchedBirds;
    private static final float BIRD_CLEANUP_Y = 0;
    private Stage stage;
    private Skin skin;
    private Label scoreLabel;
    private int score=0;
    private Array<Vector2> trajectoryPoints;
    private boolean birdLaunched = false;






    public Level2(AngryBirds game){
        super();
        game.setCurrentLevel(2);
        this.game=game;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0,-9.8f),true);
        pigDestroyed = new boolean[4];
        blockDestroyed = new boolean[18];
        activeAnimations = new Array<>();
        objectsToDestroy=new Array<>();
        contactListener = new CollisionContactListener(this);
        world.setContactListener(contactListener);
        blocks = new Array<>();
        pigs = new Array<>();
        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Create ground body
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(0, 45/ PIXELS_TO_METERS);// Position at bottom of the screen
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        Body groundBody = world.createBody(groundBodyDef);

        // Create ground fixture
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(camera.viewportWidth / 2, 1); // Width of screen, very thin height

        FixtureDef groundFixtureDef = new FixtureDef();
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.friction = 0.3f;
        groundBody.createFixture(groundFixtureDef);

        groundShape.dispose();
        createWorldBoundaries();
        Texture bgtexture=new Texture(Gdx.files.internal("Images/Level2-bg.png"));
        Texture pause=new Texture(Gdx.files.internal("Images/Pause.png"));
        bird1=new RedBird(world, "Images/redBird.png",  0.12f, 195, 172);//172
        bird2=new RedBird(world, "Images/redBird.png", 0.12f, 155, 172);
        bird3=new RedBird(world, "Images/redBird.png",  0.12f, 115, 172);
        bird4=new YellowBird(world, "Images/yellowBird.png", 0.12f, 75, 172);
        bird5=new YellowBird(world, "Images/yellowBird.png",  0.12f, 35, 172);
        block1 = new WoodBlock(world, "Images/baseBlock.png",0.28f, 505, 437, game);//137
        block2 = new WoodBlock(world, "Images/vWNew.png",0.35f,410,182, game);//82
        block3 = new WoodBlock(world, "Images/vWNew.png",0.35f,430,182, game);
        block4 = new WoodBlock(world, "Images/vWNew.png",0.35f,450,182, game);
        block5 = new WoodBlock(world, "Images/vWNew.png",0.35f,575,182, game);
        block6 = new WoodBlock(world, "Images/vWNew.png",0.35f,595,182, game);
        block7 = new WoodBlock(world, "Images/vWNew.png",0.35f,615,182, game);
//        block8 = new WoodBlock(world, "Images/smallSteel.png",0.25f,428,240, game);//350,240
//        block9 = new WoodBlock(world, "Images/smallSteel.png",0.25f,593,240, game);//490.540
//        block10 = new WoodBlock(world, "Images/blockvertical.png",0.4f,470,1185, game);//185
//        block11 = new WoodBlock(world, "Images/blockvertical.png",0.4f,555,1185, game);
//        block12 = new WoodBlock(world, "Images/h.png",0.4f,420,940, game);//340
        block13= new SteelBlock(world, "Images/steelsq.png",0.2f,425,370, game);//170
        block14= new SteelBlock(world, "Images/steelsq.png",0.2f,590,370, game);
        block15 = new SteelBlock(world, "Images/sTri.png",0.5f,425,530, game);//230
        block16 = new SteelBlock(world, "Images/sTri.png",0.55f,590,530, game);
//        block17 = new GlassBlock(world, "Images/glasstriangle.png",0.16f,390,1360, game);//260
//        block18 = new GlassBlock(world, "Images/glasstriangle.png",0.16f,430,1360, game);
//        pig1 = new Pig3(world, "Images/pig3.png",0.12f,530,740, game);
//        pig2 = new Pig2(world, "Images/pig2.png",0.17f,575,740, game);
//        pig3 = new Pig3(world, "Images/pig3.png",0.12f,485,178, game);
//        pig4 = new Pig1(world, "Images/pig1.png",0.12f,530,178, game);
//        pigs.add(pig1);
//        pigs.add(pig2);
//        pigs.add(pig3);
//        pigs.add(pig4);
        blocks.add(block1);
        blocks.add(block2);
        blocks.add(block3);
        blocks.add(block4);
        blocks.add(block5);
        blocks.add(block6);
        blocks.add(block7);
//       blocks.add(block8);
//        blocks.add(block9);
//        blocks.add(block10);
//        blocks.add(block11);
//        blocks.add(block12);
        blocks.add(block13);
        blocks.add(block14);
        blocks.add(block15);
        blocks.add(block16);
//        blocks.add(block17);
//        blocks.add(block18);
        Texture Catapult=new Texture(Gdx.files.internal("Images/Catapultimg.png"));
        birdQueue = new LinkedList<>();
        birdQueue.add(bird1);
        birdQueue.add(bird2);
        birdQueue.add(bird3);
        birdQueue.add(bird4);
        birdQueue.add(bird5);
        for(Bird b:birdQueue){
            gameObjects.add(b);
        }
        for(Block bl:blocks){
            gameObjects.add(bl);
        }
        for(Pig piggie:pigs){
            gameObjects.add(piggie);
        }
        // Initialize bird queue and store initial positions
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
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Initialize trajectory points
        trajectoryPoints = new Array<>();

        // Create score label
        scoreLabel = new Label("Score: 0", skin);
        scoreLabel.setPosition(Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 50);
        stage.addActor(scoreLabel);

        music_buff = Gdx.audio.newMusic(Gdx.files.internal("Sounds/gamePlay.mp3"));
        icon_sound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/tap.mp3"));

        music_buff.setLooping(true);
        if (!music_buff.isPlaying()) music_buff.play();

        bgSprite=new Sprite(bgtexture);
        pauseGame=new Sprite(pause);
        catapult=new Sprite(Catapult);

        pauseGame.setScale(0.2f);
        catapult.setScale(0.15f);

        pauseGame.setPosition(-150,270);
        catapult.setPosition(50,-50);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

    }

    private void calculateTrajectory(Vector2 start, Vector2 velocity, int steps) {
        trajectoryPoints.clear();
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

    private void updateScore(GameObject hitObject) {
        if (hitObject instanceof Pig) {
            score += 50; // Score for hitting a pig
        } else if (hitObject instanceof Block) {
            score += 10; // Score for hitting a block
        }

        // Update score label
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score);
        }
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


    private void updateBirdPositions() {
        // Update current bird if it exists
        if (currentBird != null && currentBird.getBirdBody() != null) {
            updateBirdSpritePosition(currentBird);
        }

        // Update queued birds
        for (Bird bird : birdQueue) {
            if (bird != null && bird.getBirdBody() != null) {
                updateBirdSpritePosition(bird);
            }
        }

        for (int i = launchedBirds.size - 1; i >= 0; i--) {
            Bird bird = launchedBirds.get(i);
            if (bird != null && bird.getBirdBody() != null) {
                updateBirdSpritePosition(bird);

                // Check if bird is below cleanup threshold
                if (bird.getBirdBody().getPosition().y * PIXELS_TO_METERS < BIRD_CLEANUP_Y) {
                    //world.destroyBody(bird.getBirdBody());
                    launchedBirds.removeIndex(i);
                    bird.dispose();
                }
            }
        }
    }

    private void updateBirdSpritePosition(Bird bird) {
        // Update sprite position to match physics body
        bird.objectSprite.setPosition(
            bird.getBirdBody().getPosition().x * PIXELS_TO_METERS - bird.objectSprite.getWidth()/2,
            bird.getBirdBody().getPosition().y * PIXELS_TO_METERS - bird.objectSprite.getHeight()/2
        );

        // Update sprite rotation to match physics body
        float angle = (float) Math.toDegrees(bird.getBirdBody().getAngle());
        bird.objectSprite.setRotation(angle);
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
            float dragX = dragCurrent.x - catapultX;
            float dragY = dragCurrent.y-catapultY;

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
        bird.setPosition(catapultX+55f, catapultY);
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
        for (int i = blocks.size - 1; i >= 0; i--) {
            Block block = blocks.get(i);
            if (block == null || block.getBlockBody() == null) {
                blocks.removeIndex(i);
            }
        }

        for (int i = pigs.size - 1; i >= 0; i--) {
            Pig pig = pigs.get(i);
            if (pig == null || pig.getPigBody() == null) {
                pigs.removeIndex(i);
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
        world.step(v, 6, 2);
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
        batch.end();

        // Draw rubber bands if dragging
        if (isDragging && currentBird != null) {
            drawRubberBands();
            Vector2 start = new Vector2(
                currentBird.getBirdBody().getPosition().x,
                currentBird.getBirdBody().getPosition().y
            );
            Vector2 velocity = new Vector2(
                dragCurrent.x - catapultX,
                dragCurrent.y - catapultY
            ).scl(LAUNCH_FORCE_MULTIPLIER);

            calculateTrajectory(start, velocity, 30);
            renderTrajectory();


        }
        // Update and draw UI
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();


        updateDestructions(v);

        batch.begin();
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
//        block8.draw(batch);
//        block9.draw(batch);
//        block10.draw(batch);
//        block11.draw(batch);
//        block12.draw(batch);
        block13.draw(batch);
        block14.draw(batch);
        block15.draw(batch);
        block16.draw(batch);
//        block17.draw(batch);
//        block18.draw(batch);
//        pig1.draw(batch);
//        pig2.draw(batch);
//        pig3.draw(batch);
//        pig4.draw(batch);
//        if (bird1.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS < 10) resetBirdCompletely(bird1, 0);
//        if (bird2.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS < 10) resetBirdCompletely(bird2, 1);
//        if (bird3.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS < 10) resetBirdCompletely(bird3, 2);
//        if (bird4.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS < 10) resetBirdCompletely(bird4, 3);
//        if (bird5.getBirdBody().getPosition().y * Bird.PIXELS_TO_METERS < 10) resetBirdCompletely(bird5, 4);

        for (CollisionDestruction anim : activeAnimations) {
            anim.render(batch);
        }
        batch.end();

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

    private void updateObjectPos() {
        // Safe updates before physics step
        for (GameObject obj : gameObjects) {
            obj.update();
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
        super.dispose();
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
//        block8.dispose();
//        block9.dispose();
//        block10.dispose();
//        block11.dispose();
//        block12.dispose();
//        block13.dispose();
//        block14.dispose();
        block15.dispose();
        block16.dispose();
//        block17.dispose();
//        block18.dispose();
//        pig1.dispose();
//        pig2.dispose();
//        pig3.dispose();
//        pig4.dispose();
        for (CollisionDestruction anim : activeAnimations) {
            anim.dispose();
        }
        activeAnimations.clear();

        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();

    }



}
