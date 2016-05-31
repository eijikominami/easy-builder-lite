
package jp.surbiton.widget;

import jp.surbiton.view.CustomView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.EditText;

/**
 * FrameLayoutに対応したEditTextです。
 *
 */
@SuppressLint("ViewConstructor")
public class CustomEditText extends EditText {

	/**
	 * EditTextを初期化する
	 *
	 * @param context		コンテクスト
	 * @param orientation	画面の向き
	 * @param width			幅（px）
	 * @param height		高さ（px）
	 * @param marginLeft	マージン（左）
	 * @param marginTop		マージン（上）
	 * @param textSize		テキストサイズ
	 * @param rrggbb		テキスト色
	 */
	public CustomEditText(Context context, int orientation, int width, int height, int marginLeft, int marginTop, int imageResource, float textSize, String rrggbb, int gravity) {
		super(context);
		setBackgroundResource(imageResource);
		setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)(textSize*DisplayManager.getMag(context, orientation)));
		setTextColor(Color.parseColor(rrggbb));
		setGravity(gravity);
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		setId(CustomView.generateId());
	}
}
