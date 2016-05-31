package jp.surbiton.widget;

import jp.surbiton.view.CustomView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.widget.Button;

/**
 * FrameLayoutに対応したButtonです。
 *
 */
@SuppressLint("ViewConstructor")
public class CustomButton extends Button {

	/** 背景画像リソースID */
	private int backgroundResource = -1;
	/** 背景色コード */
	private String colorString = "";

	/**
	 * 背景画像を指定してボタンを初期化する
	 *
	 * @param context			コンテクスト
	 * @param orientation		画面の向き
	 * @param width				幅（px）
	 * @param height			高さ（px）
	 * @param marginLeft		マージン（左）
	 * @param marginTop			マージン（上）
	 * @param colorString		色コード
	 */
	public CustomButton(Context context, int orientation, int width, int height, int marginLeft, int marginTop, String colorString) {
		super(context);

		this.colorString = colorString;
		setBackgroundColor(Color.parseColor(colorString));
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		setId(CustomView.generateId());
	}

	/**
	 * 背景色を指定してボタンを初期化する
	 *
	 * @param context			コンテクスト
	 * @param orientation		画面の向き
	 * @param width				幅（px）
	 * @param height			高さ（px）
	 * @param marginLeft		マージン（左）
	 * @param marginTop			マージン（上）
	 * @param imageResourceId	イメージのリソースID
	 */
	public CustomButton(Context context, int orientation, int width, int height, int marginLeft, int marginTop, int imageResourceId) {
		super(context);

		backgroundResource = imageResourceId;
		setBackgroundResource(imageResourceId);
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		setId(CustomView.generateId());
	}

	/**
	 * 背景画像を指定せずにボタンを初期化する
	 *
	 * @param context		コンテクスト
	 * @param orientation	画面の向き
	 * @param width			幅
	 * @param height		高さ
	 * @param marginLeft	マージン（左）
	 * @param marginTop		マージン（上）
	 */
	public CustomButton(Context context, int orientation, int width, int height, int marginLeft, int marginTop){
		super(context);

		setBackgroundColor(Color.argb(0, 0, 0, 0));
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		setId(CustomView.generateId());
	}

	/**
	 * ボタン押下時の画像を表示する
	 *
	 * @param olResourceId		押下時のボタンのリソースID
	 */
	public void setPushedBackgroundResource(int olResourceId){
		// 押下時の画像
		setBackgroundResource(olResourceId);
		// 通常の画像に戻す
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(backgroundResource!=-1){
					setBackgroundResource(backgroundResource);
				}else{
					setBackgroundColor(Color.argb(0, 0, 0, 0));
				}
			}
		}, 200);
	}

	/**
	 * ボタン押下時の色を表示する
	 *
	 * @param colorString		色コード
	 */
	public void setPushedBackgroundColor(String colorString){
		// 押下時の色
		setBackgroundColor(Color.parseColor(colorString));
		// 通常の色に戻す
		final String defaultColor = this.colorString;
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setBackgroundColor(Color.parseColor(defaultColor));
			}
		}, 200);
	}
}
