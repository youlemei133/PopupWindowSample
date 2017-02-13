package com.hudawei.popupwindowsample.popupwindow;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by hudawei on 2017/2/13.
 *
 * 方法
 *
 * 1.popBackgroundColor 设置背景色
 * 2.popAnimationExit、popAnimationEnter 设置动画
 * 3.popTouchOutsideDismiss 设置点击空白区域，是否消失
 * 4.showAtLocation、showAsDropDown 显示
 * 5.dismissPopupWindow 消失
 * 6.findViewById 查找view
 *
 * 抽象方法
 *
 * initView()
 * initData()
 * initEvent()
 *
 * 使用示例：
 * 子类继承SimplePopupWindow
 *
 * MySimplePopupWindow popupWindow = (MySimplePopupWindow) new MySimplePopupWindow(this)
     .popBackgroundColor(0x99FFF0F0)
     .popAnimationEnter(R.anim.popup_window_enter)
     .popAnimationExit(R.anim.popup_window_exit)
     .popTouchOutsideDismiss(true);

     popupWindow.showAtLocation(Gravity.CENTER, 0, 0);
 *
 *
 * 注意：
 *
 * 1.在显示的时候，要确保Activity已经onCreate了
 * 2.在Activity onDestroy或finish之前都要调用 PopupWindowManager的clear()方法
 *
 *
 */

public abstract class BasePopupWindow {
    private PopupWindow mPopupWindow;
    //该PopupWindow显示的Activity
    private Activity mActivity;
    //显示的整个布局View
    private ViewGroup mContentView;
    //在点击外部消失的时候需要用到，该innerId
    private View mInnerView;
    //是否在进入时开启动画
    private boolean mEnterAnimFlag;
    //是否在消失的时候开启动画
    private boolean mExitAnimFlag;
    //进入动画资源
    private int mEnterAnimRes;
    //消失动画资源
    private int mExitAnimRes;
    //点击外部是否消失
    private boolean mTouchOutsideFlag;
    //是否设置了背景色
    private boolean mBackgroundFlag;
    private static List<BasePopupWindow> windows = new ArrayList<>();

    public BasePopupWindow(Activity activity, int layoutRes, int innerLayoutId) {
        mActivity = activity;
        //加载布局资源
        mContentView = (ViewGroup) activity.getLayoutInflater().inflate(layoutRes, null);
        mInnerView = mContentView.findViewById(innerLayoutId);
        //创建一个PopupWindow，设置宽高，以及FocusAble为true
        mPopupWindow = new PopupWindow(mContentView, MATCH_PARENT, MATCH_PARENT, true);
        //下面的设置都是为了点击空白区域时，消失PopupWindow
        //当是上面设置了宽高为全屏，所以这里设置是多余的
//        mPopupWindow.setTouchable(true);
//        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//                // 这里如果返回true的话，touch事件将被拦截
//                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
//            }
//        });
//        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xAA000000));    //要为popWindow设置一个背景才有效

        initView();
        initData();
        initEvent();
    }

    /**
     * 用来实例化布局里面的View,通过本类中的findViewById()方法
     */
    protected abstract void initView();

    /**
     * 初始化View
     */
    protected abstract void initData();

    /**
     * 绑定View的各种事件
     */
    protected abstract void initEvent();

    /**
     * 点击空白区域，自动消失
     */
    private void touchOutSideDismiss() {
        mContentView = (ViewGroup) mPopupWindow.getContentView();
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == mContentView) {
                    dismissPopupWindow(false);
                }
            }
        };
        mInnerView.setOnClickListener(clickListener);
        mContentView.setOnClickListener(clickListener);
    }

    /**
     * 设置背景色
     */
    public BasePopupWindow popBackgroundColor(int color) {
        mBackgroundFlag = true;
        mContentView.setBackgroundColor(color);
        return this;
    }

    /**
     * 设置背景色
     */
    public BasePopupWindow popBackgroundColor(String color) {
        return popBackgroundColor(Color.parseColor(color));
    }

    /**
     * 设置PopupWindow显示时的动画
     * @param enterAnimRes 动画资源，/res/anim下
     * @return this
     */
    public BasePopupWindow popAnimationEnter(int enterAnimRes) {
        mEnterAnimFlag = true;
        mEnterAnimRes = enterAnimRes;
        return this;
    }

    /**
     * 设置PopupWindow消失时的动画
     * @param exitAnimRes 动画资源，/res/anim下
     * @return this
     */
    public BasePopupWindow popAnimationExit(int exitAnimRes) {
        mExitAnimFlag = true;
        mExitAnimRes = exitAnimRes;
        return this;
    }

    /**
     * 是否点击空白区域自动消失
     * @param touchOutside true自动消失
     * @return this
     */
    public BasePopupWindow popTouchOutsideDismiss(boolean touchOutside) {
        mTouchOutsideFlag = touchOutside;
        return this;
    }

    /**
     * 获取View实例工具方法
     * @param viewId 查找的View的id
     * @return View
     */
    public View findViewById(int viewId) {
        return mContentView.findViewById(viewId);
    }

    /**
     * 检查当前的Activity是否finish
     * @return true没有finish
     */
    private boolean checkLifeCycle() {
        if (mActivity != null && !mActivity.isFinishing()) {
            return true;
        }
        return false;
    }

    /**
     * 在显示PopupWindow前，设置PopupWindow
     */
    private void initShow() {
        //检测Activity是否finsih
        if (checkLifeCycle()) {
            //是否设置了进入动画
            if (mEnterAnimFlag) {
                //是否设置了背景色，如果设置了，动画只总用于InnerView，如果没有作用于整个contentView
                if (mBackgroundFlag) {
                    mInnerView.startAnimation(AnimationUtils.loadAnimation(mActivity, mEnterAnimRes));
                } else {
                    mContentView.startAnimation(AnimationUtils.loadAnimation(mActivity, mEnterAnimRes));
                }
            }
            //是否点击空白区域自动消失
            if (mTouchOutsideFlag) {
                touchOutSideDismiss();
            }
            //全局的PopupWindow管理类，用来存储与销毁PopupWindow
            PopupWindowManager.add(this);
        }
    }

    /**
     * 消失PopupWindow
     * @param immediate 当Activity在finish时，传入true,不会执行动画。其他情况false
     */
    public void dismissPopupWindow(boolean immediate) {
        //检测Activity是否finish
        if (checkLifeCycle()) {
            //popupWindow是否在显示
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                //如果不是Activity在finish且设置了消失动画
                if (!immediate&&mExitAnimFlag) {
                    //加载动画资源
                    Animation animation = AnimationUtils.loadAnimation(mActivity, mExitAnimRes);
                    //设置动画监听器，在动画执行完后，在调用popupWindow的dismiss()方法
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (checkLifeCycle())
                                mPopupWindow.dismiss();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    //是否设置了背景色
                    if (mBackgroundFlag) {
                        mInnerView.startAnimation(animation);
                    } else {
                        mContentView.startAnimation(animation);
                    }
                } else {
                    mPopupWindow.dismiss();
                }
            }
        }
    }

    /**
     * 对应PopupWIndow中的显示方法
     */
    public void showAtLocation(int gravity, int x, int y) {
        initShow();
        mPopupWindow.showAtLocation(mContentView, gravity, x, y);
    }
    /**
     * 对应PopupWIndow中的显示方法
     */
    public void showAsDropDown(View anchor) {
        initShow();
        mPopupWindow.showAsDropDown(anchor);
    }
    /**
     * 对应PopupWIndow中的显示方法
     */
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        initShow();
        mPopupWindow.showAsDropDown(anchor, xoff, yoff);
    }
    /**
     * 对应PopupWIndow中的显示方法
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        initShow();
        mPopupWindow.showAsDropDown(anchor, xoff, yoff, gravity);
    }

}
