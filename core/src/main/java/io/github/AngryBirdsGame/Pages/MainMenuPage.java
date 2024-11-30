package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.AngryBirdsGame.AngryBirds;
import com.badlogic.gdx.math.Rectangle;



public class MainMenuPage implements Screen {

    private AngryBirds game;
    private SpriteBatch batch;
    private Sprite bgSprite, startNewGame, continuePreviousGame, exitGame, title;
    private Music music_buff, icon_sound;
    private boolean snd = true;
    private Preferences preferences;
    private float touchCooldown = 0.5f;  // 0.5 seconds cooldown
    private float lastTouchTime = 0;  // Track last touch time

    public MainMenuPage(AngryBirds angrybirdsgame) {
        this.game = angrybirdsgame;
        this.batch = new SpriteBatch();
        Texture bgTexture = new Texture(Gdx.files.internal("Images/mainmneupg_bg.png"));
        Texture startNewGameIcon = new Texture(Gdx.files.internal("Images/newgameICON.png"));
        Texture continuePreviousGameIcon = new Texture(Gdx.files.internal("Images/prevgameICON.png"));
        Texture exitGameIcon = new Texture(Gdx.files.internal("Images/exitMainMenu.png"));
        Texture titleIcon=new Texture(Gdx.files.internal("Images/MainMenutitle.png"));

        music_buff = Gdx.audio.newMusic(Gdx.files.internal("Sounds/bg.mp3"));
        icon_sound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/tap.mp3"));

        music_buff.setLooping(true);
        if (!music_buff.isPlaying()) music_buff.play();

        bgSprite = new Sprite(bgTexture);
        startNewGame = new Sprite(startNewGameIcon);
        continuePreviousGame = new Sprite(continuePreviousGameIcon);
        exitGame = new Sprite(exitGameIcon);
        title=new Sprite(titleIcon);

        startNewGame.setScale(1f);
        continuePreviousGame.setScale(1f);
        exitGame.setScale(1f);
        title.setScale(1.3f);

        startNewGame.setPosition(120, 250);
        continuePreviousGame.setPosition(150, 150);
        exitGame.setPosition(150, 50);
        title.setPosition(120, 350);

        preferences = Gdx.app.getPreferences("AngryBirdsProgress");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(bgSprite, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        startNewGame.draw(batch);
        continuePreviousGame.draw(batch);
        exitGame.draw(batch);
        title.draw(batch);
        batch.end();

        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();


        lastTouchTime+=delta;
        if (Gdx.input.isTouched() && ( lastTouchTime > touchCooldown)) {
            lastTouchTime = 0;

            if (isButtonTouched(touchX, touchY, startNewGame.getBoundingRectangle())) {
                handleButtonClick(() -> {
                    preferences.putInteger("currentLevel", 1);  // Reset to level 1
                    preferences.flush();
                    game.setScreen(new SelectLevelPage(game, 1));
                });
            } else if (isButtonTouched(touchX, touchY, continuePreviousGame.getBoundingRectangle())) {
                handleButtonClick(() -> {
                    SaveGameData saveGameData = new SaveGameData();
                    SaveData savedGame = null;
                    for (int level = 1; level <= 3; level++) {
                        SaveData potentialSave = saveGameData.loadGame(level);
                        if (potentialSave != null) {
                            savedGame = potentialSave;
                            break;
                        }
                    }
                    if (savedGame != null) {
                        int savedLevel = savedGame.getCurrentLevel();
                        float savedScore = savedGame.getPlayerScore();
                        game.setCurrentLevel(savedLevel);

                        switch (savedLevel) {
                            case 1:
                                game.setScreen(new Level1(game));
                                break;
                            case 2:
                                game.setScreen(new Level2(game));
                                break;
                            case 3:
                                game.setScreen(new Level3(game));
                                break;
                            default:
                                game.setScreen(new Level1(game));
                                break;
                        }
                    } else {

                        game.setScreen(new Level1(game));
                    }

                });
            } else if (isButtonTouched(touchX, touchY, exitGame.getBoundingRectangle())) {
                Gdx.app.exit();
            }
        }
    }

    private boolean isButtonTouched(float x, float y, Rectangle buttonBounds) {
        return buttonBounds.contains(x,y);
//        float scaledWidth = button.getWidth() * button.getScaleX();
//        float scaledHeight = button.getHeight() * button.getScaleY();
//        return x >= button.getX() && x <= (button.getX() + scaledWidth) &&
//            y >= button.getY() && y <= (button.getY() + scaledHeight);
    }

    private void handleButtonClick(Runnable action) {
        if (snd) {
            icon_sound.play();  // Play sound once per tap
            snd = false;
        }
        action.run();
        snd=true;
    }

    @Override
    public void dispose() {
        game.dispose();
        batch.dispose();
        music_buff.dispose();
        icon_sound.dispose();
        bgSprite.getTexture().dispose();
        startNewGame.getTexture().dispose();
        continuePreviousGame.getTexture().dispose();
        exitGame.getTexture().dispose();
        title.getTexture().dispose();
    }

    // Other overridden methods are unchanged
    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    public Sprite getBgSprite() {
        return bgSprite;
    }

    public Sprite getStartNewGameSprite() {
        return startNewGame;
    }

    public Sprite getContinuePreviousGameSprite() {
        return continuePreviousGame;
    }

    public Sprite getExitGameSprite() {
        return exitGame;
    }

    public Sprite getTitleSprite() {
        return title;
    }

    public Rectangle getStartNewGameBounds() {
        return startNewGame.getBoundingRectangle();
    }

    public Rectangle getContinuePreviousGameBounds() {
        return continuePreviousGame.getBoundingRectangle();
    }

    public Rectangle getExitGameBounds() {
        return exitGame.getBoundingRectangle();
    }
}
