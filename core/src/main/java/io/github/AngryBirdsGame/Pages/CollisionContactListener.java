package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
import io.github.AngryBirdsGame.Pages.GameObject;

public class CollisionContactListener implements ContactListener {
    private Level1 level;
    private static final float DESTRUCTION_THRESHOLD = 8.0f;

    public CollisionContactListener(Level1 level) {
        this.level = level;
    }

    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        Object userDataA = bodyA.getUserData();
        Object userDataB = bodyB.getUserData();

        // Calculate impact force
        Vector2 velocity = bodyA.getLinearVelocity().sub(bodyB.getLinearVelocity());
        float impactForce = velocity.len();

        if (impactForce > DESTRUCTION_THRESHOLD) {
            // Handle pig collision
            if ((userDataA instanceof Bird && userDataB instanceof Pig) ||
                (userDataB instanceof Bird && userDataA instanceof Pig)) {
                Body pigBody = userDataA instanceof Pig ? bodyA : bodyB;
                handlePigCollision(pigBody);
            }

            // Handle block collision
            if ((userDataA instanceof Bird && userDataB instanceof Block) ||
                (userDataB instanceof Bird && userDataA instanceof Block)) {
                Body blockBody = userDataA instanceof Block ? bodyA : bodyB;
                handleBlockCollision(blockBody);
            }
        }
    }

    private void handlePigCollision(Body pigBody) {
        Pig pig = (Pig) pigBody.getUserData();
        pig.takeDamage(10f); // Adjust damage value as needed

        float x = pigBody.getPosition().x * Level1.PIXELS_TO_METERS;
        float y = pigBody.getPosition().y * Level1.PIXELS_TO_METERS;
        level.addDestructionAnimation(x, y, 0.5f);
    }

    private void handleBlockCollision(Body blockBody) {
        Block block = (Block) blockBody.getUserData();
        block.takeDamage(10f); // Adjust damage value as needed

        float x = blockBody.getPosition().x * Level1.PIXELS_TO_METERS;
        float y = blockBody.getPosition().y * Level1.PIXELS_TO_METERS;
        level.addDestructionAnimation(x, y, 0.7f);
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
