
package jp.surbiton.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
// XXX LayoutParamsを使用する際の注意
// FrameLayoutのレイアウトを設定する際は、android.widget.FrameLayout.LayoutParamsを指定すること
import android.widget.FrameLayout.LayoutParams;

/**
 * レイアウトパラメータを設定します。
 *
 */
@SuppressLint("ViewConstructor")
public class CustomLayoutParams extends LayoutParams {

	/**
	 * LayoutParamsを初期化する
	 *
	 * @param context		コンテクスト
	 * @param orientation	画面の向き
	 * @param width			横幅（px）
	 * @param height		高さ（px）
	 * @param marginLeft	マージン（左）
	 * @param marginTop		マージン（上）
	 */
	public CustomLayoutParams(Context context, int orientation, int width, int height, int marginLeft, int marginTop) {
		super((int)(width*DisplayManager.getMag(context, orientation)), (int)(height*DisplayManager.getMag(context, orientation)));
		gravity = Gravity.TOP | Gravity.LEFT;
		setMargins((int)(marginLeft*DisplayManager.getMag(context, orientation)), (int)(marginTop*DisplayManager.getMag(context, orientation)), 0, 0);
	}

}
