/**
 *
 */
package jp.surbiton.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.HorizontalScrollView;

/**
 * FrameLayoutに対応したHorizontalScrollViewです。
 *
 */
@SuppressLint("ViewConstructor")
public class CustomHorizontalScrollView extends HorizontalScrollView {

	/**
	 * HorizontalScrollViewを初期化する
	 *
	 * @param context		コンテクスト
	 * @param orientation	画面の向き
	 * @param width			幅(px)
	 * @param height		高さ(px)
	 * @param marginLeft	マージン（左）
	 * @param marginTop		マージン（右）
	 */
	public CustomHorizontalScrollView(Context context, int orientation, int width, int height, int marginLeft, int marginTop) {
		super(context);
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
	}

}
