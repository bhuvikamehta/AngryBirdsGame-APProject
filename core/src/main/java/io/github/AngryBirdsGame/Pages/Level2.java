package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Preferences;
import io.github.AngryBirdsGame.AngryBirds;

public class Level2 implements Screen{
    private AngryBirds game;
    private boolean snd = true;
    private SpriteBatch batch;
    private Sprite bgSprite, pauseGame, exitGame;
    private Music music_buff, icon_sound, shoot_sound, hit_sound;
    private Preferences preferences;
    private float touchCooldown = 0.5f;  // 0.5 seconds cooldown
    private float lastTouchTime = 0;

    public Level2(AngryBirds game){
        this.game=game;
        this.batch = new SpriteBatch();
        Texture bgtexture=new Texture(Gdx.files.internal("Images/Level2-bg.png"));
        Texture pause=new Texture(Gdx.files.internal("Images/pauseICON.jpeg"));
//        Texture exit=new Texture(Gdx.files.internal());
//        //playing gamesound before shoot
//        music_buff = Gdx.audio.newMusic(Gdx.files.internal());
        icon_sound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/tap.mp3"));
//        shoot_sound=Gdx.audio.newMusic(Gdx.files.internal());
//        hit_sound=Gdx.audio.newMusic(Gdx.files.internal());

        music_buff.setLooping(true);
        if (!music_buff.isPlaying()) music_buff.play();

        bgSprite=new Sprite(bgtexture);
        pauseGame=new Sprite(pause);
        //exitGame=new Sprite(exit);

//        pauseGame.setScale();
//        pauseGame.setPosition();
//        exitGame.setScale();
//        exitGame.setPosition();
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
        pauseGame.draw(batch);
        exitGame.draw(batch);
        batch.end();

        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (Gdx.input.isTouched() && (Gdx.graphics.getDeltaTime() - lastTouchTime > touchCooldown)) {
            lastTouchTime = Gdx.graphics.getDeltaTime();  // Update last touch time

            if (isButtonTouched(touchX, touchY, pauseGame)) {
                handleButtonClick(() -> {
                    game.setScreen(new PauseGamePage(game));
                });
            } else if (isButtonTouched(touchX, touchY, exitGame)) {
                handleButtonClick(() -> {
                    Gdx.app.exit();
                });
            }

        }


    }
    private void handleButtonClick(Runnable action) {
        if (snd) {
            icon_sound.play();  // Play sound once per tap
            snd = false;
        }
        action.run();
    }

    private boolean isButtonTouched(float x, float y, Sprite button) {
        return x >= button.getX() && x <= (button.getX() + button.getWidth()) &&
            y >= button.getY() && y <= (button.getY() + button.getHeight());
    }

    private boolean isSpriteTouched(Sprite sprite, float touchX, float touchY) {
        return touchX >= sprite.getX() && touchX <= (sprite.getX() + sprite.getWidth()) &&
            touchY >= sprite.getY() && touchY <= (sprite.getY() + sprite.getHeight());
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
        batch.dispose();
        icon_sound.dispose();
        music_buff.dispose();




    }
}
