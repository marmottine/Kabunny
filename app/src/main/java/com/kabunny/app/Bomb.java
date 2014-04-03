package com.kabunny.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class Bomb {
    private Drawable image;
    private boolean visible = false;
    private Vector2 position;
    private int image_width;

    public Bomb(Context context) {
        image = context.getResources().getDrawable(R.drawable.bomb);
        image_width = 100;  // px
    }

    public void start(Vector2 pos) {
        visible = true;
        position = pos;
    }

    public void move(Vector2 pos) {
        visible = true;
        position = pos;
    }

    public void delete() {
        visible = false;
    }

    public void draw(Canvas canvas) {
        if (visible) {
            image.setBounds(Math.round(position.x - image_width / 2),
                    Math.round(position.y - image_width / 2),
                    Math.round(position.x + image_width / 2),
                    Math.round(position.y + image_width / 2));
            image.draw(canvas);
        }
    }
}
