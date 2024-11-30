package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.physics.box2d.World;
import io.github.AngryBirdsGame.AngryBirds;

public class Pig2 extends Pig {
    private AngryBirds game;
    public Pig2(World world, String texturePath, float scale, float posX, float posY, AngryBirds g) {
        super(world, texturePath, "Pig2", scale, posX, posY, g, 200);
    }
}
