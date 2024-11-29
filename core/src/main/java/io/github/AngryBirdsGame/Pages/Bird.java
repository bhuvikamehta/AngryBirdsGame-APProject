package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public abstract class Bird extends GameObject implements PhysicsBody {

    protected String type;
    protected Body birdie;
    private boolean alive;
    private boolean hasBeenLaunched = false;

    public Bird(World world, String texturePath, String type, float scale, float posX, float posY) {
        super(world, texturePath, scale, posX, posY);
        this.type = type;
        this.alive = true;
        objectSprite.setScale(scale);
        objectSprite.setPosition(posX, posY);
        createBody(BodyDef.BodyType.DynamicBody); // DynamicBody for gravity simulation
        createCircleFixture(1.0f, 1.0f, 0.5f); // Adjust density, friction, and restitution
        birdie.setUserData(this);
    }

    public abstract float getDamage();

    public void launch() {
        hasBeenLaunched = true;
    }

    public boolean isLaunched() {
        return this.hasBeenLaunched;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void setAlive(boolean val) {
        this.alive = val;
    }

    @Override
    protected void createBody(BodyDef.BodyType bodyType) {
        BodyDef birdBody = new BodyDef();
        birdBody.type = bodyType;
        birdBody.position.set(X / PIXELS_TO_METERS, Y / PIXELS_TO_METERS); // Convert to Box2D scale
        birdie = world.createBody(birdBody);
        birdie.setUserData(this);
        birdie.setGravityScale(1.5f);
        birdie.setLinearDamping(1.0f);
    }

    public void stopMovement() {
        getBirdBody().setLinearVelocity(0, 0);
        getBirdBody().setAngularVelocity(0);
    }

    @Override
    protected void createCircleFixture(float density, float friction, float restitution) {
        CircleShape shape = new CircleShape();
        shape.setRadius((objectSprite.getWidth() * sizeObject / 2) / PIXELS_TO_METERS); // Convert radius to Box2D scale

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;      // Determines mass
        fixtureDef.friction = friction;   // Determines sliding resistance
        fixtureDef.restitution = restitution; // Determines bounciness

        birdie.createFixture(fixtureDef);
        shape.dispose(); // Dispose shape after use
    }

    public void draw(SpriteBatch batch) {
        objectSprite.setPosition(
            birdie.getPosition().x * PIXELS_TO_METERS - objectSprite.getWidth() / 2,
            birdie.getPosition().y * PIXELS_TO_METERS - objectSprite.getHeight() / 2
        ); // Sync sprite position with Box2D body
        objectSprite.draw(batch);
    }

    public void setPosition(float x, float y) {
        this.X = x;
        this.Y = y;

        // Reset the physics body
        birdie.setTransform(x / PIXELS_TO_METERS, y / PIXELS_TO_METERS, 0);
        birdie.setLinearVelocity(0, 0);
        birdie.setAngularVelocity(0); // Stop any rotation
    }

    public String getType() {
        return type;
    }

    @Override
    public void updatePos() {
        // Update position if needed, handled automatically by Box2D in most cases
    }

    @Override
    public void render() {
        // Render logic handled in draw()
    }

    @Override
    public void takeDamage(float amount) {
        // Implement if needed
    }

    @Override
    public void update() {
        if (OutOfBounds()) {
            setAlive(false);
        }
    }

    public boolean OutOfBounds() {
        // Implement bounds checking logic
        float worldWidth = 680; // Adjust based on your game world
        float worldHeight = 400;

        Vector2 position = birdie.getPosition();
        return (position.x < 0 || position.x > worldWidth / PIXELS_TO_METERS ||
            position.y < 0 || position.y > worldHeight / PIXELS_TO_METERS);
    }

    public void dispose() {
        objectSprite.getTexture().dispose();
    }

    public void launch(Vector2 launchVelocity) {
        birdie.setLinearVelocity(0, 0); // Reset any current velocity
        birdie.applyLinearImpulse(
            new Vector2(launchVelocity.x / PIXELS_TO_METERS, launchVelocity.y / PIXELS_TO_METERS),
            birdie.getWorldCenter(),
            true
        );
        setAlive(true);
    }

    public boolean isReadyForLaunch() {
        return birdie.getLinearVelocity().len() < 0.1f; // If almost stationary
    }

    public Body getBirdBody() {
        return birdie;
    }

    @Override
    public void destroyBody() {
        if (birdie != null) {
            world.destroyBody(birdie);
            birdie = null;
        }
    }
}
