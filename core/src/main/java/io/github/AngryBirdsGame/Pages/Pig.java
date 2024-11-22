package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Pig extends GameObject  {

    private String type;
    private Body pigie;
    private boolean isALive;
    private boolean isVisible;
    private float health = 0.2f;
    private boolean isDestroying = false;


    public Pig(World world, String texturePath, String type, float scale, float posX, float posY) {
        super(world, texturePath, scale, posX, posY);
        this.type=type;
        objectSprite.setScale(scale);
        objectSprite.setPosition(posX, posY);
        createBody(BodyDef.BodyType.DynamicBody);
        this.isALive=true;
        this.isVisible=false;
        createCircleFixture(1.0f, 1.0f, 0.5f);
       // body.setUserData(this);

    }


    public boolean isALive() {
        return isALive;
    }

    public void setALive(boolean ALive) {
        this.isALive = ALive;
    }

    public boolean isVisible(){
        return isVisible;
    }

    public void setVisible(boolean visible){
        this.isVisible=visible;
    }


    public void jump(){
        if(body!=null){
            body.applyLinearImpulse(new Vector2(0, 2.5f), body.getWorldCenter(), true);
        }
    }

    public void stopJump() {
        if (body != null) {
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
        }
    }





    @Override
    protected void createBody(BodyDef.BodyType bodytype){
        BodyDef pigBody=new BodyDef();
        pigBody.type=bodytype;
        pigBody.position.set(X / PIXELS_TO_METERS, Y / PIXELS_TO_METERS);
        pigie=world.createBody(pigBody);
        pigie.setUserData(this);
//        pigie.setGravityScale(1.5f); // Optional: Increases the bird's gravity effect by 1.5x
//        pigie.setLinearDamping(1.0f);  // Adjust value between 0 and 1

    }

    @Override
    protected void createCircleFixture(float density, float friction, float restitution) {
        CircleShape shape = new CircleShape();
        shape.setRadius((objectSprite.getWidth() * sizeObject / 2) / PIXELS_TO_METERS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;      // Determines mass
        fixtureDef.friction = 0.1f;   // Determines sliding resistance
        fixtureDef.restitution = restitution; // Determines bounciness
        pigie.createFixture(fixtureDef);
        shape.dispose();
    }





    public void draw(SpriteBatch batch) {
        objectSprite.setPosition(
                pigie.getPosition().x * PIXELS_TO_METERS - objectSprite.getWidth() / 2,
                pigie.getPosition().y * PIXELS_TO_METERS - objectSprite.getHeight() / 2
            );
            objectSprite.draw(batch);
    }



    public void setPosition(float x, float y) {
        this.X = x;
        this.Y = y;
        objectSprite.setPosition(x, y);
        pigie.setTransform(x / PIXELS_TO_METERS, y / PIXELS_TO_METERS, 0);
        pigie.setLinearVelocity(0, 0);
        pigie.setAngularVelocity(0); // Stop any rotation
    }

    public void lockPosition() {
        if (pigie != null) {
            pigie.setLinearVelocity(0, 0);
            pigie.setAngularVelocity(0);
            pigie.setGravityScale(0); // Disable gravity
            pigie.setType(BodyDef.BodyType.DynamicBody); // Ensure it stays dynamic
            // Optional: Set very low density and friction to minimize movement
            pigie.setTransform(X / PIXELS_TO_METERS, Y / PIXELS_TO_METERS, 0);
        }
    }

    public Object getUserData() {
        if (pigie != null) {
            return pigie.getUserData();
        }
        return null;
    }





    public String getType() {
        return type;
    }


    @Override
    public void updatePos() {

    }

    @Override
    public void render() {

    }


    public void dispose() {
        objectSprite.getTexture().dispose();
    }

    public Body getPigBody(){
        return pigie;
    }


    public void takeDamage(float damage) {
        health -= damage;
        if (health <= 0) {
            isDestroying = true;
        }
    }

    public void update(float deltaTime) {
        if (isDestroying) {
            // Gradually reduce scale and fade out
            float reductionRate = 0.5f;  // Adjust for desired destruction speed
            sizeObject -= reductionRate * deltaTime;
            objectSprite.setScale(sizeObject);

            // Remove from physics world when completely destroyed
            if (sizeObject <= 0) {
                world.destroyBody(pigie);
                pigie = null;
            }
        }
    }

    // In your collision handling method
    public void handleCollision(float impactForce) {
        takeDamage(impactForce);
    }


}
