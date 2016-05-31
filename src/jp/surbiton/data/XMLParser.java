/**
 *
 */
package jp.surbiton.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * XMLの解析を行います。
 */
public class XMLParser {

	private String result;

	/**
	 * XMLParserを初期化する
	 *
	 * @param result ByteArrayOutputStream形式のXMLデータ
	 */
	public XMLParser(ByteArrayOutputStream result){
		this.result = new String(result.toByteArray());
	}

	/**
	 * XMLParserを初期化する
	 *
	 * @param result String形式のXMLデータ
	 */
	public XMLParser(String result){
		this.result = result;
	}

	/**
	 * キーに対応する値を取得する
	 *
	 * @param	key	キー
	 * @return	値
	 */
	public String getValue(String key){
		return getValue(key, -1);
	}

	/**
	 * キーと配列インデックスに対応する値を取得する
	 *
	 * @param	key キー
	 * @param	index インデックス
	 * @return	値
	 */
	public String getValue(String key, int index){

		XmlPullParser xmlPullParser = null;
		int eventType;

		int currentIndex = 0;

		// 初期化
		try {
			xmlPullParser = Xml.newPullParser();
			xmlPullParser.setInput(new StringReader(result));
			eventType = xmlPullParser.getEventType();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		// 添字初期化
		if(index<0)index=0;
		// 解析
		try {
			while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
			    if (eventType == XmlPullParser.START_TAG){
			    	if(xmlPullParser.getName().equals(key)){
			    		if(index==currentIndex){
			    			return xmlPullParser.nextText();
			    		}else{
			    			currentIndex++;
			    		}
			    	}
			    }
			}
			return "";
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 指定したブロックタグが文書中に出現する回数を取得する
	 *
	 * @param blockName	ブロックのタグ名称
	 * @return	出現回数
	 */
	public int getBlockLength(String blockName){

		XmlPullParser xmlPullParser = null;
		int eventType;

		int count = 0;

		// 初期化
		try {
			xmlPullParser = Xml.newPullParser();
			xmlPullParser.setInput(new StringReader(result));
			eventType = xmlPullParser.getEventType();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		// 解析
		try {
			while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
			    if (eventType == XmlPullParser.START_TAG){
			    	if(xmlPullParser.getName().equals(blockName)){
			    		count++;
			    	}
			    }
			}
			return count;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 指定したブロックタグに含まれる要素をHashMap形式で取得する
	 *
	 * @param blockName	ブロック名称
	 * @param index		配列インデックス
	 * @return	HashMap
	 */
	public HashMap<String, String> getHashMap(String blockName, int index){

		XmlPullParser xmlPullParser = null;
		int eventType;

		HashMap<String, String> hashmap = new HashMap<String, String>();

		int currentIndex = 0;
		boolean detect = false;

		// 初期化
		try {
			xmlPullParser = Xml.newPullParser();
			xmlPullParser.setInput(new StringReader(result));
			eventType = xmlPullParser.getEventType();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		// 解析
		try {
			while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG){
					if(xmlPullParser.getName().equals(blockName)){
						if(index == currentIndex){
							detect = true;
						}else{
							currentIndex++;
						}
					}else{
						if(detect){
							hashmap.put(xmlPullParser.getName(), xmlPullParser.nextText());
						}
					}
				}else if(eventType == XmlPullParser.END_TAG){
					if(xmlPullParser.getName().equals(blockName) && detect){
						detect = false;
						return hashmap;
					}
				}
			}
			return null;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 元データを取得する
	 *
	 * @return	元データ
	 */
	public String toString(){
		return result;
	}
}
