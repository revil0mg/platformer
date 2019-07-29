package edu.utep.cs.cs4381.platformer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MachineGun extends GameObject {
    private int maxBullets = 10;
    private int numBullets;
    private int nextBullet;
    private int rateOfFire = 1; // bullets per second
    private long lastShotTime;
    private List<Bullet> bullets;
    private int speed = 25;

    public MachineGun() {
        bullets = new CopyOnWriteArrayList<>();
        lastShotTime = -1;
        nextBullet = -1;
    }

    public void update(long fps, float gravity) {
        for (Bullet bullet : bullets) {
            bullet.update(fps, gravity);
        }
    }

    public float getBulletX(int index) {
        if (index >= 0 && index < numBullets) {
            return bullets.get(index).getX();
        }
        return -1f;
    }

    public float getBulletY(int index) {
        if (index >= 0 && index < numBullets) {
            return bullets.get(index).getY();
        }
        return -1f;
    }

    public void hideBullet(int index) {
        bullets.get(index).hideBullet();
    }

    public int getDirection(int index) {
        //return bullets.get(index).getDirection();
        return 1;
    }

    public void upgradeRateOfFire() {
        rateOfFire += 2;
    }

    public boolean shoot(float ownerX, float ownerY,
                         int ownerFacing, float ownerHeight) {
        boolean shotFired = false;
        if (System.currentTimeMillis() - lastShotTime >
                1000/rateOfFire) {
            // spawn another bullet;
            nextBullet++;
            if (numBullets >= maxBullets) {
                numBullets = maxBullets;
            }
            if (nextBullet >= maxBullets) {
                nextBullet = 0;
            }
            lastShotTime = System.currentTimeMillis();
            bullets.add(nextBullet,
                    new Bullet(ownerX, (ownerY + ownerHeight/3),
                            speed, ownerFacing));
            shotFired = true;
            numBullets++;
        }
        return shotFired;
    }

    public int getNumBullets() {
        return numBullets;
    }

    public void setFireRate(int fireRate) {
        rateOfFire = fireRate;
    }
}

