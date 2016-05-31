/**
 *
 */
package jp.surbiton.widget;

import jp.surbiton.view.CustomView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;

/**
 * FrameLayoutに対応したViewPagerです。
 *
 */
@SuppressLint("ViewConstructor")
public class CustomViewPager extends ViewPager {

	/**
	 * ViewPagerを初期化する
	 *
	 * @param context
	 * @param orientation
	 * @param width
	 * @param height
	 * @param marginLeft
	 * @param marginTop
	 */
	public CustomViewPager(Context context, int orientation, int width, int height, int marginLeft, int marginTop) {
		super(context);
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		setId(CustomView.generateId());
	}

}
