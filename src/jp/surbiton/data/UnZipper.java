/**
 *
 */
package jp.surbiton.data;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jp.surbiton.util.Log;

import android.content.Context;

/**
 * Zipを解凍します。
 *
 */
public class UnZipper {

	/** コンテクスト */
	private Context context;

	/**
	 * UnZipperを初期化する
	 *
	 * @param context		コンテクスト
	 */
	public UnZipper(Context context){
		this.context = context;
	}

	/**
	 * Zipファイルを解凍する
	 *
	 * @param name	解凍するZipファイル名
	 * @return	成功した場合はtrueを返す
	 */
	public boolean execute(String name){
		ZipEntry entry = null;
		BufferedOutputStream bOutput = null;
		int length = 0;

		try {
			ZipInputStream input = new ZipInputStream(context.openFileInput(name));
			// Zipファイル内を検索
			while((entry = input.getNextEntry()) != null){
				File file = new File(entry.getName());
				String path = context.getFilesDir() + "/" + file.getName();
				FileOutputStream fOutput = new FileOutputStream(path);
				bOutput = new BufferedOutputStream(fOutput);

				byte[] buf = new byte[1024];
				while((length = input.read(buf)) != -1){
					bOutput.write(buf, 0, length);
				}

				input.closeEntry();
				bOutput.close();
				bOutput = null;

				Log.v(getClass(), path);
			}
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
