package com.sina.sinagame.windowattacher;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

public abstract class HeaderPopupAttacher extends FooterPopupAttacher {

	public HeaderPopupAttacher(Activity attachedActivity, int layoutResId,
			int animationViewGroupId) {
		super(attachedActivity, layoutResId, animationViewGroupId);
	}

	public HeaderPopupAttacher(Activity attachedActivity, int layoutResId) {
		super(attachedActivity, layoutResId);
	}

	@Override
	protected void showAtLocation(PopupWindow pop, View parent, int gravity,
			int x, int y) {
		super.showAtLocation(pop, parent, Gravity.TOP, 0, 0);
	}
}