

package edu.utep.cs.cs4381.platformer;

import java.util.ArrayList;

public class LevelCity extends LevelData {

    public LevelCity() {
        tiles = new ArrayList<>();
        tiles.add("p.............................333333....5555....5555.....5555555...5..5...5....555555555555555555555.....g...g.......t..");
        tiles.add(".u.u.u.u.u.u.u.u..................................................................................55.c.c.c.c.c.c.c.....");
        tiles.add("55555555555555555.......333.......................................................................5555555555555555555555");
        tiles.add(".....................................................................................l..................................");
        tiles.add("...............................33...........................................l.................e.........................");
        tiles.add("............................................................................................333333333333333333..........");
        tiles.add("............................33.......................................l.............33333..........................333...");
        tiles.add("...............l............................................................333.........................................");
        tiles.add(".......................333..................................................................................333.........");
        tiles.add(".....................................................................333................................................");
        tiles.add("............3333333............................................l......................................333...............");
        tiles.add("3..........................................................................33...........................................");
        tiles.add("3......33...............l...........................l...........................................333.....................");
        tiles.add("3........................d...........................d......3333333333..................................................");
        tiles.add("3....3....................e.......l.........l.............................................333...........................");
        tiles.add("3.....................3333333......d.........d.....33333.........................................e.....................3");
        tiles.add("333...................3.........cccccc.......u..................................................333....................3");
        tiles.add("3.....................3.........333333.....33333......................................................333..............3");
        tiles.add("3...3.................3.....l................l...................l...............l....................................l3");
        tiles.add("3...e..cccccccccccccc.3...........................................................d..................g......333........3");
        tiles.add("3.....3cccccccccccccc....c......c......c..........c.....c.......c.......c..........c..........3.c.......c.......c......3");
        tiles.add("111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");

        // declare the values for the teleports in order of appearance
        locations = new ArrayList<Location>();
        locations.add(new Location("LevelForest", 1f, 17f));

        backgroundDataList = new ArrayList<BackgroundData>();
        // note that speeds less than 2 cause problems
        backgroundDataList.add(new BackgroundData("skyline", true, -1, 3, 18, 10, 15 ));
        backgroundDataList.add(new BackgroundData("grass", true, 1, 20, 24, 24, 4 ));
    }
}

