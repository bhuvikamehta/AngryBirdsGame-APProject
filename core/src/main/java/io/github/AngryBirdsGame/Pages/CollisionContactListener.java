package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
import io.github.AngryBirdsGame.Pages.GameObject;

public class CollisionContactListener implements ContactListener {
    private Level level;
    protected static final float PIXELS_TO_METERS = 100f;

    public CollisionContactListener(Level level) {
        this.level = level;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        Bird b=null;
        Block bl=null;
        Pig p=null;

        if((bodyA.getUserData() instanceof RedBird || bodyA.getUserData() instanceof BlackBird || bodyA.getUserData() instanceof YellowBird)&&
            (bodyB.getUserData() instanceof Pig1 || bodyB.getUserData() instanceof Pig2 || bodyB.getUserData() instanceof Pig3)){
            b = (Bird) bodyA.getUserData();
            p = (Pig) bodyB.getUserData();
            handleBirdPigCollision(b, p);
            updateScore(p);
        } else if ((bodyB.getUserData() instanceof RedBird || bodyB.getUserData() instanceof YellowBird || bodyB.getUserData() instanceof BlackBird) &&
            (bodyA.getUserData() instanceof Pig1 || bodyA.getUserData() instanceof Pig2 || bodyA.getUserData() instanceof Pig3)) {
            b = (Bird) bodyB.getUserData();
            p = (Pig) bodyA.getUserData();
            handleBirdPigCollision(b, p);
            updateScore(p);
        }

        if ((bodyA.getUserData() instanceof RedBird || bodyA.getUserData() instanceof YellowBird || bodyA.getUserData() instanceof BlackBird) &&
            (bodyB.getUserData() instanceof WoodBlock || bodyB.getUserData() instanceof SteelBlock || bodyB.getUserData() instanceof GlassBlock)) {
            b = (Bird) bodyA.getUserData();
            bl = (Block) bodyB.getUserData();
            handleBirdBlockCollision(b, bl);
            updateScore(bl);
        }
        else if ((bodyB.getUserData() instanceof RedBird || bodyB.getUserData() instanceof YellowBird || bodyB.getUserData() instanceof BlackBird) &&
            (bodyA.getUserData() instanceof GlassBlock || bodyA.getUserData() instanceof WoodBlock || bodyA.getUserData() instanceof SteelBlock)) {
            b = (Bird) bodyB.getUserData();
            bl = (Block) bodyA.getUserData();
            handleBirdBlockCollision(b, bl);
            updateScore(bl);
        }

        if ((bodyA.getUserData() instanceof WoodBlock || bodyA.getUserData() instanceof SteelBlock || bodyA.getUserData() instanceof GlassBlock) &&
            (bodyB.getUserData() instanceof Pig1 || bodyB.getUserData() instanceof Pig2 || bodyB.getUserData() instanceof Pig3)) {
            bl = (Block) bodyA.getUserData();
            p = (Pig) bodyB.getUserData();
            handleBlockPigCollision(bl, p);
            updateScore(p);
        }
        else if ((bodyB.getUserData() instanceof WoodBlock || bodyB.getUserData() instanceof GlassBlock || bodyB.getUserData() instanceof SteelBlock) &&
            ( bodyA.getUserData() instanceof Pig1 || bodyA.getUserData() instanceof Pig2 || bodyA.getUserData() instanceof Pig3)) {
            bl = (Block) bodyB.getUserData();
            p = (Pig) bodyA.getUserData();
            handleBlockPigCollision(bl, p);
            updateScore(p);
        }


    }

    private void updateScore(GameObject hitObject) {
        if (level != null) {
            if (hitObject instanceof Pig) {
                // Award more points for destroying a pig
                level.addScore(80);
            } else if (hitObject instanceof Block) {
                // Award fewer points for hitting a block
                level.addScore(50);
            }
        }
    }




    private void handleBirdPigCollision(Bird bird, Pig pig) {
        Vector2 birdVelocity = bird.getBirdBody().getLinearVelocity();
        float collisionSpeed = birdVelocity.len();
        float birdMass = bird.getBirdBody().getMass();

        System.out.println("Bird-Pig Collision Detected:");
        System.out.println("Bird Velocity: " + collisionSpeed);
        System.out.println("Bird Mass: " + birdMass);

        // Calculate damage based on momentum
        float damage = Math.max(10f, collisionSpeed * birdMass * 10f);
        System.out.println("Calculated Damage to Pig: " + damage);
        pig.reduceHealth(damage);
        System.out.println("Pig Hit! Damage: " + damage + ", Remaining Health: " + pig.getHealth());
    }

    private void handleBirdBlockCollision(Bird bird, Block block) {
        if (bird == null || block == null) {
            System.out.println("Warning: Null bird or block in collision!");
            return;
        }

        Body birdBody = bird.getBirdBody();
        if (birdBody == null) {
            System.out.println("Warning: Bird has no physics body!");
            return;
        }

        Vector2 birdVelocity = bird.getBirdBody().getLinearVelocity();
        float collisionSpeed = birdVelocity.len();
        float birdMass = bird.getBirdBody().getMass();

        System.out.println("Bird-Wood Structure Collision Detected:");
        System.out.println("Bird Velocity: " + collisionSpeed);
        System.out.println("Bird Mass: " + birdMass);

        // Calculate damage based on momentum
        float damage = Math.max(10f, collisionSpeed * birdMass * 10f);
        System.out.println("Calculated Damage to Wood Structure: " + damage);

        // Apply damage to the wood structure
        if (block instanceof WoodBlock || block instanceof SteelBlock || block instanceof GlassBlock) {
            block.reduceDurability(damage);
            Body blockBody = block.getBlockBody();
            float tumbleForce = damage * 0.5f; // Adjust multiplier as needed

            // Apply rotational impulse
            blockBody.applyAngularImpulse(tumbleForce, true);

            // Apply linear impulse in the direction of collision
            Vector2 impulseDir = birdVelocity.cpy().nor();
            blockBody.applyLinearImpulse(
                impulseDir.x * tumbleForce,
                impulseDir.y * tumbleForce,
                blockBody.getWorldCenter().x,
                blockBody.getWorldCenter().y,
                true
            );
        }
    }

    private void handleBlockPigCollision(Block block, Pig pig) {
        Vector2 woodVelocity = (block).getBlockBody().getLinearVelocity();
        float impactSpeed = woodVelocity.len();
        float blockMass = (block).getBlockBody().getMass();


        System.out.println("Wood Structure-Pig Collision Detected:");
        System.out.println("Wood Structure Velocity: " + impactSpeed);
        System.out.println("Wood Structure Mass: " +blockMass);


        float damage = Math.max(5f, impactSpeed * blockMass * 5f);
        System.out.println("Calculated Damage to Pig: " + damage);

        pig.reduceHealth(damage);
        Body blockBody = block.getBlockBody();
        float tumbleForce = impactSpeed * blockMass * 0.3f; // Adjust multiplier as needed

        // Apply rotational impulse for realistic tumbling
        blockBody.applyAngularImpulse(tumbleForce, true);
        System.out.println("Pig Hit by Wood Structure! Damage: " + damage + ", Remaining Health: " + pig.getHealth());
        if (pig.getHealth() <= 0) {
            System.out.println("Pig Destroyed!");
            //addCollisionAnimation(pig);
           // checkAndMarkDestruction(pig);
        }
    }

    private void checkAndMarkDestruction(GameObject gameObject) {
        if (gameObject != null && !gameObject.isAlive()) {
            level.markForDestruction(gameObject);
        }
    }

    // Add collision animation at object's location
    private void addCollisionAnimation(GameObject gameObject) {
        if (gameObject == null) {
            System.err.println("Attempted to add animation for null game object");
            return;
        }

        Body body = gameObject.getBody();
        if (body == null) {
            System.err.println("Attempted to add animation for game object with null body");
            return;
        }
        try {
            Vector2 position = body.getPosition();
            if (position == null) {
                System.err.println("Attempted to add animation with null position");
                return;
            }

            float x = position.x * Level1.PIXELS_TO_METERS;
            float y = position.y * Level1.PIXELS_TO_METERS;
            level.addDestructionAnimation(x, y, 0.5f);
        } catch (Exception e) {
            System.err.println("Error adding collision animation: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void checkWorldBoundaries(Bird bird) {
        Vector2 position = bird.getBody().getPosition();

        // Use Gdx.graphics dimensions and convert to Box2D world coordinates
        float worldWidth = Gdx.graphics.getWidth() / PIXELS_TO_METERS;
        float worldHeight = Gdx.graphics.getHeight() / PIXELS_TO_METERS;

        // Define a small margin (e.g., 1 unit) to account for boundary thickness
        float margin = 1f / PIXELS_TO_METERS;

        // Check if bird is outside world boundaries with precise calculations
        boolean outOfBounds =
            position.x < margin ||  // Left boundary
                position.x > (worldWidth - margin) ||  // Right boundary
                position.y < margin ||  // Bottom boundary
                position.y > (worldHeight - margin);  // Top boundary

        if (outOfBounds) {
            System.out.println("Bird out of world boundaries");
            System.out.println("Bird Position: x=" + position.x + ", y=" + position.y);
            System.out.println("World Dimensions: width=" + worldWidth + ", height=" + worldHeight);

            level.markForDestruction(bird);
            level.addDestructionAnimation(
                position.x * PIXELS_TO_METERS,
                position.y * PIXELS_TO_METERS,
                0.3f
            );
        }
    }

    private float calculateImpactForce(Contact contact) {
        ContactWrapper contactWrapper = new ContactWrapper(contact);
        WorldManifold worldManifold = contactWrapper.getWorldManifold();

        Vector2 normal = worldManifold.getNormal();
        return Math.abs(normal.x) + Math.abs(normal.y);
    }



    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
