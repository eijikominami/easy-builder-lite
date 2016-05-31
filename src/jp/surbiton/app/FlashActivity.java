/**
 *
 */
package jp.surbiton.app;

import java.lang.reflect.InvocationTargetException;

import jp.surbiton.view.CustomView;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * FLASHを実装したアクティビティの共通テンプレートクラスです。
 *
 */
public abstract class FlashActivity extends BaseActivity {

	/** WebView */
	private WebView webView;
	/** フルスクリーンフラグ */
	protected boolean fullscreenFlag = false;
	/** フルスクリーン用カスタムビュー格納レイアウト */
	private FullscreenHolder fullscreenHolder;
	/** フルスクリーン用カスタムビュー */
	private View customView;
	/** フルスクリーン用カスタムビューコールバック */
	private WebChromeClient.CustomViewCallback customViewCallback;
	/** 端末の向き */
	private int originalOrientation;

	@Override
	protected void onPause() {
		try {
			WebView.class.getMethod("onPause").invoke(webView);
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		}
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(fullscreenFlag){
				clearCustomView();
			}
		}
		return super.onKeyDown(keyCode, event);
	};

	/**
	 * カスタムプレイヤーを設定する
	 *
	 * @param webView	ウェブビュー
	 */
	protected void setCustomPlayer(final WebView webView){
		// 登録
		this.webView = webView;
		// FLVの再生
		webView.setWebChromeClient(new WebChromeClient(){
			public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
				super.onShowCustomView(view, callback);
				fullscreenFlag = true;
				// Androidのバージョン情報を取得
				if (Build.VERSION.SDK_INT >= 14) {
					if (view instanceof FrameLayout) {
						webView.addView(view, new FrameLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.MATCH_PARENT,
							Gravity.CENTER
						));
						webView.setVisibility(View.VISIBLE);
					}
				}
				if (customView != null) {
					callback.onCustomViewHidden();
					return;
				}
				originalOrientation = getResources().getConfiguration().orientation;
				FrameLayout decor = (FrameLayout) getWindow().getDecorView();
				webView.setVisibility(View.INVISIBLE);
				fullscreenHolder = new FullscreenHolder(getApplicationContext());
				webView.removeAllViews();
				fullscreenHolder.addView(view, new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				decor.addView(fullscreenHolder, new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				customView = view;
				setFullscreen(true);

				customViewCallback = callback;

				setRequestedOrientation(getResources().getConfiguration().orientation);
			}

			@Override
			public void onHideCustomView() {
				clearCustomView();
			}
		});
		webView.setId(CustomView.generateId());
	}

   /**
    * 動画再生カスタムビューにフルスクリーンを設定する
    *
    * @param enabled フルスクリーンにするか
    */
	private void setFullscreen(boolean enabled) {
		Window windows = getWindow();
		WindowManager.LayoutParams layoutParams = windows.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		if (enabled) {
			layoutParams.flags |= bits;
		} else {
			layoutParams.flags &= ~bits;
		}
		windows.setAttributes(layoutParams);
	}

	/**
	 * 動画再生カスタムビューを削除する
	 */
	protected void clearCustomView() {
		webView.setVisibility(View.VISIBLE);
		if (customView == null) {
			return;
		}
		setFullscreen(false);
		FrameLayout decor = (FrameLayout) getWindow().getDecorView();
		decor.removeView(fullscreenHolder);
		fullscreenHolder = null;
		customView = null;
		customViewCallback.onCustomViewHidden();
		// Show the content view.
		setRequestedOrientation(originalOrientation);
		fullscreenFlag = false;
		// ステータスバーを非表示
		// これをかかないとはみ出る
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

    /**
     * フルスクリーン用カスタムビュー格納レイアウトです。
     */
 	private static class FullscreenHolder extends FrameLayout {
 		public FullscreenHolder(Context context) {
 			super(context);
 			setBackgroundColor(Color.BLACK);
 		}
 		@Override
 		public boolean onTouchEvent(MotionEvent event) {
 			return true;
 		}
 	}
}
