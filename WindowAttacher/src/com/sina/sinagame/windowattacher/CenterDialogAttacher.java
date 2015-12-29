package com.sina.sinagame.windowattacher;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

/**
 * 
 * Management Center Dialog.
 * 
 * @author liu_chonghui
 * 
 */
public abstract class CenterDialogAttacher implements WindowAttacher {

	private Dialog popupWindow;
	private ViewGroup animaLayout;
	private View contentView;
	protected Animation openAnima;
	protected Animation closeAnima;
	private View attachedParent;
	private boolean manualAttachedParent = false;

	private Activity activity;
	private int layoutId;
	private int animaLayoutId = -1;

	protected OnStateChangeListener mStateListener;
	protected boolean canceledOnTouchOutside = false;

	public CenterDialogAttacher(Activity attachedActivity, int layoutResId,
			int animationViewGroupId) {
		if (attachedActivity == null) {
			throw new IllegalArgumentException(
					"attachedActivity == null, Your activity has been crashed!");
		}
		if (attachedActivity.isFinishing()) {
			throw new IllegalArgumentException(
					"attachedActivity.isFinishing() == true, Your activity has been finished!");
		}
		if (layoutResId == 0) {
			throw new IllegalArgumentException(
					"CenterDialogAttacher layoutResId argument illegal!");
		}
		this.activity = attachedActivity;
		this.layoutId = layoutResId;
		this.animaLayoutId = animationViewGroupId;
		initContentView();
	}

	public CenterDialogAttacher(Activity attachedActivity, int layoutResId) {
		this(attachedActivity, layoutResId, -1);
	}

	public void setStateListener(OnStateChangeListener listener) {
		this.mStateListener = listener;
	}

	public CenterDialogAttacher setAnimation(int openAnimaResId,
			int closeAnimaResId) {
		openAnima = AnimationUtils.loadAnimation(getActivity(), openAnimaResId);
		closeAnima = AnimationUtils.loadAnimation(getActivity(),
				closeAnimaResId);
		return this;
	}

	public CenterDialogAttacher setAttacherView(View target) {
		attachedParent = target;
		if (attachedParent != null) {
			manualAttachedParent = true;
		}
		return this;
	}

	protected Activity getAttachedActivity() {
		return getActivity();
	}

	protected Activity getActivity() {
		return activity;
	}

	public View getContentView() {
		return this.contentView;
	}

	protected void starAnimation() {
		if (animaLayout != null) {
			animaLayout.startAnimation(openAnima);
		}
	}

	public void findViewByContentView(View contentView) {
		// throw new UnsupportedOperationException(
		// "refineContentView() not implement in base class");
	}

	public void adjustContentView(View contentView) {
		popupWindow.setCanceledOnTouchOutside(true);
		canceledOnTouchOutside = true;
	}

	// R.layout.textsize_popsetting
	public void initContentView() {
		contentView = LayoutInflater.from(getActivity())
				.inflate(layoutId, null);
		if (contentView != null) {
			if (animaLayoutId > 0) {
				animaLayout = (ViewGroup) contentView
						.findViewById(animaLayoutId);
			}
			findViewByContentView(contentView);
		}
		popupWindow = new Dialog(activity, R.style.windowattacher_CenterDialogAttacher) {
			@Override
			public boolean dispatchTouchEvent(MotionEvent ev) {
				Rect dialogBounds = new Rect();
				getWindow().getDecorView().getHitRect(dialogBounds);
				if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())
						&& ev.getAction() == MotionEvent.ACTION_DOWN) {
					if (canceledOnTouchOutside) {
						onOutsideClick();
					}
				}
				return super.dispatchTouchEvent(ev);
			}
		};
		// popupWindow.getWindow().setBackgroundDrawableResource(
		// android.R.color.transparent);
		// popupWindow.getWindow().addFlags(
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// popupWindow.getWindow().addFlags(
		// WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		// WindowManager.LayoutParams params = popupWindow.getWindow()
		// .getAttributes();
		// params.dimAmount = 0;
		// popupWindow.getWindow().setAttributes(params);

		popupWindow.setCanceledOnTouchOutside(false);
		canceledOnTouchOutside = false;
		popupWindow.setContentView(contentView);
	}

	public void onOutsideClick() {

	}

	public boolean manualAttachedParent() {
		return manualAttachedParent;
	}

	protected void initAttacherView() {
		if (attachedParent == null) {
			try {
				attachedParent = ((ViewGroup) activity
						.findViewById(android.R.id.content)).getChildAt(0);
			} catch (Exception e) {
				e.printStackTrace();
				attachedParent = null;
			}
		}
	}

	protected void initAnimation() {
		if (openAnima == null) {
			openAnima = createTranslationInAnimation();
		}
		if (closeAnima == null) {
			closeAnima = createTranslationOutAnimation();
		}
	}

	protected Animation createTranslationInAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
				1, type, 0);
		an.setDuration(300L);
		return an;
	}

	protected Animation createTranslationOutAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
				0, type, 1);
		an.setDuration(300L);
		an.setFillAfter(true);
		return an;
	}

	public boolean isShowing() {
		if (popupWindow != null) {
			return popupWindow.isShowing();
		}
		return false;
	}

	public void toggle() {
		if (popupWindow != null) {
			if (isShowing()) {
				closePop();
			} else {
				show();
			}
		}
	}

	public void show() {
		adjustContentView(getContentView());
		// initAnimation();
		// initAttacherView();
		showAtLocation();
		onStateChangeToShow();
	}

	protected void showAtLocation() {
		if (popupWindow != null && !popupWindow.isShowing()) {
			showAtLocation(popupWindow, attachedParent, Gravity.BOTTOM, 0, 0);
			starAnimation();
		}
	}

	protected void showAtLocation(Dialog pop, View parent, int gravity, int x,
			int y) {
		pop.show();
	}

	protected void onStateChangeToShow() {
		if (mStateListener != null) {
			mStateListener.onStateChanged(true);
		}
	}

	protected void onStateChangeToDismiss() {
		if (mStateListener != null) {
			mStateListener.onStateChanged(false);
		}
	}

	public void closePop() {
		if (popupWindow != null && popupWindow.isShowing()) {
			if (animaLayout != null && closeAnima != null) {
				closeAnima.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						sendClosePopMessage();
					}
				});
				animaLayout.startAnimation(closeAnima);
			} else {
				sendClosePopMessage();
			}
		}
	}

	protected void sendClosePopMessage() {
		Message message = new Message();
		message.what = CLOSE_CURRENT_WINDOW_MESSAGE;
		handler.sendMessage(message);
	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case CLOSE_CURRENT_WINDOW_MESSAGE:
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
					onStateChangeToDismiss();
				}
				break;
			}
			return false;
		}
	});

	public void enable() {
		popupWindow = new Dialog(activity, R.style.windowattacher_CenterDialogAttacher) {
			@Override
			public boolean dispatchTouchEvent(MotionEvent ev) {
				Rect dialogBounds = new Rect();
				getWindow().getDecorView().getHitRect(dialogBounds);
				if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())
						&& ev.getAction() == MotionEvent.ACTION_DOWN) {
					onOutsideClick();
				}
				return super.dispatchTouchEvent(ev);
			}
		};

		popupWindow.setCanceledOnTouchOutside(false);
		canceledOnTouchOutside = false;
		popupWindow.setContentView(contentView);
	}

	public void disable() {
		popupWindow = null;
	}

	public boolean isEnable() {
		return popupWindow != null;
	}

}
