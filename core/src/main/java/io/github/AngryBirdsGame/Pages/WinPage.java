package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.AngryBirdsGame.AngryBirds;

public class WinPage implements Screen{
    private AngryBirds game;
    private SpriteBatch batch;
    private final Sprite bgSprite;
    private final Sprite nextLevel;
    private final Sprite goToLevels;
    private final Sprite exit;
    private final Music music_buff;
    private final Music icon_sound;
    private boolean snd = true;
    private float touchCooldown = 0.5f;  
    private float lastTouchTime = 0; 
    private OrthographicCamera camera;

    public WinPage(AngryBirds angrybirdsgame){
        this.game=angrybirdsgame;
        this.batch=new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Texture bgTexture = new Texture(Gdx.files.internal("Images/wbg.png"));
        Texture nextLevelIcon = new Texture(Gdx.files.internal("Images/n_new.png"));
        Texture goToLevelsIcon = new Texture(Gdx.files.internal("Images/gotolevelsICON.png"));
        Texture exitIcon = new Texture(Gdx.files.internal("Images/exitgameICON.png"));
        music_buff = Gdx.audio.newMusic(Gdx.files.internal("assets/Sounds/win.mp3"));
        icon_sound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/tap.mp3"));

            music_buff.play();

        bgSprite = new Sprite(bgTexture);
        nextLevel = new Sprite(nextLevelIcon);
        goToLevels = new Sprite(goToLevelsIcon);
        exit = new Sprite(exitIcon);

        nextLevel.setScale(0.7f);
        goToLevels.setScale(1f);
        exit.setScale(1f);

        nextLevel.setPosition(230,145);
        goToLevels.setPosition(125,100);
        exit.setPosition(150,20);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(bgSprite, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        nextLevel.draw(batch);
        goToLevels.draw(batch);
        exit.draw(batch);
        batch.end();

        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
        
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (nextLevel.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
               music_buff.stop();
                playSound();
                game.setScreen(new Level1(game));
            } else if (goToLevels.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                music_buff.stop();
                playSound();
                game.setScreen(new SelectLevelPage(game,1));
            } else if (exit.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                music_buff.stop();
                playSound();
                game.setScreen(new MainMenuPage(game));
            }
        }
    }
    private void playSound() {
        if (icon_sound != null) {
            icon_sound.play();
        }
    }
    private boolean isButtonTouched(float x, float y, Sprite button) {
        return x >= button.getX() && x <= (button.getX() + button.getWidth()) &&
            y >= button.getY() && y <= (button.getY() + button.getHeight());
    }

    private void handleButtonClick(Runnable action) {
        if (snd) {
            icon_sound.play();  
            snd = false;
        }
        action.run();
    }



    @Override
    public void resize(int i, int i1) {

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
        music_buff.dispose();
        icon_sound.dispose();
        bgSprite.getTexture().dispose();
        nextLevel.getTexture().dispose();
        goToLevels.getTexture().dispose();
        exit.getTexture().dispose();

    }
}
