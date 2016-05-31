/**
 *
 */
package jp.surbiton.app;

import jp.surbiton.content.ResourcesUtils;
import jp.surbiton.widget.CustomImageView;
import jp.surbiton.widget.DisplayManager;
import android.content.res.Configuration;
import android.view.Gravity;
import android.widget.FrameLayout.LayoutParams;

/**
 * 横向き画面用アクティビティの共通テンプレートクラスです。<br>
 *
 */
public abstract class LandscapeActivity extends BaseActivity {

	/** 画面向き（縦向き固定） */
	protected final static int ORIENTATION = Configuration.ORIENTATION_LANDSCAPE; // XXX 変数は子クラスで上書きしても親クラスの処理では反映されない

	@Override
	protected void setBackground(int backgroundResourceId, int mainResourceId) {
		int resourceId = ResourcesUtils.getResourcesID(getApplicationContext(), "pixelsperLine", "integer");
		int pixelsperLine = getResources().getInteger(resourceId);

		if(backgroundResourceId!=-1){
			CustomImageView backgroundView = new CustomImageView(this, ORIENTATION, pixelsperLine, (int)(pixelsperLine*0.5625), 0, 0, backgroundResourceId);
			frameLayout.addView(backgroundView);
		}
		if(mainResourceId!=-1){
			CustomImageView mainView = new CustomImageView(this, ORIENTATION, pixelsperLine, (int)(pixelsperLine*0.5625), 0, 0, mainResourceId);
			frameLayout.addView(mainView);
		}
	}

	@Override
	protected LayoutParams getFrameLayoutParams() {
		// ソースコードはBaseActivityと同一
		int[] displayStd = DisplayManager.getDisplayStandard(getApplicationContext(), ORIENTATION);
		int[] margin = DisplayManager.getMargin(getApplicationContext(), ORIENTATION);
		LayoutParams params = new LayoutParams((int)(displayStd[0]*DisplayManager.getMag(this, ORIENTATION)), (int)(displayStd[1]*DisplayManager.getMag(this, ORIENTATION)));
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.setMargins(margin[0], margin[1], 0, 0);

		return params;
	}

}
