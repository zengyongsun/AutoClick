package com.zy.autoclick;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.Random;

public class RobService extends AccessibilityService {

    public static final String TAG = AccessibilityService.class.getSimpleName();
    private ClickThread clickThread;
    private Random random;
    private int timer;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                //界面点击
                Log.d(TAG, "onAccessibilityEvent: 界面点击" + event.getWindowId());
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list =
                            nodeInfo.findAccessibilityNodeInfosByViewId("com.ophone.reader.ui:id/reader_content_view");
                    Log.d(TAG, "onAccessibilityEvent: listSize" + list.size());
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                //界面文字改动
                Log.d(TAG, "onAccessibilityEvent: WINDOW_STATE_CHANGED");
                AccessibilityNodeInfo nodeInfo1 = getRootInActiveWindow();

                if (nodeInfo1 != null) {
                    if ("com.ophone.reader.ui".equals(nodeInfo1.getPackageName().toString())) {
                        List<AccessibilityNodeInfo> list =
                                nodeInfo1.findAccessibilityNodeInfosByViewId("com.ophone.reader.ui:id/reader_content_view");
                        Log.d(TAG, "onAccessibilityEvent: listSize" + list.size());
                        if (list.size() > 0) {
                            autoClick();
                        } else {
                            stopClick();
                        }
                    } else {
                        stopClick();
                    }
                }

                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                Log.d(TAG, "onAccessibilityEvent: GESTURE_START");
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                Log.d(TAG, "onAccessibilityEvent: GESTURE_END");
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                Log.d(TAG, "onAccessibilityEvent: TYPE_GESTURE_DETECTION_START");
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                Log.d(TAG, "onAccessibilityEvent: TYPE_GESTURE_DETECTION_END");
                break;

            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                Log.d(TAG, "onAccessibilityEvent: TYPE_WINDOWS_CHANGED");
                break;


        }

    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                dispatchGestureClick(760, 760);
            } else if (msg.what == 1) {
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        }
    };

    public void dispatchGestureClick(int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x - 100, y);
        boolean click = dispatchGesture(new GestureDescription
                .Builder()
                .addStroke(new GestureDescription.StrokeDescription(path,
                        0, 100)).build(), null, null);
        Log.i(TAG, "dispatchGestureClick: " + click);
    }


    private void stopClick() {
        Log.i(TAG, "dispatchGestureClick: stopClick");
        handler.removeCallbacks(clickThread);
    }

    private void autoClick() {
        Log.i(TAG, "dispatchGestureClick: startClick");
        handler.postDelayed(clickThread, FixTime * 1000 + random.nextInt(RandomTime * 1000));
    }

    class ClickThread implements Runnable {

        @Override
        public void run() {
            if (timer >= 60 * 18) {
                handler.sendEmptyMessage(1);
            } else {
                handler.sendEmptyMessage(0);
                handler.postDelayed(clickThread, FixTime * 1000 + random.nextInt(RandomTime * 1000));
            }
            timer += (FixTime + RandomTime / 2);
            Log.i(TAG, "dispatchGestureClick: " + timer);
        }
    }

    @Override
    public void onInterrupt() {

    }

    private int FixTime = 0;
    private int RandomTime = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onAccessibilityEvent: 服务创建");
        clickThread = new ClickThread();
        random = new Random();
        timer = 0;
        FixTime = Integer.parseInt((String) SPUtils.get(getBaseContext(), SPUtils.fixed_time, "30"));
        RandomTime = Integer.parseInt((String) SPUtils.get(getBaseContext(), SPUtils.random_time, "20"));
    }
}
