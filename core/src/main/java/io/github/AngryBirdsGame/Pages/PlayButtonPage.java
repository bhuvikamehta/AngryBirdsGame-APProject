package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.AngryBirdsGame.AngryBirds;

public class PlayButtonPage implements Screen{
    private AngryBirds game;
    private SpriteBatch batch;
    private boolean snd = true;
    private Sprite bgSprite, play;
    private Music music_buff;
    private Music icon_sound;

    public PlayButtonPage(AngryBirds angrybirdsgame){
        this.game=angrybirdsgame;
        batch = game.batch;

        Texture bgTexture = new Texture(Gdx.files.internal("Images/PlayButtonPg_bg.png"));
        Texture playicon=new Texture(Gdx.files.internal("Images/Play.png"));
        music_buff = Gdx.audio.newMusic(Gdx.files.internal("Sounds/bg.mp3"));
        icon_sound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/tap.mp3"));

        music_buff.setLooping(true);
        if(!music_buff.isPlaying())
            music_buff.play();

        bgSprite = new Sprite(bgTexture);
        play=new Sprite(playicon);

        play.setScale(0.5f);
        play.setPosition(80, 200);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(bgSprite,0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        play.draw(batch);
        batch.end();
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
        if (touchX >= play.getX() && touchX <= play.getX() + play.getWidth() &&
            touchY >= play.getY() && touchY <= play.getY() + play.getHeight()) {
            if (Gdx.input.isTouched()) {
                if (snd) {
                    icon_sound.play();
                    snd = false;
                }

                game.setScreen(new MainMenuPage(game));
            }
        } else {
            snd = true;
        }

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
        play.getTexture().dispose();
    }
}
