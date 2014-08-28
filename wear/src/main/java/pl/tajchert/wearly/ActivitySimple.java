package pl.tajchert.wearly;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.tajchert.wearly.timelytextview.TimelyView;

public class ActivitySimple extends Activity {
    private static final String TAG = "ActivitySimple";
    private final static IntentFilter intentFilter;
    static {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }
    private final static int DURATION = 2000;//in miliseconds


    private TimelyView mTextViewOne;
    private TimelyView mTextViewTwo;
    private TimelyView mTextViewThree;
    private TimelyView mTextViewFour;

    private Calendar calendar;

    private int prevHoursOne = -1;
    private int prevHoursTwo = -1;
    private int prevMinOne = -1;
    private int prevMinTwo = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        setViewStuff();
        timeInfoReceiver.onReceive(this, registerReceiver(null, intentFilter));
        registerReceiver(timeInfoReceiver, intentFilter);
    }

    private void setViewStuff() {
        mTextViewOne = (TimelyView) findViewById(R.id.textViewTimelyOne);
        mTextViewTwo = (TimelyView) findViewById(R.id.textViewTimelyTwo);
        mTextViewThree = (TimelyView) findViewById(R.id.textViewTimelyThree);
        mTextViewFour = (TimelyView) findViewById(R.id.textViewTimelyFour);
    }

    public BroadcastReceiver timeInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            calendar = Calendar.getInstance();
            try {
                if(calendar.get(Calendar.HOUR_OF_DAY) != Integer.parseInt(prevHoursOne + "" + prevHoursTwo)) {
                    setTimeHour();
                }
                if(calendar.get(Calendar.MINUTE) != Integer.parseInt(prevMinOne + "" + prevMinTwo)) {
                    setTimeMinutes();
                }
            } catch (NumberFormatException e) {
                setTimeHour();
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
