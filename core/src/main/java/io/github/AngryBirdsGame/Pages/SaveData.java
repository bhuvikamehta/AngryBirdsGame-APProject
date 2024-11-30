package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.utils.Array;
import io.github.AngryBirdsGame.Pages.Bird;
import io.github.AngryBirdsGame.Pages.Block;
import io.github.AngryBirdsGame.Pages.Pig;

import java.io.Serializable;

public class SaveData implements Serializable {
    private int currentLevel;
    private float playerScore;

    public SaveData() {

    }
    public SaveData(int currentLevel, float playerScore) {
        this.currentLevel = currentLevel;
        this.playerScore = playerScore;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public float getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(float playerScore) {
        this.playerScore = playerScore;
    }

    public boolean isValid() {
        return currentLevel > 0 && playerScore >= 0;
    }
}
