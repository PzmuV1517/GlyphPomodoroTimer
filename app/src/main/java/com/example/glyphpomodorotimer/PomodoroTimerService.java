package com.example.glyphpomodorotimer;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;

import com.nothing.ketchum.GlyphMatrixManager;
import com.nothing.ketchum.GlyphMatrixFrame;
import com.nothing.ketchum.GlyphMatrixObject;
import com.nothing.ketchum.GlyphToy;
import com.nothing.ketchum.Glyph;

public class PomodoroTimerService extends Service {
    private static final int WORK_DURATION = 25 * 60; // 25 minutes in seconds
    private static final int BREAK_DURATION = 5 * 60; // 5 minutes in seconds

    private GlyphMatrixManager mGM;
    private Handler mHandler;
    private Runnable mTimerRunnable;

    private int mCurrentTimeSeconds;
    private boolean mIsWorkSession = true; // true for work, false for break
    private boolean mIsRunning = false;
    private boolean mTimerStarted = false;

    @Override
    public IBinder onBind(Intent intent) {
        init();
        return serviceMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        cleanup();
        return false;
    }

    private void init() {
        mGM = GlyphMatrixManager.getInstance(getApplicationContext());
        mGM.init(new GlyphMatrixManager.Callback() {
            @Override
            public void onServiceConnected(ComponentName name) {
                if (mGM != null) {
                    mGM.register(Glyph.DEVICE_23112);
                    displayWaitingState();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // Handle service disconnection
            }
        });

        mHandler = new Handler(Looper.getMainLooper());
        setupTimerRunnable();
    }

    private void cleanup() {
        if (mHandler != null && mTimerRunnable != null) {
            mHandler.removeCallbacks(mTimerRunnable);
        }
        if (mGM != null) {
            mGM.unInit();
            mGM = null;
        }
    }

    private void setupTimerRunnable() {
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                if (mIsRunning && mCurrentTimeSeconds > 0) {
                    mCurrentTimeSeconds--;
                    updateDisplay();
                    mHandler.postDelayed(this, 1000);
                } else if (mIsRunning && mCurrentTimeSeconds <= 0) {
                    // Timer finished, switch to next phase
                    switchToNextPhase();
                }
            }
        };
    }

    private void startTimer() {
        if (!mTimerStarted) {
            // First time starting - begin with work session
            mIsWorkSession = true;
            mCurrentTimeSeconds = WORK_DURATION;
            mTimerStarted = true;
        }

        mIsRunning = true;
        mHandler.post(mTimerRunnable);
        updateDisplay();
    }

    private void switchToNextPhase() {
        mIsWorkSession = !mIsWorkSession;
        mCurrentTimeSeconds = mIsWorkSession ? WORK_DURATION : BREAK_DURATION;
        mIsRunning = true;
        updateDisplay();
        mHandler.postDelayed(mTimerRunnable, 1000);
    }

    private void skipToNext() {
        if (mHandler != null && mTimerRunnable != null) {
            mHandler.removeCallbacks(mTimerRunnable);
        }
        switchToNextPhase();
    }

    private void displayWaitingState() {
        try {
            Bitmap waitingBitmap = createTextBitmap("LONG PRESS\nTO START", Color.WHITE);

            GlyphMatrixObject.Builder objectBuilder = new GlyphMatrixObject.Builder();
            GlyphMatrixObject waitingObject = objectBuilder
                .setImageSource(waitingBitmap)
                .setPosition(0, 0)
                .setBrightness(200)
                .setScale(100)
                .build();

            GlyphMatrixFrame.Builder frameBuilder = new GlyphMatrixFrame.Builder();
            GlyphMatrixFrame frame = frameBuilder.addTop(waitingObject).build(getApplicationContext());

            if (mGM != null) {
                mGM.setMatrixFrame(frame.render());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDisplay() {
        try {
            int minutes = mCurrentTimeSeconds / 60;
            int seconds = mCurrentTimeSeconds % 60;
            String timeText = String.format("%02d:%02d", minutes, seconds);
            String phaseText = mIsWorkSession ? "WORK" : "BREAK";

            // Create timer display with clear numbers
            Bitmap timerBitmap = createTimerBitmap(timeText, phaseText, mIsWorkSession);

            GlyphMatrixObject.Builder objectBuilder = new GlyphMatrixObject.Builder();
            GlyphMatrixObject timerObject = objectBuilder
                .setImageSource(timerBitmap)
                .setPosition(0, 0)
                .setBrightness(255) // Full brightness for clear visibility
                .setScale(100)
                .build();

            GlyphMatrixFrame.Builder frameBuilder = new GlyphMatrixFrame.Builder();
            GlyphMatrixFrame frame = frameBuilder.addTop(timerObject).build(getApplicationContext());

            if (mGM != null) {
                mGM.setMatrixFrame(frame.render());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap createTextBitmap(String text, int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(5); // Smaller text for circular display
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        // Create 25x25 bitmap for circular Glyph Matrix
        Bitmap bitmap = Bitmap.createBitmap(25, 25, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);

        String[] lines = text.split("\n");
        float startY = 12.5f - (lines.length * 3); // Center vertically
        for (int i = 0; i < lines.length; i++) {
            canvas.drawText(lines[i], 12.5f, startY + (i * 6), paint);
        }

        return bitmap;
    }

    private Bitmap createTimerBitmap(String timeText, String phaseText, boolean isWorkSession) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        // Create 25x25 bitmap optimized for circular display
        Bitmap bitmap = Bitmap.createBitmap(25, 25, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);

        // Phase indicator at top (within circle bounds)
        paint.setTextSize(3);
        canvas.drawText(phaseText, 12.5f, 5, paint);

        // Main timer numbers in center (largest readable size for circle)
        paint.setTextSize(6);
        canvas.drawText(timeText, 12.5f, 14, paint);

        // Simple dot indicator at bottom for work/break
        paint.setTextSize(8);
        String indicator = isWorkSession ? "●" : "○"; // Single dot indicator
        canvas.drawText(indicator, 12.5f, 22, paint);

        return bitmap;
    }

    private final Handler serviceHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GlyphToy.MSG_GLYPH_TOY: {
                    Bundle bundle = msg.getData();
                    String event = bundle.getString(GlyphToy.MSG_GLYPH_TOY_DATA);

                    if (GlyphToy.EVENT_CHANGE.equals(event)) {
                        // Long press event
                        if (!mTimerStarted || !mIsRunning) {
                            // Start the timer
                            startTimer();
                        } else {
                            // Skip to next phase
                            skipToNext();
                        }
                    }
                    break;
                }
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private final Messenger serviceMessenger = new Messenger(serviceHandler);
}
