package io.github.AngryBirdsGame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.AngryBirdsGame.Pages.LoadingPage;

public class AngryBirds extends Game{

    public SpriteBatch batch;
    public Stage stage;

    @Override
    public void create() {
        batch = new SpriteBatch();
        stage = new Stage();
        setScreen(new LoadingPage(this));

    }
    @Override
    public void render() {
        super.render();

    }
    @Override
    public void dispose() {
        batch.dispose();

    }



}
