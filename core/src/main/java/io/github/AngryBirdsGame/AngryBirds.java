package io.github.AngryBirdsGame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.AngryBirdsGame.Pages.Box2DWorld;
import io.github.AngryBirdsGame.Pages.LoadingPage;


public class AngryBirds extends Game{
    public SpriteBatch batch;
    public Stage stage;
    private Box2DWorld box2DWorld;
    private int currentLevel;

    public int getCurrentLevel(){
        return this.currentLevel;
    }

    public void setCurrentLevel(int l){
        this.currentLevel=l;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        stage = new Stage();
        box2DWorld=new Box2DWorld();
        this.setScreen(new LoadingPage(this));

    }

    @Override
    public void render() {
        super.render();
        // Update and render the Box2D world
        box2DWorld.update(Gdx.graphics.getDeltaTime());
        box2DWorld.render();

        // Render other game objects and stage elements (UI)
        batch.begin();
        // Render your game objects (e.g., birds, blocks)
        batch.end();

        // Draw the UI elements
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
