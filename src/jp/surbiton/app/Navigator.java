/**
 *
 */
package jp.surbiton.app;

import android.widget.FrameLayout;

/**
 * ナビゲータボタン
 */
public interface Navigator {

	/**
	 * ナビゲータを構成するFrameLayoutを取得する
	 *
	 * @return ナビゲータのFrameLayout
	 */
	abstract FrameLayout getNavigatorLayout();

	/**
	 * 戻るボタンを構成するFrameLayoutを取得する
	 *
	 * @param resourceId タイトルイメージのリソースID
	 * @return	戻るボタンのFrameLayout
	 */
	abstract FrameLayout getBackButtonLayout(int resourceId);

}
