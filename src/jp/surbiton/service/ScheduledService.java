package jp.surbiton.service;

import java.util.List;

import jp.surbiton.data.ShareData;
import jp.surbiton.util.Log;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.IBinder;

/**
 * 定期実行用のサービスを提供します。
 */
public abstract class ScheduledService extends Service { // ウィジェットをサービス化しておくことで、OSによって更新を止められてしまうことを防ぐ

	/** Shared Preferences */
	protected ShareData sd;

	/** 更新間隔（ミリ秒）*/
	protected int interval = 10000;

	/**
	 * 更新動作を行う
	 */
	protected abstract void update(Intent intent);

	/**
	 * サービスの遅延呼び出しを開始する<br>
	 * この処理を定期的に行うことでサービスの定期実行を行う<br>
	 * 指定した実行間隔より延びないようにゆらぎ吸収を行う
	 *
	 * @param time 遅延時間（ミリ秒）
	 */
	protected void setAlarm(int time){
		Intent alarmIntent = new Intent(getApplicationContext(), getClass());
		PendingIntent operation = PendingIntent.getService(getApplicationContext(), 0, alarmIntent, 0);
		AlarmManager am = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		long now = System.currentTimeMillis() + 1; // + 1 は確実に未来時刻になるようにする保険
		long oneHourAfter = now + time - now % (time);
		am.set(AlarmManager.RTC, oneHourAfter, operation);
	}

	/**
	 * サービスの遅延呼び出しを停止する
	 */
	protected void cancelAlarm(){
		Intent alarmIntent = new Intent(getApplicationContext(), getClass());
		PendingIntent operation = PendingIntent.getService(getApplicationContext(), 0, alarmIntent, 0);
		AlarmManager am = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		am.cancel(operation);
	}

	/**
	 * サービスを停止する
	 */
//	protected void cancelService(){
//		stopSelf();
//	}

	protected void restartService(){
		stopSelf();
		startService(new Intent(getApplicationContext(), getClass()));
	}

	/**
	 * インテント受信用レシーバ
	 */
	protected BroadcastReceiver myreceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = "";
			try {
				// インテントの取得
				action = intent.getAction();
				Log.v(getClass(), "Receive intent at onReceive() : " + action);
				// NULLインテント受信対策
				if(action==null){
					action = "";
				}
			} catch (NullPointerException e) {
				// NULLインテント受信対策
				action = "";
				Log.v(getClass(), "NullPointerExeption : " + e.toString());
			} catch (Exception ex){
				Log.v(getClass(), "Exeption at onReceive : " + ex.toString());
			}

			// スクリーンOFF時
			if(action.equals(Intent.ACTION_SCREEN_OFF)){
				cancelAlarm();
			}
			// スクリーンON時
			if(action.equals(Intent.ACTION_SCREEN_ON)){
				cancelAlarm();
				setAlarm(interval);
			}
			// キーガード解除時
			if(action.equals(Intent.ACTION_USER_PRESENT)){
				cancelAlarm();
				setAlarm(interval);
			}
			// 端末状態変化時
			if(action.equals(Intent.ACTION_CONFIGURATION_CHANGED)){
				// 端末の向きが横向き
				if(context.getResources().getConfiguration().orientation
						 == Configuration.ORIENTATION_LANDSCAPE){
					cancelAlarm();
					restartService();
				}
				// 端末の向きが縦向き
				else if(context.getResources().getConfiguration().orientation
						 == Configuration.ORIENTATION_PORTRAIT){
					cancelAlarm();
					restartService();
				}
			}
			// アプリ設定値変更時
			// 呼ばれるべきタイミングでonDestroy()が呼ばれないことがあるので、
			// アプリ設定画面を開いた際にサービスを一度終了させ、再度サービスを生成する形をとる
			if(action.contains("ACTION_RECONF")){
				cancelAlarm();
				try{
					// TODO レシーバの消去
					getApplicationContext().unregisterReceiver(myreceiver);
				}catch(Exception e){
					Log.e(getClass(), "Exeption at ACTION_RECONF : "+ e.toString());
				}
				restartService();
			}
			// ウィジェット終了時
			if(action.contains("ACTION_ENDPROC")){
				try{
					// レシーバの消去
					getApplicationContext().unregisterReceiver(myreceiver);
				}catch(Exception e){
					Log.e(getClass(), "Exeption at ACTION_ENDPROC : "+ e.toString());
				}
				// レシーバの再登録（アプリ設定値変更のみ受信）
				// broadcastreceiverがたまっていくのでタイミングごとに全て消し、必要なものだけ再登録する形とする
				getApplicationContext().registerReceiver(myreceiver, new IntentFilter("jp.surbiton.easybuilderlite.ACTION_RECONF"));

				// 残りのウィジェットも更新を全て一旦停止する
	 			cancelAlarm();
			}
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		update(intent);
		setAlarm(interval);
		// XXX intentのNULL対応
		if(intent!=null){
			if(intent.getAction()!=null){
				Log.v(getClass(), "onStartCommand : " + intent.getAction());
			}else{
				Log.v(getClass(), "onStartCommand : null");
			}
		}else{
			Log.v(getClass(), "onStartCommand : null");
		}
		return super.onStartCommand(intent, flags, startId);
	}

	// 1度目のサービス生成時に呼ばれる
	@Override
	public void onCreate() {
		super.onCreate();
		// Shared Preferencesの初期化
		sd = new ShareData(getApplicationContext());
		try{
			// レシーバの消去
			getApplicationContext().unregisterReceiver(myreceiver);
		}catch(Exception e){
			Log.e(getClass(), "Exeption at onCreate() : " + e.toString());
		}

		// レシーバの登録
		getApplicationContext().registerReceiver(myreceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		getApplicationContext().registerReceiver(myreceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		getApplicationContext().registerReceiver(myreceiver, new IntentFilter(Intent.ACTION_USER_PRESENT));
		getApplicationContext().registerReceiver(myreceiver, new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED));
		getApplicationContext().registerReceiver(myreceiver, new IntentFilter("jp.surbiton.easybuilderlite.ACTION_RECONF"));
		getApplicationContext().registerReceiver(myreceiver, new IntentFilter("jp.surbiton.easybuilderlite.ACTION_ENDPROC"));

		Log.v(getClass(), "onCreate");
	}

	// サービス終了時に呼ばれる
	@Override
	public void onDestroy() {
		Log.v(getClass(), "onDestroy");
		try{
			// レシーバの消去
			getApplicationContext().unregisterReceiver(myreceiver);
		}catch(Exception e){
			Log.e(getClass(), "Exeption at onDestroy : " + e.toString());
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v(getClass(), "onBind");
		return null;
	}

	@Override
	public void onRebind(Intent intent) {
		Log.v(getClass(), "onRebind");
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.v(getClass(), "onUnbind");
		cancelAlarm();
		// XXX ServiceをUnbind()したときにonDestroy()が呼ばれるようにする
		stopSelf();
		return super.onUnbind(intent);
	}

	/**
	 * サービスが起動中か判定する
	 *
	 * @param context 	コンテクスト
	 * @param cls		サービス
	 * @return 起動中なら ture を返す
	 */
	public static boolean isServiceRunning(Context context, Class<?> cls){
		ActivityManager manager = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningService = manager.getRunningServices(Integer.MAX_VALUE);
		for(RunningServiceInfo info : runningService) {
			if(cls.getName().equals(info.service.getClassName())){
				return true;
			}
		}
		return false;
	}
}
