/**
 *
 */
package jp.surbiton.io;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

/**
 * ネットワーク設定に関する基本機能を提供します。
 *
 */
public class NetworkUtils {

	/**
	 * プロキシ設定を取得する<br>
	 * android.os.NetworkOnMainThreadExceptionが発生する恐れがあるため、メインスレッドは使用しないこと
	 *
	 * @return	Proxy
	 */
	public static Proxy getProxy() {
        // (未設定時は、host=null、port=null)
    	// android4対応
        String host = System.getProperty("http.proxyHost");
        String port = System.getProperty("http.proxyPort");
        if ((host != null) && (port != null)) {
            SocketAddress addr = new InetSocketAddress(host, Integer.parseInt(port));
            return new Proxy(Proxy.Type.HTTP, addr);
        } else {
            return null;
        }
    }

}
