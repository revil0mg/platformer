package edu.utep.cs.cs4381.platformer;

import android.util.Log;

public class Shield {

    private int duration;
    private int shieldAmount;
    private boolean shieldIsActive;

    public Shield() {
        shieldAmount = 1;
        duration = 5000; // 5 seconds
    }

    public void upgradeShield() {
        shieldAmount++;
    }

    public void activateShield(long timeActivated) {
        shieldIsActive = true;

        if (System.currentTimeMillis() - timeActivated > duration) {
            Log.e("==============++", "Shield Activated");
            deactivateShield();
        }
    }

    public void deactivateShield() {
        shieldIsActive = false;
    }
}
