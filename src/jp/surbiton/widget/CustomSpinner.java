
package jp.surbiton.widget;

import jp.surbiton.view.CustomView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * FrameLayoutに対応したSpinnerです。
 *
 */
@SuppressLint("ViewConstructor")
public class CustomSpinner extends Spinner {

	/**
	 * Spinnerを初期化する
	 *
	 * @param context			コンテクスト
	 * @param orientation		画面の向き
	 * @param width				幅(px)
	 * @param height			高さ(px)
	 * @param marginLeft		マージン（左）
	 * @param marginTop			マージン（右）
	 * @param imageResourceId	イメージのリソースID
	 * @param arrayResource		アイテムリスト
	 */
	public CustomSpinner(Context context, int orientation, int width, int height, int marginLeft, int marginTop, int imageResourceId, int arrayResource, int viewResource){
		super(context);
		if(imageResourceId!=-1)setBackgroundResource(imageResourceId);
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		// アイテムの設定
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, arrayResource, viewResource);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		setAdapter(adapter);
		setId(CustomView.generateId());
	}
}
