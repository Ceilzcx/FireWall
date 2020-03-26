package com.example.firewall.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

public class WifiUtils {
	// 上下文Context对象
	private Context mContext;
	// WifiManager对象
	private WifiManager mWifiManager;
	public WifiUtils(Context mContext) {
		this.mContext = mContext;
		mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
	}

	/**
	 * 判断手机是否连接在Wifi上
	 */
	public boolean isConnectWifi() {
		// 获取ConnectivityManager对象
		ConnectivityManager conMgr = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取NetworkInfo对象
		NetworkInfo info = conMgr.getActiveNetworkInfo();
		// 获取连接的方式为wifi
		State wifi = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();

		if (info != null && info.isAvailable() && wifi == State.CONNECTED)

		{
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 获取当前手机所连接的wifi信息
	 */
	public WifiInfo getCurrentWifiInfo() {
		return mWifiManager.getConnectionInfo();
	}

	/**
	 * 添加一个网络并连接 传入参数：WIFI发生配置类WifiConfiguration
	 */
	public boolean addNetwork(WifiConfiguration wcg) {
		int wcgID = mWifiManager.addNetwork(wcg);
		return mWifiManager.enableNetwork(wcgID, true);
	}




	public List<ScanResult> noSameName(List<ScanResult> oldSr)
	{
		List<ScanResult> newSr = new ArrayList<ScanResult>();
		for (ScanResult result : oldSr)
		{
			if (!TextUtils.isEmpty(result.SSID) && !containName(newSr, result.SSID))
				newSr.add(result);
		}
		return newSr;
	}
	public boolean containName(List<ScanResult> sr, String name)
	{
		for (ScanResult result : sr)
		{
			if (!TextUtils.isEmpty(result.SSID) && result.SSID.equals(name))
				return true;
		}
		return false;
	}

	/**
	 * 搜索附近的热点信息，并返回所有热点为信息的SSID集合数据
	 */
	public List<String> getScanWifiResult() {
		// 扫描的热点数据
		List<ScanResult> resultList;
		// 开始扫描热点
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}

		mWifiManager.startScan();
		resultList = mWifiManager.getScanResults();
		Log.d("WifiScan",String.valueOf(resultList.size()));
		resultList=noSameName(resultList);
		ArrayList<String> ssids = new ArrayList<String>();
		if (resultList != null) {
			for (ScanResult scan : resultList) {
				ssids.add(scan.SSID);// 遍历数据，取得ssid数据集
			}
		}
		return ssids;
	}

	/**
	 * 连接wifi 参数：wifi的ssid及wifi的密码
	 */
	public boolean connectWifiTest(final String ssid, final String pwd) {
		boolean isSuccess = false;
		boolean flag = false;
		mWifiManager.disconnect();
		boolean addSucess = addNetwork(CreateWifiInfo(ssid, pwd, 3));
		if (addSucess) {
			while (!flag && !isSuccess) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				String currSSID = getCurrentWifiInfo().getSSID();
				if (currSSID != null)
					currSSID = currSSID.replace("\"", "");
				int currIp = getCurrentWifiInfo().getIpAddress();
				if (currSSID != null && currSSID.equals(ssid) && currIp != 0) {
					isSuccess = true;
				} else {
					flag = true;
				}
			}
		}
		return isSuccess;

	}

	/**
	 * 创建WifiConfiguration对象 分为三种情况：1没有密码;2用wep加密;3用wpa加密
	 * 
	 * @param SSID
	 * @param Password
	 * @param Type
	 * @return
	 */
	public WifiConfiguration CreateWifiInfo(String SSID, String Password,
                                            int Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		if (Type == 1) // WIFICIPHER_NOPASS
		{
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 2) // WIFICIPHER_WEP
		{
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 3) // WIFICIPHER_WPA
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

}
