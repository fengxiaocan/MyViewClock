package com.evil.clock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

/**
 * 完美的时钟
 * @author 风小灿
 * @date 2016-7-2
 */
public class ClockView
        extends View
{

    private Paint mPaint;
    private Paint mTextPaint;
    private float mCx;
    private float mCy;
    private float mRadius;
    private float mGap;//圆与外部控件之间的空隙
    private float mDialLong;//长刻度的长度
    private float mDialShort;//短刻度的长度
    private float mTextSize;//字体的大小
    private float mSecondLong;//秒针的长度
    private float mSecondShot;//秒针的短度
    private float mSecondWidth;//秒针尾部的宽度
    private float mSecondHeight;//秒针尾部的高度
    private float mMinuteLong;//分针的长度
    private float mMinuteShot;//分针的短度
    private float mMinuteWidth;//分针尾部的宽度
    private float mMinuteHeight;//分针尾部的高度
    private float mHourLong;//时针的长度
    private float mHourShot;//时针的短度
    private float mHourWidth;//时针尾部的宽度
    private float mHourHeight;//时针尾部的高度
    private Paint mNeedlePaint;//针的画笔
    private float mSecondDegree;//秒针度数
    private float mMinuteDegree;//分针度数
    private float mHourDegree;//时针度数
    private float mMinuteValue = 0;//分针精确到1度的误差值
    private float mHourValue   = 0;//时针精确到1度的误差值
    private Paint mBackgroundPaint;//背景的画笔
    private int   mSecondColor;//秒针的颜色
    private int   mMinuteColor;//分针的颜色
    private int   mHourColor;//时针的颜色

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //画笔的初始化
        initPaint();

        initLengthData();

        //初始化时针分针秒针的颜色
        initNeedleColor();

        //初始化时针的误差值
        initHourValue();

        //初始化时钟的角度
        initDegree();
    }

    /**
     * 初始化长度,获取配置文件中的像素大小
     */
    private void initLengthData() {
        mGap = getResources().getDimensionPixelSize(R.dimen.clock_circle_gap);
        mDialLong = getResources().getDimensionPixelSize(R.dimen.clock_dial_long);
        mDialShort = getResources().getDimensionPixelSize(R.dimen.clock_dial_short);
        mTextSize = getResources().getDimensionPixelSize(R.dimen.clock_text_size);
        mSecondLong = getResources().getDimensionPixelSize(R.dimen.clock_second_long);
        mSecondShot = getResources().getDimensionPixelSize(R.dimen.clock_second_short);
        mMinuteLong = getResources().getDimensionPixelSize(R.dimen.clock_minute_long);
        mMinuteShot = getResources().getDimensionPixelSize(R.dimen.clock_minute_short);
        mHourLong = getResources().getDimensionPixelSize(R.dimen.clock_hour_long);
        mHourShot = getResources().getDimensionPixelSize(R.dimen.clock_hour_short);
        mSecondWidth = getResources().getDimensionPixelSize(R.dimen.clock_second_width);
        mSecondHeight = getResources().getDimensionPixelSize(R.dimen.clock_second_height);
        mMinuteHeight = getResources().getDimensionPixelSize(R.dimen.clock_minute_height);
        mMinuteWidth = getResources().getDimensionPixelSize(R.dimen.clock_minute_width);
        mHourHeight = getResources().getDimensionPixelSize(R.dimen.clock_hour_height);
        mHourWidth = getResources().getDimensionPixelSize(R.dimen.clock_hour_width);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();//画圆和刻度的画笔
        mTextPaint = new Paint();//画数字的画笔
        mPaint.setStyle(Style.STROKE);//画空心
        mPaint.setAntiAlias(true);//去锯齿
        mNeedlePaint = new Paint();
        mNeedlePaint.setAntiAlias(true);//去锯齿
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
    }

    /**
     * 初始化时针分针秒针的颜色
     */
    private void initNeedleColor() {
        mSecondColor = Color.BLUE;
        mMinuteColor = Color.GREEN;
        mHourColor = Color.RED;
    }

    //初始化时针的误差值
    private void initHourValue() {
        //获取当前时间值
        long     timeMillis = System.currentTimeMillis();
        Calendar calendar   = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        //获取分值
        int minute = calendar.get(calendar.MINUTE);
        mHourValue = minute / 2;
    }

    /**
     * 设置秒针的颜色
     * @param colors
     */
    public void setSecondColor(int colors) {
        mSecondColor = colors;
    }

    /**
     * 设置分针的颜色
     * @param colors
     */
    public void setMinuteColor(int colors) {
        mMinuteColor = colors;
    }

    /**
     * 设置时针的颜色
     * @param colors
     */
    public void setHourColor(int colors) {
        mHourColor = colors;
    }


    public ClockView(Context context) {
        this(context, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画时钟的圆环
        drawCircke(canvas);

        //画背景
        drawBackground(canvas);

        //画刻度
        drawDial(canvas);

        //画数字
        drawNum(canvas);

        //画秒针
        drawSecond(canvas);

        //画分针
        drawMinute(canvas);

        //画时针
        drawHour(canvas);
    }

    /**
     * 画背景
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        int colors[] = {Color.BLACK,
                        Color.LTGRAY,
                        Color.WHITE,
                        Color.LTGRAY};
        //着色器
        Shader shader = new RadialGradient(mCx, mCy, mRadius, colors, null, Shader.TileMode.CLAMP);
        //设置着色器
        mBackgroundPaint.setShader(shader);
        canvas.drawCircle(mCx, mCy, mRadius, mBackgroundPaint);
    }

    /**
     * 画时针
     * @param canvas
     */
    private void drawHour(Canvas canvas) {
        Path path = new Path();
        //设置时针的属性
        mNeedlePaint.setColor(mHourColor);
        //旋转时针
        canvas.rotate(mHourDegree, mCx, mCy);
        // 时针的路径
        float x1 = mCx, y1 = mCy - mHourLong;
        float x2 = mCx + mHourWidth / 2, y2 = mCy + mHourShot;
        float x3 = mCx, y3 = mCy + mHourHeight;
        float x4 = mCx - mHourWidth / 2, y4 = mCy + mHourShot;

        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.lineTo(x4, y4);
        path.lineTo(x1, y1);

        canvas.drawPath(path, mNeedlePaint);
        canvas.rotate(-mHourDegree, mCx, mCy);
    }

    /**
     * 画分针
     * @param canvas
     */
    private void drawMinute(Canvas canvas) {
        Path path = new Path();
        //设置分针的属性
        mNeedlePaint.setColor(mMinuteColor);
        //旋转分针
        canvas.rotate(mMinuteDegree, mCx, mCy);

        // 分针的路径
        float x1 = mCx, y1 = mCy - mMinuteLong;
        float x2 = mCx + mMinuteWidth / 2, y2 = mCy + mMinuteShot;
        float x3 = mCx, y3 = mCy + mMinuteHeight;
        float x4 = mCx - mMinuteWidth / 2, y4 = mCy + mMinuteShot;

        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.lineTo(x4, y4);
        path.lineTo(x1, y1);

        canvas.drawPath(path, mNeedlePaint);

        canvas.rotate(-mMinuteDegree, mCx, mCy);
    }

    /**
     * 画秒针
     */
    private void drawSecond(Canvas canvas) {
        Path path = new Path();
        //设置秒针的属性
        mNeedlePaint.setColor(mSecondColor);
        //旋转
        canvas.rotate(mSecondDegree, mCx, mCy);
        // 秒针的路径
        float x1 = mCx, y1 = mCy - mSecondLong;
        float x2 = mCx + mSecondWidth / 2, y2 = mCy + mSecondShot;
        float x3 = mCx, y3 = mCy + mSecondHeight;
        float x4 = mCx - mSecondWidth / 2, y4 = mCy + mSecondShot;

        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.lineTo(x4, y4);
        path.lineTo(x1, y1);

        canvas.drawPath(path, mNeedlePaint);

        canvas.rotate(-mSecondDegree, mCx, mCy);//返回旋转的角度
    }

    /**
     * 画数字
     * @param canvas
     */
    private void drawNum(Canvas canvas) {
        float positionX = mCx;//字体所在的X轴位置
        float positionY = mGap + mDialLong + mTextSize;//字体所在的Y轴位置

        float px = mCx;//字体中心x轴坐标
        float py = mGap + mDialLong + mTextSize / 2;//字体中心Y轴坐标

        for (int i = 0; i < 12; i++) {
            mTextPaint.setTextAlign(Paint.Align.CENTER);//设置字体居中
            mTextPaint.setTextSize(mTextSize);//设置字体大小

            //把字体掰正
            canvas.rotate(-i * 30, px, py);

            if (i == 0) {
                canvas.drawText("12", positionX, positionY, mTextPaint);
            } else {
                canvas.drawText(String.valueOf(i), positionX, positionY, mTextPaint);
            }

            canvas.rotate(i * 30, px, py);//把画布旋转回来

            canvas.rotate(30, mCx, mCy);//旋转画布
        }
    }

    /**
     * 绘制刻度
     * @param canvas
     */
    private void drawDial(Canvas canvas) {
        for (int i = 0; i < 60; i++) {
            canvas.rotate(i * 6, mCx, mCy);//每次旋转30度

            float startX = mCx;
            float startY = mGap;
            float stopX  = mCx;
            float stopY;
            if (i % 5 == 0) {
                stopY = mDialLong;        //长刻度
            } else {
                stopY = mDialShort;        //短刻度
            }
            canvas.drawLine(startX, startY, stopX, stopY, mPaint);//开始画刻度

            canvas.rotate(-i * 6, mCx, mCy);//画完以后都要旋转回来
        }
    }

    /**
     * 画时钟的圆环
     * @param canvas
     */
    private void drawCircke(Canvas canvas) {
        //画一个圆
        mCx = getMeasuredWidth() / 2;
        mCy = getMeasuredHeight() / 2;
        mRadius = getMeasuredWidth() / 2 - mGap;
        canvas.drawCircle(mCx, mCy, mRadius, mPaint);
    }

    /**
     * 当控件添加到Activity里面Windows时调用
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //时钟的更新
        renewTime();
    }

    /**
     * 时钟的更新
     */
    private void renewTime() {
        //每隔一秒钟更新一次
        postDelayed(mMyRunnble, 1000);
    }

    private Runnable mMyRunnble = new Runnable() {
        @Override
        public void run() {
            initDegree();
            invalidate();//重新绘制
            renewTime();
        }
    };

    /**
     * 当view从windows中移除时调用
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //停止更新时钟
        removeCallbacks(mMyRunnble);
    }

    /**
     *  初始化角度
     */
    private void initDegree() {
        //获取当前时间值
        long     timeMillis = System.currentTimeMillis();
        Calendar calendar   = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        //获取秒值
        int second = calendar.get(calendar.SECOND);
        //获取分值
        int minute = calendar.get(calendar.MINUTE);
        //获取时值
        int hour = calendar.get(calendar.HOUR);

        if (second % 10 == 0) {
            //秒针的值等于为10 的倍数时
            if (second == 0) {
                mMinuteValue = 0;//把分针的误差值置为0
            } else {
                mMinuteValue++;//把分针的误差值置自增
            }
        }

        if (minute % 2 == 0 && second == 0) {
            //每过两分钟时,时针的角度加1
            if (minute == 0) {
                mHourValue = 0;//把时针的误差值置为0
            } else {
                mHourValue++;//把时针的误差值置自增
            }
        }

        //把秒值转化为秒针的角度
        mSecondDegree = second * 6;
        //把秒值转化为分针的角度,并加上误差值
        mMinuteDegree = minute * 6 + mMinuteValue;

        //把分值转化为时针的角度,并加上误差值
        mHourDegree = hour * 30 + mHourValue;
    }
}
