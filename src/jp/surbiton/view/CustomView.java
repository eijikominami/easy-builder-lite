/**
 *
 */
package jp.surbiton.view;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.view.View;

/**
 * 最適化されたViewです。
 *
 */
public class CustomView extends View {

	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	public CustomView(Context context) {
		super(context);
	}

	/**
	 * {@link #setId(int)} で使用するのに適したID値を生成する<br>
	 * この値はR.idのためにAAPTによって生成されたID値と衝突しない
	 *
	 * @return	生成されたIDアタイ
	 */
	public static int generateId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}

}
