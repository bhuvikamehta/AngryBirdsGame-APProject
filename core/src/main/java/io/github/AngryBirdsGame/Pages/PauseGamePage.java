package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.AngryBirdsGame.AngryBirds;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class PauseGamePage implements Screen {
    private AngryBirds game;
    private SpriteBatch batch;
    private Sprite bgSprite, resume, restart,goToMainMenu, saveAndExit;
    private Music music_buff,icon_sound;
    private boolean snd = true;
    private float touchCooldown = 0.5f;  // 0.5 seconds cooldown
    private float lastTouchTime = 0;  // Track last touch time
    private OrthographicCamera camera;


    public PauseGamePage(AngryBirds angrybirdsgame){
        this.game=angrybirdsgame;
        this.batch= new SpriteBatch();

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Texture bgTexture = new Texture(Gdx.files.internal("Images/pausepage_bg.jpeg")); //images to be set abhi
        Texture resumeIcon=new Texture(Gdx.files.internal("images/resume-pause.png"));
        Texture restartIcon=new Texture(Gdx.files.internal("Images/restart-pause.png"));
        Texture goToMainMenuIcon=new Texture(Gdx.files.internal("Images/goMM.png"));
        Texture saveAndExitIcon=new Texture(Gdx.files.internal("Images/seMM.png"));

        music_buff = Gdx.audio.newMusic(Gdx.files.internal("Sounds/bg.mp3"));
        icon_sound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/tap.mp3"));
        music_buff.setLooping(true);
        if (!music_buff.isPlaying()) music_buff.play();

        bgSprite = new Sprite(bgTexture);
        resume=new Sprite(resumeIcon);
        restart=new Sprite(restartIcon);
        goToMainMenu=new Sprite(goToMainMenuIcon);
        saveAndExit=new Sprite(saveAndExitIcon);

        resume.setScale(0.4f);
        restart.setScale(0.4f);
        goToMainMenu.setScale(0.4f);
        saveAndExit.setScale(0.4f);

        resume.setPosition(-50,240);
        restart.setPosition(-50,170);
        goToMainMenu.setPosition(-50,100);
        saveAndExit.setPosition(-50,30);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(bgSprite, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        restart.draw(batch);
        resume.draw(batch);
        goToMainMenu.draw(batch);
        saveAndExit.draw(batch);

        batch.end();

        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
             int currL=game.getCurrentLevel();
            // Check each sprite's bounds
            if (resume.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                playSound();
                if(currL==1){
                    game.setScreen(new Level1(game));
                }
                else if(currL==2){
                    game.setScreen(new Level2(game));
                }
                else{
                    game.setScreen(new Level3(game));
                }
            } else if (restart.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                playSound();
                if(currL==1){
                    game.setScreen(new Level1(game));
                }
                else if(currL==2){
                    game.setScreen(new Level2(game));
                }
                else{
                    game.setScreen(new Level3(game));
                }
            } else if (goToMainMenu.getBoundingRectangle().contains(touchPos.x, touchPos.y)) {
                playSound();
                game.setScreen(new MainMenuPage(game));
            }
            else if(saveAndExit.getBoundingRectangle().contains(touchPos.x, touchPos.y)){
                playSound();
                game.setScreen(new MainMenuPage(game));
            }
        }



    }

    private void playSound() {
        if (snd && icon_sound != null) {
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
        resume.getTexture().dispose();
        restart.getTexture().dispose();
        goToMainMenu.getTexture().dispose();
        saveAndExit.getTexture().dispose();

    }
}
