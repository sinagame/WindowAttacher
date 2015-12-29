package com.sina.sinagame.windowattacher;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Management Center PopupWindow.
 * 
 * @author liu_chonghui
 * 
 */
public abstract class CenterPopupAttacher extends FooterPopupAttacher {

	public CenterPopupAttacher(Activity attachedActivity, int layoutResId,
			int animationViewGroupId) {
		super(attachedActivity, layoutResId, animationViewGroupId);
	}

	public CenterPopupAttacher(Activity attachedActivity, int layoutResId) {
		super(attachedActivity, layoutResId);
	}

	@Override
	protected void showAtLocation(PopupWindow pop, View parent, int gravity,
			int x, int y) {
		super.showAtLocation(pop, parent, Gravity.CENTER, 0, 0);
	}

}
