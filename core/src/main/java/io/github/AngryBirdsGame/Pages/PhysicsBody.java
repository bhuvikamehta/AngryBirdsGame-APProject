package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.physics.box2d.Body;

public interface PhysicsBody {
    void destroyBody();

    Body getBody();
}
