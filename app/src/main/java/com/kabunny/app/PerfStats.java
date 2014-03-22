package com.kabunny.app;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.SystemClock;

public class PerfStats {
    private long last_time;
    private long last_cputime;
    private int frame_count;

    private float fps;
    private float cpu;

    private Paint paint;

    public PerfStats() {
        last_time = SystemClock.elapsedRealtime();
        last_cputime = SystemClock.currentThreadTimeMillis();

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        paint.setTypeface(Typeface.MONOSPACE);
    }

    public void onDraw(Canvas canvas) {
        long now_time = SystemClock.elapsedRealtime();
        long delta_time = now_time - last_time;
        if (delta_time >= 1000) {
            long now_cputime = SystemClock.currentThreadTimeMillis();
            long delta_cputime = now_cputime - last_cputime;

            fps = 1000f * frame_count / delta_time;
            cpu = 100f * delta_cputime / delta_time;

            last_time = now_time;
            last_cputime = now_cputime;

            frame_count = 0;
        } else {
            frame_count++;
        }
        canvas.drawText(String.format("%6.02f fps %6.02f cpu", fps, cpu), 0, 20, paint);
    }
}
