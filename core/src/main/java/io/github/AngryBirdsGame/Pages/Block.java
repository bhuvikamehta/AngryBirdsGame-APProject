package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.AngryBirdsGame.AngryBirds;

public abstract class Block extends GameObject implements PhysicsBody{
    private float health;
    private Level level;
    private static final float BLOCK_DAMAGE_MULTIPLIER = 0.5f;
    private float durability;
    private AngryBirds game;
    private float destroyTimer=-1;
    private boolean breaking=false;

    protected abstract float getDensity();
    protected abstract float getFriction();
    protected abstract float getRestitution();

    public void setBreaking(boolean breaking) {
        this.breaking = breaking;
    }

    public boolean isBreaking() {
        return breaking;
    }

    public void setDestroyTimer(float seconds) {
        this.destroyTimer = seconds;
    }


    @Override
    public void render() {

    }

    private String type;
    private Body blockie;
    private boolean destroyed;

    public Block(World world, String texturePath, String type, float scale, float posX, float posY, AngryBirds g) {
        super(world, texturePath, scale, posX, posY);
        this.type=type;
        this.destroyed=false;
        objectSprite.setScale(scale);
        objectSprite.setPosition(posX, posY);
        createBody(BodyDef.BodyType.DynamicBody);
        this.durability=20;
        this.game=g;
        blockie.setUserData(this);
        initBlockProperties();

    }
    protected abstract void initBlockProperties();

    public boolean isDestroyed(){
        return destroyed;
    }

    public void setDestroyed(boolean v){
        this.destroyed=v;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Body getBlockie() {
        return blockie;
    }

    public void setBlockie(Body blockie) {
        this.blockie = blockie;
    }

    public void reduceDurability(float damage) {
        this.durability -= damage;
        if (this.durability <= 0) {
            setDestroyed(true);
        }
    }

    public float getDurability(){
        return this.durability;
    }

    private void markForDestruction() {
        if (game != null && game.getCurrentLevel() != 0) {
            if(game.getCurrentLevel()==1){
                game.getLeve1().markForDestruction(this);
            }
            else if(game.getCurrentLevel()==2){
                game.getLeve2().markForDestruction(this);
            }
            else if(game.getCurrentLevel()==3){
                game.getLeve3().markForDestruction(this);
            }


        }
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }


    public boolean isAlive(){
        if(this.health<=0){
            return false;
        }
        else{
            return true;
        }
    }

    public void setAlive(boolean b){
        this.isAlive=b;
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
        fixtureDef.density = 5.0f;  // Static bodies don't need mass
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.2f;// No bounce for blocks
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


    public void draw(SpriteBatch batch) {

        objectSprite.draw(batch);
    }



    public void setPosition(float x, float y) {
        this.X = x;
        this.Y = y;
        objectSprite.setPosition(x, y);
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

    public void takeDamage(float impactForce) {
        float damage = impactForce * BLOCK_DAMAGE_MULTIPLIER;
        health -= damage;
        if (health <= 0) {
            destroyed = true;
        }
    }

    @Override
    public void update() {
        if (isDestroyed()) {
            // Destroy the block's body and remove it from the world
            if (blockie != null) {
                //blockie.setType(BodyDef.BodyType.DynamicBody);
                world.destroyBody(blockie);
                blockie = null; // Prevent further interaction
            }
        } else {

            // Synchronize sprite position with the physics body
            if (blockie != null) {
                Vector2 position = blockie.getPosition();
                objectSprite.setPosition(
                    (position.x * PIXELS_TO_METERS) - (objectSprite.getWidth() / 2),
                    (position.y * PIXELS_TO_METERS) - (objectSprite.getHeight() / 2)
                );
                objectSprite.setRotation((float) Math.toDegrees(blockie.getAngle()));
            }
        }
    }

    @Override
    public void destroyBody() {
        if (blockie != null && world != null) {
            try {
                world.destroyBody(blockie);
                blockie = null;
            } catch (Exception e) {
                System.err.println("Error destroying block body: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void handleCollision(float impactForce) {
        takeDamage(impactForce);
    }
}
