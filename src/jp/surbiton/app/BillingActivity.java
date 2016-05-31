/**
 *
 */
package jp.surbiton.app;

import java.util.Arrays;

import jp.surbiton.billing_utilities.IabHelper;
import jp.surbiton.billing_utilities.IabResult;
import jp.surbiton.billing_utilities.Inventory;
import jp.surbiton.billing_utilities.Purchase;
import jp.surbiton.util.Log;
import android.content.Intent;
import android.os.Bundle;

/**
 * 課金機能を実装したアクティビティの共通テンプレートクラスです。<br>
 *
 */
public abstract class BillingActivity extends BaseActivity {

    /** 初期化判定 */
    protected boolean mIsInitialized = false;

    /** （任意）リクエストコード */
    private static final int RC_REQUEST = 10001;
    /** 購入するアイテムID */
    private String sku;
    /** インベントリ*/
    private Inventory mInventory;
    /** 購入情報を参照するアイテムID */
    private String[] skusArray;
	/** ヘルパーオブジェクト */
	private IabHelper mHelper;

	/**
	 * アプリのBase64エンコードされた公開鍵を取得する
	 *
	 * @return	公開鍵
	 */
	protected abstract String getBase64EncodedPublicKey();

	/**
	 * 課金アイテムのアイテムIDを取得する
	 *
	 * @return　アイテムID
	 */
	protected abstract String getSKU();

	/**
	 * 情報を取得するアイテムIDを取得する
	 *
	 * @return　アイテムID
	 */
	protected abstract String[] getSKUsArray();

	/**
	 * 初期化後の処理を行う
	 *
	 * @param inv	インベントリ
	 */
	protected abstract void onPostInitialized(Inventory inv);

	/**
	 * 購入後の処理を行う
	 */
	protected abstract void onPostPurchased();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // アイテムIDの取得
		sku = getSKU();
		skusArray = getSKUsArray();

		// base64EncodedPublicKeyには、Google Play developer console上のアプリ用公開鍵を入力する
        // ただし、developer public keyではなく、アプリごとの公開鍵を使用すること
        // 鍵そのものをプログラム内に格納するのではなく、ビット操作等で鍵を生成することで本当の鍵を隠蔽している
        // しかし、攻撃者が鍵を入れ替えてメッセージを偽ることが容易になることは望まない
		String base64EncodedPublicKey = getBase64EncodedPublicKey();

		// コンテクストと公開鍵からヘルパーを作成する
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		// デバッグログを有効にする（リリース時はfalseとすること）
		mHelper.enableDebugLogging(true);

		// セットアップを開始する
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

			@Override
			public void onIabSetupFinished(IabResult result) {
				// セットアップ失敗
				if (!result.isSuccess()) {
                    Log.e(getClass(), "Problem setting up in-app billing: " + result);
                    return;
                }

                // Null対策
                if (mHelper == null) return;

                // 成功時はインベントリを取得する
                //mHelper.queryInventoryAsync(true, mGotInventoryListener);
                if(sku==null){
                	mHelper.queryInventoryAsync(mGotInventoryListener);
                }else{
                	mHelper.queryInventoryAsync(true, Arrays.asList(skusArray), mGotInventoryListener);
                }
			}
		});
	}

	@Override
    public void onDestroy() {
        super.onDestroy();
        // XXX 非常に重要
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    // 所有するアイテムやサブスクリプションの照会が終了するとこのリスナーは呼び出される
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

        	// Null対策
            if (mHelper == null) return;

            // セットアップ失敗
            if (result.isFailure()) {
                Log.e(getClass(), "Failed to query inventory: " + result);
                return;
            }
            // インベントリ
            mInventory = inventory;

            // インベントリ後の処理を行う
            mIsInitialized = true;
            onPostInitialized(inventory);
        }
    };

    // 購入処理が終了したときに呼び出される
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

        	// Null対策
            if (mHelper == null) return;

            // 処理失敗
            if (result.isFailure()) {
            	Log.e(getClass(), "Error purchasing: " + result);
                return;
            }
            // developer payloadの正規判定
            if (!verifyDeveloperPayload(purchase)) {
            	Log.e(getClass(), "Error purchasing. Authenticity verification failed.");
                return;
            }

	        // 購入
	        Log.v(getClass(), "Purchase is SKU=" + sku + ".");
	        onPostPurchased();
        }
    };

    /**
     * 購入に紐づけられたdeveloper payloadの正当性を検証する<br>
     * 購入時に送ったdeveloper payloadと同一であれば true を返す<br>
     * ただし、購入時の端末と再購入時の端末が異なる場合に認証できなくなることに留意すること
     *
     * @param p	購入オブジェクト
     * @return	認証できれば true を返す
     */
    private boolean verifyDeveloperPayload(Purchase p) {
        // String payload = p.getDeveloperPayload();

        /*
         * developer payloadは以下の形式とすることが望ましい
         *
         * 1. 異なるユーザがアイテムを購入した場合、それぞれのdeveloper payloadは異なるものとする。
         * 　　こうすることで、一方のユーザの購入が他のユーザで使用されることはない
         *
         * 2. developer payloadは、ユーザが端末を変更したときにでも、
         * 　　改めて認証することができるようにすべきである
         *
         *　自社のサーバで、developer payloadの保存と認証を行えることが望ましい
         */

        return true;
    }

    /**
     * 購入済みであるか判定する
     *
     * @param SKU	アイテムID
     * @return		購入済みであれば true を返す
     */
    protected boolean isPurchased(String SKU){
        // 所有するアイテムを確認する
        // 購入が正しいか否かdeveloper payloadを確認している
        Purchase purchase = mInventory.getPurchase(SKU);
        boolean purchased = (purchase != null && verifyDeveloperPayload(purchase));
        Log.v(getClass(), sku + " is " + (purchased ? "PURCHASED" : "NOT PURCHASED"));
        return purchased;
    }

    /**
     * 購入処理を行う
     *
     * @return	購入処理に進む場合は true を返す
     */
    protected boolean purchase(){

    	// XXX セキュリティ向上のためdeveloper payloadを入れておくことが望ましい
    	String payload = "";
    	if(isPurchased(sku)){
    		return false;
    	}else{
    		// 購入
    		mHelper.launchPurchaseFlow(this, sku, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
    		return true;
    	}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    	// Null対策
    	if (mHelper == null) return;

    	// ヘルパーがハンドリング時は、onActivityResult処理を省略する
    	if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
    		super.onActivityResult(requestCode, resultCode, data);
    	}else{
    		Log.v(getClass(), "onActivityResult handled by IABUtil.");
    	}
    }


}
