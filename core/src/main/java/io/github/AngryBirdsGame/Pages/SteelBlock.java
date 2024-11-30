package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.physics.box2d.World;
import io.github.AngryBirdsGame.AngryBirds;

public class SteelBlock extends Block{
    private float health;
    private float durability;
    public SteelBlock(World world, String texturePath, float scale, float posX, float posY, AngryBirds g) {
        super(world, texturePath, "steel", scale, posX, posY, g);
    }
    @Override
    protected void initBlockProperties() {
        this.health=30f;
        this.durability=300f;

    }
    @Override
    protected float getDensity() {
        return 20.0f;
    }

    @Override
    protected float getFriction() {
        return 1f;
    }

    @Override
    protected float getRestitution() {
        return 0f;
    }


}
