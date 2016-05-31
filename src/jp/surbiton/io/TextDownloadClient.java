package jp.surbiton.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import android.os.AsyncTask;

/**
 * テキストデータを非同期で取得します。
 */
public class TextDownloadClient extends AsyncTask<String, Void, ByteArrayOutputStream> {

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
}
