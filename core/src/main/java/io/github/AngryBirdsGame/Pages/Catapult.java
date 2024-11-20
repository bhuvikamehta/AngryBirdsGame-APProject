package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Catapult {
    private Body body;
    private World world;
    private Vector2 position;

    public Catapult(World world, Vector2 position) {
        this.world = world;
        this.position = position;
        createCatapultBody();
    }

    private void createCatapultBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(2, 0.5f); // Width and height of the catapult base

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void launchBird(Bird bird) {
        // You can define the launch logic based on the catapult's string
        Vector2 launchVelocity = new Vector2(20, 10); // Example velocity
        bird.launch(launchVelocity);
    }
}
