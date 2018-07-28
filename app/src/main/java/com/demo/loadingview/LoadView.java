package com.demo.loadingview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by yueban on 2018/7/24.
 */

public class LoadView extends View {
    //屏幕对角线尺寸
    private float mDiagonalSize;
    private Paint paintCircle;
    private Paint paintBackground;
    //背景颜色  有时候我们加载全屏遮盖体验不好  可将背景色设置透明
    private int bgColor = Color.WHITE;
    private ValueAnimator mAnimator;
    //旋转一周时间
    private long mRotateTime = 1500;
    private AnimatorState mState;
    //当前旋转的角度
    private float mCurrentDegree;
    private int[] mCircleColors;
    //大圆半径
    private int mBigCircleRadus = 80;
    //小圆半径
    private int mSmallCircleRadus = 18;
    private float mCenterX;
    private float mCenterY;
    //扩散半径
    private float mSpreadRadus = 0f;
    //扩散时间
    private long mSpreadTime = 1000;
    //收缩时当前大圆半径
    private float mCurrentBigRadus = mBigCircleRadus;

    public LoadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LoadView(Context context) {
        super(context);
        init(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2f;
        mCenterY = h / 2f;
        mDiagonalSize = (float) Math.sqrt(w * w + h * h) / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mState == null) {
            mState = new RotateState();
        }
        mState.drawAnimator(canvas);
    }

    private void init(Context context) {
        mCircleColors = context.getResources().getIntArray(R.array.splash_circle_colors);
        paintCircle = new Paint();
        paintBackground = new Paint();
        paintCircle.setAntiAlias(true);
        paintBackground.setAntiAlias(true);
        paintBackground.setColor(bgColor);
        //Paint.Style.STROKE 只绘制图形轮廓（描边）
        //Paint.Style.FILL 只绘制图形内容
        //Paint.Style.FILL_AND_STROKE 既绘制轮廓也绘制内容
        paintBackground.setStyle(Paint.Style.STROKE);
    }

    /**
     * 策略模式基类 提供分别做动画的画法操作函数和取消函数
     */
    private abstract class AnimatorState {
        public abstract void drawAnimator(Canvas canvas);

        public void cancel() {
            mAnimator.cancel();
        }
    }

    /**
     * 加载完成后结束旋转进行聚合动画
     */
    public void endRotate() {
        if (mState != null && mState instanceof RotateState) {
            RotateState rotateState = (RotateState) mState;
            rotateState.cancel();
            post(new Runnable() {
                @Override
                public void run() {
                    mState = new ContractState();
                }
            });
        }
    }

    //旋转动画
    private class RotateState extends AnimatorState {
        public RotateState() {
            mAnimator = ValueAnimator.ofFloat(0, 2 * (float) Math.PI);
            mAnimator.setInterpolator(new LinearInterpolator());//匀速
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentDegree = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            mAnimator.setDuration(mRotateTime);
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.start();
        }

        @Override
        public void drawAnimator(Canvas canvas) {
            drawBackground(canvas);
            drawCircle(canvas);
        }
    }

    private void drawCircle(Canvas canvas) {
        //每个小圆相隔的角度
        float rotateDegree = (float) (Math.PI * 2 / mCircleColors.length);
        for (int i = 0; i < mCircleColors.length; i++) {
            float mDegree = rotateDegree * i + mCurrentDegree;
            float centerX = (float) (mCurrentBigRadus * Math.cos(mDegree));
            float centerY = (float) (mCurrentBigRadus * Math.sin(mDegree));
            paintCircle.setColor(mCircleColors[i]);
            canvas.drawCircle(mCenterX + centerX, mCenterY + centerY, mSmallCircleRadus, paintCircle);
        }
    }

    private void drawBackground(Canvas canvas) {
        if (mSpreadRadus > 0f) {
            //轮廓宽度
            float stroke = mDiagonalSize - mSpreadRadus;
            //高亮半径
            float mCurrentHoolRadus = mSpreadRadus + stroke / 2;
            paintBackground.setStrokeWidth(stroke);
            canvas.drawCircle(mCenterX, mCenterY, mCurrentHoolRadus, paintBackground);
        } else {
            canvas.drawColor(bgColor);
        }
    }

    //收缩状态
    private class ContractState extends AnimatorState {
        public ContractState() {
            mAnimator = ValueAnimator.ofFloat(0, mBigCircleRadus);
            mAnimator.setInterpolator(new OvershootInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentBigRadus = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    //动画结束进行扩散
                    mState = new SpreadState();
                }
            });
            mAnimator.setDuration(mRotateTime);
            mAnimator.reverse();
        }

        @Override
        public void drawAnimator(Canvas canvas) {
            drawBackground(canvas);
            drawCircle(canvas);
        }
    }

    //扩散状态
    private class SpreadState extends AnimatorState {

        public SpreadState() {
            mAnimator = ValueAnimator.ofFloat(mSmallCircleRadus, mDiagonalSize);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mSpreadRadus = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mAnimator.setDuration(mSpreadTime);
            mAnimator.start();
        }

        @Override
        public void drawAnimator(Canvas canvas) {
            drawBackground(canvas);
        }
    }
}
