package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.physics.box2d.World;
import io.github.AngryBirdsGame.AngryBirds;

public class WoodBlock extends Block{
    private float health;
    private float durability;
    public WoodBlock(World world, String texturePath, float scale, float posX, float posY, AngryBirds g) {
        super(world, texturePath, "wood", scale, posX, posY, g);
    }

    @Override
    protected void initBlockProperties() {
        this.health=15f;
        this.durability=150f;
    }


    @Override
    protected float getDensity() {
        return 50.0f;  // highest density
    }

    @Override
    protected float getFriction() {
        return 50f;  // Less friction
    }

    @Override
    protected float getRestitution() {
        return 0.0f;  // Low bounciness
    }
}
