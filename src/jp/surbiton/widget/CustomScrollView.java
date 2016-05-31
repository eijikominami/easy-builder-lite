/**
 *
 */
package jp.surbiton.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ScrollView;

/**
 * FrameLayoutに対応したScrollViewです。
 *
 */
@SuppressLint("ViewConstructor")
public class CustomScrollView extends ScrollView {

	/**
	 * ScrollViewを初期化する
	 *
	 * @param context		コンテクスト
	 * @param orientation	画面の向き
	 * @param width			幅(px)
	 * @param height		高さ(px)
	 * @param marginLeft	マージン（左）
	 * @param marginTop		マージン（右）
	 */
	public CustomScrollView(Context context, int orientation, int width, int height, int marginLeft, int marginTop) {
		super(context);
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
	}

}
