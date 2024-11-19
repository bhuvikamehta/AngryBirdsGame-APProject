package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Box2DWorld {
    public World world;
    public Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    public Box2DWorld() {
        world = new World(new Vector2(0, -10.0f), true); // Gravity pointing downwards
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(); // Set up a simple orthographic camera
        camera.setToOrtho(false, 800, 480); // You can adjust the width and height as needed
    }

    public void update(float deltaTime) {
        world.step(deltaTime, 6, 2); // Box2D time step
    }

    public void render() {
        debugRenderer.render(world, camera.combined); // Use the camera for rendering
    }
}
