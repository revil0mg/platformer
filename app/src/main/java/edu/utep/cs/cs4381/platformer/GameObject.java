package edu.utep.cs.cs4381.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

public abstract class GameObject {

    private Vector2Point5D worldLocation;
    private float width;
    private float height;

    private boolean active = true;
    private boolean visible = true;
    private int animFrameCount = 1;
    private char type;

    // Most objects only have 1 frame
    // And don't need to bother with these
    private Animation anim = null;
    private boolean animated = false;
    private int animFps = 1;

    private float xVelocity;
    private float yVelocity;
    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    private int facing;
    private boolean moves = false;
    private boolean traversable = false;

    private String bitmapName;
    private RectHitbox rectHitbox = new RectHitbox();

    public Bitmap prepareBitmap(Context context, String bitmapName, int pixelsPerMeter) {
        int resId = context.getResources().getIdentifier(bitmapName,
                "drawable", context.getPackageName());

        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resId);

        bitmap = Bitmap.createScaledBitmap(bitmap,
                (int) (width * animFrameCount * pixelsPerMeter),
                (int) (height * pixelsPerMeter),
                false);

        return bitmap;
    }

    public abstract void update(long fps, float gravity);

    /** To be called by update(). */
    public void move(long fps) {
        if (xVelocity != 0) {
            this.worldLocation.x += xVelocity / fps;
        }
        if (yVelocity != 0) {
            this.worldLocation.y += yVelocity / fps;
        }
    }


    public String getBitmapName() {
        return bitmapName;
    }

    public Vector2Point5D getWorldLocation() {
        return worldLocation;
    }

    public void setWorldLocation(float x, float y, int z) {
        this.worldLocation = new Vector2Point5D();
        worldLocation.x = x;
        worldLocation.y = y;
        worldLocation.z = z;
    }

    public void setRectHitbox() {
        rectHitbox.setTop(worldLocation.y);
        rectHitbox.setLeft(worldLocation.x);
        rectHitbox.setBottom(worldLocation.y + height);
        rectHitbox.setRight(worldLocation.x + width);
    }


    public boolean isActive() {
        return active;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setVisible(boolean b) {
       visible = b;
    }

    public boolean isVisible() {
        return visible;
    }

    protected void setBitmapName(String player) {
        this.bitmapName = player;
    }

    protected void setType(char p) {
        this.type = p;
    }

    protected void setWidth(float width) {
        this.width = width;
    }

    protected void setHeight(float height) {
        this.height = height;
    }

    public char getType() {
        return type;
    }

    protected void setFacing(int direction) {
        facing = direction;
    }

    public void setWorldLocationY(float y) {
        this.worldLocation.y = y;
    }

    public void setWorldLocationX(float x) {
        this.worldLocation.x = x;
    }

    protected float getxVelocity() {
        return xVelocity;
    }

    protected void setxVelocity(float maxXVelocity) {
        xVelocity = maxXVelocity;
    }

    protected void setyVelocity(float yv) {
        yVelocity = yv;
    }

    public RectHitbox getHitbox() {
        return rectHitbox;
    }

    public boolean isAnimated() {
        return animated;
    }

    public void setAnimated(Context context, int pixelsPerMeter, boolean animated){
        this.animated = animated;
        this.anim = new Animation(context, bitmapName,
                height,
                width,
                animFps,
                animFrameCount,
                pixelsPerMeter );
    }

    public int getFacing() {
        return facing;
    }

    public boolean isMoves() {
        return moves;
    }

    public void setMoves(boolean moves) {
        this.moves = moves;
    }

    public Rect getRectToDraw(long currentTimeMillis) {
        return anim.getCurrentFrame(currentTimeMillis, xVelocity, isMoves());
    }

    public void setAnimFps(int animFps) {
        this.animFps = animFps;
    }

    public void setAnimFrameCount(int animFrameCount) {
        this.animFrameCount = animFrameCount;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setTraversable(){
        traversable = true;
    }

    public boolean isTraversable(){
        return traversable;
    }

    public float getyVelocity() {
        return yVelocity;
    }
}
