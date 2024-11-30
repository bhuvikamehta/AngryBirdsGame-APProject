package io.github.AngryBirdsGame.Pages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import java.io.Serializable;

public class SaveGameData implements Serializable {
    private static final String SAVE_FILE_1 = "level1.json";
    private static final String SAVE_FILE_2 = "level2.json";
    private static final String SAVE_FILE_3 = "level3.json";
    private Json json;

    public SaveGameData() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
    }

    public void saveGame(SaveData gameState, int saveSlot) {
        try {
            String filename = getSaveFileName(saveSlot);
            FileHandle file = Gdx.files.local(filename);
            file.writeString(json.toJson(gameState), false);
            Gdx.app.log("SaveGame", "Game saved successfully to slot " + saveSlot);
        } catch (Exception e) {
            Gdx.app.error("SaveGame", "Failed to save game", e);
        }
    }

    public SaveData loadGame(int saveSlot) {
        try {
            String filename = getSaveFileName(saveSlot);
            FileHandle file = Gdx.files.local(filename);

            if (file.exists()) {
                SaveData loadedData = json.fromJson(SaveData.class, file.readString());
                Gdx.app.log("LoadGame", "Game loaded successfully from slot " + saveSlot);
                return loadedData;
            } else {
                Gdx.app.log("LoadGame", "No save data found in slot " + saveSlot);
                return null;
            }
        } catch (Exception e) {
            Gdx.app.error("LoadGame", "Failed to load game", e);
            return null;
        }
    }



    private String getSaveFileName(int currLevel) {
        switch (currLevel) {
            case 1: return SAVE_FILE_1;
            case 2: return SAVE_FILE_2;
            case 3: return SAVE_FILE_3;
            default: throw new IllegalArgumentException("Invalid save slot");
        }
    }


}
