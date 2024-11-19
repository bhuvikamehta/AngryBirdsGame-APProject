package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Bird extends GameObject {

    private String type;
    private Body birdie;

    public Bird(World world, String texturePath, String type, float scale, float posX, float posY) {
        super(world, texturePath, scale, posX, posY);
        this.type = type;
        objectSprite.setScale(scale);
        objectSprite.setPosition(posX, posY);
        createBody(BodyDef.BodyType.DynamicBody); // DynamicBody for gravity simulation
        createCircleFixture(1.0f, 1.0f, 0.2f); // Adjust density, friction, and restitution
    }

    @Override
    protected void createBody(BodyDef.BodyType bodytype) {
        BodyDef birdBody = new BodyDef();
        birdBody.type = bodytype;
        birdBody.position.set(X / PIXELS_TO_METERS, Y / PIXELS_TO_METERS); // Convert to Box2D scale
        birdie = world.createBody(birdBody);
        birdie.setUserData(this); // Store this Bird object in the body for later reference
        birdie.setGravityScale(1.5f); // Optional: Increases the bird's gravity effect by 1.5x

        birdie.setLinearDamping(1.0f);  // Adjust value between 0 and 1


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

    public void dispose() {
        objectSprite.getTexture().dispose();
    }

    public void launch(Vector2 launchVelocity) {
        birdie.setLinearVelocity(0, 0); // Reset any current velocity
        birdie.applyLinearImpulse(
            new Vector2(launchVelocity.x / PIXELS_TO_METERS, launchVelocity.y / PIXELS_TO_METERS),
            birdie.getWorldCenter(),
            true
        ); // Launch the bird with a specific velocity
    }

    public boolean isReadyForLaunch() {
        // Optional helper method: check if bird is stationary and ready to launch
        return birdie.getLinearVelocity().len() < 0.1f; // If almost stationary
    }
    public Body getBirdBody() {
        return birdie;
    }
}
