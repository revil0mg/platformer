package edu.utep.cs.cs4381.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

//TODO: Teleport to next level on command (put t close to bob)
//TODO: Make shield interact with enemy drone. Add graphic if time allows
//TODO: Fix "Earthquake" (Spawning 2 Bobs) or 2 Bob hitboxes. 2 platformViews created
public class LevelManager {
    private String level;
    protected int mapWidth;
    protected int mapHeight;

    private Player player;
    protected int playerIndex;
    private boolean playing;
    protected float gravity;
    private LevelData levelData;

    protected List<GameObject> gameObjects;
    ArrayList<Background> backgrounds;
    private List<Rect> currentButtons;
    protected Bitmap[] bitmapsArray;

    public LevelManager(Context context, int pixelsPerMeter, int screenWidth,
                        InputController ic, String level, float px, float py) {
        Log.e("+==================+", "Lvl Mngr Created");
        this.level = level;
        switch (level) {
            case "LevelCave":
                levelData = new LevelCave();
                break;
            case "LevelCity":
                levelData = new LevelCity();
                break;
            case "LevelForest":
                levelData = new LevelForest();
                break;
            case "LevelMountain":
                levelData = new LevelMountain();
                break;
        }
        gameObjects = new ArrayList<>();
        bitmapsArray = new Bitmap[25];
        loadMapData(context, pixelsPerMeter, px, py);
        //playing = true;
        loadBackgrounds(context, pixelsPerMeter, screenWidth);

        setWaypoints();
    }

    private void loadBackgrounds(Context context, int pixelsPerMeter, int screenWidth) {
        backgrounds = new ArrayList<>();
        for (BackgroundData bgData : levelData.backgroundDataList) {
            backgrounds.add(new Background(context, pixelsPerMeter, screenWidth, bgData));
        }
    }


    public void switchPlayingStatus() {
        playing = !playing;
        if (playing) {
            gravity = 6;
        } else {
            gravity = 0;
        }
    }

    public int getBitmapIndex(char blockType) {
        int index = 0;
        switch (blockType) {
            case '.':
                index = 0;
                break;
            case '1':
                index = 1;
                break;
            case 'p':
                index = 2;
                break;
            case 'c':
                index = 3;
                break;
            case 'u':
                index = 4;
                break;
            case 'e':
                index = 5;
                break;
            case 'd':
                index = 6;
                break;
            case 'g':
                index = 7;
                break;
            case 'f':
                index = 8;
                break;
            case '2':
                index = 9;
                break;
            case '3':
                index = 10;
                break;
            case '4':
                index = 11;
                break;
            case '5':
                index = 12;
                break;
            case '6':
                index = 13;
                break;
            case '7':
                index = 14;
                break;
            case 'w':
                index = 15;
                break;
            case 'x':
                index = 16;
                break;
            case 'l':
                index = 17;
                break;
            case 'r':
                index = 18;
                break;
            case 's':
                index = 19;
                break;
            case 'm':
                index = 20;
                break;
            case 'z':
                index = 21;
                break;
            case 't':
                index = 22;
                break;
            case 'o':
                index = 23;
        }
        return index;
    }

    public Bitmap getBitmap(char blockType) {
        return bitmapsArray[getBitmapIndex(blockType)];
    }

    private void loadMapData(Context context, int pixelsPerMeter, float px, float py) {
        int currentIndex = -1;
        int teleportIndex = -1;
        mapHeight = levelData.tiles.size();
        mapWidth = levelData.tiles.get(0).length();
        for (int i = 0; i < levelData.tiles.size(); i++) {
            for (int j = 0; j < levelData.tiles.get(i).length(); j++) {
                char c = levelData.tiles.get(i).charAt(j);
                if (c != '.') {
                    currentIndex++;
                    switch (c) {
                        case '1':
                            gameObjects.add(new Grass(j, i, c));
                            break;
                        case 'p':
                            Log.e("+==================+", "Player Created");
                            player = new Player(context, px, py, pixelsPerMeter);
                            gameObjects.add(player);
                            playerIndex = currentIndex;
                            break;
                        case 'c':
                            gameObjects.add(new Coin(j, i, c));
                            break;
                        case 'u':
                            gameObjects.add(new MachineGunUpgrade(j, i, c));
                            break;
                        case 'e':
                            gameObjects.add(new ExtraLife(j, i, c));
                            break;
                        case 'd':
                            gameObjects.add(new Drone(j, i, c));
                            break;
                        case 'g':
                            gameObjects.add(new Guard(context, j, i, c, pixelsPerMeter));
                            break;
                        case 'f':
                            gameObjects.add(new Fire (context, j, i, c, pixelsPerMeter));
                            break;
                        case '2':
                            gameObjects.add(new Snow(j, i, c));
                            break;
                        case '3':
                            gameObjects.add(new Brick(j, i, c));
                            break;
                        case '4':
                            gameObjects.add(new Coal(j, i, c));
                            break;
                        case '5':
                            gameObjects.add(new Concrete(j, i, c));
                            break;
                        case '6':
                            gameObjects.add(new Scorched(j, i, c));
                            break;
                        case '7':
                            gameObjects.add(new Stone(j, i, c));
                            break;
                        case 'w':
                            gameObjects.add(new Tree(j, i, c));
                            break;
                        case 'x':
                            gameObjects.add(new Tree2(j, i, c));
                            break;
                        case 'l':
                            gameObjects.add(new Lampost(j, i, c));
                            break;
                        case 'r':
                            gameObjects.add(new Stalactite(j, i, c));
                            break;
                        case 's':
                            gameObjects.add(new Stalagmite(j, i, c));
                            break;
                        case 'm':
                            gameObjects.add(new Cart(j, i, c));
                            break;
                        case 'z':
                            gameObjects.add(new Boulders(j, i, c));
                            break;
                        case 't':
                            teleportIndex++;
                            gameObjects.add(new Teleport(j, i, c, levelData.locations.get(teleportIndex)));
                            break;
                        case 'o':
                            gameObjects.add(new ShieldUpgrade(j, i, c));
                            break;
                    }
                    if (bitmapsArray[getBitmapIndex(c)] == null) {
                        GameObject go = gameObjects.get(currentIndex);
                        bitmapsArray[getBitmapIndex(c)] = go.prepareBitmap(context,
                                go.getBitmapName(), pixelsPerMeter);
                    }
                }
            }
        }
    }

    public void setWaypoints() {
        for (GameObject go: gameObjects) {
            /** Check if GameObject is a Guard **/
            if (go.getType() == 'g') {
                int startTileIndex = -1,  startGuardIndex = 0;
                float waypointX1 = -1, waypointX2 = -1;
                for (GameObject tile: gameObjects) {
                    startTileIndex++;
                    if (tile.getWorldLocation().y == go.getWorldLocation().y + 2 &&
                            tile.getWorldLocation().x == go.getWorldLocation().x) {
                        for (int i = 0; i < 5; i++) {
                            if (!gameObjects.get(startTileIndex - i).isTraversable()) {
                                waypointX1 = gameObjects.get(startTileIndex - (i + 1)).getWorldLocation().x;
                                break;
                            } else {
                                waypointX1 = gameObjects.get(startTileIndex - 5).getWorldLocation().x;
                            }
                        }
                        for (int i = 0; i < 5; i++) {
                            if (!gameObjects.get(startTileIndex + i).isTraversable()) {
                                waypointX2 = gameObjects.get(startTileIndex + (i - 1)).getWorldLocation().x;
                                break;
                            } else {
                                waypointX2 = gameObjects.get(startTileIndex + 5).getWorldLocation().x;
                            }
                        }
                        ((Guard) go).setWaypoints(waypointX1, waypointX2);
                    } } } } }


    public boolean isPlaying() {
        return playing;
    }

    protected Player player() {
        return player;
    }
}
