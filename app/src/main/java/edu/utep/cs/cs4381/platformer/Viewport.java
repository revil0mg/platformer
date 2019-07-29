package edu.utep.cs.cs4381.platformer;

import android.graphics.Rect;

public class Viewport {

    private Vector2Point5D currentViewportWorldCenter;
    private Rect convertedRect;
    private int pixelsPerMeterX;
    private int pixelsPerMeterY;
    private int screenXResolution;
    private int screenYResolution;
    private int screenCenterX;
    private int screenCenterY;
    private int metresToShowX;
    private int metresToShowY;
    private int numClipped;

    public Viewport(int x, int y) {
        screenXResolution = x;
        screenYResolution = y;
        screenCenterX = screenXResolution / 2;
        screenCenterY = screenYResolution / 2;
        pixelsPerMeterX = screenXResolution / 32;
        pixelsPerMeterY = screenYResolution / 18;
        metresToShowX = 34;
        metresToShowY = 20;
        convertedRect = new Rect();
        currentViewportWorldCenter = new Vector2Point5D();
    }


    /**
     * Convert the location of an object currently in the visible viewport from
     * world coordinates to pixel coordinates that can actually be drawn to the screen.
     */
    public Rect worldToScreen(float x, float y, float width, float height) {
        int left = (int) (screenCenterX - (currentViewportWorldCenter.x - x) * pixelsPerMeterX);
        int top = (int) (screenCenterY - (currentViewportWorldCenter.y - y) * pixelsPerMeterY);
        int right = (int) (left + width * pixelsPerMeterX);
        int bottom = (int) (top + height * pixelsPerMeterY);
        convertedRect.set(left, top, right, bottom);
        return convertedRect;
    }


    /**  Is the given object outside the viewport. */
    public boolean clipObject(float x, float y, float width, float height) {
        boolean isInside = (x - width < currentViewportWorldCenter.x + metresToShowX / 2)
                && (x + width > currentViewportWorldCenter.x - metresToShowX / 2)
                && (y - height < currentViewportWorldCenter.y + metresToShowY / 2)
                && (y + height > currentViewportWorldCenter.y - metresToShowY / 2);
        if (!isInside) { // for debugging
            numClipped++;
        }
        return !isInside;
    }

    public int getScreenWidth() {
        return screenXResolution;
    }

    public int getScreenHeight() {
        return screenYResolution;
    }

    public int getPixelsPerMeterX() {
        return pixelsPerMeterX;
    }

    public int getPixelsPerMeterY() {
        return pixelsPerMeterY;
    }

    public void setWorldCenter(float x, float y) {
        currentViewportWorldCenter.x = x;
        currentViewportWorldCenter.y = y;
    }

    public int getNumClipped() {
        return numClipped;
    }

    public void resetNumClipped() {
        numClipped = 0;
    }

    public float getyCenter() {
        return screenCenterY;
    }

    public float getViewportWorldCenterY(){
        return currentViewportWorldCenter.y;
    }
}
