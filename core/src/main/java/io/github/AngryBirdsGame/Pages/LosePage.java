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


public class LosePage implements Screen{
    private AngryBirds game;
    private SpriteBatch batch;
    private final Sprite bgSprite;
    private final Sprite tryAgain;
    private final Sprite goToLevels;
    private final Sprite exit;
    private final Music music_buff;
    private final Music icon_sound;
    private boolean snd = true;
    private Preferences preferences;
    private float touchCooldown = 0.5f;  // 0.5 seconds cooldown
    private float lastTouchTime = 0;  // Track last touch time
    private OrthographicCamera camera;

    public LosePage(AngryBirds angrybirdsgame){
        this.game=angrybirdsgame;
        this.batch=new SpriteBatch();

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Texture bgTexture = new Texture(Gdx.files.internal("Images/lbg.png"));
        Texture tryAgainIcon = new Texture(Gdx.files.internal("Images/restart.png"));
        Texture goToLevelsIcon = new Texture(Gdx.files.internal("Images/gotolevelsICON.png"));
        Texture exitIcon = new Texture(Gdx.files.internal("Images/exitgameICON.png"));

        music_buff = Gdx.audio.newMusic(Gdx.files.internal("Sounds/lose.mp3"));
        icon_sound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/tap.mp3"));

//        music_buff.setLooping(true);
//        if(!music_buff.isPlaying())
            music_buff.play();

        bgSprite = new Sprite(bgTexture);
        tryAgain = new Sprite(tryAgainIcon);
        goToLevels = new Sprite(goToLevelsIcon);
        exit = new Sprite(exitIcon);

        tryAgain.setScale(0.42f);
        goToLevels.setScale(1f);
        exit.setScale(1f);

        tryAgain.setPosition(200,125);
        goToLevels.setPosition(130,100);
        exit.setPosition(150,20);

    }


    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(bgSprite, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        tryAgain.draw(batch);
        goToLevels.draw(batch);
        exit.draw(batch);
        batch.end();

        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Handle touch input
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            // Check each sprite's bounds
            if (tryAgain.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                //music_buff.stop();
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
            icon_sound.play();  // Play sound once per tap
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
        game.dispose();
        batch.dispose();
        music_buff.dispose();
        icon_sound.dispose();
        bgSprite.getTexture().dispose();
        tryAgain.getTexture().dispose();
        goToLevels.getTexture().dispose();
        exit.getTexture().dispose();
    }
}
