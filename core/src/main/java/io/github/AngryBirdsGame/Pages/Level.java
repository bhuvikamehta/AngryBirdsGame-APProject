package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.AngryBirdsGame.Pages.SaveData;
import io.github.AngryBirdsGame.Pages.SaveGameData;
import io.github.AngryBirdsGame.Pages.Bird;
import io.github.AngryBirdsGame.Pages.Pig;
import io.github.AngryBirdsGame.Pages.Block;
import io.github.AngryBirdsGame.AngryBirds;

import java.util.ArrayList;

public abstract class Level implements Screen {
    protected World world;
    protected static final float PIXELS_TO_METERS = 100f;
    protected ArrayList<GameObject> gameObjects;
    protected ArrayList<GameObject> objectsToRemove;
    protected ArrayList<Bird> remBirds;
    protected ArrayList<Pig> remPigs;
    protected ArrayList<Block> remBlocks;
    protected int currentLevel;
    protected int score;
    protected SaveGameData saveManager;
    protected SaveData currentGameState;
    private Label scoreLabel;


    public Level() {
        gameObjects = new ArrayList<>();
        objectsToRemove = new ArrayList<>();
        remBirds=new ArrayList<>();
        remPigs=new ArrayList<>();
        remBlocks=new ArrayList<>();
        saveManager = new SaveGameData();
        currentGameState = new SaveData();
    }
    public World getWorld() {
        return world;
    }

    public void removeGameObject(GameObject object) {
        if (object != null) {
            objectsToRemove.add(object);
        }
    }

    public void saveGame(int saveSlot) {
//        // Capture current game state
//        currentGameState.currentLevel = this.currentLevel;
//        currentGameState.score = this.score;
//
//        // Clear and repopulate game objects
//        currentGameState.birds.clear();
//        currentGameState.pigs.clear();
//        currentGameState.blocks.clear();
//
//        // Add alive game objects to save state
//        for (GameObject obj : gameObjects) {
//            if (obj instanceof Bird && obj.isAlive()) {
//                currentGameState.birds.add((Bird) obj);
//            }
//            if (obj instanceof Pig && obj.isAlive()) {
//                currentGameState.pigs.add((Pig) obj);
//            }
//            if (obj instanceof Block && obj.isAlive()) {
//                currentGameState.blocks.add((Block) obj);
//            }
//        }
//
//        // Save the game
//        saveManager.saveGame(currentGameState, saveSlot);
    }


    public boolean loadGame(int saveSlot) {
        SaveData loadedGameState = saveManager.loadGame(saveSlot);

        if (loadedGameState != null) {
            // Clear existing game objects
            gameObjects.clear();
            objectsToRemove.clear();

//            // Restore game state
//            this.currentLevel = loadedGameState.currentLevel;
//            this.score = loadedGameState.score;

            // Restore game objects
            restoreGameObjects(loadedGameState);

            return true;
        }

        return false;
    }

    protected abstract void restoreGameObjects(SaveData loadedGameState);


    // Update method to handle object removal
    public void update(float delta) {
        // Remove marked objects
        for (GameObject obj : objectsToRemove) {
            gameObjects.remove(obj);
        }
        objectsToRemove.clear();
    }

    public void addScore(int points) {
        score += points;
        // If you have a scoreLabel, update it here
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score);
        }
    }

    public void resetScore() {
        this.score = 0;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getScore() {
        return score;
    }

    protected ArrayList<Bird> getRemBirds(){
        for(GameObject g:gameObjects){
            if(g instanceof Bird && g.isAlive()){
                remBirds.add((Bird)g);
            }
        }
        return remBirds;
    };

    protected ArrayList<Pig> getRemPigs(){
        for(GameObject g:gameObjects){
            if(g instanceof Pig && g.isAlive()){
                remPigs.add((Pig)g);
            }
        }
        return remPigs;
    };

    protected ArrayList<Block> getRemBlocks(){
        for(GameObject g:gameObjects){
            if(g instanceof Block && g.isAlive()){
                remBlocks.add((Block)g);
            }
        }
        return remBlocks;
    };


    protected void handlePigCollision(Body pigBody) {
        Pig pig = (Pig) pigBody.getUserData();
        if (pig != null && pig.isAlive()) {
            pig.takeDamage(0.1f); // Adjust damage value as needed

            float x = pigBody.getPosition().x * PIXELS_TO_METERS;
            float y = pigBody.getPosition().y * PIXELS_TO_METERS;
            addDestructionAnimation(x, y, 0.5f);
            pig.setAlive(false);  // Note: corrected from setALive to setAlive
            world.destroyBody(pigBody);
            removeGameObject(pig);
        }
    }

    protected void handleBlockCollision(Body blockBody) {
        Block block = (Block) blockBody.getUserData();
        if (block != null && !block.isDamaged()) {
            block.takeDamage(0.1f); // Adjust damage value as needed

            float x = blockBody.getPosition().x * PIXELS_TO_METERS;
            float y = blockBody.getPosition().y * PIXELS_TO_METERS;
            addDestructionAnimation(x, y, 0.7f);
            block.setDamaged(true);  // Note: corrected from setDamage to setDamaged
            world.destroyBody(blockBody);
            removeGameObject(block);
        }
    }


    // Add common methods that all levels will need
    public abstract void addDestructionAnimation(float x, float y, float scale);
    public abstract void markForDestruction(GameObject object);

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        update(delta);
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {
        saveGame(currentLevel);
    }

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (world != null) {
            world.dispose();
        }
    }

}
