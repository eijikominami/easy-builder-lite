/**
 *
 */
package jp.surbiton.widget;

import java.lang.ref.WeakReference;

import jp.surbiton.util.Log;
import jp.surbiton.view.CustomSurfaceView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * FrameLayoutに対応したSeekBarです。
 *
 */
@SuppressLint("ViewConstructor")
public class CustomSeekBar extends SeekBar implements Runnable, OnSeekBarChangeListener {

	/** SurfaceView */
	private CustomSurfaceView surfaceView;
	/** ハンドラ */
	private CustomHandler handler;
	/** 生存フラグ */
	private boolean isAlive = false;

	/**
	 * シークバーを初期化する
	 *
	 * @param context		コンテクスト
	 * @param orientation	向き
	 * @param width			幅（px）
	 * @param height		高さ(px)
	 * @param marginLeft	マージン(左)
	 * @param marginTop		マージン(上)
	 * @param surfaceView	再生プレイヤーを含むSurfaceView
	 */
	public CustomSeekBar(Context context, int orientation, int width, int height, int marginLeft, int marginTop, CustomSurfaceView surfaceView){
		super(context);
		this.surfaceView = surfaceView;
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		this.setProgress(0);
		this.setMax(surfaceView.getDuration());
		// リスナの設定
		this.setOnSeekBarChangeListener(this);
		// ハンドラの初期化
		handler = new CustomHandler(this);
	}

	/**
	 * シークバーの描画処理を開始する
	 */
	public void configure(){
		if(!isAlive){
			isAlive = true;
			// マルチスレッド処理
			Thread thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void run() {
		while(isAlive){
			try {
				Thread.sleep(50);
				// XXX 実行キューに処理を入れる
				handler.handleMessage(Message.obtain(handler, surfaceView.getCurrentPosition()));
			} catch (InterruptedException e) {
				Log.e(getClass(), e.toString());
			}
		}
	}

	/**
	 * シークバーの描画処理を停止する
	 */
	public void release(){
		isAlive = false;
	}

	/**
	 * SeekBarの描画割込を行うハンドラクラスです。
	 */
	private static class CustomHandler extends Handler{

		/** 弱参照オブジェクト */
		private final WeakReference<CustomSeekBar> refSeekBar; // GC時に参照が一つもない状態であれば解放される

		/**
		 * ハンドラを初期化する
		 *
		 * @param seekBar	シークバー
		 */
		public CustomHandler(CustomSeekBar seekBar){
			refSeekBar = new WeakReference<CustomSeekBar>(seekBar);
		}

		@Override
		public void handleMessage(Message msg) {
			CustomSeekBar seekBar = refSeekBar.get();
			if(seekBar!=null)
				seekBar.setProgress(msg.what);
		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	// XXX シークバーを離したときに実行される
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		Log.v(getClass(), "onStopTrackingTouch");
		surfaceView.seekTo(seekBar.getProgress());
	}

}
