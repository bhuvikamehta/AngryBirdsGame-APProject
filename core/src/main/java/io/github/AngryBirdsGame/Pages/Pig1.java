package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.physics.box2d.World;
import io.github.AngryBirdsGame.AngryBirds;

public class Pig1 extends Pig {
    private AngryBirds game;
    public Pig1(World world, String texturePath, float scale, float posX, float posY, AngryBirds g) {
        super(world, texturePath, "Pig1", scale, posX, posY, g, 100);
    }
}
