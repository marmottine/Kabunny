package com.kabunny.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class Bomb {
    private Drawable image;
    private boolean visible = false;
    public Vector2 position;
    private int image_width;
    public float explosion_range;
    private Paint range_paint;

    public Bomb(Context context) {
        image = context.getResources().getDrawable(R.drawable.bomb);
        image_width = 100;  // px
        explosion_range = 300; // radius in px
        range_paint = new Paint();
        range_paint.setARGB(100, 100, 100, 100);
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
            // darken range
            canvas.drawCircle(position.x, position.y, explosion_range, range_paint);

            // draw bomb itself
            image.setBounds(Math.round(position.x - image_width / 2),
                    Math.round(position.y - image_width / 2),
                    Math.round(position.x + image_width / 2),
                    Math.round(position.y + image_width / 2));
            image.draw(canvas);
        }
    }

    public void explode() {
        // TODO
    }

}
