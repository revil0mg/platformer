package edu.utep.cs.cs4381.platformer;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class InputController {

    private Rect left, right, jump, shoot, pause, shieldBtn;

    public InputController(int screenWidth, int screenHeight) {
        //Configure the player buttons
        int buttonWidth = screenWidth / 8;
        int buttonHeight = screenHeight / 7;
        int buttonPadding = screenWidth / 80;

        left = new Rect(buttonPadding,
                screenHeight - buttonHeight - buttonPadding,
                buttonWidth,
                screenHeight - buttonPadding);

        right = new Rect(buttonWidth + buttonPadding,
                screenHeight - buttonHeight - buttonPadding,
                buttonWidth + buttonPadding + buttonWidth,
                screenHeight - buttonPadding);

        jump = new Rect(screenWidth - buttonWidth - buttonPadding,
                screenHeight - buttonHeight - buttonPadding - buttonHeight - buttonPadding,
                screenWidth - buttonPadding,
                screenHeight - buttonPadding - buttonHeight - buttonPadding);

        shoot = new Rect(screenWidth - buttonWidth - buttonPadding,
                screenHeight - buttonHeight - buttonPadding,
                screenWidth - buttonPadding,
                screenHeight - buttonPadding);

        pause = new Rect(screenWidth - buttonPadding - buttonWidth,
                buttonPadding,
                screenWidth - buttonPadding,
                buttonPadding + buttonHeight);

        shieldBtn = new Rect(screenWidth - (buttonWidth * 2) - buttonPadding*2,
                screenHeight - buttonHeight - buttonPadding,
                screenWidth - buttonPadding*2 - buttonWidth,
                screenHeight - buttonPadding);
    }

    public List<Rect> getButtons() {
        //create an array of buttons for the draw method
        ArrayList<Rect> currentButtonList = new ArrayList<>();
        currentButtonList.add(left);
        currentButtonList.add(right);
        currentButtonList.add(jump);
        currentButtonList.add(shoot);
        currentButtonList.add(pause);
        currentButtonList.add(shieldBtn);
        return  currentButtonList;
    }

    public void handleInput(MotionEvent motionEvent, LevelManager lm, SoundManager sm, Viewport vp) {
        int pointerCount = motionEvent.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            int x = (int) motionEvent.getX(i);
            int y = (int) motionEvent.getY(i);
            if (lm.isPlaying()) {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (right.contains(x, y)) {
                            lm.player().setPressingRight(true);
                            lm.player().setPressingLeft(false);
                        } else if (left.contains(x, y)) {
                            lm.player().setPressingLeft(true);
                            lm.player().setPressingRight(false);
                        } else if (jump.contains(x, y)) {
                            lm.player().startJump(sm);
                        } else if (shoot.contains(x, y)) {
                            if (lm.player().pullTrigger()) {
                                sm.play(SoundManager.Sound.SHOOT);
                            }
                        } else if (pause.contains(x, y)) {
                            lm.switchPlayingStatus();
                        } else if (shieldBtn.contains(x, y)) {
                            lm.player().shield.activateShield(System.currentTimeMillis());
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        if (right.contains(x, y)) {
                            lm.player().setPressingRight(false);
                        } else if (left.contains(x, y)) {
                            lm.player().setPressingLeft(false);
                        }
                        break;

                }
            } else {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        if (pause.contains(x, y)) {
                            lm.switchPlayingStatus();
                        }
                        break;
                }
            }
        }
    }

}
