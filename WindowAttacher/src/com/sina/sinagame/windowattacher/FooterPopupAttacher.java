package com.sina.sinagame.windowattacher;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

/**
 * Management Footer PopupWindow.
 * 
 * @author liu_chonghui
 * 
 */
public abstract class FooterPopupAttacher implements WindowAttacher {

	private PopupWindow popupWindow;
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

	/**
	 * @param attachedActivity
	 *            : "getActivity()".
	 * @param layoutResId
	 *            : "R.layout.anyone" layout to inflate.
	 * @param animationViewGroupId
	 *            : "R.id.anyviewgroup" in layout above.
	 */
	public FooterPopupAttacher(Activity attachedActivity, int layoutResId,
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
					"FooterPopupAttacher layoutResId argument illegal!");
		}
		this.activity = attachedActivity;
		this.layoutId = layoutResId;
		this.animaLayoutId = animationViewGroupId;
		initContentView();
	}

	public FooterPopupAttacher(Activity attachedActivity, int layoutResId) {
		this(attachedActivity, layoutResId, -1);
	}

	public void setStateListener(OnStateChangeListener listener) {
		this.mStateListener = listener;
	}

	public WindowAttacher setAnimation(int openAnimaResId, int closeAnimaResId) {
		openAnima = AnimationUtils.loadAnimation(getActivity(), openAnimaResId);
		closeAnima = AnimationUtils.loadAnimation(getActivity(),
				closeAnimaResId);
		return this;
	}

	public FooterPopupAttacher setAttacherView(View target) {
		attachedParent = target;
		if (attachedParent != null) {
			manualAttachedParent = true;
		}
		return this;
	}

	public View getAttacherView() {
		return attachedParent;
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

	protected void showAtLocation() {
		if (popupWindow != null && !popupWindow.isShowing()
				&& attachedParent != null) {
			showAtLocation(popupWindow, attachedParent, Gravity.BOTTOM, 0, 0);
			starAnimation();
		}
	}

	protected void showAtLocation(PopupWindow pop, View parent, int gravity,
			int x, int y) {
		pop.showAtLocation(parent, gravity, x, y);
	}

	protected void starAnimation() {
		if (contentView != null && bgAnimation) {
			contentView.startAnimation(createAlphaInAnimation());
		}
		if (animaLayout != null) {
			animaLayout.startAnimation(openAnima);
		}
	}

	protected Animation createAlphaInAnimation() {
		AlphaAnimation an = new AlphaAnimation(0, 1);
		an.setDuration(300L);
		return an;
	}

	protected Animation createAlphaOutAnimation() {
		AlphaAnimation an = new AlphaAnimation(1, 0);
		an.setDuration(300L);
		an.setFillAfter(true);
		return an;
	}

	public void findViewByContentView(View contentView) {
		// throw new UnsupportedOperationException(
		// "refineContentView() not implement in base class");
	}

	public void adjustContentView(View contentView) {
		contentView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onOutsideClick();
			}
		});
	}

	public void onOutsideClick() {
		closePop();
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
		popupWindow = new PopupWindow(contentView,
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
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

	public final void show() {
		adjustContentView(getContentView());
		initAnimation();
		initAttacherView();
		showAtLocation();
		onStateChangeToShow();
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

	public final void closePop() {
		if (popupWindow != null && popupWindow.isShowing()) {
			if (contentView != null && bgAnimation) {
				contentView.startAnimation(createAlphaOutAnimation());
			}
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
		// handler.sendMessageDelayed(message, 300L);
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

	boolean bgAnimation = true;

	public FooterPopupAttacher enableBgAnimation() {
		bgAnimation = true;
		return this;
	}

	public FooterPopupAttacher disableBgAnimation() {
		bgAnimation = false;
		return this;
	}

	public void enable() {
		popupWindow = new PopupWindow(contentView,
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	public void disable() {
		popupWindow = null;
	}

	public boolean isEnable() {
		return popupWindow != null;
	}

}
