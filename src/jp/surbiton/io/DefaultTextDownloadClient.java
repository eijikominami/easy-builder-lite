package jp.surbiton.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;

import jp.surbiton.util.Log;

/**
 * テキストデータを非同期で取得します。
 */
public class DefaultTextDownloadClient extends TextDownloadClient {

	/** コールバック */
	private AsyncCallback asyncCallback = null;

	/**
	 * テキストデータ非同期取得クラスを作成する
	 *
	 * @param asyncCallback コールバック
	 */
	public DefaultTextDownloadClient(AsyncCallback asyncCallback){
		this.asyncCallback = asyncCallback;
	}

	@Override
	protected ByteArrayOutputStream doInBackground(String... urls) {
		//Log.v("DownloadData", "doInBackground url = " + urls[0] + " mode = " + mode);

		HttpURLConnection http = null;

		try {
			//　URLの指定
			URL url = new URL(urls[0] + "?t=" + Long.toString(System.currentTimeMillis()));
			// プロキシの設定
			Proxy proxy = NetworkUtils.getProxy();
			if(proxy != null){
				http = (HttpURLConnection)url.openConnection(proxy);
			}else{
				http = (HttpURLConnection)url.openConnection();
			}
			// 各種設定
			http.setConnectTimeout(3500);
			http.setReadTimeout(5000);
			http.setRequestMethod("GET");
			// コネクト
			http.connect();
			// バッファードインプットストリームの準備
			BufferedInputStream inputStream = new BufferedInputStream(http.getInputStream());
			ByteArrayOutputStream responseArray = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];

			int length;
			// 読み込み
			while((length = inputStream.read(buff)) != -1){
				if(length > 0){
					responseArray.write(buff, 0, length);
				}
			}
			inputStream.close();
			http.disconnect();

			return responseArray;
		// 取得に失敗した場合
		} catch (Exception e) {
			//Log.v("DownloadData", "Exeption : " + e.toString());
			try {
				if(http!=null){
					http.disconnect();
				}
			} catch (Exception ex) {
			}
			return null;
		}
	}

	@Override
	protected void onPostExecute(ByteArrayOutputStream result) {
		if(result!=null){
			this.asyncCallback.onPostExecute(result);
		}else{
			this.asyncCallback.onCancelled();
		}
	}

	/**
	 * 非同期処理時のコールバックインタフェースです。
	 */
	public interface AsyncCallback {

		/** 非同期処理前の処理を行う */
		//void onPreExecute();
		/** 非同期処理後の処理を行う（データを返す） */
		void onPostExecute(ByteArrayOutputStream result);
		/** 非同期処理中の処理を行う */
		//void onProgressUpdate(int progress);
		/** 非同期処理キャンセル時の処理を行う */
		void onCancelled();

	}

	/**
	 * 取得したデータを配列に変換する
	 *
	 * @param result	取得したデータ
	 * @return	配列
	 */
	public static ArrayList<String[]> parseArray(ByteArrayOutputStream result){
		// ByteArrayOutputStream から BufferedReader へ変換
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(result.toByteArray())));
		// データの読み込み
		String str;
		ArrayList<String[]> resultArray = new ArrayList<String[]>();
		try {
			while((str = br.readLine()) != null){
				// BOM対策
				String c = String.valueOf('\ufeff');
				str = str.replace(c, "");
				// ArrayListに格納
				// XXX 引数に-1を入れることであるだけ範囲を確保してくれる　大事！！！
				String[] lineStr = str.split(",", -1);
				resultArray.add(lineStr);
			}
			// 終了処理
			br.close();
		} catch (IOException e) {
			Log.e(DefaultTextDownloadClient.class, "IOExeption : " + e.toString());
		}
		return resultArray;
	}
}
