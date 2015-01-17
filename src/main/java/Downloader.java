

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.MarketSession.Callback;
import com.gc.android.market.api.model.Market.AppsRequest;
import com.gc.android.market.api.model.Market.AppsResponse;
import com.gc.android.market.api.model.Market.GetAssetResponse.InstallAsset;
import com.gc.android.market.api.model.Market.ResponseContext;





public class Downloader {
	private static String searchData = "";
	public void downloadApp(String assetID, String assetName, MarketSession session) throws IndexOutOfBoundsException {
		try{
		InstallAsset ia = null;
		
			try {
				ia = session.queryGetAssetRequest(assetID).getInstallAsset(0);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		if (ia != null) {
		String cookieName = ia.getDownloadAuthCookieName();
		String cookieValue = ia.getDownloadAuthCookieValue();
		HttpURLConnection conn = null;
		URL resourceUrl;
		String url = ia.getBlobUrl();
		
			boolean shouldNotBreak = true;
			while (shouldNotBreak) {
				resourceUrl = new URL(url);		
				conn = (HttpURLConnection)resourceUrl.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("User-Agent", "Android-Market/2 (sapphire PLAT-RC33); gzip");
			    conn.setRequestProperty("Cookie", cookieName + "=" + cookieValue);
			    
			    switch (conn.getResponseCode())
			     {
			        case HttpURLConnection.HTTP_MOVED_PERM:
			        case HttpURLConnection.HTTP_MOVED_TEMP:
			           url = conn.getHeaderField("Location");
			           continue;
			        default: shouldNotBreak = false;
			     }
			}
		
		
	    
	    InputStream inputstream = (InputStream) conn.getInputStream();
	    System.out.println("File size: "+ inputstream.available());
	    String fileToSave = assetName + ".apk";
	    System.out.println("Downloading: " + fileToSave + "...");
	    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(fileToSave));
        byte buf[] = new byte[1024];
        int k = 0;
        for(long l = 0L; (k = inputstream.read(buf)) != -1; l += k )
            stream.write(buf, 0, k);
        inputstream.close();
        stream.close(); 
        System.out.println("Download complete");
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String searchApp(String query, MarketSession session) {
		try {
			AppsRequest appsRequest = AppsRequest.newBuilder().setQuery(query).setStartIndex(0).setEntriesCount(10).setWithExtendedInfo(true).build();
			
			session.append(appsRequest,  new Callback<AppsResponse>() { 
				@Override
				public void onResult(ResponseContext context, AppsResponse response) {
					System.out.println(response.toString());
					searchData = "";
					for (int i = 0; i<response.getAppCount(); i++) {
						// Title, ID, Creator, Description
					searchData += response.getApp(i).getTitle() + "<" + response.getApp(i).getId() + "<" + response.getApp(i).getCreator() + "<" + response.getApp(i).getExtendedInfo().getDescription() + ">";
					//response.getApp(index)
					}
					//System.out.println(response.getApp(0).getId());
				}
			});
			session.flush();
			return searchData;
		} catch(Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public MarketSession authenticate(String androidID, boolean secure, String token) {
		try{
			
			MarketSession session = new MarketSession(secure);
			System.out.println("Login...");
	//		session.setIsSecure(secure);
			session.setAndroidId(androidID);
			session.setAuthSubToken(token);		
			System.out.println("Login done");
			return session;
		} catch (Exception e) {
			MarketSession errorSession = new MarketSession(secure);		
			e.printStackTrace();
			return errorSession;
		}
		
	}

	
}
