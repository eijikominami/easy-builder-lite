
package jp.surbiton.widget;

import jp.surbiton.content.ResourcesUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

/**
 * ディスプレイの情報を提供します。
 *
 */
public class DisplayManager {

	/**
	 * 画面の横幅を取得する
	 *
	 * @param  context	コンテクスト
	 * @return 画面の横幅(px)
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getDisplayWidth(Context context){

		Display dislpay = ((WindowManager) context.getSystemService(android.content.Context.WINDOW_SERVICE)).getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= 13) {
			Point point = new Point();
			dislpay.getSize(point);
			return point.x;
		}else{
			return dislpay.getWidth();
		}
	}

	/**
	 * 画面の高さを取得する
	 *
	 * @param  context	コンテクスト
	 * @return 画面の高さ(px)
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getDisplayHeight(Context context){

		Display dislpay = ((WindowManager) context.getSystemService(android.content.Context.WINDOW_SERVICE)).getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= 13) {
			Point point = new Point();
			dislpay.getSize(point);
			return point.y;
		}else{
			return dislpay.getHeight();
		}
	}

	/**
	 * 画面サイズの倍率を取得する
	 *
	 * @param	context			コンテクスト
	 * @param	orientation		向き
	 * @return	画面サイズの倍率
	 */
	public static double getMag(Context context, int orientation){
		int resourceId = ResourcesUtils.getResourcesID(context, "pixelsperLine", "integer");
		int pixelsperLine = context.getResources().getInteger(resourceId);
		if(orientation==Configuration.ORIENTATION_LANDSCAPE){
			return (double) getDisplayWidth(context)/pixelsperLine;
		}else{
			return (double) getDisplayHeight(context)/pixelsperLine;
		}
	}

	/**
	 * 画面のマージンを取得する
	 *
	 * @param context		コンテクスト
	 * @param orientation	向き
	 * @return	画面のマージン(x, y)
	 */
	public static int[] getMargin(Context context, int orientation){
		if(orientation==Configuration.ORIENTATION_PORTRAIT){
			int[] margin = {(int) ((getDisplayWidth(context) - getDisplayHeight(context)*0.5625)/2), 0};
			return margin;
		}else{
			int[] margin = {0, (int) ((getDisplayHeight(context) - getDisplayWidth(context)*0.5625)/2)};
			return margin;
		}
	}

	/**
	 * 画面のアスペクト比を取得する
	 *
	 * @param context		コンテクスト
	 * @param orientation	向き
	 * @return	画面のアスペクト比
	 */
	public static double getAspect(Context context, int orientation){
		if(orientation==Configuration.ORIENTATION_PORTRAIT){
			return (getDisplayWidth(context))/(getDisplayHeight(context));
		}else{
			return (getDisplayHeight(context))/(getDisplayWidth(context));
		}
	}

	/**
	 * サイズを指定する際に基準となる画面サイズを取得する<br>
	 * この数値は実際の画面解像度とは無関係である
	 *
	 * @param context		コンテクスト
	 * @param orientation	向き
	 * @return	基準となる画面サイズ（width, height）
	 */
	public static int[] getDisplayStandard(Context context, int orientation){
		int resourceId = ResourcesUtils.getResourcesID(context, "pixelsperLine", "integer");
		int ppl = context.getResources().getInteger(resourceId);
		if(orientation==Configuration.ORIENTATION_PORTRAIT){
			int[] display = {(int)(ppl*0.5625), ppl};
			return display;
		}else{
			int[] display = {ppl, (int)(ppl*0.5625)};
			return display;
		}
	}

}
