package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.physics.box2d.World;

public class BlackBird extends Bird {

    public BlackBird(World world, String texturePath, float scale,float posX, float posY) {
        super(world, texturePath, "BlackBird", scale, posX, posY);
    }

    @Override
    public void updatePos() {

    }

    @Override
    public void render() {

    }

    @Override
    public float getDamage() {
        return 200f; // Strongest bird
    }
}
