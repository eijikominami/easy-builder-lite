/**
 *
 */
package jp.surbiton.io;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

/**
 * ファイルを非同期で取得し、アプリのプライベート領域に保存します。
 *
 */
public class FileDownloadClient extends AsyncTask<String, Void, Boolean>{

	/** 通信モード */
	protected enum Mode{
		/** 通常モード */
		DEFAULT,
		/** ダイジェスト認証モード */
		DIGEST}
	/** コンテクスト */
	protected Context context;
	/** モード */
	protected Mode mode;
	/** ユーザ */
	protected String user;
	/** パス */
	protected String pass;
	/** ファイル名 */
	protected String fileName = null;

	/**
	 * FileDownloadClientを初期化する
	 *
	 * @param context 		コンテクスト
	 */
	public FileDownloadClient(Context context){
		mode = Mode.DEFAULT;
		this.context = context;
	}

	/**
	 * FileDownloadClientをDigest認証情報で初期化する
	 *
	 * @param context		コンテクスト
	 * @param user			ユーザ名
	 * @param pass			パスワード
	 */
	public FileDownloadClient(Context context, String user, String pass){
		mode = Mode.DIGEST;
		this.context = context;
		this.user = user;
		this.pass = pass;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		if(params.length > 0){
			// URL文字列
			String urlStr = params[0];
			try {
				// ホストの設定
				AndroidHttpClient httpClient = AndroidHttpClient.newInstance("easybuilderlite user agent");
				URL url = new URL(urlStr);
				HttpHost host = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
				AuthScope scope = new AuthScope(url.getHost(), url.getPort());
				// リクエストの送信
				HttpGet get = new HttpGet(urlStr);
				HttpResponse response;
				switch(mode){
					case DEFAULT:
						response = httpClient.execute(host, get);
						break;
					case DIGEST:
						// 認証
						UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, pass);
						CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
						credentialsProvider.setCredentials(scope, credentials);
						HttpContext httpContext = new BasicHttpContext();
						httpContext.setAttribute(ClientContext.CREDS_PROVIDER, credentialsProvider);
						response = httpClient.execute(host, get, httpContext);
						break;
					default:
						response = httpClient.execute(host, get);
						break;
				}
				// ステータスコード
				int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode==200){
					// ファイル名の指定
					String[] nameArray = urlStr.split("/", -1);
					for (String name : nameArray) {
						fileName = name;
					}
					// ソース
					InputStream input = response.getEntity().getContent();
					// ディスティネーション
					FileOutputStream output = context.openFileOutput(fileName, Context.MODE_PRIVATE);
					// 書き込み
					byte[] buf = new byte[1024];
					int length = 0;
					while((length = input.read(buf)) >= 0){
						// XXX write(buf)は使用しないこと！
						output.write(buf, 0, length);
					}
					output.close();
					httpClient.close();
					return true;
				}else{
					httpClient.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
