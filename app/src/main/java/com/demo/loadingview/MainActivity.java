package com.demo.loadingview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView img;
    private LoadView loadView;
    private FrameLayout parent;
    private FrameLayout mMainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parent = findViewById(R.id.parent);
        loadView = new LoadView(this);
        parent.addView(loadView);
        startLoadData();
    }

    Handler handler = new Handler();

    private void startLoadData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //数据加载完毕，进入主界面--->开启后面的两个动画
                loadView.endRotate();
            }
        }, 5000);//延迟时间
    }

    /**
     * *****************************以下为属性动画内容可忽略*************************
     */
    private void startSetAnimotar(ImageView img) {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(img, "translationX", 0f, 500F);
        ObjectAnimator animator12 = ObjectAnimator.ofFloat(img, "translationY", 0f, 500F);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(img, "alpha", 0f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(img, "scaleX", 0f, 2f);
        ObjectAnimator animator32 = ObjectAnimator.ofFloat(img, "scaleY", 0f, 2f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(img, "rotation", 0, 360);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(2000);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {

            }
        });
        //animatorSet.play(animator3).with(animator2).after(animator1);//链式调用顺序
        //animatorSet.play(animator3).with(animator2).before(animator1);//animator1
        animatorSet.playTogether(animator1, animator12, animator2, animator3, animator32, animator4);//一起执行
//        animatorSet.playSequentially(animator1, animator2, animator3);//顺序执行
        animatorSet.start();
    }

    private void startRotateAnimotar(ImageView img) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img, "rotation", 0, 360);
        objectAnimator.setDuration(3000);
        objectAnimator.setRepeatCount(3);//循环次数
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);//从头开始  REVERSE：倒叙开始
        objectAnimator.start();
    }

    private void startTranslateAnimotar(ImageView img) {
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("translationX", 0f, 200f);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("translationY", 0f, 200f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(img, holderX, holderY);
        objectAnimator.setDuration(3000);
        objectAnimator.start();
    }

    private void startScaleAnimotar(ImageView img) {
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0f);
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(img, holderX, holderY);
        objectAnimator.setDuration(3000);
        objectAnimator.start();
    }

    /**
     * 属性动画简单实现  第二个参数代表动画类型
     * alpha 渐变透明度动画效果
     * scale 渐变尺寸伸缩动画效果
     * translate 画面转换位置移动动画效果
     * rotate 画面转移旋转动画效果
     * AccelerateDecelerateInterpolator 在动画开始与介绍的地方速率改变比较慢，在中间的时候加速
     * AccelerateInterpolator 在动画开始的地方速率改变比较慢，然后开始加速
     * AnticipateInterpolator 开始的时候向后然后向前甩
     * AnticipateOvershootInterpolator 开始的时候向后然后向前甩一定值后返回最后的值
     * BounceInterpolator 动画结束的时候弹起
     * CycleInterpolator 动画循环播放特定的次数，速率改变沿着正弦曲线
     * DecelerateInterpolator 在动画开始的地方快然后慢
     * LinearInterpolator 以常量速率改变
     * OvershootInterpolator 向前甩一定值后再回到原来位置
     *
     * @param img
     */
    private void startAlphaAnimotar(ImageView img) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img, "alpha", 1.0f, 0f, 1.0f);
        objectAnimator.setDuration(3000);
        objectAnimator.start();
    }

}
