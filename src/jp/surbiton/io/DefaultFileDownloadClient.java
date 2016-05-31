/**
 *
 */
package jp.surbiton.io;

import android.content.Context;

/**
 * ファイルを非同期で取得し、アプリのプライベート領域に保存します。
 *
 */
public class DefaultFileDownloadClient extends FileDownloadClient{

	/** コールバック */
	private AsyncCallback asyncCallback = null;

	/**
	 * FileDownloadClientを初期化する
	 *
	 * @param context 		コンテクスト
	 * @param asyncCallback コールバック
	 */
	public DefaultFileDownloadClient(Context context, AsyncCallback asyncCallback){
		super(context);
		this.asyncCallback = asyncCallback;
	}

	/**
	 * FileDownloadClientをDigest認証情報で初期化する
	 *
	 * @param context		コンテクスト
	 * @param asyncCallback	コールバック
	 * @param user			ユーザ名
	 * @param pass			パスワード
	 */
	public DefaultFileDownloadClient(Context context, AsyncCallback asyncCallback, String user, String pass){
		super(context, user, pass);
		this.asyncCallback = asyncCallback;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if(result){
			asyncCallback.onPostExecute(fileName);
		}else{
			asyncCallback.onCancelled();
		}
	}

	/**
	 * 非同期処理時のコールバックインタフェースです。
	 */
	public interface AsyncCallback {

		/** 非同期処理前の処理を行う */
		//void onPreExecute();
		/** 非同期処理後の処理を行う（ファイル名を返す） */
		void onPostExecute(String fileName);
		/** 非同期処理中の処理を行う */
		//void onProgressUpdate(int progress);
		/** 非同期処理キャンセル時の処理を行う */
		void onCancelled();

	}

}
