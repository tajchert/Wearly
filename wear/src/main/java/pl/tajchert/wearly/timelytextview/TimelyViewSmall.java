package pl.tajchert.wearly.timelytextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.util.Property;

import pl.tajchert.wearly.timelytextview.animation.TimelyEvaluator;
import pl.tajchert.wearly.timelytextview.model.NumberUtils;

public class TimelyViewSmall extends View {
    private static final float                           RATIO                   = 1f;
    private static final Property<TimelyViewSmall, float[][]> CONTROL_POINTS_PROPERTY = new Property<TimelyViewSmall, float[][]>(float[][].class, "controlPoints") {
        @Override
        public float[][] get(TimelyViewSmall object) {
            return object.getControlPoints();
        }

        @Override
        public void set(TimelyViewSmall object, float[][] value) {
            object.setControlPoints(value);
        }
    };
    private              Paint                           mPaint                  = null;
    private              Path                            mPath                   = null;
    private              float[][]                       controlPoints           = null;

    public TimelyViewSmall(Context context) {
        super(context);
        init();
    }

    public TimelyViewSmall(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimelyViewSmall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public float[][] getControlPoints() {
        return controlPoints;
    }

    public void setControlPoints(float[][] controlPoints) {
        this.controlPoints = controlPoints;
        invalidate();
    }

    public ObjectAnimator animate(int start, int end) {
        float[][] startPoints = NumberUtils.getControlPointsFor(start);
        float[][] endPoints = NumberUtils.getControlPointsFor(end);

        return ObjectAnimator.ofObject(this, CONTROL_POINTS_PROPERTY, new TimelyEvaluator(), startPoints, endPoints);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (controlPoints == null) return;

        int length = controlPoints.length;

        int height = (getMeasuredHeight()-5);
        int width = (getMeasuredWidth()-5);

        float minDimen = height > width ? width : height;

        mPath.reset();
        mPath.moveTo(minDimen * controlPoints[0][0], minDimen * controlPoints[0][1]);
        for (int i = 1; i < length; i += 3) {
            mPath.cubicTo(minDimen * controlPoints[i][0], minDimen * controlPoints[i][1],
                          minDimen * controlPoints[i + 1][0], minDimen * controlPoints[i + 1][1],
                          minDimen * controlPoints[i + 2][0], minDimen * controlPoints[i + 2][1]);
        }
        //canvas.drawColor(Color.BLUE);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = 35;
        int height = 35;
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heigthWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        int maxWidth = (int) (heigthWithoutPadding * RATIO);
        int maxHeight = (int) (widthWithoutPadding / RATIO);

        if (widthWithoutPadding > maxWidth) {
            width = maxWidth + getPaddingLeft() + getPaddingRight();
        } else {
            height = maxHeight + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(width, height);
    }

    private void init() {
        // A new paint with the style as stroke.
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(4.0f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPath = new Path();
    }
}
