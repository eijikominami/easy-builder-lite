/**
 *
 */
package jp.surbiton.io;

import java.io.File;

/**
 * 名前とファイルで構成されたデータ型を規定します。
 *
 */
public class NameFilePair{

	/** 名前 */
	private String name;
	/** ファイル */
	private File file;

	/**
	 * 初期化する
	 *
	 * @param name			名前
	 * @param file			ファイル
	 */
	public NameFilePair(String name, File file){
		this.name = name;
		this.file = file;
	}

	/**
	 * 名前を取得する
	 *
	 * @return	名前
	 */
	public String getName(){
		return name;
	}

	/**
	 * ファイルを取得する
	 *
	 * @return	ファイル
	 */
	public File getFile(){
		return file;
	}

}
