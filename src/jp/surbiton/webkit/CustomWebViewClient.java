/**
 *
 */
package jp.surbiton.webkit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * 読み込み中にプログレスダイアログを表示したり、
 * エラー時にポップアップ表示を行うWebViewClientです。
 *
 */
public class CustomWebViewClient extends WebViewClient{

	/** コンテクスト */
	private Context context;
	/** ダイアログ */
	private ProgressDialog dialog;

	/**
	 * CustomWebViewClientを初期化する
	 *
	 * @param context	コンテクスト（アクティビティ）
	 * @param dialog	ダイアログ
	 */
	public CustomWebViewClient(Context context, ProgressDialog dialog){
		this.context = context;
		this.dialog = dialog;
	}

	@Override
	public void onPageStarted(WebView view, String url,
			Bitmap favicon) {
		// プログレスダイアログ
		showProgressDialog("読み込み中", "しばらくお待ちください...");
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		dismissProgressDialog();
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		dismissProgressDialog();
		showToast("読み込みエラー");
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view,
			String url) {
		// XXX WebViewを使用するときの注意点
		// shouldOverrideUrlLoadingをfalseにしないと標準ブラウザが開いてしまう
		// また、flag_activity_new_taskのエラーが発生してしまう
		return false;
	}

	/**
	 * プログレスダイアログを表示する
	 *
	 * @param dialog	ダイアログ
	 * @param title		タイトル
	 * @param message	メッセージ
	 */
	private void showProgressDialog(String title, String message){
		if(dialog!=null){
			dialog.dismiss();
			dialog=null;
		}
		dialog = new ProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// XXX アクティビティが終了しているのにプログレスダイアログを表示しようとすると
		//     android.view.WindowManager$BadTokenException: Unable to add window --
		//     token android.os.BinderProxy@XXX is not valid; is your activity running?エラーが発生する
		if(!((Activity)context).isFinishing()){
			dialog.show();
		}
	}

	/**
	 * プログレスダイアログを隠す
	 *
	 * @param dialog	ダイアログ
	 */
	private void dismissProgressDialog(){
		if(dialog!=null){
			if(dialog.isShowing()){
				dialog.dismiss();
				dialog=null;
			}
		}
	}

	/**
	 * Toast表示を行う
	 *
	 * @param text 表示テキスト
	 */
	private void showToast(String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

}
