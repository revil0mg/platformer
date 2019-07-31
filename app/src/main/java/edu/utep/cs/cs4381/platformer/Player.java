package edu.utep.cs.cs4381.platformer;

import android.content.Context;
import android.util.Log;

public class Player extends GameObject {

    private static final float MAX_X_VELOCITY = 10;

    private boolean isPressingRight = false;
    private boolean isPressingLeft = false;

    public boolean isFalling;
    private boolean isJumping;
    private long jumpTime;
    private long maxJumpTime = 700; // jump 7 10ths of second

    private RectHitbox rectHitboxHead;
    private RectHitbox rectHitboxFeet;
    private RectHitbox rectHitboxLeft;
    private RectHitbox rectHitboxRight;

    protected MachineGun bfg;

    public Player(Context context, float worldStartX, float worldStartY, int pixelsPerMetre) {
        final float HEIGHT = 2;
        final float WIDTH = 1;

        setHeight(HEIGHT); // 2 meters tall
        setWidth(WIDTH); // 1 meter wide
        setType('p');
        setBitmapName("player");
        setWorldLocation(worldStartX, worldStartY, 0);

        // Standing still to start with
        setxVelocity(0);
        setyVelocity(0);
        setFacing(LEFT);
        isFalling = false;

        setMoves(true);
        setActive(true);
        setVisible(true);

        bfg = new MachineGun();
        
        // Set this object up to be animated
        final int ANIMATION_FPS = 16;
        final int ANIMATION_FRAME_COUNT = 5;

        setAnimFps(ANIMATION_FPS);
        setAnimFrameCount(ANIMATION_FRAME_COUNT);
        setAnimated(context, pixelsPerMetre, true);

        rectHitboxFeet = new RectHitbox();
        rectHitboxHead = new RectHitbox();
        rectHitboxLeft = new RectHitbox();
        rectHitboxRight = new RectHitbox();

    }

    public void update(long fps, float gravity) {
        if (isPressingRight) {
            setxVelocity(MAX_X_VELOCITY);
        } else if (isPressingLeft) {
            setxVelocity(-MAX_X_VELOCITY);
        } else {
            setxVelocity(0);
        }

        // which way is player facing?
        if (getxVelocity() > 0) {
            setFacing(RIGHT);
        } else if (getxVelocity() < 0) {
            setFacing(LEFT);
        }

        if (isJumping) {
            long timeJumping = System.currentTimeMillis() - jumpTime;
            if (timeJumping < maxJumpTime) {
                if (timeJumping < maxJumpTime / 2) {
                    setyVelocity(-gravity); // on the way up
                } else if (timeJumping > maxJumpTime / 2) {
                    setyVelocity(gravity); // going down
                }
            } else {
                isJumping = false;
            }
        } else {
            setyVelocity(gravity);
            isFalling = true;
        }

        bfg.update(fps, gravity);
        this.move(fps);

        Vector2Point5D location = getWorldLocation();
        float lx = location.x;
        float ly = location.y;
        rectHitboxFeet.top = ly + getHeight() * .95f;
        rectHitboxFeet.left = lx + getWidth() * .2f;
        rectHitboxFeet.bottom = ly + getHeight() * .98f;
        rectHitboxFeet.right = lx + getWidth() * .8f;

        rectHitboxHead.top = ly;
        rectHitboxHead.left = lx + getWidth() * .4f;
        rectHitboxHead.bottom = ly + getHeight() * .2f;
        rectHitboxHead.right = lx + getWidth() * .6f;

        rectHitboxLeft.top = ly + getHeight() * .2f;
        rectHitboxLeft.left = lx + getWidth() * .2f;
        rectHitboxLeft.bottom = ly + getHeight() * .8f;
        rectHitboxLeft.right = lx + getWidth() * .3f;

        rectHitboxRight.top = ly + getHeight() * .2f;
        rectHitboxRight.left = lx + getWidth() * .8f;
        rectHitboxRight.bottom = ly + getHeight() * .8f;
        rectHitboxRight.right = lx + getWidth() * .7f;

    }

    public int checkCollisions(RectHitbox rectHitbox) {
        int collided = 0; // no collision
        if (this.rectHitboxLeft.intersects(rectHitbox)) {
            // move player just to right of current hitbox
            this.setWorldLocationX(rectHitbox.right - getWidth() * .2f);
            collided = 1;
        }
        if (this.rectHitboxRight.intersects(rectHitbox)) {
            // move player just to left of current hitbox
            this.setWorldLocationX(rectHitbox.left - getWidth() * .8f);
            collided = 1;
        }
        if (this.rectHitboxFeet.intersects(rectHitbox)) {
            // move feet to just above current hitbox
            this.setWorldLocationY(rectHitbox.top - getHeight());
            collided = 2;
        }
        if (this.rectHitboxHead.intersects(rectHitbox)) {
            // move head to just below current hitbox bottom
            this.setWorldLocationY(rectHitbox.bottom);
            collided = 3;
        }


        return collided;
    }

    public void setPressingRight(boolean b){
        isPressingRight = b;
    }

    public  void setPressingLeft(boolean b){
        isPressingLeft = b;
    }

    public void startJump(SoundManager sm) {
        if (!isFalling && // can't jump if falling
                !isJumping) { // not already jumping?
            isJumping = true;
            jumpTime = System.currentTimeMillis();
            sm.play(SoundManager.Sound.JUMP);
        }
    }


    public boolean pullTrigger() {
        return bfg.shoot(getWorldLocation().x, getWorldLocation().y,
                getFacing(), getHeight());
    }

    public MachineGun getMachineGun() {
        return bfg;
    }

    public void restorePreviousVelocity() {
        if (!isJumping && !isFalling) {
            if (getFacing() == LEFT) {
                isPressingLeft = true;
                setxVelocity(-MAX_X_VELOCITY);
            } else {
                isPressingRight = true;
                setxVelocity(MAX_X_VELOCITY);
            }
        }
    }

}
