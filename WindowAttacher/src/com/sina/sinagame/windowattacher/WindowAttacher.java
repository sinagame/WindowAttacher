package com.sina.sinagame.windowattacher;

import android.view.View;

/**
 * 
 * @author liu_chonghui
 * 
 */
public interface WindowAttacher {

	static final int CLOSE_CURRENT_WINDOW_MESSAGE = 0x267018;

	View getContentView();

	void findViewByContentView(View contentView);

	void adjustContentView(View contentView);

	void initContentView();

	boolean isShowing();

	void toggle();

	void show();

	void closePop();

	void enable();

	void disable();

	boolean isEnable();

	void onOutsideClick();
}
