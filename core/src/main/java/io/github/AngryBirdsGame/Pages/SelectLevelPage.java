package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Preferences;
import io.github.AngryBirdsGame.AngryBirds;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;


public class SelectLevelPage implements Screen{

    private AngryBirds game;
    private boolean snd = true;
    private SpriteBatch batch;
    private Sprite bgSprite, level1, level2, level3, exitGame, lockedGame1, lockedGame2;
    private Music music_buff, icon_sound;
    private Preferences preferences;
    private int maxLevels = 5;
    private float touchCooldown = 0.5f;  // 0.5 seconds cooldown
    private float lastTouchTime = 0;
    private boolean isLevel1Won;
    private boolean isLevel2Won;


    public SelectLevelPage(AngryBirds game, int currentLevel) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.preferences = Gdx.app.getPreferences("AngryBirdsProgress");
        Texture bgtexture=new Texture(Gdx.files.internal("Images/levelpagebg.jpeg"));
        Texture l1=new Texture(Gdx.files.internal("Images/l1.png"));
        Texture l2=new Texture(Gdx.files.internal("Images/l2.png"));
        Texture l3=new Texture(Gdx.files.internal("Images/l3.png"));
        Texture exit=new Texture(Gdx.files.internal("Images/backimg.png"));
        //  Texture lock=new Texture(Gdx.files.internal("Images/lock.png"));
        Texture lock=new Texture(Gdx.files.internal("Images/LockedLevel.png"));
        music_buff = Gdx.audio.newMusic(Gdx.files.internal("Sounds/bg.mp3"));
        icon_sound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/tap.mp3"));

        music_buff.setLooping(true);
        if (!music_buff.isPlaying()) music_buff.play();
//
        bgSprite=new Sprite(bgtexture);
        level1=new Sprite(l1);
        level2=new Sprite(l2);
        level3=new Sprite(l3);
        // lockedicon=new Sprite(lock);
        exitGame=new Sprite(exit);
        lockedGame1=new Sprite(lock);
        lockedGame2=new Sprite(lock);

        level1.setScale(0.15f);
        level2.setScale(0.15f);
        level3.setScale(0.15f);
        // lockedicon.setScale(0.2f);
        exitGame.setScale(0.2f);
        lockedGame1.setScale(.9f);
        lockedGame2.setScale(.9f);
        level1.setPosition(-220,-20);
        level2.setPosition(-115, 30);
        level3.setPosition(10, 130);
        exitGame.setPosition(-140, -120);
        lockedGame1.setPosition(300, 165);
        lockedGame2.setPosition(495, 150);
//        isLevel1Won=preferences.getBoolean("Level1Completed", false);
//        isLevel2Won=preferences.getBoolean("Level2Completed", false);


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(bgSprite, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        level1.draw(batch);
//        if(isLevel1Won){
//            level2.draw(batch);
//        }else{
//            lockedGame1.draw(batch);
//        }
//        if (isLevel1Won && isLevel2Won){
//            level3.draw(batch);
//        }
//        else{
//            lockedGame2.draw(batch);
//        }
//
        level2.draw(batch);
        level3.draw(batch);
        exitGame.draw(batch);
        lockedGame1.draw(batch);
        lockedGame2.draw(batch);
        batch.end();



        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        lastTouchTime += delta;
        if (Gdx.input.isTouched() && (lastTouchTime > touchCooldown)) {
            lastTouchTime = 0;

            if (isButtonTouched(touchX, touchY, level1.getBoundingRectangle())) {
                handleButtonClick(() -> {
                    music_buff.stop();
                    preferences.putInteger("currentLevel", 1);
                    preferences.flush();
                    game.setScreen(new Level1(game));
                });
            }
            else if (isButtonTouched(touchX, touchY, level2.getBoundingRectangle())) {
                //if(isLevel1Won)elselockedLevelMessage
                handleButtonClick(() -> {
                    music_buff.stop();
                    int savedLevel = preferences.getInteger("currentLevel", 2);
                    game.setScreen(new Level2(game));
                });
            }
            else if (isButtonTouched(touchX, touchY, level3.getBoundingRectangle())) {
                //if(isLevel1Won&&islevel2Won)elselockedLevelMessage
                handleButtonClick(() -> {
                    music_buff.stop();
                    preferences.putInteger("currentLevel", 3);
                    preferences.flush();
                    game.setScreen(new Level3(game));
                });
            }
            else if (isButtonTouched(touchX, touchY, exitGame.getBoundingRectangle())) {
                handleButtonClick(() -> {
                    game.setScreen(new MainMenuPage(game));
                });            }
        }
    }

    private void lockedLevelMessage(){
        if(snd){
            System.out.println("Level is locked. Complete previous levels first.");
        }
    }


    //call after successfully completing level
    public void unlockNext(int currLevel) {
        Preferences preferences = Gdx.app.getPreferences("AngryBirdsProgress");

        int unlockedLevel = preferences.getInteger("unlockedLevel", 1);

        if (currLevel > unlockedLevel) {
            preferences.putInteger("unlockedLevel", currLevel);
            preferences.flush();
        }
    }


    private boolean isButtonTouched(float x, float y, Rectangle buttonBounds) {
        return buttonBounds.contains(x, y);
    }

    private void handleButtonClick(Runnable action) {
        if (snd) {
            icon_sound.play();
            snd = false;
        }
        action.run();
        snd = true;
    }

//    private boolean isButtonTouched(float x, float y, Sprite button) {
//        x=Gdx.graphics.getHeight()-y;
//        return button.getBoundingRectangle().contains(x,y);
////        return x >= button.getX() && x <= (button.getX() + button.getWidth()) &&
////            y >= button.getY() && y <= (button.getY() + button.getHeight());
//    }

    private boolean isSpriteTouched(Sprite sprite, float touchX, float touchY) {
        return touchX >= sprite.getX() && touchX <= (sprite.getX() + sprite.getWidth()) &&
            touchY >= sprite.getY() && touchY <= (sprite.getY() + sprite.getHeight());
    }

    // Check if the level is unlocked
    private boolean isLevelUnlocked(int level) {
        int savedLevel = preferences.getInteger("currentLevel", 1);  // Get saved progress
        return level <= savedLevel;
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
        bgSprite.getTexture().dispose();
        icon_sound.dispose();
        music_buff.dispose();
        level1.getTexture().dispose();
        level2.getTexture().dispose();
        level3.getTexture().dispose();
        exitGame.getTexture().dispose();
        lockedGame2.getTexture().dispose();
        lockedGame1.getTexture().dispose();

    }
}
