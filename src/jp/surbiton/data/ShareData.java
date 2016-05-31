package jp.surbiton.data;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * プリファレンスへのデータ入出力インタフェースです。<br>
 * SharedPreferences名はpackage nameと同一です。<br>
 */
public class ShareData {

	/** コンテクスト */
	private Context context;
	/** Twitter トークン情報を格納するSharedPreferences名 */
	private static String PREFERENCES_NAME;

	/**
	 * 入出力インタフェースを作成する
	 *
	 * @param context コンテキスト
	 */
	public ShareData(Context context){
		 this.context = context;
		 PREFERENCES_NAME = context.getPackageName();
	}

	/**
	 * データをint型で返す
	 *
	 * @param	key	キー
	 * @return	値<br>
	 * 			該当の値が無い場合は、-1を返す
	 */
	public int getParameter(String key){
			SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE );
			try{
				return sharedPreferences.getInt(key, -1);
			}catch(Exception e){
				return -1;
			}
	}

	/**
	 * データをint型で返す
	 *
	 * @param	key	キー
	 * @param	def	デフォルト値
	 * @return	値<br>
	 * 			該当の値が無い場合は、def値を返す
	 */
	public int getParameter(String key, int def){
			SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE );
			try{
				return sharedPreferences.getInt(key, def);
			}catch(Exception e){
				return def;
			}
	}

	/**
	 * データをlong型で返す
	 *
	 * @param	key キー
	 * @return	値<br>
	 * 			該当の値が無い場合は、-1を返す
	 */
	public long getParameterL(String key){
			SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE );
			try{
				return sharedPreferences.getLong(key, -1);
			}catch(Exception e){
				return -1;
			}
	}

	/**
	 * データをlong型で返す
	 *
	 * @param	key	キー
	 * @param	def	デフォルト値
	 * @return	値<br>
	 * 			該当の値が無い場合は、def値を返す
	 */
	public long getParameterL(String key, int def){
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE );
		try{
			return sharedPreferences.getLong(key, def);
		}catch(Exception e){
			return def;
		}
	}

	/**
	 * データをString型で返す
	 *
	 * @param	key	キー
	 * @return	値<br>
	 * 			該当の値が無い場合は、""を返す
	 */
	public String getStr(String key){
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE );
		try{
			return sharedPreferences.getString(key, "");
		}catch(Exception e){
			return "";
		}
	}

	/**
	 * データをSet<String>型で返す
	 *
	 * @param key	キー
	 * @return		値
	 * 				該当の値が無い場合は、新しいインスタンスを返す
	 */
	public Set<String> getStringSet(String key){
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE );
		Set<String> savedSet = sharedPreferences.getStringSet(key, new HashSet<String>());
		// XXX getStringSetにBugが存在するため、ディープコピーが必要である
		HashSet<String> returnSet = new HashSet<String>(savedSet.size());
		for(String str : savedSet){
			returnSet.add(str);
		}
		return returnSet;
	}

	/**
	 * データをint型で保存する
	 *
	 * @param key	キー
	 * @param value 値
	 */
	public void putParameter(String key, int value){
			SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE );
			try{
				sharedPreferences.edit().putInt(key, value).commit();

			}catch(Exception e){
			}

	}

	/**
	 * データをlong型で保存する
	 *
	 * @param key	キー
	 * @param value	値
	 */
	public void putParameterL(String key, long value){
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE );
		try{
			sharedPreferences.edit().putLong(key, value).commit();

		}catch(Exception e){}
	}

	/**
	 * データをString型で保存する
	 *
	 * @param key	キー
	 * @param value 値
	 */
	public void putStr(String key, String value){
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE );
		try{
			sharedPreferences.edit().putString(key, value).commit();
		}catch(Exception e){

		}
	}

	/**
	 * データをSet<String>型で保存する
	 *
	 * @param key	キー
	 * @param value	値
	 */
	public void putSetString(String key, String value){
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE );
		Set<String> set = getStringSet(key);
		set.add(value);
		sharedPreferences.edit().putStringSet(key, set).commit();
	}
}
