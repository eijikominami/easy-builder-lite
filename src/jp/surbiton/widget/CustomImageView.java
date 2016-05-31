
package jp.surbiton.widget;

import jp.surbiton.view.CustomView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * FrameLayoutに対応したImageViewです。
 *
 */
@SuppressLint("ViewConstructor")
public class CustomImageView extends ImageView {

	/**
	 * 背景画像を指定してViewを初期化する
	 *
	 * @param context			コンテクスト
	 * @param orientation		画面の向き
	 * @param width				幅（px）
	 * @param height			高さ（px）
	 * @param marginLeft		マージン（左）
	 * @param marginTop			マージン（上）
	 * @param imageResourceId	イメージのリソースID
	 */
	public CustomImageView(Context context, int orientation, int width, int height, int marginLeft, int marginTop, int imageResourceId) {
		super(context);

		setBackgroundResource(imageResourceId);
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		setScaleType(ScaleType.FIT_XY);
		setId(CustomView.generateId());
	}

	/**
	 * 背景画像を指定してViewを初期化する
	 *
	 * @param context			コンテクスト
	 * @param orientation		画面の向き
	 * @param width				幅（px）
	 * @param height			高さ（px）
	 * @param marginLeft		マージン（左）
	 * @param marginTop			マージン（上）
	 * @param drawable			イメージ
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public CustomImageView(Context context, int orientation, int width, int height, int marginLeft, int marginTop, Drawable drawable) {
		super(context);

		if (Build.VERSION.SDK_INT >= 16) {
			setBackground(drawable);
		}else{
			setBackgroundDrawable(drawable);
		}
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		setScaleType(ScaleType.FIT_XY);
		setId(CustomView.generateId());
	}

	/**
	 * 背景色を指定してViewを初期化する
	 *
	 * @param context			コンテクスト
	 * @param orientation		画面の向き
	 * @param width				幅（px）
	 * @param height			高さ（px）
	 * @param marginLeft		マージン（左）
	 * @param marginTop			マージン（上）
	 * @param colorString		色コード
	 */
	public CustomImageView(Context context, int orientation, int width, int height, int marginLeft, int marginTop, String colorString) {
		super(context);

		setBackgroundColor(Color.parseColor(colorString));
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		setScaleType(ScaleType.FIT_XY);
		setId(CustomView.generateId());
	}

	/**
	 * 背景画像を指定せずにViewを初期化する
	 *
	 * @param context		コンテクスト
	 * @param orientation	画面の向き
	 * @param width			幅（px）
	 * @param height		高さ（px）
	 * @param marginLeft	マージン（左）
	 * @param marginTop		マージン（上）
	 */
	public CustomImageView(Context context, int orientation, int width, int height, int marginLeft, int marginTop){
		super(context);

		setBackgroundColor(Color.argb(0, 0, 0, 0));
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		setScaleType(ScaleType.FIT_XY);
		setId(CustomView.generateId());
	}

	/**
	 * テキストを指定してViewを初期化する<br>
	 *
	 * @param context		コンテクスト
	 * @param width			幅（px）
	 * @param height		高さ（px）
	 * @param marginLeft	マージン（左）
	 * @param marginTop		マージン（右）
	 * @param text			テキスト
	 * @param rrggbb		テキスト色
	 * @param textSize		テキストサイズ
	 */
	public CustomImageView(Context context, int width, int height, int marginLeft, int marginTop, String text, String rrggbb, float textSize){
		super(context);

		int orientation = Configuration.ORIENTATION_LANDSCAPE;
		int gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;

		// 幅/大きさ
		int layoutWidth = (int)(width*DisplayManager.getMag(context, Configuration.ORIENTATION_PORTRAIT));
		int layoutHeight = (int)(height*DisplayManager.getMag(context, Configuration.ORIENTATION_PORTRAIT));

		// XXX　TextViewをImageViewに変換
		// テキストビュー
		TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(layoutWidth, layoutHeight));
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setTextColor(Color.parseColor(rrggbb));
        textView.setBackgroundColor(Color.TRANSPARENT);
        textView.setGravity(gravity);
        // ビットマップ
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        textView.layout(0, 0, width, height);
        textView.draw(c);
        // イメージビュー
        setLayoutParams(new CustomLayoutParams(context, orientation, height, width, marginTop, 720-marginLeft-width));
        // XXX 回転
        Matrix matrix = new Matrix();
        matrix.postRotate(-90.0f);
        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        setImageBitmap(rotateBitmap);
        setId(CustomView.generateId());
	}
}
