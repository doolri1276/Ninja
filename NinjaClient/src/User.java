import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import itempack.AttackItem;
import itempack.PassiveItem;



public class User {
	String userCode="aa";
	String id;
	String psw;
	
	int playedTimes;
	int won;
	int lost;
	double winRate;
	
	int ranking;
	boolean onlineStatus;
	String currentLocation;
	Socket mySocket;
	
	User opponent;

	DataOutputStream dos;
	
	boolean isReady;
	
	Room room;
	

	
	public User() {}
	
	public User(String id){
		this.id=id;
		
	}
	
	public User(String id,String psw) {
		this.id=id;
		this.psw=psw;
		
	}
	
	
	
	public String getUserCode() {return userCode;}
	public String getID() {	return id;}
	public String getPSW() {return psw;}
	public int getPlayedTimes() {return playedTimes;}
	public int getWon() {return won;}
	public int getLost() {return lost;}
	public double getWinRate() {return winRate;}
	public int getRanking() {return ranking;}
	public boolean getOnlineStatus(){return onlineStatus;}
	public String getCurrentLocation() {return currentLocation;	}
	public Socket getMySocket() {return mySocket;}
	public DataOutputStream getDos() {return dos;}
	public boolean getIsReady(){	return isReady;	}
	public Room getRoom() {
		return room;
	}
	
	
	public void setUserCode(String userCode) {this.id=userCode;}
	public void setID(String id) {this.id=id;}
	public void setPSW(String psw) {this.psw=psw;}
	public void setPlayedTimes(String timess) {
		int playedTimes=Integer.parseInt(timess);
		this.playedTimes=playedTimes;
	}
	public void setWon(String wons) {
		int won=Integer.parseInt(wons);	
		this.won = won;
	}
	
	public void setLost(String losts) {
		int lost=Integer.parseInt(losts);
		this.lost = lost;
	}
	
	public void setWinRate(String winRates) {
		double winRate=Double.parseDouble(winRates);
		this.winRate = winRate;
	}
	
	public void setRanking(String rankings) {
		int ranking=Integer.parseInt(rankings);
		this.ranking = ranking;
	}
	
	public void setOnlineStatus(boolean online) {onlineStatus=online;}
	
	public void setCurrentLocation(String currentLocation) {this.currentLocation = currentLocation;	}
	
	public void setMySocket(Socket mySocket) {	this.mySocket = mySocket;}
	
	public void setOpponent(User opponent) {
		this.opponent = opponent;
	}
	
	
	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}
	public void setIsReady(boolean isReady) {this.isReady=isReady;}
	public void setRoom(Room room) {
		this.room = room;
	}
	
	
	
	
	public void playedTimesPlus() {
		playedTimes++;
	}
	
	public String getDBWrite() {
		
		return getUserCode()+":"+getID()+":"+getPSW()+":"+getPlayedTimes()+":"+getWon()+":"+getLost()+":"+getWinRate()+":"+getRanking()+":"+"nowhere";
	}
	
	
}
