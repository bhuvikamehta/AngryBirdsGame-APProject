package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class GameObject {
    protected Sprite objectSprite;
    protected float size;
    protected float sizeObject;
    protected float X;
    protected float Y;
    protected Body body;
    protected World world;
    protected static final float PIXELS_TO_METERS = 100f;

    public GameObject(World world, String texturePath, float size, float posX, float posY) {
        this.world=world;
        Texture objectTexture = new Texture(Gdx.files.internal(texturePath));
        this.objectSprite = new Sprite(objectTexture);
        this.size = size;
        this.sizeObject = size;
        this.X = posX;
        this.Y = posY;

    }

    //later we will have to add for physics objects

    public abstract void updatePos();
    public abstract void render();

    protected void createBody(BodyDef.BodyType bodyType) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(X / PIXELS_TO_METERS, Y / PIXELS_TO_METERS);

        body = world.createBody(bodyDef);
        body.setUserData(this);
    }

    protected void createCircleFixture(float density, float friction, float restitution) {
        if (body == null) return;

        CircleShape circle = new CircleShape();
        circle.setRadius((objectSprite.getWidth() * sizeObject / 2) / PIXELS_TO_METERS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
//        fixtureDef.density = density;
//        fixtureDef.friction = friction;
//        fixtureDef.restitution = restitution;

        body.createFixture(fixtureDef);
        circle.dispose();
    }

    protected void createRectangleFixture(float density, float friction, float restitution) {
        if (body == null) return;

        PolygonShape box = new PolygonShape();
        box.setAsBox(
            (objectSprite.getWidth() * sizeObject / 2) / PIXELS_TO_METERS,
            (objectSprite.getHeight() * sizeObject / 2) / PIXELS_TO_METERS
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
//        fixtureDef.density = density;
//        fixtureDef.friction = friction;
//        fixtureDef.restitution = restitution;

        body.createFixture(fixtureDef);
        box.dispose();
    }

    public float getX() {
        return X;
    }

    public float getY() {
        return Y;
    }

    public void setPosition(float x, float y) {
        this.X = x;
        this.Y = y;



        objectSprite.setPosition(x - objectSprite.getWidth() * sizeObject / 2,
            y - objectSprite.getHeight() * sizeObject / 2);
    }

    public float getSize() {
        return sizeObject;
    }

    public void setSize(float size) {
        this.sizeObject = size;
        objectSprite.setScale(size);


    }

    public void draw(SpriteBatch batch) {
        objectSprite.draw(batch);
    }

    public Body getBody() {
        return body;
    }

    public void dispose() {
        objectSprite.getTexture().dispose();
        if (body!=null) {
            world.destroyBody(body);
        }

    }


    protected float toMeters(float pixels) {
        return pixels / PIXELS_TO_METERS;
    }

    // Helper method to convert meters to pixels
    protected float toPixels(float meters) {
        return meters * PIXELS_TO_METERS;
    }
}
