/**
 *
 */
package jp.surbiton.content;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * リソース処理に関する基本機能を提供します。
 */
public class ResourcesUtils {

	/**
	 * リソースIDを取得する
	 *
	 * @param context	コンテクスト
	 * @param name		リソース名
	 * @param defType	リソースタイプ
	 * @return	リソースID
	 */
	public static int getResourcesID(Context context, String name, String defType){
		return context.getResources().getIdentifier(name, defType, context.getPackageName());
	}

	/**
	 * Drawableを取得する
	 *
	 * @param context	コンテクスト
	 * @param name		リソース名
	 * @param defType	リソースタイプ
	 * @return	Drawable
	 */
	public static Drawable getDrawable(Context context, String name, String defType){
		return context.getResources().getDrawable(getResourcesID(context, name, defType));
	}

}
