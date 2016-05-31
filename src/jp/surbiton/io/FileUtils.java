/**
 *
 */
package jp.surbiton.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;

/**
 * ファイル入出力関連のユーティリティクラスです。
 *
 */
public class FileUtils {

	/**
	 * ファイルをコピーする
	 *
	 * @param sourcePath	コピー元ファイルのパス
	 * @param destPath		コピー先ファイルのパス
	 * @return				成功したら ture を返す
	 */
	public static boolean copy(String sourcePath, String destPath){

		File inFile = new File(sourcePath);
		File outFile = new File(destPath);

		FileChannel inChannel;
		FileChannel outChannel;

		try {
			inChannel = new FileInputStream(inFile).getChannel();
			outChannel = new FileOutputStream(outFile).getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
			inChannel.close();
			outChannel.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ギャラリーアプリに写真情報を登録する
	 *
	 * @param context	コンテクスト
	 * @param file		ファイル
	 * @param mimeType	MIMEタイプ
	 */
	public static void updateGallery(Context context, File file, String mimeType){
		ContentValues values = new ContentValues();
		ContentResolver resolver = context.getContentResolver();
		// データ指定
		if(mimeType.equals("image/jpeg")){
			values.put(Images.Media.TITLE, file.getName());
			values.put(Images.Media.DISPLAY_NAME, file.getName());
			values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
			values.put(Images.Media.MIME_TYPE, mimeType);
			values.put(Images.Media.ORIENTATION, 0);
			values.put(Images.Media.DATA, file.getPath());
			values.put(Images.Media.SIZE, file.length());
			// 確定
			resolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
		}else if(mimeType.equals("video/mp4")){
			values.put(Video.Media.TITLE, file.getName());
			values.put(Video.Media.DISPLAY_NAME, file.getName());
			values.put(Video.Media.DATE_TAKEN, System.currentTimeMillis());
			values.put(Video.Media.MIME_TYPE, mimeType);
			values.put(Video.Media.DATA, file.getPath());
			values.put(Video.Media.SIZE, file.length());
			// 確定
			resolver.insert(Video.Media.EXTERNAL_CONTENT_URI, values);
		}
	}

	/**
	 * FileChooserに対応しているか判定する
	 *
	 * @return FileChooserに対応していればtrueを返す
	 */
	public static boolean isSupportedFileChooser(){
		String release = Build.VERSION.RELEASE;
		if(release.matches(".*4.4.0.*")){
			return false;
		}else if(release.matches(".*4.4.1.*")){
			return false;
		}else if(release.matches(".*4.4.2.*")){
			return false;
		}else if(release.matches(".*4.4.3.*")){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * ファイルのハッシュ値を取得する
	 *
	 * @param file	ファイル
	 * @return	ハッシュ値
	 */
	public static String getMD5Checksum(File file){
		// XXX MD5を計算
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			DigestInputStream inputStream = new DigestInputStream(new BufferedInputStream(new FileInputStream(file)), messageDigest);
			// ファイル読み込みと計算
			while(inputStream.read()!=-1);
			// ハッシュ値を出力
			byte[] hash = messageDigest.digest();
			inputStream.close();
			// Stringに変換
			StringBuilder builder = new StringBuilder();
			for(byte b : hash){
				String str = String.format("%02x", b);
				builder.append(str);
			}
			return builder.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
