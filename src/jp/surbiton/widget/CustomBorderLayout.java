
package jp.surbiton.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.widget.FrameLayout;

/**
 * Border付きFrameLayoutです。
 *
 */
@SuppressLint("ViewConstructor")
public class CustomBorderLayout extends FrameLayout {

	/** 左位置にボーダを設置する<br> Constant Value: 3 (0x00000003)  */
	public final static int BORDER_LEFT = 3;
	/** 右位置にボーダを設置する<br> Constant Value: 5 (0x00000005)  */
	public final static int BORDER_RIGHT = 5;
	/** 上位置にボーダを設置する<br> Constant Value: 48 (0x00000030)  */
	public final static int BORDER_TOP = 48;
	/** 下位置にボーダを設置する<br> Constant Value: 80 (0x00000050)  */
	public final static int BORDER_BOTTOM = 80;
	/** 上下左右にボーダを設置する<br> Constant Value: 136 (0x00000088)  */
	public final static int BORDER = 136;

	/**
	 * 背景画像とボーダを指定してViewを初期化する
	 *
	 * @param context			コンテクスト
	 * @param orientation		画面の向き
	 * @param width				幅（px）
	 * @param height			高さ（px）
	 * @param marginLeft		マージン（左）
	 * @param marginTop			マージン（上）
	 * @param imageResourceId	イメージのリソースID
	 * @param borderWidth		ボーダ幅(px)
	 * @param borderColorString	ボーダ色コード
	 * @param position			ポジション
	 */
	public CustomBorderLayout(Context context, int orientation, int width, int height, int marginLeft, int marginTop, int imageResourceId, int borderWidth, String borderColorString, int position) {
		super(context);
		int[] borderParams = getBorderPrams(borderWidth, position);
		this.setLayoutParams(new CustomLayoutParams(context, orientation, width+borderParams[0], height+borderParams[1], marginLeft+borderParams[2], marginTop+borderParams[3]));
		this.setBackgroundColor(Color.parseColor(borderColorString));
		// ImageView
		CustomImageView imageView = new CustomImageView(context, orientation, width, height, -borderParams[2], -borderParams[3], imageResourceId);
		this.addView(imageView);
	}

	/**
	 * 背景色とボーダを指定してViewを初期化する
	 *
	 * @param context				コンテクスト
	 * @param orientation			画面の向き
	 * @param width					幅（px）
	 * @param height				高さ（px）
	 * @param marginLeft			マージン（左）
	 * @param marginTop				マージン（上）
	 * @param backgroundColor		背景色コード
	 * @param borderWidth			ボーダ幅(px)
	 * @param borderColorString		ボーダ色コード
	 * @param position				ポジション
	 */
	public CustomBorderLayout(Context context, int orientation, int width, int height, int marginLeft, int marginTop, String backgroundColor, int borderWidth, String borderColorString, int position) {
		super(context);
		int[] borderParams = getBorderPrams(borderWidth, position);
		this.setLayoutParams(new CustomLayoutParams(context, orientation, width+borderParams[0], height+borderParams[1], marginLeft+borderParams[2], marginTop+borderParams[3]));
		this.setBackgroundColor(Color.parseColor(borderColorString));
		// ImageView
		CustomImageView imageView = new CustomImageView(context, orientation, width, height, -borderParams[2], -borderParams[3], backgroundColor);
		this.addView(imageView);
	}

	/**
	 * 背景色とボーダと文字を指定してViewを初期化する
	 *
	 * @param context				コンテクスト
	 * @param orientation			画面の向き
	 * @param width					幅(px)
	 * @param height				高さ(px)
	 * @param marginLeft			マージン（左）
	 * @param marginTop				マージン（上）
	 * @param textSize				テキストサイズ
	 * @param textColorString		テキスト色
	 * @param text					テキスト
	 * @param bgColorString			背景色
	 * @param gravity				位置
	 * @param borderWidth			ボーダ幅(px)
	 * @param borderColorString		ボーダ色コード
	 * @param position				ポジション
	 */
	public CustomBorderLayout(Context context, int orientation, int width, int height, int marginLeft, int marginTop, float textSize, String textColorString, String text, String bgColorString, int gravity, int borderWidth, String borderColorString, int position) {
		super(context);
		int[] borderParams = getBorderPrams(borderWidth, position);
		this.setLayoutParams(new CustomLayoutParams(context, orientation, width+borderParams[0], height+borderParams[1], marginLeft+borderParams[2], marginTop+borderParams[3]));
		this.setBackgroundColor(Color.parseColor(borderColorString));
		// TextView
		CustomTextView textView = new CustomTextView(context, orientation, width, height, -borderParams[2], -borderParams[3], textSize, textColorString, bgColorString, gravity);
		textView.setText(text);
		this.addView(textView);
	}

	/**
	 * ボーダパラメータを取得する
	 *
	 * @param borderWidth	ボーダ幅
	 * @param position		ポジション
	 * @return	ボーダパラメータ
	 */
	private int[] getBorderPrams(int borderWidth, int position){
		int w = borderWidth;
		switch (position) {
		// LRTB
		case BORDER: {
			int[] tmp = {2*w,2*w,-1*w,-1*w};// WHLT
			return tmp;
		}
		case BORDER_LEFT|BORDER_RIGHT|BORDER_TOP|BORDER_BOTTOM: {
			int[] tmp = {2*w,2*w,-1*w,-1*w};// WHLT
			return tmp;
		}
		// L_TB
		case BORDER_LEFT|BORDER_TOP|BORDER_BOTTOM: {
			int[] tmp = {1*w,2*w,-1*w,-1*w};// WHLT
			return tmp;
		}
		// LRT_
		case BORDER_LEFT|BORDER_RIGHT|BORDER_TOP: {
			int[] tmp = {2*w,1*w,-1*w,-1*w};// WHLT
			return tmp;
		}
		// LR_B
		case BORDER_LEFT|BORDER_RIGHT|BORDER_BOTTOM: {
			int[] tmp = {2*w,1*w,-1*w,0*w};// WHLT
			return tmp;
		}
		// _RTB
		case BORDER_RIGHT|BORDER_TOP|BORDER_BOTTOM: {
			int[] tmp = {1*w,2*w,0*w,-1*w};// WHLT
			return tmp;
		}
		// LR__
		case BORDER_LEFT|BORDER_RIGHT: {
			int[] tmp = {2*w,0*w,-1*w,0*w};// WHLT
			return tmp;
		}
		// L_T_
		case BORDER_LEFT|BORDER_TOP: {
			int[] tmp = {1*w,1*w,-1*w,-1*w};// WHLT
			return tmp;
		}
		// L__B
		case BORDER_LEFT|BORDER_BOTTOM: {
			int[] tmp = {1*w,1*w,-1*w,0*w};// WHLT
			return tmp;
		}
		// _RT_
		case BORDER_RIGHT|BORDER_TOP: {
			int[] tmp = {1*w,1*w,0*w,-1*w};// WHLT
			return tmp;
		}
		// _R_B
		case BORDER_RIGHT|BORDER_BOTTOM: {
			int[] tmp = {1*w,1*w,0*w,0*w};// WHLT
			return tmp;
		}
		// __TB
		case BORDER_TOP|BORDER_BOTTOM: {
			int[] tmp = {1*w,2*w,0*w,-1*w};// WHLT
			return tmp;
		}
		// L___
		case BORDER_LEFT: {
			int[] tmp = {1*w,0*w,-1*w,0*w};// WHLT
			return tmp;
		}
		// _R__
		case BORDER_RIGHT: {
			int[] tmp = {1*w,0*w,0*w,0*w};// WHLT
			return tmp;
		}
		// __T_
		case BORDER_TOP: {
			int[] tmp = {0*w,1*w,0*w,-1*w};// WHLT
			return tmp;
		}
		// ___B
		case BORDER_BOTTOM: {
			int[] tmp = {0*w,1*w,0*w,0*w};// WHLT
			return tmp;
		}
		default:{
			int[] tmp = {0*w,0*w,0*w,0*w};
			return tmp;
		}
		}
	}
}
