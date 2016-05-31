
package jp.surbiton.widget;

import jp.surbiton.view.CustomView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * FrameLayoutに対応したWebViewです。
 *
 */
@SuppressLint("ViewConstructor")
public class CustomWebView extends WebView {

	/**
	 * WebViewを初期化する
	 *
	 * @param context		コンテクスト
	 * @param orientation	画面の向き
	 * @param width			幅(px)
	 * @param height		高さ(px)
	 * @param marginLeft	マージン（左）
	 * @param marginTop		マージン（右）
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	public CustomWebView(Context context, int orientation, int width, int height, int marginLeft, int marginTop){
		super(context);
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		// 各種設定
		// XXX Javascriptを有効化
		getSettings().setJavaScriptEnabled(true);
		// XXX DomStorageを有効化
		getSettings().setDomStorageEnabled(true);
		// XXX スクロールの余白を消す
		setVerticalScrollbarOverlay(true);
		// XXX プラグインの有効化
		// android4対応
		if (Build.VERSION.SDK_INT >= 8 && Build.VERSION.SDK_INT < 18) {
			getSettings().setPluginState(WebSettings.PluginState.ON);
		}
		// XXX WebViewを使用するときの注意点
		// shouldOverrideUrlLoadingをfalseにしないと標準ブラウザが開いてしまう
		// また、flag_activity_new_taskのエラーが発生してしまう
		setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
		setId(CustomView.generateId());
	}
}
