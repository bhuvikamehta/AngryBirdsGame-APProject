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

    public Pig(World world, String texturePath, String type, float scale, float posX, float posY) {
        super(world, texturePath, scale, posX, posY);
        this.type=type;
        objectSprite.setScale(scale);
        objectSprite.setPosition(posX, posY);
        createBody(BodyDef.BodyType.StaticBody);

    }

    @Override
    protected void createBody(BodyDef.BodyType bodytype){
        BodyDef birdBody=new BodyDef();
        birdBody.type=bodytype;
        birdBody.position.set(X / PIXELS_TO_METERS, Y / PIXELS_TO_METERS);
        pigie=world.createBody(birdBody);
        pigie.setUserData(this);

    }

    @Override
    protected void createCircleFixture(float density, float friction, float restitution) {
        CircleShape shape = new CircleShape();
        shape.setRadius((objectSprite.getWidth() * sizeObject / 2) / PIXELS_TO_METERS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;



        pigie.createFixture(fixtureDef);


        shape.dispose();
    }





    public void draw(SpriteBatch batch) {
        objectSprite.draw(batch);
    }



    public void setPosition(float x, float y) {
        this.X = x;
        this.Y = y;
        objectSprite.setPosition(x, y);
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


}
