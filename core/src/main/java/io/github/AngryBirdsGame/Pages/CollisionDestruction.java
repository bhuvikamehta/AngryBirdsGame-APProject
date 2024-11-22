package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CollisionDestruction{
    private Vector2 position;
    private float stateTime;
    private float scale;
    private boolean isFinished;
    private ShapeRenderer shapeRenderer;
    private static final float ANIMATION_DURATION = 0.5f;

    public CollisionDestruction(float x, float y, float scale) {
        position = new Vector2(x, y);
        stateTime = 0f;
        this.scale = scale;
        isFinished = false;
        shapeRenderer = new ShapeRenderer();
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        if (stateTime >= ANIMATION_DURATION) {
            isFinished = true;
        }
    }

    public void render(SpriteBatch batch) {
        if (!isFinished) {
            // Pause the sprite batch rendering
            batch.end();

            // Start shape rendering
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeType.Filled);

            // Calculate animation progress
            float progress = stateTime / ANIMATION_DURATION;

            // Create an expanding, fading explosion effect
            for (int i = 0; i < 5; i++) {
                float radius = progress * scale * (i + 1) * 20;
                float alpha = 1 - progress;

                shapeRenderer.setColor(new Color(1f, 0.5f, 0f, alpha)); // Orange-red color
                shapeRenderer.circle(position.x, position.y, radius);
            }

            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            // Restart sprite batch rendering
            batch.begin();
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
