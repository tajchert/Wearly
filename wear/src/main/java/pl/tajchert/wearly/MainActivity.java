package pl.tajchert.wearly;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.tajchert.wearly.timelytextview.TimelyView;
import pl.tajchert.wearly.timelytextview.TimelyViewSmall;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private final static IntentFilter intentFilter;
    static {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }
    private final static int DURATION = 2000;//in miliseconds
    private final static int DURATION_SECONDS = 300;//in miliseconds

    private Handler mHandler = new Handler();
    private boolean isActive = true;
    private Runnable runner;

    private TimelyView mTextViewOne;
    private TimelyView mTextViewTwo;
    private TimelyView mTextViewThree;
    private TimelyView mTextViewFour;
    private TimelyViewSmall mTextViewFive;
    private TimelyViewSmall mTextViewSix;

    private Calendar calendar;

    private int prevHoursOne = 0;
    private int prevHoursTwo = 0;
    private int prevMinOne = 0;
    private int prevMinTwo = 0;
    private int prevSecOne = 0;
    private int prevSecTwo = 0;

    private AlphaAnimation fadeOut;
    private AlphaAnimation fadeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViewStuff();

        timeInfoReceiver.onReceive(this, registerReceiver(null, intentFilter));
        registerReceiver(timeInfoReceiver, intentFilter);

        fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);

        fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);

        runner = new Runnable() {
            @Override
            public void run() {
                setTimeSeconds();
            }
        };

        /*final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);//TODO use it
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

            }
        });*/
    }

    private void setViewStuff() {
        mTextViewOne = (TimelyView) findViewById(R.id.textViewTimelyOne);
        mTextViewTwo = (TimelyView) findViewById(R.id.textViewTimelyTwo);
        mTextViewThree = (TimelyView) findViewById(R.id.textViewTimelyThree);
        mTextViewFour = (TimelyView) findViewById(R.id.textViewTimelyFour);
        mTextViewFive = (TimelyViewSmall) findViewById(R.id.textViewTimelyFive);
        mTextViewSix = (TimelyViewSmall) findViewById(R.id.textViewTimelySix);
    }

    public BroadcastReceiver timeInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            calendar = Calendar.getInstance();
            if(calendar.get(Calendar.HOUR_OF_DAY) != Integer.parseInt(prevHoursOne + "" + prevHoursTwo)) {
                setTimeHour();
            }
            if(calendar.get(Calendar.MINUTE) != Integer.parseInt(prevMinOne + "" + prevMinTwo)) {
                setTimeMinutes();
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timeInfoReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        calendar = Calendar.getInstance();
        isActive = true;
        if(mTextViewFive != null && mTextViewSix != null) {
            if(fadeIn != null) {
                mTextViewSix.startAnimation(fadeIn);
                mTextViewFive.startAnimation(fadeIn);
            }
            mTextViewFive.setVisibility(View.VISIBLE);
            mTextViewSix.setVisibility(View.VISIBLE);
            startSecondUpdate();
        }
    }

    @Override
    protected void onPause() {
        isActive = false;
        mHandler.removeCallbacks(runner);
        if(mTextViewFive != null && mTextViewSix != null) {
            if(fadeOut != null) {
                mTextViewSix.startAnimation(fadeOut);
                mTextViewFive.startAnimation(fadeOut);
            }
            mTextViewFive.setVisibility(View.INVISIBLE);
            mTextViewSix.setVisibility(View.INVISIBLE);
        }
        super.onPause();
    }

    private void startSecondUpdate(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isActive) {
                    try {
                        Thread.sleep(1000);
                        mHandler.post(runner);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();
    }

    private void setTimeHour(){
        if (mTextViewOne != null && mTextViewTwo != null) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if(hour >= 10) {
                List<Integer> digits = digits(hour);
                mTextViewOne.animate(prevHoursOne, digits.get(1)).setDuration(DURATION).start();
                mTextViewTwo.animate(prevHoursTwo, digits.get(0)).setDuration(DURATION).start();
                prevHoursOne = digits.get(1);
                prevHoursTwo = digits.get(0);
            } else {
                mTextViewOne.animate(prevHoursOne, 0).setDuration(DURATION).start();
                mTextViewTwo.animate(prevHoursTwo, hour).setDuration(DURATION).start();
                prevHoursOne = 0;
                prevHoursTwo = hour;
            }
        }
    }
    private void setTimeMinutes(){
        if (mTextViewThree != null && mTextViewFour != null) {
            int minutes = calendar.get(Calendar.MINUTE);
            if(minutes < 10) {
                mTextViewThree.animate(prevMinOne, 0).setDuration(DURATION).start();
                mTextViewFour.animate(prevMinTwo, minutes).setDuration(DURATION).start();
                prevMinOne = 0;
                prevMinTwo = minutes;
            } else {
                List<Integer> digits = digits(minutes);
                mTextViewThree.animate(prevMinOne, digits.get(1)).setDuration(DURATION).start();
                mTextViewFour.animate(prevMinTwo, digits.get(0)).setDuration(DURATION).start();
                prevMinOne = digits.get(1);
                prevMinTwo = digits.get(0);

            }
        }
    }
    private void setTimeSeconds(){
        if (mTextViewFive != null && mTextViewSix != null) {
            int seconds = Calendar.getInstance().get(Calendar.SECOND);
            if(seconds < 10) {
                mTextViewFive.animate(prevSecOne, 0).setDuration(DURATION_SECONDS).start();
                mTextViewSix.animate(prevSecTwo, seconds).setDuration(DURATION_SECONDS).start();
                prevSecOne = 0;
                prevSecTwo = seconds;
            } else {
                List<Integer> digits = digits(seconds);
                mTextViewFive.animate(prevSecOne, digits.get(1)).setDuration(DURATION_SECONDS).start();
                mTextViewSix.animate(prevSecTwo, digits.get(0)).setDuration(DURATION_SECONDS).start();
                prevSecOne = digits.get(1);
                prevSecTwo = digits.get(0);
            }
        }
    }

    /**
     * Split number into digits
     * @param number
     * @return digit array
     */
    private static List<Integer> digits(int number) {
        List<Integer> digits = new ArrayList<Integer>();
        while(number > 0) {
            digits.add(number % 10);
            number /= 10;
        }
        return digits;
    }

}
