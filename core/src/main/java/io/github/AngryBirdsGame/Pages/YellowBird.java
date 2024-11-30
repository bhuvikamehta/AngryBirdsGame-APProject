package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.physics.box2d.World;

public class YellowBird extends Bird {

    public YellowBird(World world, String texturePath, float scale,float posX, float posY) {
        super(world, texturePath, "YellowBird", scale, posX, posY);
    }

    @Override
    public void updatePos() {

    }

    @Override
    public void render() {

    }

    @Override
    public float getDamage() {
        return 100f; // Medium strength bird
    }
}
