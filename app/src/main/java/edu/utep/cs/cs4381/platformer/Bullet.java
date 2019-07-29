package edu.utep.cs.cs4381.platformer;

public class Bullet {
    private float x;
    private float y;
    private float xVelocity;
    private int direction;

    public Bullet(float x, float y, int speed, int direction) {
        this.direction = direction;
        this.x = x;
        this.y = y;
        this.xVelocity = speed * direction;
    }

    public void update(long fps, float gravity) {
        x += xVelocity / fps;
    }

    public void hideBullet() {
        this.x = -100;
        this.xVelocity = 0;
    }

    // getters …
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getxVelocity() {
        return xVelocity;
    }

    public float getDirection() {
        return direction;
    }
}
