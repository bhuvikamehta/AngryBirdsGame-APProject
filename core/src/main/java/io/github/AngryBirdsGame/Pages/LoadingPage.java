package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import io.github.AngryBirdsGame.AngryBirds;

//import io.github.AngryBirdsGame.AngryBirds.offline.StoryMode;
//import org.angrypigs.game.online.JoinGame;

public class LoadingPage implements Screen{
    private AngryBirds game;
    private SpriteBatch batch;
    private Sprite bgSprite;
    private float timeElapsed = 0;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private float loadingTime = 3f;
    //setting bar dimensions
    private static final int BAR_WIDTH = 300;
    private static final int BAR_HEIGHT = 20;
   // private static final int BAR_SPACING = 80;

    public LoadingPage(AngryBirds angrybirdsgame){
        this.game=angrybirdsgame;
        this.batch=new SpriteBatch();
        Texture bgTexture = new Texture(Gdx.files.internal("Images/loadingpg_bg.jpg"));
        bgSprite = new Sprite(bgTexture);
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        font.setColor(Color.BLACK);
        bgSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        bgSprite.setPosition(0, 0);


    }


    @Override
    public void show() {

    }


    @Override
    public void render(float v) {
        timeElapsed += v;
        float progress = Math.min(timeElapsed / loadingTime, 1);

        // Clear the screen and draw the loading page
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int screenWidth = Gdx.graphics.getWidth();
        float centerX = screenWidth / 2f;
        float bottomMargin = 50;
        float barY = bottomMargin;

        batch.begin();
        bgSprite.draw(batch);
        batch.end();

        batch.begin();
        float textY = barY + BAR_HEIGHT + 5;
        font.setColor(1, 1, 1, 0.5f);  // Shadow
        font.draw(batch, "Loading...", centerX - BAR_WIDTH/2 + 2, textY - 2);
        font.setColor(0, 0, 0, 1);     // Main text
        font.draw(batch, "Loading...", centerX - BAR_WIDTH/2, textY);
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeType.Filled);
        float barProgress = Math.max(0, Math.min(1, progress * 3));

        // Draw outer rectangle (white border)
        shapeRenderer.setColor(1, 1, 1, 0.9f);
        shapeRenderer.rect(centerX - BAR_WIDTH/2 - 2, barY - 2, BAR_WIDTH + 4, BAR_HEIGHT + 4);

        shapeRenderer.setColor(0, 0, 0, 0.9f);
        shapeRenderer.rect(centerX - BAR_WIDTH/2 - 1, barY - 1, BAR_WIDTH + 2, BAR_HEIGHT + 2);

        // Draw white background
        shapeRenderer.setColor(1, 1, 1, 0.9f);
        shapeRenderer.rect(centerX - BAR_WIDTH/2, barY, BAR_WIDTH, BAR_HEIGHT);

        // Draw progress (black)
        shapeRenderer.setColor(0, 0, 0, 0.9f);
        shapeRenderer.rect(centerX - BAR_WIDTH/2, barY, BAR_WIDTH * barProgress, BAR_HEIGHT);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        if (timeElapsed >= loadingTime) {
            game.setScreen(new PlayButtonPage(game));
            dispose();
        }

    }

    @Override
    public void resize(int i, int i1) {
        bgSprite.setSize(i, i1);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        bgSprite.getTexture().dispose();
        shapeRenderer.dispose();
        font.dispose();

    }
}
