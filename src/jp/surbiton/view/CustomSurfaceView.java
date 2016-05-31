/**
 *
 */
package jp.surbiton.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import jp.surbiton.content.ResourcesUtils;
import jp.surbiton.util.Log;
import jp.surbiton.widget.CustomLayoutParams;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * カメラ表示およびビデオ録画に対応したSurfaceViewです。
 *
 */
@SuppressLint("ViewConstructor")
public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	/** 動作モード */
	private enum Mode {
		/** カメラ */
		CAMERA,
		/** レコーダ */
		MEDIA_RECORDER,
		/** プレイヤー */
		MEDIA_PLAYER
	}
	/** ビデオソース<br>Source : DEFAULT */
	private static final int VIDEO_SOURCE = MediaRecorder.VideoSource.CAMERA;
	/** オーディオソース<br>Source : DEFAULT */
	private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.CAMCORDER;
	/** ビデオフォーマット<br>Format : MPEG4 */
	private static final int OUTPUT_FORMAT = MediaRecorder.OutputFormat.MPEG_4;
	/** ビデオエンコーダ<br>Encorder : H.264 */
	private static final int VIDEO_ENCORDER = MediaRecorder.VideoEncoder.H264;
	/** オーディオエンコーダ<br>Encorder : DEFAULT */
	private static final int AUDIO_ENCORDER = MediaRecorder.AudioEncoder.AAC;
	/** ビデオフレームレート(fps)<br>Rate : 30fps */
	private static final int FRAME_RATE = 30;
	/** オーディオチャンネル<br>Channel : MONO */
	private static final int AUDIO_CHANNELS = 1;
	/** ビデオ横幅(px)<br>Default Width : 640px */
	private int videoWidth = 640;
	/** ビデオ縦幅(px)<br>Default Height : 480px  */
	private int videoHeight = 480;
	/** ビデオレート<br>Default Bitrate : 700kbps  */
	private int videoRate = 700 * 1000;
	/** オーディオサンプリングレート<br>Rate : 32000Hz */
	private static final int AUDIO_SAMPLING_RATE = 32000;
	/** オーディオレート<br>Default Bitrate : 96kbps  */
	private int audioRate = 96 * 1000;
	/** アスペクト比許容誤差 */
	private static final double ASPECT_TOLERANCE = 0.2;
	/** コンテクスト */
	private Context context;
	/** レコーダ */
	private MediaRecorder recorder;
	/** カメラ */
	private Camera camera;
	/** プレイヤー */
	private MediaPlayer player;
	/** サーフェスホルダ */
	private SurfaceHolder holder;
	/** 素材のパス */
	private String path;
	/** モード */
	private Mode mode;
	/** 録画フラグ */
	private boolean isRecording = false;
	/** プレイヤー準備フラグ */
	private boolean isPlayerPrepared = false;
	/** カメラID */
	private int cameraId = 0;
	/** 再生終了コールバック */
	private PlayerCallBack playerCallback = null;
	/** レコーダエラー検知リスナ */
	private OnRecorderErrorListenner recorderErrorListenner = null;
	/** プレビューサイズ変更検知リスナ */
	private OnChangedPreviewSizeListenner changedPreviewSizeListenner = null;

	/**
	 * SurfaceViewをカメラモードで初期化する<br>
	 * 指定したプレビューサイズ(width, height)は、画面に適したサイズに伸縮される
	 *
	 * @param context		コンテクスト
	 * @param orientation	画面の向き
	 * @param width			プレビューで使用する画面解像度(幅)
	 * @param height		プレビューで使用する画面解像度(高さ)
	 * @param marginLeft	マージン（左）
	 * @param marginTop		マージン（右）
	 */
	public CustomSurfaceView(Context context, int orientation, int width, int height, int marginLeft, int marginTop) {
		super(context);
		this.context = context;
		// 画面サイズに合わせて伸縮させる
		// 例）画面解像度 : 720x480 -> 表示サイズ 1280x583
		int resourceId = ResourcesUtils.getResourcesID(context, "pixelsperLine", "integer");
		int displayWidth = context.getResources().getInteger(resourceId);
		int displayHeight = height*displayWidth/width;
		// レイアウトの指定
		setLayoutParams(new CustomLayoutParams(context, orientation, displayWidth, displayHeight, marginLeft, marginTop));
		// タイプ
		this.mode = Mode.CAMERA;
		this.videoWidth = width;
		this.videoHeight = height;
		// XXX SurfaceHolder.Callbackの設定
		holder = getHolder();
		holder.addCallback(this);
	}

	/**
	 * SurfaceViewをレコーダモードで初期化する
	 *
	 * @param context		コンテクスト
	 * @param orientation	画面の向き
	 * @param width			幅(px)
	 * @param height		高さ(px)
	 * @param marginLeft	マージン（左）
	 * @param marginTop		マージン（右）
	 * @param videoWidth	ビデオの横幅(px)
	 * @param videoHeight	ビデオの縦幅(px)
	 * @param videoRate		ビデオレート(bps)
	 * @param audioRate		オーディオレート(bps)
	 * @param path			保存先のパス
	 */
	public CustomSurfaceView(Context context, int orientation, int width, int height, int marginLeft, int marginTop, int videoWidth, int videoHeight, int videoRate, int audioRate, String path) {
		super(context);
		this.context = context;
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		// タイプ
		this.mode = Mode.MEDIA_RECORDER;
		this.videoWidth = videoWidth;
		this.videoHeight = videoHeight;
		this.videoRate = videoRate;
		this.audioRate = audioRate;
		this.path = path;
		// XXX SurfaceHolder.Callbackの設定
		holder = getHolder();
		holder.addCallback(this);
	}

	/**
	 * SurfaceViewをプレイヤーモードで初期化する
	 *
	 * @param context		コンテクスト
	 * @param orientation	画面の向き
	 * @param width			幅(px)
	 * @param height		高さ(px)
	 * @param marginLeft	マージン（左）
	 * @param marginTop		マージン（右）
	 * @param path			素材のパス
	 */
	public CustomSurfaceView(Context context, int orientation, int width, int height, int marginLeft, int marginTop, String path) {
		super(context);
		this.context = context;
		setLayoutParams(new CustomLayoutParams(context, orientation, width, height, marginLeft, marginTop));
		// タイプ
		this.mode = Mode.MEDIA_PLAYER;
		this.path = path;
		// XXX SurfaceHolder.Callbackの設定
		holder = getHolder();
		holder.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v(getClass(), "surfaceChanged");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(getClass(), "surfaceCreated");
		initialize();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(getClass(), "surfaceDestroyed");
		clearSurface();
	}

	/**
	 * Surfaceのリセットと終了処理を行う
	 */
	private void clearSurface(){
		// フラグ初期化
		// XXX 先にフラグを初期化しておかないとnullのオブジェクトを呼び出してしまうことがある
		isPlayerPrepared = false;
		isRecording = false;
		// オブジェクト初期化
		if(camera!=null){
			camera.stopPreview();
			camera.release();
			camera=null;
		}
		if(recorder!=null){
			recorder.setPreviewDisplay(null);
			recorder.reset();
			recorder.release();
			recorder=null;
		}
		if(player!=null){
			player.release();
			player=null;
		}
	}

	/**
	 * あらかじめ設定したモードで初期化する
	 */
	private void initialize(){
		// 各初期化処理
		switch (mode) {
		case CAMERA:
			if(!initializeCamera(0))
				Log.e(getClass(), "initialize : failed to initialize camera device");
			break;
		case MEDIA_RECORDER:
			if(!initializeMediaRecorder())
				Log.e(getClass(), "initialize : failed to initialize media recorder");
			break;
		case MEDIA_PLAYER:
			initializeMediaPlayer(path);
			break;
		}
	}

	/**
	 * MediaPlayerを初期化する
	 *
	 * @param path ファイルパス
	 */
	private void initializeMediaPlayer(String path){
		if(path==null)return;
		isPlayerPrepared = false;
		// XXX playerの初期化
		player = new MediaPlayer();
		player.setDisplay(holder);
		try {
			// XXX player.setDataSource(String)で実行すると、Prepare failed.: status=0x1エラーが発生する
			player.setDataSource(new FileInputStream(new File(context.getFilesDir() + "/" + path)).getFD());
			player.prepare();
		} catch (Exception e) {
			Log.e(getClass(), "Exception at initializeMediaPlayer : " + e.getMessage());
		}
		// XXX 準備完了通知
		player.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				isPlayerPrepared = true;
				player.seekTo(0);
				if(playerCallback!=null)
					playerCallback.onPrepared(player.getVideoWidth(), player.getVideoHeight());
				Log.v(getClass(), "initializeMediaPlayer : prepared media player");
			}
		});
		// XXX 再生終了通知
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				player.seekTo(0);
				if(playerCallback!=null)
					playerCallback.onStopped();
			}
		});
	}

	/**
	 * MediaRecorderを初期化する
	 *
	 * @param videoWidth	ビデオの横幅(px)
	 * @param videoHeight	ビデオの縦幅(px)
	 * @param videoRate		ビデオレート(bps)
	 * @param audioRate		オーディオレート(bps)
	 * @param path			保存先のパス
	 * @return　初期化に成功すれば true を返す
	 */
	public boolean initializeMediaRecorder(int videoWidth, int videoHeight, int videoRate, int audioRate, String path){
		this.videoWidth = videoWidth;
		this.videoHeight = videoHeight;
		this.videoRate = videoRate;
		this.audioRate = audioRate;
		this.path = path;
		return initializeMediaRecorder();
	}

	/**
	 * MediaRecorderを初期化する
	 *
	 * @return 初期化に成功すれば true を返す
	 */
	@SuppressLint("NewApi")
	private boolean initializeMediaRecorder(){
		// カメラを初期化
		if(camera==null){
			if(!initializeCamera(0)){
				Log.e(getClass(), "initialize : failed to initialize camera device");
				return false;
			}
		}
		try {
			// XXX カメラパラメータの取得（アンロックの前にないといけない）
			Parameters parameters = camera.getParameters();
			// XXX 手ぶれ機能ON
			if (Build.VERSION.SDK_INT >= 15) {
				if(parameters.isVideoStabilizationSupported())
					parameters.setVideoStabilization(true);
					Log.v(getClass(), "initializeMediaRecorder : video stabilization is enabled.");
			}
			// XXX アンロックしないとsetCamera()が使えない
			camera.unlock();
			// XXX MediaRecorderの設定
			recorder = new MediaRecorder();
			recorder.setCamera(camera); // XXX カメラの設定
			// XXX まずソースの指定を行う
			recorder.setVideoSource(VIDEO_SOURCE); // XXX ビデオソースの設定
			recorder.setAudioSource(AUDIO_SOURCE); // XXX オーディオソースの設定
			// XXX 次に出力形式の指定を行う
			recorder.setOutputFormat(OUTPUT_FORMAT);
			// XXX 最後にパラメータの指定を行う
			recorder.setVideoEncoder(VIDEO_ENCORDER);
			recorder.setVideoFrameRate(FRAME_RATE);
			// XXX カメラ対応解像度
			setPictureSize(parameters);
			recorder.setVideoEncodingBitRate(videoRate);
			recorder.setAudioChannels(AUDIO_CHANNELS);
			recorder.setAudioEncoder(AUDIO_ENCORDER);
			recorder.setAudioSamplingRate(AUDIO_SAMPLING_RATE);
			recorder.setAudioEncodingBitRate(audioRate);
			recorder.setOutputFile(context.getFilesDir() + "/" + path); // XXX アプリ内ファイルディレクトリ
			recorder.setPreviewDisplay(holder.getSurface());
			// XXX MediaRecorderの準備
			recorder.prepare();
			recorder.setOnErrorListener(new OnErrorListener() {

				@Override
				public void onError(MediaRecorder mr, int what, int extra) {
					if(recorderErrorListenner!=null)
						recorderErrorListenner.onRecorderError();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * カメラを初期化する
	 *
	 * @param cameraId カメラID
	 * @return	初期化に成功すれば true を返す
	 */
	@SuppressLint("NewApi")
	private boolean initializeCamera(int cameraId){
		camera = Camera.open(cameraId);
		try {
			camera.setPreviewDisplay(holder);
			//camera.autoFocus(null); // XXX オートフォーカスを入れると逆にボケる
			camera.startPreview(); // XXX これがないと映らない
		} catch (IOException e) {
			Log.v(getClass(), "Exception at initializeCamera : " + e.toString());
			return false;
		}
		// XXX カメラ顔検出
		if (Build.VERSION.SDK_INT >= 14) {
			camera.setFaceDetectionListener(new FaceDetectionListener() {

				@Override
				public void onFaceDetection(Face[] faces, Camera camera) {
					for(Face face : faces){
						Log.v(getClass(), "onFaceDetection : id = " + face.id + " score = " + face.score);
					}
				}
			});
			try {
				// 検知開始
				camera.startFaceDetection();
			} catch (Exception e) {
				Log.e(getClass(), "initializeCamera : " + e.getMessage());
			}
		}
		// プレビューサイズの設定
		setPictureSize(camera.getParameters());
		return true;
	}

	/**
	 * プレビューサイズおよびビデオサイズを指定する
	 *
	 * @param width		幅(px)
	 * @param height	高さ(px)
	 * @return 設定が完了すれば true を返す
	 */
	public boolean setPictureSize(int width, int height){
		this.videoWidth = width;
		this.videoHeight = height;
		return setPictureSize(camera.getParameters());
	}

	/**
	 * プレビューサイズおよびビデオサイズを指定する
	 *
	 * @param parameters	カメラパラメータ
	 * @return 設定が完了すれば true を返す
	 */
	private boolean setPictureSize(Parameters parameters){
		int[] videoSize = getVideoSize(parameters);
		int[] previewSize = getPreviewSize(parameters);
		if(videoSize==null && previewSize==null){
			return false;
		}else{
			int[] size;
			if(videoSize!=null){
				size = videoSize;
			}else{
				size = previewSize;
			}
			parameters.setPreviewSize(size[0], size[1]);
			if(recorder!=null)
				// XXX getSupportedVideoSizes()で、サイズ取得できないことがあるのでPreviewSizeで代用
				recorder.setVideoSize(size[0], size[1]);
			if(changedPreviewSizeListenner!=null)
				changedPreviewSizeListenner.onChangedPreviewSize(size[0], size[1]);
			return true;
		}
	}

	/**
	 * ビデオサイズを取得する
	 *
	 * @param parameters カメラパラメータ
	 * @return ビデオサイズ
	 */
	@TargetApi(11)
	private int[] getVideoSize(Parameters parameters){
		if(camera==null){
			Log.v(getClass(), "getVideoSize() : Camera is not initialized.");
			return null;
		}
		// XXX カメラ対応解像度
		List<Size> sizes = parameters.getSupportedVideoSizes();
		// XXX VideoSizeが取得できない端末対策
		if(sizes==null || sizes.size()==0){
			Log.v(getClass(), "getVideoSize() : size = null");
			return null;
		}
		Size size = null;
		double minDiff = Double.MAX_VALUE;
		for(Iterator<Size> iterator = sizes.iterator(); iterator.hasNext();){
			Size tmpSize = (Size)iterator.next();
			double diff = Math.abs(videoWidth - tmpSize.width);
			double aspect = Math.abs((double)videoHeight/videoWidth-(double)tmpSize.height/tmpSize.width);
			// XXX Samsung端末は録画時のアスペクト比が崩れるので許容するアスペクト比の範囲を指定する
			if(minDiff>diff && aspect<ASPECT_TOLERANCE){
				minDiff = diff;
				size = tmpSize;
			}
		}
		if(size==null){
			Log.v(getClass(), "getVideoSize() : size = null");
			return null;
		}else{
			int[] videoSize = {size.width, size.height};
			Log.v(getClass(), "getVideoSize() : size = (" + size.width + ", " + size.height + ")");
			return videoSize;
		}
	}

	/**
	 * ビデオサイズを取得する
	 *
	 * @return ビデオサイズ
	 */
	@TargetApi(11)
	private int[] getVideoSize(){
		int[] qualities = {CamcorderProfile.QUALITY_HIGH, CamcorderProfile.QUALITY_1080P, CamcorderProfile.QUALITY_720P, CamcorderProfile.QUALITY_480P, CamcorderProfile.QUALITY_CIF, CamcorderProfile.QUALITY_QCIF};
		CamcorderProfile profile = null;
		double minDiff = Double.MAX_VALUE;
		for (int quality : qualities) {
			if(CamcorderProfile.hasProfile(cameraId, quality)){
				CamcorderProfile tmpProf = CamcorderProfile.get(cameraId, quality);
				double diff = Math.abs(videoWidth - tmpProf.videoFrameWidth);
				double aspect = Math.abs((double)videoHeight/videoWidth-(double)tmpProf.videoFrameHeight/tmpProf.videoFrameWidth);
				// XXX Samsung端末は録画時のアスペクト比が崩れるので許容するアスペクト比の範囲を指定する
				if(minDiff>diff && aspect<ASPECT_TOLERANCE){
					minDiff = diff;
					profile = tmpProf;
				}
			}
		}
		if(profile==null){
			Log.v(getClass(), "getVideoSize() : size = null");
			return null;
		}else{
			int[] videoSize = {profile.videoFrameWidth, profile.videoFrameHeight};
			Log.v(getClass(), "getVideoSize() : size = (" + profile.videoFrameWidth + ", " + profile.videoFrameHeight + ")");
			return videoSize;
		}
	}

	/**
	 * プレビューサイズを取得する
	 *
	 * @param parameters カメラパラメータ
	 * @return プレビューサイズ
	 */
	private int[] getPreviewSize(Parameters parameters){
		if(camera==null){
			Log.v(getClass(), "getPreviewSize() : Camera is not initialized.");
			return null;
		}
		// XXX カメラ対応解像度
		List<Size> sizes = parameters.getSupportedPreviewSizes();
		// PreviewSizeが取得できない端末対策
		if(sizes==null || sizes.size()==0){
			Log.v(getClass(), "getPreviewSize() : size = null");
			return null;
		}
		Size size = null;
		double minDiff = Double.MAX_VALUE;
		for(Iterator<Size> iterator = sizes.iterator(); iterator.hasNext();){
			Size tmpSize = (Size)iterator.next();
			double diff = Math.abs(videoWidth - tmpSize.width);
			if(minDiff>diff){
				minDiff = diff;
				size = tmpSize;
			}
		}
		if(size==null){
			Log.v(getClass(), "getPreviewSize() : size = null");
			return null;
		}else{
			int[] previewSize = {size.width, size.height};
			Log.v(getClass(), "getPreviewSize() : size = (" + size.width + ", " + size.height + ")");
			return previewSize;
		}
	}

	/**
	 * カメラIDを取得する
	 *
	 * @return	カメラID
	 */
	public int getCameraId(){
		return cameraId;
	}

	/**
	 * カメラを変更する
	 */
	public void switchCameraDevice(){
		// XXX カメラ数の取得
		int num = Camera.getNumberOfCameras();
		if(num>1){
			switch (cameraId) {
			case 0:
				cameraId = 1;
				break;
			case 1:
				cameraId = 0;
				break;
			}
			clearSurface(); // きちんとクリアする
			initializeCamera(cameraId);
			Log.v(getClass(), "switchCameraDevice : cameraId = " + cameraId);
		}
	}

	/**
	 * プレイヤーコールバックを設定する
	 *
	 * @param playerCallback	コールバック
	 */
	public void setPlayerCallback(PlayerCallBack playerCallback){
		this.playerCallback = playerCallback;
	}

	/**
	 * レコーダエラー検知リスナを設定する
	 *
	 * @param recorderErrorListenner
	 */
	public void setOnRecorderErrorListenner(OnRecorderErrorListenner recorderErrorListenner){
		this.recorderErrorListenner = recorderErrorListenner;
	}

	/**
	 * プレビューサイズ変更検知リスナを設定する
	 *
	 * @param changedPreviewSizeListenner	リスナ
	 */
	public void setOnChangedPreviewSizeListenner(OnChangedPreviewSizeListenner changedPreviewSizeListenner){
		this.changedPreviewSizeListenner = changedPreviewSizeListenner;
	}

	/**
	 * 録画を開始する
	 */
	public void startMediaRecorder(){
		if(!isRecording && recorder!=null){
			try {
				recorder.start();
			} catch (Exception e) {
				if(recorderErrorListenner!=null)
					recorderErrorListenner.onRecorderError();
			}
			isRecording = true;
			Log.v(getClass(), "startMediaRecorder :  recorder start");
		}else{
			Log.e(getClass(), "failed to start media recorder");
		}
	}

	/**
	 * 録画を停止する
	 */
	public void stopMediaRecorder(){
		if(isRecording && recorder!=null){ // XXX 先にclearSurface()が走るとrecorderがnullとなっている可能性がある
			try {
				recorder.setPreviewDisplay(null); // XXX レコーダを停止する前に必要
				recorder.stop();
				recorder.release();
			} catch (Exception e) {
				if(recorderErrorListenner!=null)
					recorderErrorListenner.onRecorderError();
			}
			recorder=null; // XXX nullにしないとclearSurface()で競合する
			isRecording = false;
			Log.v(getClass(), "stopMediaRecorder :  recorder stop");
		}else{
			Log.e(getClass(), "failed to stop media recorder");
		}
	}

	/**
	 * 再生を開始する
	 */
	public void startMediaPlayer(){
		if(player!=null && isPlayerPrepared){
			Log.v(getClass(), "startMediaPlayer :  player start");
			player.start();
		}else{
			Log.e(getClass(), "startMediaPlayer :  failed to start player");
		}
	}

	/**
	 * 再生を一時停止する
	 */
	public void pauseMediaPlayer(){
		if(player!=null && player.isPlaying()){
			player.pause();
			Log.v(getClass(), "pauseMediaPlayer");
			if(playerCallback!=null)
				playerCallback.onStopped();
		}
	}

	/**
	 * 再生を終了する
	 */
	public void stopMediaPlayer(){
		if(player!=null && player.isPlaying()){
			player.stop();
			Log.v(getClass(), "stopMediaPlayer");
			if(playerCallback!=null)
				playerCallback.onStopped();
		}
	}

	/**
	 * 再生ファイルの再生長を取得する
	 *
	 * @return	再生長
	 */
	public int getDuration(){
		if(player!=null && isPlayerPrepared){
			return player.getDuration();
		}else{
			return 0;
		}
	}

	/**
	 * 再生位置を取得する
	 *
	 * @return	再生位置
	 */
	public int getCurrentPosition(){
		if(player!=null && isPlayerPrepared){
			return player.getCurrentPosition();
		}else{
			return 0;
		}
	}

	/**
	 * 指定したタイムコードへシークする
	 *
	 * @param msec	タイムコード
	 */
	public void seekTo(int msec){
		player.seekTo(msec);
	}

	/**
	 * 録画の状態を返す
	 *
	 * @return	録画中であれば true を返す
	 */
	public boolean isRecording(){
		return isRecording;
	}

	/**
	 * 再生の状態を返す
	 *
	 * @return 再生中であれば true を返す
	 */
	public boolean isPlaying(){
		if(player!=null){
			return player.isPlaying();
		}else{
			return false;
		}
	}

	/**
	 * プレイヤーのコールバックインタフェースです。
	 */
	public interface PlayerCallBack {

		/** 再生準備完了後の処理を行う
		 *
		 * @param width		幅
		 * @param height	高さ
		 */
		void onPrepared(int width, int height);

		/** 再生停止後の処理を行う */
		void onStopped();

	}

	/**
	 * レコーダエラー検知リスナです。
	 */
	public interface OnRecorderErrorListenner {

		void onRecorderError();

	}

	/**
	 * プレビューサイズ変更検知リスナです。
	 */
	public interface OnChangedPreviewSizeListenner {

		/** ビデオサイズ変更後の処理を行う
		 *
		 *  @param width	幅
		 *  @param height	高い
		 */
		void onChangedPreviewSize(int width, int height);

	}

}
