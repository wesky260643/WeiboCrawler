package com.cj.weiboCrawler.user;

import java.util.Random;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.webcollector.crawler.DeepCrawler;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;

public class FansAndFollsList extends DeepCrawler {
	public int fixedSeconds = 2;
	public int randomSeconds = 2;
	public int count = 0;
	public int followerCount = 0;
	public int friendCount = 0;
	public String friendsList = null;
	public String followersList = null;
	
	public int getFixedSeconds() {
		return fixedSeconds;
	}

	public void setFixedSeconds(int fixedSeconds) {
		this.fixedSeconds = fixedSeconds;
	}

	public FansAndFollsList(String crawlPath) {
		super(crawlPath);
	}
	
	public int getRandomSeconds() {
		return randomSeconds;
	}

	public void setRandomSeconds(int timeInternal) {
		this.randomSeconds = timeInternal;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getFollowerCount() {
		return followerCount;
	}

	public void setFollowerCount(int followerCount) {
		this.followerCount = followerCount;
	}

	public int getFriendCount() {
		return friendCount;
	}

	public void setFriendCount(int friendCount) {
		this.friendCount = friendCount;
	}

	public String getFriendsList() {
		return friendsList;
	}

	public void setFriendsList(String friendsList) {
		this.friendsList = friendsList;
	}

	public String getFollowersList() {
		return followersList;
	}

	public void setFollowersList(String followersList) {
		this.followersList = followersList;
	}

	@Override
	public Links visitAndGetNextLinks(Page page) {
		// 访问网页随机延时设置
		Random random = new Random();
		try {
			Thread.sleep((Math.abs(random.nextInt()) % randomSeconds) * 1000+fixedSeconds*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println(page.getHtml());
		String flag = page.getUrl().split("[?]")[0].split("/")[4].trim();
		Elements divs = page.getDoc().select("tr");
		String curPage = "";
		for (Element div : divs) {
			// System.out.println(div.select("a").toString().split("\"")[1]);
			String oneUser = div.text().trim();
			if (oneUser.length() != 0) {
				count++;
				String fUid = div.select("td[valign=top]").get(0).select("img").toString().split("/")[3];
				//String fName = oneUser.split(" ")[0];
				String fName = div.select("td[valign=top]").get(1).select("a").first().text();
				String fFans =null;
				if(fName.equals("")){
					fFans = oneUser.split(" ")[0];
				}else{
					fFans = oneUser.split(" ")[1];
				}
				int fFansNum = Integer.parseInt(fFans.substring(fFans.indexOf("丝") + 1, fFans.indexOf("人")));
				curPage += fUid + "," + fName + "," + fFansNum + ";";
			}
		}
		if (flag.equals("fans")) {
			followersList += curPage;
		} else if (flag.equals("follow")) {
			friendsList += curPage;
		}
		return null;
	}

	public void startCrawler(String uid, int friendNum, int followerNum) throws Exception {
		count = 0;
		friendsList = "";
		followersList = "";
		if(friendNum==0 && followerNum == 0){
			System.out.println("\n------粉丝数和关注数都为0------\n");
		} else{
			if(friendNum>200){
				for (int i = 1; i <= 20; i++) {
					this.addSeed("http://weibo.cn/" + uid + "/follow?&page=" + i);
				}
			}else if(friendNum!=0){
				for (int i = 1; i <= friendNum/10+1; i++) {
					this.addSeed("http://weibo.cn/" + uid + "/follow?&page=" + i);
				}
			}
			if(followerNum>200){
				for (int i = 1; i <= 20; i++) {
					this.addSeed("http://weibo.cn/" + uid + "/fans?&page=" + i);
				}
			}else if(followerNum!=0){
				for (int i = 1; i <= followerNum/10+1; i++) {
					this.addSeed("http://weibo.cn/" + uid + "/fans?&page=" + i);
				}
			}
			
			this.setThreads(1);
			System.out.println("\n------正在获取粉丝列表------\n");
			this.start(1);
			
			if(friendNum==0){
				friendCount=0;
			}else{
				friendCount = friendsList.split(";").length;
			}
			if(followerNum==0){
				followerCount = 0;
			}else{
				followerCount = followersList.split(";").length;
			}
			System.out.println("粉丝和关注总数：" + count + ";关注数" + friendCount + ";粉丝数：" + followerCount);
		}
	}

	/*public static void main(String[] args) throws Exception {
		FansAndFollsList crawler = new FansAndFollsList("_T_WM=0ad402eccc4ac9e9d9ad1db3807caeb8; M_WEIBOCN_PARAMS=rl%3D1; SUB=_2A255mUqvDeTxGeRH61UT9CjOwj2IHXVbYlbnrDV6PUJbrdAKLUP4kW0Z1mQal2TiEXU-UpE67oW2jRS1sw..; gsid_CTandWM=4ullea251TntlNVyagWn9ccj44H","Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; MANMJS; rv:11.0) like Gecko");
		crawler.startCrawler("3283818445");
	}*/
}