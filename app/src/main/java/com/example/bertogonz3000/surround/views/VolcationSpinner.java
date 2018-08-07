package com.example.bertogonz3000.surround.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.bertogonz3000.surround.R;

public class VolcationSpinner extends View {

    private Paint paint, radiusPaint, volumeBarPaint;
    private float x, y, touchDistance, mTranslateX, mTranslateY, widgetDistFront, widgetDistEnd, volumeBarRadius;
    private int radius, thumbRadius, volumeThumbRadius, thumbX,
            thumbY, volThumbX, volThumbY, volumeThumbDist, volWidgetX, volWidgetY, maxVol;
    private Drawable thumb, volumeThumb;
    private OnThumbChangeListener listener;
    private double angle, volWidgetXEnd, volWidgetYEnd;
    private boolean ignoreThumb = false, ignoreVol = false;

    public VolcationSpinner(Context context) {
        super(context);

        setUpPaints();

        init(null);

    }

    public VolcationSpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setUpPaints();

        init(attrs);
    }

    public VolcationSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setUpPaints();

        init(attrs);
    }

    public VolcationSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setUpPaints();

        init(attrs);
    }

    private void init(@Nullable AttributeSet set){

        TypedArray a = getContext().getTheme().obtainStyledAttributes(set, R.styleable.VolcationSpinner, 0, 0);

        try{
            maxVol = a.getInt(R.styleable.VolcationSpinner_maxVol, 10);
        } finally {
            a.recycle();
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);

        createCircle(canvas);

        createLine(canvas);

        createThumb(canvas);

        createVolumeWidget(canvas);

        createVolumeThumb(canvas);

        //Log.e("VOLUME", "Volume = " + getVolumeByDistance());
        Log.e("LOCATION", "Location = " + getLocationByAngle());


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        int circleDiameter = min - getPaddingLeft();
        mTranslateX = (int) (width * 0.5f);
        mTranslateY = (int) (height * 0.5f);
        radius = circleDiameter / 2;
        thumbRadius = radius/4;
        volumeThumbRadius = thumbRadius/4;
        volumeThumbDist = radius/4;
        y = height / 2;
        x = width / 2;
        //TODO - Math.atan2 stuff for the angle
        thumbX = (int) (radius * Math.cos(Math.toRadians(angle - Math.toDegrees(Math.PI / 2))));
        thumbY = (int) (radius * Math.sin(Math.toRadians(angle - Math.toDegrees(Math.PI / 2))));
        volThumbX = (int) (volumeThumbDist * Math.cos(Math.toRadians(angle - Math.toDegrees(Math.PI / 2))));
        volThumbY = (int) (volumeThumbDist * Math.sin(Math.toRadians(angle - Math.toDegrees(Math.PI / 2))));
        volWidgetX = thumbX/10;
        volWidgetY = thumbY/10;
        volWidgetXEnd = thumbX/1.2;
        volWidgetYEnd = thumbY/1.2;
        widgetDistFront = (float) Math.sqrt((volWidgetX*volWidgetX) + (volWidgetY*volWidgetY));
        widgetDistEnd = (float) Math.sqrt((volWidgetXEnd*volWidgetXEnd) + (volWidgetYEnd*volWidgetYEnd));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.getParent().requestDisallowInterceptTouchEvent(true);


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                updateOnTouch(event);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("ANGLE", "Angle = " + getLocationByAngle());
                listener.onLocationChanged(getVolumeByDistance(), getLocationByAngle());
                updateOnTouch(event);
                break;
            case MotionEvent.ACTION_UP:
                if(ignoreThumb) {
                    ignoreThumb = false;
                    volumeThumbRadius = volumeThumbRadius/2;
                }
                ignoreVol = false;
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    public void setOnThumbChangeListener(OnThumbChangeListener l) {
        listener = l;
    }


    //Todo - ISSUE - touch doesn't lock because of the MOVE event
    private void updateOnTouch(MotionEvent event){
        touchDistance = getDistFromCenter(event);


        if (testIgnoreTouch()) {return;}

        float xForAngle = (event.getX() - mTranslateX);
        float yForAngle = (event.getY() - mTranslateY);


        if (!ignoreVol && volumeThumb.getBounds().contains((int) event.getX(), (int) event.getY()) && (touchDistance <= widgetDistEnd && touchDistance >= widgetDistFront)){
            volumeThumbDist = (int) touchDistance;
            ignoreThumb = true;
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                volumeThumbRadius = volumeThumbRadius*2;
            }
        }

        if(!ignoreThumb) {
            angle = Math.toDegrees(Math.atan2(yForAngle, xForAngle) + (Math.PI/2) - Math.toRadians(180));

            thumbX = (int) (radius * Math.cos(Math.toRadians(angle - Math.toDegrees(Math.PI / 2))));
            thumbY = (int) (radius * Math.sin(Math.toRadians(angle - Math.toDegrees(Math.PI / 2))));

            ignoreVol = true;
        }

        volThumbX = (int) (volumeThumbDist * Math.cos(Math.toRadians(angle - Math.toDegrees(Math.PI / 2))));
        volThumbY = (int) (volumeThumbDist * Math.sin(Math.toRadians(angle - Math.toDegrees(Math.PI / 2))));


        if (angle < 0){
            angle = 360 + angle;
        }


        volWidgetX = thumbX/10;
        volWidgetY = thumbY/10;
        volWidgetXEnd = thumbX/1.2;
        volWidgetYEnd = thumbY/1.2;

        invalidate();

    }

    private boolean testIgnoreTouch(){
        boolean ignore = false;


        if (touchDistance > radius){
            ignore = true;
        }

        return ignore;
    }

    //TODO - make sizes relative
    private void setUpPaints(){
        //Circle paint
        paint = new Paint();
        paint.setColor(Color.BLUE);
//            paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) 10);

        //Circle Radius paint
        volumeBarRadius = 100;
        radiusPaint = new Paint();
        radiusPaint.setColor(Color.WHITE);
        radiusPaint.setStrokeWidth(volumeBarRadius);

        //Volume bar paint
        volumeBarPaint = new Paint();
        volumeBarPaint.setColor(Color.GRAY);
        volumeBarPaint.setStrokeWidth(volumeBarRadius/4);


    }

    private void createCircle(Canvas canvas){
        canvas.drawCircle(x, y, radius, paint);
        canvas.drawCircle(x, y, thumbRadius, paint);
    }

    private void createThumb(Canvas canvas){
        thumb = ContextCompat.getDrawable(getContext(), R.drawable.ic_launcher_foreground);
        thumb.setBounds((int) mTranslateX - thumbX - thumbRadius, (int) mTranslateY - thumbY - thumbRadius, (int) mTranslateX - thumbX + thumbRadius, (int) mTranslateY - thumbY + thumbRadius);
        thumb.draw(canvas);
    }

    private void createLine(Canvas canvas){
        canvas.drawLine(x,y, mTranslateX - thumbX, mTranslateY - thumbY, radiusPaint);
        canvas.drawCircle(x,y, volumeBarRadius/2, radiusPaint);
    }

    private void createVolumeWidget(Canvas canvas){
        canvas.drawLine((mTranslateX - volWidgetX),(mTranslateY - volWidgetY), (float) (mTranslateX - volWidgetXEnd), (float) (mTranslateY - volWidgetYEnd), volumeBarPaint);
    }

    private void createVolumeThumb(Canvas canvas){
        volumeThumb = ContextCompat.getDrawable(getContext(), R.drawable.ic_launcher_background);
        volumeThumb.setBounds((int)(mTranslateX - volThumbX - volumeThumbRadius), (int)(mTranslateY - volThumbY - volumeThumbRadius), (int)(mTranslateX - volThumbX + volumeThumbRadius), (int)(mTranslateY - volThumbY + volumeThumbRadius));
        volumeThumb.draw(canvas);
    }

    private float getDistFromCenter(MotionEvent event){
        float xDist = event.getX() - x;
        float yDist = event.getY() - y;
        return (float) Math.sqrt((xDist*xDist) + (yDist*yDist));
    }

    public float getVolumeByDistance(){
        float volumeDistance = (float) Math.sqrt((volThumbX*volThumbX) + (volThumbY*volThumbY));
        volumeDistance -= widgetDistFront;
        float widgetLength = widgetDistEnd - widgetDistFront;
        float volumePercent = volumeDistance/widgetLength;
        return volumePercent;
    }

    public float getLocationByAngle(){
        return (float) (angle/360);
    }

    public void setMaxVol(int maxVol){
        this.maxVol = maxVol;
    }

    public interface OnThumbChangeListener{

        //Notification that thumb location has changed
        //TODO -CALL this somewhere so that it's called on touches and can e
        void onLocationChanged(float distance, float angle);

    }
}
