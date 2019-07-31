package edu.utep.cs.cs4381.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

public class PlatformView extends SurfaceView implements Runnable {

    private boolean debugging = true;
    private volatile boolean running;
    private Thread gameThread = null;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder holder;

    private Context context;
    private long startFrameTime;
    private long timeThisFrame;
    private long fps;

    private LevelManager lm;
    private Viewport vp;
    private InputController ic;
    private SoundManager sm;
    private PlayerState ps;


    public PlatformView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.context = context;
        holder = getHolder();
        paint = new Paint();

        sm = SoundManager.instance(context);

        vp = new Viewport(screenWidth, screenHeight);
        ps = new PlayerState();
        loadLevel("LevelCave", 1, 16);

    }

    private void loadLevel(String level, float px, float py) {
        ic = new InputController(vp.getScreenWidth(), vp.getScreenHeight());
        PointF location = new PointF(px, py);
        ps.saveLocation(location);

        lm = new LevelManager(context, vp.getPixelsPerMeterX(),
                vp.getScreenWidth(), ic, level, px, py);
        vp.setWorldCenter(
                lm.gameObjects.get(lm.playerIndex).getWorldLocation().x,
                lm.gameObjects.get(lm.playerIndex).getWorldLocation().y);
        lm.player().bfg.setFireRate(ps.getFireRate());

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (lm != null) {
            ic.handleInput(motionEvent, lm, sm, vp);
        }
        /*
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lm.switchPlayingStatus();
                break;
        }
        */
        return true;
    }

    @Override
    public void run() {
        while (running) {
            startFrameTime = System.currentTimeMillis();
            update();
            draw();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void update() {
        for (GameObject go : lm.gameObjects) {
            if (go.isActive()) {
                boolean clipped = vp.clipObject(go.getWorldLocation().x, go.getWorldLocation().y, go.getWidth(), go.getHeight());
                go.setVisible(!clipped);

                if (!vp.clipObject(go.getWorldLocation().x, go.getWorldLocation().y, go.getWidth(), go.getHeight())) {
                    go.setVisible(true);

                    // check collisions with player
                    int hit = lm.player().checkCollisions(go.getHitbox());
                    //hit = go instanceof Player?0:hit;
                    if (hit > 0) {
                        switch (go.getType()) {
                            case 'c':
                                sm.play(SoundManager.Sound.COIN_PICKUP);
                                go.setActive(false);
                                go.setVisible(false);
                                ps.gotCredit();
                                if (hit != 2) { // hit not by feet
                                    lm.player().restorePreviousVelocity();
                                }
                                break;
                            case 'e':
                                go.setActive(false);
                                go.setVisible(false);
                                sm.play(SoundManager.Sound.EXTRA_LIFE);
                                ps.addLife();
                                if (hit != 2) {
                                    lm.player().restorePreviousVelocity();
                                }
                                break;
                            case 'u':
                                sm.play(SoundManager.Sound.GUN_UPGRADE);
                                go.setActive(false);
                                go.setVisible(false);
                                lm.player().bfg.upgradeRateOfFire();
                                ps.increaseFireRate();
                            case 'd':
                                PointF location;
                                //hit by drone
                                sm.play(SoundManager.Sound.BURN);
                                ps.loseLife();
                                location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                                lm.player().setWorldLocationX(location.x);
                                lm.player().setWorldLocationY(location.y);
                                lm.player().setxVelocity(0);
                                break;
                            case 'g':
                                sm.play(SoundManager.Sound.BURN);
                                ps.loseLife();
                                location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                                lm.player().setWorldLocationX(location.x);
                                lm.player().setWorldLocationY(location.y);
                                lm.player().setxVelocity(0);
                                break;
                            case 'f':
                                sm.play(SoundManager.Sound.BURN);
                                ps.loseLife();
                                location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                                lm.player().setWorldLocationX(location.x);
                                lm.player().setWorldLocationY(location.y);
                                lm.player().setxVelocity(0);
                                break;
                            case 't':
                                Teleport teleport = (Teleport) go;
                                Location t = teleport.getTarget();
                                loadLevel(t.level, t.x, t.y);
                                sm.play(SoundManager.Sound.TELEPORT);
                                break;
                            case 'o':
                                go.setActive(false);
                                go.setVisible(false);
                                sm.play(SoundManager.Sound.EXTRA_LIFE);
                                ps.addLife();
                                if (hit != 2) {
                                    lm.player().restorePreviousVelocity();
                                }
                                break;

                            default:
                                if (hit == 1) { // left or right
                                    lm.player().setxVelocity(0);
                                    lm.player().setPressingRight(false);
                                }
                                if (hit == 2) { // feet
                                    lm.player().isFalling = false;
                                }
                                break;
                        }
                    }
                    /** Bullet Hitbox Collisions**/
                    for (int i = 0; i < lm.player().bfg.getNumBullets(); i++) {
                        RectHitbox r = new RectHitbox();
                        r.setLeft(lm.player().bfg.getBulletX(i));
                        r.setTop(lm.player().bfg.getBulletY(i));
                        r.setRight(lm.player().bfg.getBulletX(i) + .1f);
                        r.setBottom(lm.player().bfg.getBulletY(i) + .1f);
                        if (go.getHitbox().intersects(r)) {
                            lm.player().bfg.hideBullet(i);
                            switch (go.getType()) {
                                case 'g': // guard
                                    go.setWorldLocationX(go.getWorldLocation().x + 2 * (lm.player().bfg.getDirection(i)));
                                    sm.play(SoundManager.Sound.HIT_GUARD);
                                    break;
                                case 'd': // drone
                                    sm.play(SoundManager.Sound.EXPLODE);
                                    go.setWorldLocation(-100, -100, 0);
                                    break;

                                default:
                                    sm.play(SoundManager.Sound.RICOCHET);
                            }
                        }
                    }

                }
                if (lm.isPlaying()) {
                    go.update(fps, lm.gravity);
                    if (go.getType() == 'd') {
                        Drone d = (Drone) go;
                        d.setWaypoint(lm.player().getWorldLocation());
                    }

                }

            } else {
                go.setVisible(false);
            }

        }

        if (lm.isPlaying()) {
            // reset the player’s location as the center of the viewport
            vp.setWorldCenter(lm.player().getWorldLocation().x, lm.player().getWorldLocation().y);

            // has player fallen out of the map?
            if (lm.player().getWorldLocation().x < 0 || lm.player().getWorldLocation().x > lm.mapWidth
                    || lm.player().getWorldLocation().y > lm.mapHeight) {
                sm.play(SoundManager.Sound.BURN);
                ps.loseLife();
                PointF location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                lm.player().setWorldLocationX(location.x);
                lm.player().setWorldLocationY(location.y);
                lm.player().setxVelocity(0);
            }
            // check if game is over
            if (ps.getLives() == 0) {
                ps = new PlayerState();
                loadLevel("LevelCave", 1, 16);
            }

        }

    }

    private void drawBackground(int start, int stop) {
        Rect fromRect1 = new Rect(), toRect1 = new Rect();
        Rect fromRect2 = new Rect(), toRect2 = new Rect();
        for (Background bg: lm.backgrounds) {
            if (bg.z < start && bg.z > stop) {

                // clip anything off-screen
                if (!vp.clipObject(-1, bg.y, 1000, bg.height)) {
                    int startY = (int) (vp.getyCenter() -  (vp.getViewportWorldCenterY() - bg.y) *
                            vp.getPixelsPerMeterY());
                    int endY = (int) (vp.getyCenter() - (vp.getViewportWorldCenterY() - bg.endY) *
                            vp.getPixelsPerMeterY());

                    // define what portion of bitmaps to capture and what coordinates to draw them at
                    fromRect1 = new Rect(0, 0, bg.width - bg.xClip,  bg.height);
                    toRect1 = new Rect(bg.xClip, startY, bg.width, endY);
                    fromRect2 = new Rect(bg.width - bg.xClip, 0, bg.width, bg.height);
                    toRect2 = new Rect(0, startY, bg.xClip, endY);
                }
                // draw backgrounds
                if (!bg.reversedFirst) {
                    canvas.drawBitmap(bg.bitmap, fromRect1, toRect1, paint);
                    canvas.drawBitmap(bg.bitmapReversed, fromRect2, toRect2, paint);
                } else {
                    canvas.drawBitmap(bg.bitmap, fromRect2, toRect2, paint);
                    canvas.drawBitmap(bg.bitmapReversed, fromRect1, toRect1, paint);
                }

                // calculate the next value for the background's clipping position by modifying xClip
                // and switching which background is drawn first, if necessary.
                bg.xClip -= lm.player().getxVelocity() / (20 / bg.speed);
                if (bg.xClip >= bg.width) {
                    bg.xClip = 0;
                    bg.reversedFirst = !bg.reversedFirst;
                } else if (bg.xClip <= 0) {
                    bg.xClip = bg.width;
                    bg.reversedFirst = !bg.reversedFirst;
                }
            }
        }
    }


    private void draw() {
        if (holder.getSurface().isValid()){
            canvas = holder.lockCanvas();
            paint.setColor(Color.argb(255, 0, 0, 255));
            canvas.drawColor(Color.argb(255, 0, 0, 255));

            drawBackground(0, -3);  // behind Bob (at z=0)

            // rect in the slides is toScreen2d
            Rect toScreen2d = new Rect();
            for (int layer = -1; layer <= 1; layer++) {
                for (GameObject go : lm.gameObjects) {
                    if (go.isVisible() && go.getWorldLocation().z == layer) {
                        toScreen2d.set(vp.worldToScreen(go.getWorldLocation().x, go.getWorldLocation().y,
                                go.getWidth(), go.getHeight()));

                        if (go.isAnimated()) {
                            if (go.getFacing() == GameObject.RIGHT) { // rotate and draw?
                                Matrix flipper = new Matrix();
                                flipper.preScale(-1, 1);
                                Rect r = go.getRectToDraw(System.currentTimeMillis());
                                Bitmap b = Bitmap.createBitmap(lm.getBitmap(go.getType()),
                                        r.left, r.top, r.width(), r.height(), flipper, true);
                                canvas.drawBitmap(b, toScreen2d.left, toScreen2d.top, paint);
                            } else {
                                canvas.drawBitmap(lm.getBitmap(go.getType()),
                                        go.getRectToDraw(System.currentTimeMillis()), toScreen2d, paint);
                            }
                        }
                        else { // no animation; just draw the whole bitmap
                            canvas.drawBitmap(lm.getBitmap(go.getType()),
                                    toScreen2d.left, toScreen2d.top, paint);
                        }
                    }
                }
            }

            drawBackground(4, 0); // parallax backgrounds from layer 1 to 3

            // draw the HUD
            int topSpace = vp.getPixelsPerMeterY() / 4;
            int iconSize = vp.getPixelsPerMeterX();
            int padding = vp.getPixelsPerMeterX() / 5;
            int centring = vp.getPixelsPerMeterY() / 6;
            paint.setTextSize(vp.getPixelsPerMeterY() / 2);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.argb(100, 0, 0, 0));
            canvas.drawRect(0,0,iconSize * 7.0f, topSpace*2 + iconSize,paint);
            paint.setColor(Color.argb(255, 255, 255, 0));
            canvas.drawBitmap(lm.getBitmap('e'), 0, topSpace, paint);
            canvas.drawText("" + ps.getLives(), (iconSize * 1) + padding, iconSize - centring, paint);
            canvas.drawBitmap(lm.getBitmap('c'), iconSize * 2.5f + padding, topSpace, paint);
            canvas.drawText("" + ps.getCredits(), (iconSize * 3.5f) + padding * 2, iconSize - centring, paint);
            canvas.drawBitmap(lm.getBitmap('u'), iconSize * 5.0f + padding, topSpace, paint);
            canvas.drawText("" + ps.getFireRate(), iconSize * 6.0f + padding * 2, iconSize - centring, paint);

            // draw buttons
            paint.setColor(Color.argb(80, 255, 255, 255));
            List<Rect> buttonsToDraw = ic.getButtons();
            for (Rect r: buttonsToDraw) {
                RectF rf = new RectF(r.left, r.top, r.right, r.bottom);
                canvas.drawRoundRect(rf, 15f, 15f, paint);
            }

//           Text for debugging
            if (debugging) {
                paint.setTextSize(24);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                canvas.drawText("fps:" + fps, 10, 60, paint);
                canvas.drawText("num objects:" + lm.gameObjects.size(), 10, 80, paint);
                canvas.drawText("num clipped:" + vp.getNumClipped(), 10, 100, paint);
                canvas.drawText("playerX:" + lm.gameObjects.get(lm.playerIndex).getWorldLocation().x, 10, 120, paint);
                canvas.drawText("playerY:" + lm.gameObjects.get(lm.playerIndex).getWorldLocation().y, 10, 140, paint);

                canvas.drawText("Gravity:" + lm.gravity, 10, 160, paint);
                canvas.drawText("X velocity:" + lm.gameObjects.get(lm.playerIndex).getxVelocity(), 10, 180, paint);
                canvas.drawText("Y velocity:" + lm.gameObjects.get(lm.playerIndex).getyVelocity(), 10, 200, paint);

                //for reset the number of clipped objects each frame
                vp.resetNumClipped();
            }

                // draw paused text
            if (!lm.isPlaying()) {
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(120);
                canvas.drawText("Paused", vp.getScreenWidth() / 2,
                        vp.getScreenHeight() / 2, paint);
            }

            paint.setColor(Color.argb(255, 255, 255, 255));
            for (int i = 0; i < lm.player().getMachineGun().getNumBullets(); i++) {
                toScreen2d.set(vp.worldToScreen(lm.player().bfg.getBulletX(i), lm.player().bfg.getBulletY(i),
                        .25f, .05f)); // bullet width and height
                canvas.drawRect(toScreen2d, paint);
            }


            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("error", "failed to pause thread");
        }
    }

    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}