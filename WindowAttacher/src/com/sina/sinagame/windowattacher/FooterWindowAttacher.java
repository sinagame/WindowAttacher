package com.sina.sinagame.windowattacher;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

/**
 * Management Footer Window Attacher (Fragment use only). WARNNING: USE
 * getAttachedActivity() instead of getActivity()
 * 
 * @author liu_chonghui
 * 
 */
public abstract class FooterWindowAttacher extends Fragment implements
		WindowAttacher {

	protected String TAG;

	private ViewGroup animaLayout;
	private View contentView;
	protected Animation openAnima;
	protected Animation closeAnima;
	private View attachedParent;
	private boolean manualAttachedParent = false;

	private Activity activity;
	private FragmentManager manager;
	private int layoutId;
	private int animaLayoutId = -1;

	protected OnStateChangeListener mStateListener;
	protected boolean canceledOnTouchOutside = false;

	public FooterWindowAttacher() {
		super();
	}

	public FooterWindowAttacher(Activity attachedActivity,
			Fragment attachedFragment, int layoutResId, int animationViewGroupId) {
		FragmentManager supportManager = null;
		if (attachedFragment != null) {
			supportManager = attachedFragment.getChildFragmentManager();
		}
		init(attachedActivity, supportManager, layoutResId,
				animationViewGroupId);
	}

	public FooterWindowAttacher(Activity attachedActivity,
			Fragment attachedFragment, int layoutResId) {
		FragmentManager supportManager = null;
		if (attachedFragment != null) {
			supportManager = attachedFragment.getChildFragmentManager();
		}
		init(attachedActivity, supportManager, layoutResId, -1);
	}

	public FooterWindowAttacher(Activity attachedActivity, int layoutResId) {
		FragmentManager supportManager = null;
		if (attachedActivity != null
				&& attachedActivity instanceof FragmentActivity) {
			FragmentActivity activity = (FragmentActivity) attachedActivity;
			supportManager = activity.getSupportFragmentManager();
		}
		init(attachedActivity, supportManager, layoutResId, -1);
	}

	public FooterWindowAttacher(Activity attachedActivity, int layoutResId,
			int animationViewGroupId) {
		FragmentManager supportManager = null;
		if (attachedActivity != null
				&& attachedActivity instanceof FragmentActivity) {
			FragmentActivity activity = (FragmentActivity) attachedActivity;
			supportManager = activity.getSupportFragmentManager();
		}
		init(attachedActivity, supportManager, layoutResId,
				animationViewGroupId);
	}

	protected void init(Activity attachedActivity,
			FragmentManager supportManager, int layoutResId,
			int animationViewGroupId) {
		if (attachedActivity == null) {
			throw new IllegalArgumentException(
					"attachedActivity == null, Your activity has been crashed!");
		}
		if (attachedActivity.isFinishing()) {
			throw new IllegalArgumentException(
					"attachedActivity.isFinishing() == true, Your activity has been finished!");
		}
		if (supportManager == null) {
			throw new IllegalArgumentException(
					"attachedActivity.getSupportFragmentManager() == true, Your activity has lost SupportFragmentManager!");

		}
		if (layoutResId == 0) {
			throw new IllegalArgumentException(
					"FooterWindowAttacher layoutResId argument illegal!");
		}
		this.TAG = getClass().getName();
		this.activity = attachedActivity;
		this.manager = supportManager;
		this.layoutId = layoutResId;
		this.animaLayoutId = animationViewGroupId;
		initContentView();
	}

	public void setStateListener(OnStateChangeListener listener) {
		this.mStateListener = listener;
	}

	public FooterWindowAttacher setAnimation(int openAnimaResId,
			int closeAnimaResId) {
		openAnima = AnimationUtils.loadAnimation(getAttachedActivity(),
				openAnimaResId);
		closeAnima = AnimationUtils.loadAnimation(getAttachedActivity(),
				closeAnimaResId);
		return this;
	}

	public FooterWindowAttacher setAttacherView(View target) {
		attachedParent = target;
		if (attachedParent != null) {
			manualAttachedParent = true;
		}
		return this;
	}

	protected Activity getAttachedActivity() {
		return activity;
	}

	public View getContentView() {
		return this.contentView;
	}

	protected void starAnimation() {
		if (contentView != null) {
			contentView.startAnimation(createAlphaInAnimation());
		}
		if (animaLayout != null) {
			animaLayout.startAnimation(openAnima);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		InputMethodManager inputMethod = (InputMethodManager) getAttachedActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethod.isActive()) {
			View focusView = getAttachedActivity().getCurrentFocus();
			if (focusView != null) {
				inputMethod.hideSoftInputFromWindow(focusView.getWindowToken(),
						0);
			}
		}

		adjustContentView(getContentView());

		ViewGroup decorView = (ViewGroup) getAttachedActivity().getWindow()
				.getDecorView();
		if (decorView != null) {
			decorView.addView(contentView);
		}
		starAnimation();
		// if (contentView != null) {
		// contentView.startAnimation(createAlphaInAnimation());
		// }
		// if (animaLayout != null) {
		// animaLayout.startAnimation(createTranslationInAnimation());
		// }
		return super.onCreateView(inflater, container, savedInstanceState);
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

	// R.layout.textsize_popsetting
	public void initContentView() {
		contentView = LayoutInflater.from(getAttachedActivity()).inflate(
				layoutId, null);
		if (contentView != null) {
			if (animaLayoutId > 0) {
				animaLayout = (ViewGroup) contentView
						.findViewById(animaLayoutId);
			}
			findViewByContentView(contentView);
		}
	}

	public void onOutsideClick() {
		closePop();
	}

	public boolean manualAttachedParent() {
		return manualAttachedParent;
	}

	protected void initAnimation() {
		if (openAnima == null) {
			openAnima = createTranslationInAnimation();
		}
		if (closeAnima == null) {
			closeAnima = createTranslationOutAnimation();
		}
	}

	public boolean isShowing() {
		return showing;
	}

	public void toggle() {
		if (isShowing()) {
			closePop();
		} else {
			show();
		}
	}

	boolean showing = false;

	public void show() {
		initAnimation();
		showAtLocation();
		onStateChangeToShow();
	}

	protected void showAtLocation() {
		showing = true;
		FragmentTransaction ft = manager.beginTransaction();
		ft.add(this, TAG);
		ft.addToBackStack(null);
		ft.commit();
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
		if (showing) {
			// if (animaLayout != null && closeAnima != null) {
			// closeAnima.setAnimationListener(new AnimationListener() {
			// @Override
			// public void onAnimationStart(Animation animation) {
			// }
			//
			// @Override
			// public void onAnimationRepeat(Animation animation) {
			// }
			//
			// @Override
			// public void onAnimationEnd(Animation animation) {
			// sendClosePopMessage();
			// }
			// });
			// animaLayout.startAnimation(closeAnima);
			// } else {
			sendClosePopMessage();
			// }
		}
	}

	protected void sendClosePopMessage() {
		Message message = new Message();
		message.what = CLOSE_CURRENT_WINDOW_MESSAGE;
		handler.sendMessage(message);
	}

	protected Fragment getSelf() {
		return this;
	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case CLOSE_CURRENT_WINDOW_MESSAGE:
				if (showing) {
					showing = false;
					try {
						manager.popBackStack();
						FragmentTransaction ft = manager.beginTransaction();
						ft.remove(getSelf());
						ft.commit();
					} catch (Exception e) {
						e.printStackTrace();
						showing = true;
						Message message = new Message();
						message.what = CLOSE_CURRENT_WINDOW_MESSAGE;
						handler.sendMessageDelayed(message, 1000L);
					}
				}
				break;
			}
			return false;
		}
	});

	protected Animation createTranslationInAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
				1, type, 0);
		an.setDuration(300L);
		return an;
	}

	protected Animation createAlphaInAnimation() {
		AlphaAnimation an = new AlphaAnimation(0, 1);
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

	protected Animation createAlphaOutAnimation() {
		AlphaAnimation an = new AlphaAnimation(1, 0);
		an.setDuration(300L);
		an.setFillAfter(true);
		return an;
	}

	@Override
	public void onDestroyView() {
		if (contentView != null) {
			contentView.startAnimation(createAlphaOutAnimation());
		}
		if (animaLayout != null && closeAnima != null) {
			animaLayout.startAnimation(closeAnima);
		}
		contentView.postDelayed(new Runnable() {
			@Override
			public void run() {
				ViewGroup decorView = (ViewGroup) getAttachedActivity()
						.getWindow().getDecorView();
				if (decorView != null) {
					decorView.removeView(contentView);
				}
				onStateChangeToDismiss();
			}
		}, 300L);
		super.onDestroyView();
	}

	public void enable() {
	}

	public void disable() {
	}

	public boolean isEnable() {
		return true;
	}

}
