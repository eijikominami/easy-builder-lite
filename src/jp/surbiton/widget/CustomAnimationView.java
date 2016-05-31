
package jp.surbiton.widget;

import jp.surbiton.content.ResourcesUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;

/**
 * FrameLayoutに対応したAnimationViewです。
 *
 */
@SuppressLint("ViewConstructor")
public class CustomAnimationView extends CustomImageView {

	/** アニメーション */
	public AnimationDrawable animation;
	/** フレーム数 */
	protected int frameNum;
	/** 1フレームあたりの表示秒 */
	protected int duration;

	/**
	 * Viewを初期化する
	 *
	 * @param context		コンテクスト
	 * @param orientation	画面の向き
	 * @param width			幅（px）
	 * @param height		高さ（px）
	 * @param marginLeft	マージン（左）
	 * @param marginTop		マージン（上）
	 * @param imageStr		リソース名称
	 * @param isOneShot		1回だけの再生か否か
	 * @param frameNum		フレーム数
	 * @param duration		表示秒（ミリ秒）
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public CustomAnimationView(Context context, int orientation, int width, int height, int marginLeft, int marginTop, String imageStr, Boolean isOneShot, int frameNum, int duration) {
		super(context, orientation, width, height, marginLeft, marginTop);

		this.frameNum = frameNum;
		this.duration = duration;

		animation = new AnimationDrawable();
		for(int i=1; i<=frameNum; i++){
			// XXX リソースの連番処理
			// フレームにリソースを指定
			animation.addFrame(ResourcesUtils.getDrawable(context, imageStr + "_" + i, "drawable"), duration);
		}
		// 繰り返し設定
		animation.setOneShot(isOneShot);
		// Viewに設定
		if (Build.VERSION.SDK_INT >= 16) {
			setBackground(animation);
		}else{
			setBackgroundDrawable(animation);
		}
	}
}
