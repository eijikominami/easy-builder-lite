/**
 *
 */
package jp.surbiton.io;

import java.io.IOException;
import java.net.Proxy;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

import jp.surbiton.data.ShareData;
import jp.surbiton.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

/**
 * テキストデータを非同期で送信します。
 */
public class UploadData extends AsyncTask<String, Void, String>{

	/** 送信モード */
	public static enum Mode{
		/** POSTモード */
		POST,
		/** 通知サーバ登録URL取得モード*/
		REFER_NOTIFICATION_SERVER,
		/** 通知サーバ登録モード*/
		JOIN_NOTIFICATION_SERVER}

	/** Proxy */
	private Proxy proxy;
	/** POSTテキストパラメータ */
	private ArrayList<NameValuePair> postTextParams;
	/** POSTファイルパラメータ */
	private ArrayList<NameFilePair> postFileParams;
	/** GCMパラメータ */
	private ArrayList<NameValuePair> gcmParams;
	/** モード */
	private Mode mode;
	/** コンテクスト */
	private Context context;
	/** POST処理完了検知リスナ */
	private OnPostCompletionListenner onPostCompletionListenner;
	/** Shared Preferences */
	private ShareData sd;

	/**
	 * 初期化する
	 *
	 * @param context		コンテクスト
	 * @param mode			送信モード
	 * @param textParams	テキストパラメータ
	 * @param fileParams	ファイルパラメータ
	 */
	public UploadData(Context context, Mode mode, ArrayList<NameValuePair> textParams, ArrayList<NameFilePair> fileParams){
		switch(mode){
		case POST:
			postTextParams = new ArrayList<NameValuePair>(textParams);
			if(fileParams!=null){
				postFileParams = new ArrayList<NameFilePair>(fileParams);
			}else{
				postFileParams = null;
			}
			break;
		case JOIN_NOTIFICATION_SERVER:
			gcmParams = new ArrayList<NameValuePair>(textParams);
			break;
		case REFER_NOTIFICATION_SERVER:
			gcmParams = new ArrayList<NameValuePair>(textParams);
			break;
		default:
			break;
		}
		this.mode = mode;
		this.proxy = NetworkUtils.getProxy();
		this.context = context;
		sd = new ShareData(context);
	}

	/**
	 * POST送信を行う
	 *
	 * @param url		送信先URL
	 * @param textParams	送信パラメータ
	 * @return			返り値
	 */
	private String execPost(String url, ArrayList<NameValuePair> textParams, ArrayList<NameFilePair> fileParams){
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;

		// デバッグ用
		Log.v(getClass(), "mode = " + mode.toString() + " url = " + url + " params = " + textParams.toString());

		try {
			request = new HttpPost(new URI(url));

			if(fileParams==null){
				request.setEntity(new UrlEncodedFormEntity(textParams, "UTF-8"));
			}else{
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				builder.setCharset(Charset.forName("UTF-8"));
				// ファイル情報をセット
				for(Iterator<NameFilePair> iterator = fileParams.iterator(); iterator.hasNext();){
					NameFilePair nameFilePair = iterator.next();
					builder.addPart(nameFilePair.getName(), new FileBody(nameFilePair.getFile()));
				}
				// テキスト情報をセット
				for(Iterator<NameValuePair> iterator = textParams.iterator(); iterator.hasNext();){
					NameValuePair nameValuePair = iterator.next();
					builder.addTextBody(nameValuePair.getName(), nameValuePair.getValue(), ContentType.create("text/plain", MIME.UTF8_CHARSET)); // XXX これを指定しないとUTF-8で送ってくれない、とても大事
				}
				request.setEntity(builder.build());
			}
			if (proxy != null)client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			return client.execute(request, new ResponseHandler<String>() {

				@Override
				public String handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					return EntityUtils.toString(response.getEntity(), "UTF-8");
				}
			});
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected String doInBackground(String... urls) {

		switch(mode){
		case JOIN_NOTIFICATION_SERVER:
			gcmParams.add(new BasicNameValuePair("cmd", "join"));
			gcmParams.add(new BasicNameValuePair("dmodel", Build.MODEL));
			gcmParams.add(new BasicNameValuePair("dversion", Build.VERSION.RELEASE));
			return execPost(urls[0], gcmParams, null);
		case POST:
			return execPost(urls[0], postTextParams, postFileParams);
		case REFER_NOTIFICATION_SERVER:
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("cmd", "geturl"));
			return execPost(urls[0], params, null);
		default:
			return null;

		}

	}

	@Override
	protected void onPostExecute(String result) {
		switch(mode){
			case JOIN_NOTIFICATION_SERVER:
				if(result!=null && result.equals("OK")){
					sd.putParameter("notification_failed", 0);
				}else{
					// 登録失敗
					sd.putParameter("notification_failed", 1);
				}
				break;
			case POST:
				break;
			case REFER_NOTIFICATION_SERVER:
				if(result!=null && !result.equals("")){
					// 通知データを登録
					UploadData uploadData = new UploadData(context, Mode.JOIN_NOTIFICATION_SERVER, gcmParams, null);
					uploadData.execute(result);
				}else{
					// TODO 処理
				}
				break;
			default:
				break;
		}
		// コールバック
		if(onPostCompletionListenner!=null)onPostCompletionListenner.onPostCompletion(result);
		Log.v(getClass(), "return = " + result);
		super.onPostExecute(result);
	}

	/**
	 * POST処理完了検知リスナを設定する
	 *
	 * @param onPostCompletionListenner	リスナ
	 */
	public void setOnPostCompletionListenner(OnPostCompletionListenner onPostCompletionListenner){
		this.onPostCompletionListenner = onPostCompletionListenner;
	}

	/**
	 * POST処理完了検知リスナです。
	 */
	public interface OnPostCompletionListenner {

		/**
		 * POST処理完了後の処理を行う
		 *
		 * @param result 結果
		 */
		void onPostCompletion(String result);
	}

}
