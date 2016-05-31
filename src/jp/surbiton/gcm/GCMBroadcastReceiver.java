/**
 *
 */
package jp.surbiton.gcm;

import jp.surbiton.app.BaseActivity;
import jp.surbiton.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * 通知インテントを受信します。
 */
public class GCMBroadcastReceiver extends BroadcastReceiver {

	/** 通知マネージャー */
	private NotificationManager notificationManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 初期化
		GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
		String messageType = googleCloudMessaging.getMessageType(intent);
		Log.v(getClass(), "messageType = " + messageType);
		 if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
	        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
	        } else {
	        	String message = intent.getExtras().getString("message");
	    		NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setTicker(message)
																			.setDefaults(Notification.DEFAULT_ALL)
																			.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
																			.setContentText(message);
	        	sendNotification(context, builder, new Intent(context, BaseActivity.class));
	        }
	}

	/**
	 * 通知を実行する
	 *
	 * @param context	コンテクスト
	 * @param builder	ビルダー
	 * @param intent	アプリ起動インテント
	 */
	protected void sendNotification(Context context, NotificationCompat.Builder builder, Intent intent) {
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		builder.setContentIntent(pendingIntent);
		notificationManager.notify(1, builder.build());
	}

}
