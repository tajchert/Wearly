package pl.tajchert.wearly;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.nineoldandroids.animation.ObjectAnimator;

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


    private volatile ObjectAnimator objectAnimatorFirst = null;
    private volatile ObjectAnimator objectAnimatorSecond = null;
    private final static int DURATION = 2000;//in miliseconds
    private final static int DURATION_SECONDS = 500;//in miliseconds

    private Handler mHandler = new Handler();
    private boolean isActive = true;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewOne = (TimelyView) findViewById(R.id.textViewTimelyOne);
        mTextViewTwo = (TimelyView) findViewById(R.id.textViewTimelyTwo);
        mTextViewThree = (TimelyView) findViewById(R.id.textViewTimelyThree);
        mTextViewFour = (TimelyView) findViewById(R.id.textViewTimelyFour);
        mTextViewFive = (TimelyViewSmall) findViewById(R.id.textViewTimelyFive);
        mTextViewSix = (TimelyViewSmall) findViewById(R.id.textViewTimelySix);

        timeInfoReceiver.onReceive(this, registerReceiver(null, intentFilter));
        registerReceiver(timeInfoReceiver, intentFilter);

        /*final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);//TODO use it
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

            }
        });*/
    }

    public BroadcastReceiver timeInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            calendar = Calendar.getInstance();
            setTimeHour();
            setTimeMinutes();
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
        setTimeHour();
        setTimeMinutes();
        if(mTextViewFive != null && mTextViewSix != null) {
            mTextViewFive.setVisibility(View.VISIBLE);
            mTextViewSix.setVisibility(View.VISIBLE);
        }
        isActive = true;
        startSecondUpdate();
    }

    @Override
    protected void onPause() {
        isActive = false;
        if(mTextViewFive != null && mTextViewSix != null) {
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
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                setTimeSeconds();
                            }
                        });
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
            if(hour < 10) {
                objectAnimatorFirst = mTextViewOne.animate(prevHoursOne, 0);
                objectAnimatorFirst.setDuration(DURATION);
                objectAnimatorFirst.start();
                objectAnimatorFirst = mTextViewTwo.animate(prevHoursTwo, hour);
                objectAnimatorFirst.setDuration(DURATION);
                objectAnimatorFirst.start();
                prevHoursOne = 0;
                prevHoursTwo = hour;
            } else {
                List<Integer> digits = digits(hour);
                objectAnimatorFirst = mTextViewOne.animate(prevHoursOne, digits.get(1));
                objectAnimatorFirst.setDuration(DURATION);
                objectAnimatorFirst.start();
                objectAnimatorFirst = mTextViewTwo.animate(prevHoursTwo, digits.get(0));
                objectAnimatorFirst.setDuration(DURATION);
                objectAnimatorFirst.start();
                prevHoursOne = digits.get(1);
                prevHoursTwo = digits.get(0);
            }
        }
    }
    private void setTimeMinutes(){
        if (mTextViewThree != null && mTextViewFour != null) {
            int minutes = calendar.get(Calendar.MINUTE);
            if(minutes < 10) {
                objectAnimatorFirst = mTextViewThree.animate(prevMinOne, 0);
                objectAnimatorFirst.setDuration(DURATION);
                objectAnimatorFirst.start();
                objectAnimatorFirst = mTextViewFour.animate(prevMinTwo, minutes);
                objectAnimatorFirst.setDuration(DURATION);
                objectAnimatorFirst.start();
                prevMinOne = 0;
                prevMinTwo = minutes;
            } else {
                List<Integer> digits = digits(minutes);
                objectAnimatorFirst = mTextViewThree.animate(prevMinOne, digits.get(1));
                objectAnimatorFirst.setDuration(DURATION);
                objectAnimatorFirst.start();
                objectAnimatorFirst = mTextViewFour.animate(prevMinTwo, digits.get(0));
                objectAnimatorFirst.setDuration(DURATION);
                objectAnimatorFirst.start();
                prevMinOne = digits.get(1);
                prevMinTwo = digits.get(0);

            }
        }
    }
    private void setTimeSeconds(){
        if (mTextViewFive != null && mTextViewSix != null) {
            int seconds = Calendar.getInstance().get(Calendar.SECOND);
            if(seconds < 10) {
                objectAnimatorFirst = mTextViewFive.animate(prevSecOne, 0);
                objectAnimatorSecond = mTextViewSix.animate(prevSecTwo, seconds);
                prevSecOne = 0;
                prevSecTwo = seconds;
            } else {
                List<Integer> digits = digits(seconds);
                objectAnimatorFirst = mTextViewFive.animate(prevSecOne, digits.get(1));
                objectAnimatorSecond = mTextViewSix.animate(prevSecTwo, digits.get(0));
                prevSecOne = digits.get(1);
                prevSecTwo = digits.get(0);
            }
            objectAnimatorFirst.setDuration(DURATION_SECONDS);
            objectAnimatorFirst.start();
            objectAnimatorSecond.setDuration(DURATION_SECONDS);
            objectAnimatorSecond.start();
        }
    }

    /**
     * Split number into digits
     * @param number
     * @return digit array
     */
    List<Integer> digits(int number) {
        List<Integer> digits = new ArrayList<Integer>();
        while(number > 0) {
            digits.add(number % 10);
            number /= 10;
        }
        return digits;
    }
}
