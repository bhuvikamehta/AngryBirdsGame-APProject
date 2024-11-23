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
        box2DWorld.update(Gdx.graphics.getDeltaTime());
        box2DWorld.render();

        batch.begin();
        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
