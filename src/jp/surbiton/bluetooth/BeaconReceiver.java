package jp.surbiton.bluetooth;
import jp.surbiton.util.Log;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * BLEの信号を受信します。
 */
@TargetApi(18)
public class BeaconReceiver {

	/** コールバック */
	public BeaconCallback beaconCallback;

	/** コンテクスト */
	private Context context;
	/** Bluetooth Manager */
	private BluetoothManager bluetoothManager;
	/** Bluetooth Adapter */
	private BluetoothAdapter bluetoothAdapter;
	/** コールバック */
	private LeScanCallback leScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			// 検知を停止
			stopLeScan();
			// 文字列を抽出
			StringBuilder builder = new StringBuilder();
			for(byte b : scanRecord){
				String str = String.format("%02x", b);
				builder.append(str);
			}
			Log.v(getClass(), "uuID " + builder.toString());
			beaconCallback.onLeScan(builder.toString());
		}
	};

	/**
	 * iBeaconレシーバを生成する
	 *
	 * @param context	コンテクスト
	 * @param callback	コールバック
	 */
	@SuppressLint("InlinedApi")
	public BeaconReceiver(Context context, BeaconCallback callback){
		bluetoothManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();
		this.context = context;
		this.beaconCallback = callback;
	}

	/**
	 * レシーバの状態を返す
	 *
	 * @return　レシーバが使用できる状態であれば true を返す
	 */
	public boolean isEnabled(){
		// XXX Bluetoothが存在するか判定する
		if(bluetoothAdapter==null){
			return false;
		// XXX BluetoothがONであるか判定する
		}else if(!bluetoothAdapter.isEnabled()){
			return false;
		// XXX BLEに対応しているか判定する
		}else if(!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * iBeaconのスキャンを開始する
	 *
	 * @return	スキャンの開始に成功したら true を返す
	 */
	public boolean startLeScan(){
		if(bluetoothAdapter!=null){
			return bluetoothAdapter.startLeScan(leScanCallback);
		}else{
			return false;
		}
	}

	/**
	 * iBeaconのスキャンを停止する
	 */
	public void stopLeScan(){
		bluetoothAdapter.stopLeScan(leScanCallback);
	}

	/**
	 * iBeacon検知時のコールバックインタフェースです。
	 *
	 */
	public interface BeaconCallback{

		/**
		 * iBeacon検知時に呼び出される
		 *
		 * @param uuID	uuID
		 */
		void onLeScan(String uuID);
	}

}
