package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.physics.box2d.World;

public class RedBird extends Bird {

    public RedBird(World world, String texturePath, float scale,float posX, float posY) {
        super(world, texturePath, "RedBird", scale, posX, posY);
    }

    @Override
    public void updatePos() {

    }

    @Override
    public void render() {

    }

    @Override
    public float getDamage() {
        return 70f; // Weakest bird
    }
}
