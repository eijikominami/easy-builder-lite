
package jp.surbiton.widget;

import jp.surbiton.view.CustomView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * FrameLayoutに対応したTextViewです。
 *
 */
@SuppressLint("ViewConstructor")
public class CustomTextView extends TextView {

	/** 文字色　*/
	private String textColorString;

	/**
	 * TextViewを初期化する
	 *
	 * @param context			コンテクスト
	 * @param orientation		画面の向き
	 * @param width				幅(px)
	 * @param height			高さ(px)
	 * @param marginLeft		マージン（左）
	 * @param marginTop			マージン（上）
	 * @param textSize			テキストサイズ
	 * @param textColorString	テキスト色
	 * @param gravity			位置
	 */
	public CustomTextView(Context context, int orientation, int width, int height, int marginLeft, int marginTop, float textSize, String textColorString, int gravity) {
		super(context);
		this.textColorString = textColorString;
		setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)(textSize*DisplayManager.getMag(context, orientation)));
		setTextColor(Color.parseColor(textColorString));
		setGravity(gravity);
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		setId(CustomView.generateId());
	}

	/**
	 * TextViewを背景色付きで初期化する
	 *
	 * @param context			コンテクスト
	 * @param orientation		画面の向き
	 * @param width				幅(px)
	 * @param height			高さ(px)
	 * @param marginLeft		マージン（左）
	 * @param marginTop			マージン（上）
	 * @param textColorString	テキスト色
	 * @param bgColorString		背景色
	 * @param gravity			位置
	 */
	public CustomTextView(Context context, int orientation, int width, int height, int marginLeft, int marginTop, float textSize, String textColorString, String bgColorString, int gravity) {
		super(context);
		this.textColorString = textColorString;
		setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)(textSize*DisplayManager.getMag(context, orientation)));
		setTextColor(Color.parseColor(textColorString));
		setBackgroundColor(Color.parseColor(bgColorString));
		setGravity(gravity);
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		setId(CustomView.generateId());
	}

	/**
	 * テキストビュー押下時の色を表示する
	 *
	 * @param colorString		色コード
	 */
	public void setPushedBackgroundColor(String colorString){
		// 押下時の色
		setTextColor(Color.parseColor(colorString));
		// 通常の色に戻す
		final String defaultColor = this.textColorString;
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setTextColor(Color.parseColor(defaultColor));
			}
		}, 200);
	}
}
