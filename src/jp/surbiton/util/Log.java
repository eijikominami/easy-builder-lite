/**
 *
 */
package jp.surbiton.util;

/**
 * クラス情報付きでログを出力します。
 */
public class Log {

	private static final boolean DEBUG = true;

	/**
	 * 詳細ログを表示する
	 *
	 * @param cls		クラス
	 * @param msg		メッセージ
	 */
	public static void v(Class<?> cls, String msg){
		if(DEBUG)
			android.util.Log.v(cls.getName(), msg);
	}

	/**
	 * 詳細ログを表示する
	 *
	 * @param tag		タグ
	 * @param msg		メッセージ
	 */
	public static void v(String tag, String msg){
		if(DEBUG)
			android.util.Log.v(tag, msg);
	}

	/**
	 * エラーログを表示する
	 *
	 * @param cls		クラス
	 * @param msg		メッセージ
	 */
	public static void e(Class<?> cls, String msg){
		if(DEBUG)
			android.util.Log.e(cls.getName(), msg);
	}

	/**
	 * エラーログを表示する
	 *
	 * @param tag		タグ
	 * @param msg		メッセージ
	 */
	public static void e(String tag, String msg){
		if(DEBUG)
			android.util.Log.v(tag, msg);
	}

}
