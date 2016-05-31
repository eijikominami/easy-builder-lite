/**
 *
 */
package jp.surbiton.widget;

import jp.surbiton.view.CustomView;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.widget.Switch;

/**
 * FrameLayoutに対応したSwitchです。
 *
 */
@SuppressLint("ViewConstructor")
@TargetApi(14) // XXX Apiの制限
public class CustomToggleSwitch extends Switch {

	/**
	 * ToggleSwitchを初期化する
	 *
	 * @param context		コンテクスト
	 * @param orientation	画面の向き
	 * @param width			幅(px)
	 * @param height		高さ(px)
	 * @param marginLeft	マージン(左)
	 * @param marginTop		マージン(上)
	 */
	public CustomToggleSwitch(Context context, int orientation, int width, int height, int marginLeft, int marginTop){
		super(context);
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		setId(CustomView.generateId());
	}

}
