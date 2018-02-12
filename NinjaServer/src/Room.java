import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class Room {
	
	private String roomTitle;
	
	private ArrayList<User> playerList;
	
	private String roomNum;//숫자로 되어있다.1~4로..
	private String state;

	
	boolean isPlaying;
	
	private Random rnd;
	
	public Room(String roomNum,User user,String title) {
		this.roomNum=roomNum;
		playerList=new ArrayList<>();
		playerList.add(user);
		roomTitle=title;

		state="RWAITING";
		user.setRoom(this);
		rnd=new Random();
		
		
	}
	
	synchronized public String exit(User user) {
		if(user.getOpponent()!=null) {

			user.getOpponent().setOpponent(null);
			user.setOpponent(null);

		}
		if(user.room!=null)user.room=null;
		playerList.remove(user);

		user.setCurrentLocation("WAITING");
		
		if(playerList.size()<1) {

			return"WAITING:ROOM"+roomNum+":REMOVED";
			
		}else {

			return"WAITING:ROOM"+roomNum+":CHANGED:"+playerList.get(0).getID();
		}
		
	}
	
	synchronized public void enter(User user) {
		playerList.add(user);

		user.setOpponent(playerList.get(0));
		playerList.get(0).setOpponent(user);
		state="READY";

		user.setRoom(this);
	}
	
	synchronized public boolean setReady() {
		for(User u:playerList) {
			if(!u.getIsReady()) {
				return false;
			}
		}
		
		return true;
	}
	
	public String getRndItem() {
		
		String iteminfo="GAME:ITEM:";
		
		int x=rnd.nextInt(5);
		int y=rnd.nextInt(5);
		
		int item=rnd.nextInt(10);
		iteminfo+=x+":"+y+":"+item;
		
		return iteminfo;
		
		
	}
	
	public boolean getIsPlaying(){	return isPlaying;}
	
	
	public void setIsPlaying(boolean isPlaying) { this.isPlaying=isPlaying;}
	
	
	
	
	
	
	
	
	
	
	
	

	public String getRoomTitle() {return roomTitle;}
	public ArrayList<User> getPlayerList() {return playerList;}
	public String getRoomNum() {return roomNum;}
	public String getState() {return state;	}
	
	
	
	public void setRoomTitle(String roomTitle) {this.roomTitle = roomTitle;}
	public void setPlayerList(ArrayList<User> playerList) {	this.playerList = playerList;}
	public void setRoomNum(String roomNum) {	this.roomNum = roomNum;}

	
	
	
	
	
}
