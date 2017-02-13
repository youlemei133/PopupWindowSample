package com.hudawei.popupwindowsample;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainActivity extends AppCompatActivity {

    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void basicPop(View v) {
        initPopupWindow(this, v);
    }
    public void customPop(View v) {
        simplePopupWindow();
    }


    public void simplePopupWindow() {
        MySimplePopupWindow popupWindow = (MySimplePopupWindow) new MySimplePopupWindow(this)
                .popBackgroundColor(0x99FFF0F0)
                .popAnimationEnter(R.anim.popup_window_enter)
                .popAnimationExit(R.anim.popup_window_exit)
                .popTouchOutsideDismiss(true);

        popupWindow.showAtLocation(Gravity.CENTER, 0, 0);

//        popupWindow.dismissPopupWindow();
    }


    public void initPopupWindow(final Activity activity, View parentView) {
        //1.设置显示的layout
        View contentView = activity.getLayoutInflater().inflate(R.layout.popwindow, null);
        //创建一个PopupWindow，设置宽高，能获取焦点
        mPopupWindow = new PopupWindow(contentView, MATCH_PARENT, MATCH_PARENT, true);
        //2.设置动画，没什么卵用
        mPopupWindow.setAnimationStyle(R.anim.popup_window_exit);
        //3.1 设置点击空白区域消失，只适合PopupWindow里面的布局不同时为MATCH_PARENT情况下使用
        mPopupWindow.setTouchable(true);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xAA000000));    //要为popWindow设置一个背景才有效

        //3.2 设置点击空白区域消失,只适合PopupWindow里面的布局同时为MATCH_PARENT情况下使用
        touchOutSide(activity, mPopupWindow);
        //4.设置layout里面的事件
        contentView.findViewById(R.id.negativeText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "选择取消", Toast.LENGTH_SHORT).show();
                dismissPopupWindow(activity, mPopupWindow);
            }
        });

        contentView.findViewById(R.id.positiveText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "选择确定", Toast.LENGTH_SHORT).show();
                dismissPopupWindow(activity, mPopupWindow);
            }
        });

        //5.设置显示的位置
//        mPopupWindow.showAsDropDown(parentView,100,0);
        mPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
    }

    /**
     * 消失PopupWindow
     *
     * @param activity
     * @param popupWindow
     */
    public void dismissPopupWindow(Activity activity, PopupWindow popupWindow) {
        if (activity != null && !activity.isFinishing()) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }
    }

    /**
     * 在PopupWindow里面的布局的宽高都为MATCH_PARENT，点击空白区域消失
     *
     * @param activity
     * @param mPopupWindow
     */
    public void touchOutSide(final Activity activity, final PopupWindow mPopupWindow) {
        final ViewGroup baseParentView = (ViewGroup) mPopupWindow.getContentView();
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == baseParentView) {
                    dismissPopupWindow(activity, mPopupWindow);
                }
            }
        };
        baseParentView.findViewById(R.id.contentLayout).setOnClickListener(clickListener);
        baseParentView.setOnClickListener(clickListener);
    }

}
