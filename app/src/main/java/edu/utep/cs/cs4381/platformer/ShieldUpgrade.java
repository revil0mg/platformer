package edu.utep.cs.cs4381.platformer;

public class ShieldUpgrade extends GameObject {

    public ShieldUpgrade(float worldStartX, float worldStartY, char type) {
        final float HEIGHT = .8f;
        final float WIDTH = .65f;
        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);
        setBitmapName("shield");
        setWorldLocation(worldStartX, worldStartY, 0);
        setRectHitbox();
    }

    public void update(long fps, float gravity) {}
}
