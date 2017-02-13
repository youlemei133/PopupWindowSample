package com.hudawei.popupwindowsample;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hudawei.popupwindowsample.popupwindow.SimplePopupWindow;

/**
 * Created by hudawei on 2017/2/13.
 */

public class MySimplePopupWindow extends SimplePopupWindow implements View.OnClickListener {
    private Activity mActivity;
    private TextView negativeText;
    private TextView positiveText;

    public MySimplePopupWindow(Activity activity) {
        super(activity, R.layout.popwindow, R.id.contentLayout);
        mActivity = activity;
    }

    @Override
    protected void initView() {
        negativeText = (TextView) findViewById(R.id.negativeText);
        positiveText = (TextView) findViewById(R.id.positiveText);
    }

    @Override
    protected void initEvent() {
        negativeText.setOnClickListener(this);
        positiveText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.negativeText:
                Toast.makeText(mActivity, "选择取消", Toast.LENGTH_SHORT).show();
                dismissPopupWindow(false);
                break;
            case R.id.positiveText:
                Toast.makeText(mActivity, "选择确定", Toast.LENGTH_SHORT).show();
                dismissPopupWindow(false);
                break;
        }
    }
}
