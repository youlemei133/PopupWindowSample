package com.hudawei.popupwindowsample.popupwindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hudawei on 2017/2/13.
 * 方法：
 * add  在PopupWindow显示的时候，将PopupWindow添加到列表中。
 * clear 移除列表中所有在显示的PopupWindow
 */

public class PopupWindowManager {
    private static List<BasePopupWindow> mWindows=new ArrayList<>();

    /**
     * 在PopupWindow显示的时候，将PopupWindow添加到列表中。
     * @param popupWindow BasePopupWindow
     */
    public static void add(BasePopupWindow popupWindow){
        mWindows.add(popupWindow);
    }

    /**
     * 移除列表中所有在显示的PopupWindow
     * @param immediate 在Activity调用finish或onDestroy的时候为true
     */
    public static void clear(boolean immediate){
        if(mWindows.size()!=0){
            for(BasePopupWindow window:mWindows){
                if(window!=null){
                    window.dismissPopupWindow(immediate);
                }
            }
            mWindows.clear();
        }
    }
}
