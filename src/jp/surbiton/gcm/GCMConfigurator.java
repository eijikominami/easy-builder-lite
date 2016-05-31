/**
 *
 */
package jp.surbiton.gcm;

import java.io.IOException;
import java.util.ArrayList;

import jp.surbiton.content.ResourcesUtils;
import jp.surbiton.data.ShareData;
import jp.surbiton.io.UploadData;
import jp.surbiton.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Google Cloud Messagingを設定します。
 */
public class GCMConfigurator {

	/** GCM登録状態保持期間 */
	private static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;

	/** コンテクスト */
	private Context context;
	/** 通知URL */
	private String notificationURL;
	/** 通知項目 */
	private String aFlag;
	/** Shared Preferences */
	private ShareData sd;
	/** Google Cloud Messaging (GCM) */
	private GoogleCloudMessaging googleCloudMessaging;
	/** */
	private boolean gcmRegistered = false;

	/**
	 * GCMConfiguratorを初期化する
	 *
	 * @param context			コンテクスト
	 * @param sd				SharedPreferences
	 * @param notificationURL	通知URL
	 * @param aFlag				通知項目
	 */
	public GCMConfigurator(Context context, ShareData sd, String notificationURL, String aFlag){
		this.context = context;
		this.sd = sd;
		this.notificationURL = notificationURL;
		this.aFlag = aFlag;
	}

	/**
	 * GCM RegistrationIdを返す
	 *
	 * @param	context コンテクスト
	 * @return	GCM RegistrationId
	 */
	private String getGCMRegistrationId(Context context) {
		String registrationId = sd.getStr("property_reg_id");
		if(registrationId.length()==0){
			return "";
		}
		// アプリのアップデートによりGCM Messageが競合状態となることを避ける
		int registeredVersion = sd.getParameter("property_app_version");
		int currentVersion = getAppVersion(context);
		// 期限切れチェック
		// 端末が送った登録情報をサーバが失ってしまった場合に再登録が必要
		if (registeredVersion != currentVersion || System.currentTimeMillis() > sd.getParameterL("property_on_server_expiration_time")) {
			return "";
		}
		return registrationId;
	}

	/**
	 * GCMに登録を行う
	 */
	private void registerGCMBackground() {
		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>(){

			@Override
			protected String doInBackground(Void... params) {
				String regId ="";
				if(googleCloudMessaging==null){
					googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
				}
				try {
					regId = googleCloudMessaging.register(context.getResources().getString(ResourcesUtils.getResourcesID(context, "senderids", "string")));
					Log.v(getClass(), "regId = " + regId);
					setGCMRegistrationId(context, regId);
				} catch (IOException e) {
					Log.e(getClass(), "regId = NULL");
				}
				return regId;
			}

		};
		task.execute();
	}

	/**
	 * GCM RegistrationIdを設定する
	 *
	 * @param context	コンテクスト
	 * @param regId		GCM RegistrationId
	 */
	private void setGCMRegistrationId(Context context, String regId) {
		ShareData sd = new ShareData(context);
		int appVersion = getAppVersion(context);
		long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;
		// SharedPreferencesに保存
		sd.putStr("property_reg_id", regId);
		sd.putParameter("property_app_version", appVersion);
		sd.putParameterL("property_on_server_expiration_time", expirationTime);
		// HTTP-based servers へ登録
		postGCMRegistrationId();
	}

	/**
	 * HTTP-based serversへRegIdを送信する
	 */
	public void postGCMRegistrationId(){
		if(getGCMRegistrationId(context).length()==0){
			if(gcmRegistered){
				Log.e(getClass(), "Can not post GCM registration id, because regId is NULL.");
			}else{
				registerGCMBackground();
			}
		}else{
			if(!notificationURL.equals("") && !aFlag.equals("")){
				int appVersion = getAppVersion(context);
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("token", sd.getStr("property_reg_id")));
				params.add(new BasicNameValuePair("aname", context.getPackageName()));
				params.add(new BasicNameValuePair("aversion", Integer.toString(appVersion)));
				params.add(new BasicNameValuePair("aflag", aFlag));
				UploadData uploadData = new UploadData(context, UploadData.Mode.REFER_NOTIFICATION_SERVER, params, null);
				uploadData.execute(notificationURL);
			}else{
				Log.e(getClass(), "Can not post GCM registration id, because url or aflag is NULL.");
			}
		}

	}

	/**
	 * アプリのバージョンを返す
	 *
	 * @return	アプリのバージョン<br>
	 * 			バージョンを取得できない場合は -1 を返す
	 */
	private int getAppVersion(Context context){
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			return -1;
		}
	}

}
