package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Block extends GameObject {
    @Override
    public void render() {

    }

    private String type;
    private Body blockie;
    private float health = 0.2f;  // Initial health
    private boolean isDestroying = false;

    public Block(World world, String texturePath, String type, float scale, float posX, float posY) {
        super(world, texturePath, scale, posX, posY);
        this.type=type;
        objectSprite.setScale(scale);
        objectSprite.setPosition(posX, posY);
        createBody(BodyDef.BodyType.DynamicBody);
       // body.setUserData(this);

    }

    @Override
    protected void createBody(BodyDef.BodyType bodytype){
        BodyDef blockBody=new BodyDef();
        blockBody.type=bodytype;
        blockBody.position.set(X / PIXELS_TO_METERS, Y / PIXELS_TO_METERS);
        blockie=world.createBody(blockBody);
        blockie.setUserData(this);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            (objectSprite.getWidth() * sizeObject / 2) / PIXELS_TO_METERS,
            (objectSprite.getHeight() * sizeObject / 2) / PIXELS_TO_METERS
        );
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;  // Static bodies don't need mass
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.0f;  // No bounce for blocks

        blockie.createFixture(fixtureDef);
        shape.dispose();


    }

    @Override
    protected void createCircleFixture(float density, float friction, float restitution) {
        CircleShape shape = new CircleShape();
        shape.setRadius((objectSprite.getWidth() * sizeObject / 2) / PIXELS_TO_METERS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;



        blockie.createFixture(fixtureDef);


        shape.dispose();
    }

//    @Override
//    public void render(SpriteBatch batch) {
//
//    }

    public void draw(SpriteBatch batch) {
        objectSprite.draw(batch);
    }



    public void setPosition(float x, float y) {
        this.X = x;
        this.Y = y;
        objectSprite.setPosition(x, y);
    }

    public Object getUserData() {
        if (blockie != null) {
            return blockie.getUserData();
        }
        return null;
    }




    public String getType() {
        return type;
    }


    @Override
    public void updatePos() {

    }

    public Body getBlockBody(){
        return blockie;
    }



    public void dispose() {
        objectSprite.getTexture().dispose();
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
                world.destroyBody(blockie);
                blockie = null;
            }
        }
    }

    public void handleCollision(float impactForce) {
        takeDamage(impactForce);
    }


}
