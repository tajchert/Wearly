package pl.tajchert.wearly;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.tajchert.wearly.timelytextview.TimelyView;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private final static IntentFilter intentFilter;
    static {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

    private volatile ObjectAnimator objectAnimator = null;
    private final static int DURATION = 2000;//in miliseconds

    private TimelyView mTextViewOne;
    private TimelyView mTextViewTwo;
    private TimelyView mTextViewThree;
    private TimelyView mTextViewFour;

    private Calendar calendar;

    private int prevHoursOne = 0;
    private int prevHoursTwo = 0;
    private int prevMinOne = 0;
    private int prevMinTwo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewOne = (TimelyView) findViewById(R.id.textViewTimelyOne);
        mTextViewTwo = (TimelyView) findViewById(R.id.textViewTimelyTwo);
        mTextViewThree = (TimelyView) findViewById(R.id.textViewTimelyThree);
        mTextViewFour = (TimelyView) findViewById(R.id.textViewTimelyFour);

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
    }

    private void setTimeHour(){
        if (mTextViewOne != null && mTextViewTwo != null) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if(hour < 10) {
                objectAnimator = mTextViewOne.animate(prevHoursOne, 0);
                objectAnimator.setDuration(DURATION);
                objectAnimator.start();
                objectAnimator = mTextViewTwo.animate(prevHoursTwo, hour);
                objectAnimator.setDuration(DURATION);
                objectAnimator.start();
                prevHoursOne = 0;
                prevHoursTwo = hour;
            } else {
                List<Integer> digits = digits(hour);
                objectAnimator = mTextViewOne.animate(prevHoursOne, digits.get(1));
                objectAnimator.setDuration(DURATION);
                objectAnimator.start();
                objectAnimator = mTextViewTwo.animate(prevHoursTwo, digits.get(0));
                objectAnimator.setDuration(DURATION);
                objectAnimator.start();
                prevHoursOne = digits.get(1);
                prevHoursTwo = digits.get(0);
            }
        }
    }
    private void setTimeMinutes(){
        if (mTextViewOne != null && mTextViewTwo != null) {
            int minutes = calendar.get(Calendar.MINUTE);
            if(minutes < 10) {
                objectAnimator = mTextViewThree.animate(prevMinOne, 0);
                objectAnimator.setDuration(DURATION);
                objectAnimator.start();
                objectAnimator = mTextViewFour.animate(prevMinTwo, minutes);
                objectAnimator.setDuration(DURATION);
                objectAnimator.start();
                prevMinOne = 0;
                prevMinTwo = minutes;
            } else {
                List<Integer> digits = digits(minutes);
                objectAnimator = mTextViewThree.animate(prevMinOne, digits.get(1));
                objectAnimator.setDuration(DURATION);
                objectAnimator.start();
                objectAnimator = mTextViewFour.animate(prevMinTwo, digits.get(0));
                objectAnimator.setDuration(DURATION);
                objectAnimator.start();
                prevMinOne = digits.get(1);
                prevMinTwo = digits.get(0);

            }
        }
    }
    List<Integer> digits(int i) {
        List<Integer> digits = new ArrayList<Integer>();
        while(i > 0) {
            digits.add(i % 10);
            i /= 10;
        }
        return digits;
    }
}
