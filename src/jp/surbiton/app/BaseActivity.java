package jp.surbiton.app;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import jp.surbiton.content.ResourcesUtils;
import jp.surbiton.data.ShareData;
import jp.surbiton.util.Log;
import jp.surbiton.widget.CustomAnimationView;
import jp.surbiton.widget.CustomButton;
import jp.surbiton.widget.CustomEditText;
import jp.surbiton.widget.CustomImageView;
import jp.surbiton.widget.CustomSpinner;
import jp.surbiton.widget.CustomTextView;
import jp.surbiton.widget.CustomWebView;
import jp.surbiton.widget.DisplayManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

/**
 * アクティビティの共通テンプレートクラスです。<br>
 * このクラスは以下の機能を実装しています。<br>
 * <br>
 * <ul>
 * <li>ProgresDialog, Toastの表示機能</li>
 * <li>SharedPreferencesによるデータの保存機能の呼び出し</li>
 * <li>onDestroy時にメモリ解放</li>
 * <li>音声再生機能</li>
 * <li>背景レイアウトの設定</li>
 * <li>Google Analytics</li>
 * <li>ログの出力</li>
 * <li>画面遷移時のアニメーションを無効化</li>
 * </ul>
 *
 *
 */
public abstract class BaseActivity extends Activity {

	/** 画面向き（縦向き固定） */
	protected final static int ORIENTATION = Configuration.ORIENTATION_PORTRAIT;

	/** 基本となるレイアウト */
	protected FrameLayout frameLayout;
	/** Shared Preferences */
	protected ShareData sd;

	/** 音声再生プレイヤー */
	protected MediaPlayer player;
	/** ダイアログ */
	protected ProgressDialog dialog;

	/**
	 * アクティビティの初期化を行う<br>
	 * Shared Preferencesの初期化を行う<br>
	 * Google Analyticsを設定する<br>
	 * setLayout()を呼び出す
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// デバッグ用
		Log.v(getClass(), "onCreate");
		// Shared Preferencesの初期化
		sd = new ShareData(getApplicationContext());
		// Google Analytics
		Tracker easyTracker = EasyTracker.getInstance(this);
		easyTracker.set(Fields.SCREEN_NAME, getClass().getName());
		easyTracker.send(MapBuilder.createAppView().build());
		// レイアウトの設定
		FrameLayout rootView = new FrameLayout(this);
		frameLayout = new FrameLayout(this);
		frameLayout.setLayoutParams(getFrameLayoutParams());
		frameLayout.requestLayout();
		setLayout(frameLayout);
		rootView.addView(frameLayout);
		setContentView(rootView);
	}

	@Override
	protected void onResume() {
		// デバッグ用
		Log.v(getClass(), "onResume");
		super.onResume();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// デバッグ用
		Log.v(getClass(), "onNewIntent");
		super.onNewIntent(intent);
	}

	@Override
	protected void onPause() {
		// デバッグ用
		Log.v(getClass(), "onPause");
		if(dialog!=null){
			dialog.dismiss();
			dialog=null;
		}
		super.onPause();
	}

	/**
	 * アクティビティの終了処理を行う<br>
	 * 音声プレイヤーの停止、各Viewの終了処理を行う
	 */
	@Override
	protected void onDestroy() {
		// デバッグ用
		Log.v(getClass(), "onDestroy");
		if(player!=null){
			player.stop();
			player=null;
		}
		if(dialog!=null){
			dialog.dismiss();
			dialog=null;
		}
		disposeView(frameLayout);
		super.onDestroy();
	}

	/**
	 * ビューの終了処理を行う
	 *
	 * @param view 対象のビュー
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void disposeView(View view){
		// android4対応
		if(view instanceof FrameLayout){
			FrameLayout layout = (FrameLayout) view;
			if (Build.VERSION.SDK_INT >= 16) {
				layout.setBackground(null);
			}else{
				layout.setBackgroundDrawable(null);
			}
		}else if(view instanceof CustomAnimationView){
			CustomAnimationView animationView = (CustomAnimationView) view;
			if (Build.VERSION.SDK_INT >= 16) {
				animationView.setBackground(null);
			}else{
				animationView.setBackgroundDrawable(null);
			}
		}else if(view instanceof CustomButton){
			CustomButton button = (CustomButton) view;
			if (Build.VERSION.SDK_INT >= 16) {
				button.setBackground(null);
			}else{
				button.setBackgroundDrawable(null);
			}
		}else if(view instanceof CustomEditText){
			CustomEditText editText = (CustomEditText) view;
			if (Build.VERSION.SDK_INT >= 16) {
				editText.setBackground(null);
			}else{
				editText.setBackgroundDrawable(null);
			}
		}else if(view instanceof CustomImageView){
			CustomImageView imageView = (CustomImageView) view;
			if (Build.VERSION.SDK_INT >= 16) {
				imageView.setBackground(null);
			}else{
				imageView.setBackgroundDrawable(null);
			}
		}else if(view instanceof CustomSpinner){
			CustomSpinner spinner = (CustomSpinner) view;
			if (Build.VERSION.SDK_INT >= 16) {
				spinner.setBackground(null);
			}else{
				spinner.setBackgroundDrawable(null);
			}
		}else if(view instanceof CustomTextView){
			CustomTextView textView = (CustomTextView) view;
			if (Build.VERSION.SDK_INT >= 16) {
				textView.setBackground(null);
			}else{
				textView.setBackgroundDrawable(null);
			}
		}else if(view instanceof CustomWebView){
			CustomWebView webView = (CustomWebView) view;
			if (Build.VERSION.SDK_INT >= 16) {
				webView.setBackground(null);
			}else{
				webView.setBackgroundDrawable(null);
			}
		}
		if (Build.VERSION.SDK_INT >= 16) {
			view.setBackground(null);
		}else{
			view.setBackgroundDrawable(null);
		}
		if(view instanceof ViewGroup){
			ViewGroup group = (ViewGroup) view;
			for(int i=0; i<group.getChildCount(); i++){
				disposeView(group.getChildAt(i));
			}
		}
	}

	@Override
	protected void onStart() {
		// デバッグ用
		Log.v(getClass(), "onStart");
		EasyTracker.getInstance(this).activityStart(this);
		super.onStart();
	}

	@Override
	protected void onStop() {
		// デバッグ用
		Log.v(getClass(), "onStop");
		EasyTracker.getInstance(this).activityStop(this);
		super.onStop();
	}

	/**
	 * 音声を再生する
	 *
	 * @param resourceId	再生する音声のリソースID
	 */
	protected void playAudio(int resourceId){
		AudioManager audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		// XXX マナーモード判定
		if (audioManager.getRingerMode() != AudioManager.MODE_NORMAL) {
			if(player!=null){
				if(player.isPlaying()){
					player.stop();
				}
				player.reset();
				player.release();
				player=null;
			}
			player = MediaPlayer.create(getApplicationContext(), resourceId);
			player.start();
		}
	}

	/**
	 * プログレスダイアログを表示する
	 *
	 * @param title		タイトル
	 * @param message	メッセージ
	 */
	protected void showProgressDialog(String title, String message){
		if(dialog!=null){
			dialog.dismiss();
			dialog=null;
		}
		dialog = new ProgressDialog(this);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.show();
	}

	/**
	 * プログレスダイアログを表示する
	 *
	 * @param titleId		タイトルのリソースID
	 * @param messageId		メッセージのリソースID
	 */
	protected void showProgressDialog(int titleId, int messageId){
		showProgressDialog(getResources().getString(titleId), getResources().getString(messageId));
	}

	/**
	 * プログレスダイアログを隠す
	 */
	protected void dismissProgressDialog(){
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
	protected void showToast(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Toast表示を行う
	 *
	 * @param resourceId 表示テキストのリソースID
	 */
	protected void showToast(int resourceId){
		Toast.makeText(this, getResources().getString(resourceId), Toast.LENGTH_SHORT).show();
	}

	/**
	 * アラートダイアログを表示する
	 *
	 * @param message			メッセージ
	 * @param audioResourceId	再生音声のリソースID
	 */
	protected void showAlertDialog(String message, int audioResourceId){
		// 音声
		if(audioResourceId!=-1)playAudio(audioResourceId);
		// ポップアップ
		new AlertDialog.Builder(BaseActivity.this) // XXX 親のActivityを指定
		.setMessage(message)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}

	/**
	 * アラートダイアログを表示する
	 *
	 * @param msgResourceId		メッセージのリソースID
	 * @param audioResourceId	再生音声のリソースID
	 */
	protected void showAlertDialog(int msgResourceId, int audioResourceId){
		// 音声
		if(audioResourceId!=-1)playAudio(audioResourceId);
		// ポップアップ
		new AlertDialog.Builder(BaseActivity.this) // XXX 親のActivityを指定
		.setMessage(msgResourceId)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}

	/**
	 * アラートダイアログを表示する
	 *
	 * @param titleResourceId	タイトルのリソースID
	 * @param msgResourceId		メッセージのリソースID
	 * @param audioResourceId	再生音声のリソースID
	 */
	protected void showAlertDialog(int titleResourceId, int msgResourceId, int audioResourceId){
		// 音声
		if(audioResourceId!=-1)playAudio(audioResourceId);
		// ポップアップ
		new AlertDialog.Builder(BaseActivity.this) // XXX 親のActivityを指定
		.setTitle(titleResourceId)
		.setMessage(msgResourceId)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}

	/**
	 * 背景レイアウトを設定する<br>
	 * 背景レイアウトは背景イメージとメインイメージで構成される<br>
	 * リソースIDに-1を指定するとイメージは設定されない
	 *
	 * @param backgroundResourceId	背景イメージのリソースID
	 * @param mainResourceId		メインイメージのリソースID
	 */
	protected void setBackground(int backgroundResourceId, int mainResourceId){
		int resourceId = ResourcesUtils.getResourcesID(getApplicationContext(), "pixelsperLine", "integer");
		int pixelsperLine = getResources().getInteger(resourceId);

		if(backgroundResourceId!=-1){
			CustomImageView backgroundView = new CustomImageView(this, ORIENTATION, (int)(pixelsperLine*0.5625), pixelsperLine, 0, 0, backgroundResourceId);
			frameLayout.addView(backgroundView);
		}
		if(mainResourceId!=-1){
			CustomImageView mainView = new CustomImageView(this, ORIENTATION, (int)(pixelsperLine*0.5625), pixelsperLine, 0, 0, mainResourceId);
			frameLayout.addView(mainView);
		}
	}

	/**
	 * アニメーション効果をつけて指定したアクティビティに遷移する<br>
	 *
	 * @param intent		インテント
	 * @param enterAnimResourceId		アクティビティ表示時のアニメーションリソースID
	 * @param exitAnimResourceId		アクティビティ終了時のアニメーションリソースID
	 */
	public void startActivity(Intent intent, int enterAnimResourceId, int exitAnimResourceId){
		super.startActivity(intent);
		overridePendingTransition(enterAnimResourceId, exitAnimResourceId);
	}

	/**
	 * アニメーション効果を入れずに指定したアクティビティに遷移する<br>
	 *
	 * @param intent		インテント
	 */
	@Override
	public void startActivity(Intent intent) {
		try {
			super.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(getClass(), e.getMessage());
		}
		// XXX アニメーション無効化
		overridePendingTransition(0, 0);
	}

	/**
	 * アニメーション効果をつけて現在のアクティビティを終了する
	 *
	 * @param enterAnimResourceId		アクティビティ表示時のアニメーションリソースID
	 * @param exitAnimResourceId		アクティビティ終了時のアニメーションリソースID
	 */
	public void finish(int enterAnimResourceId, int exitAnimResourceId){
		super.finish();
		overridePendingTransition(enterAnimResourceId, exitAnimResourceId);
	}

	/**
	 * アニメーション効果を入れずに現在のアクティビティを終了する<br>
	 */
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, 0);
	}

	/**
	 * リソースから文字列を取得する
	 *
	 * @param resourceId	リソース
	 * @return	文字列
	 */
	public String getResourceString(int resourceId){
		return getResources().getString(resourceId);
	}

	/**
	 * FrameLayoutのレイアウトパラメータを取得する
	 *
	 * @return　FrameLayoutのレイアウトパラメータ
	 */
	protected LayoutParams getFrameLayoutParams(){
		int[] displayStd = DisplayManager.getDisplayStandard(getApplicationContext(), ORIENTATION);
		int[] margin = DisplayManager.getMargin(getApplicationContext(), ORIENTATION);
		LayoutParams params = new LayoutParams((int)(displayStd[0]*DisplayManager.getMag(this, ORIENTATION)), (int)(displayStd[1]*DisplayManager.getMag(this, ORIENTATION)));
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.setMargins(margin[0], margin[1], 0, 0);

		return params;
	}

	/**
	 * レイアウトを設定する<br>
	 * frameLayoutに対してViewを追加する処理を記述すること<br>
	 * frameLayout.addView(追加するView)
	 *
	 * @param frameLayout	レイアウト
	 */
	abstract protected void setLayout(FrameLayout frameLayout);
}
