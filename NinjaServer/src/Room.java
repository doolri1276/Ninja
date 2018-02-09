import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class Room {
	
	String roomTitle;
	ArrayList<User> playerList;
	
	
	String roomNum;//숫자로 되어있다.1~4로..
	String state;
	
	DataOutputStream dos;
	GameManager gameManager;
	
	boolean isPlaying;
	
	Random rnd;
	
	final static int POWERUP=0;
	final static int HOLD=1;
	final static int DOUBLEATTACK=2;
	final static int MULTIATTACK=3;
	final static int SEETHROUGHALL=4;
	final static int SHIELD=5;
	final static int MOVEUP=6;
	final static int TELEPORT=7;
	final static int ENERGY=8;
	final static int SEETHROUGH=9;

	
	public Room(String roomNum,User user,String title,DataOutputStream dos, GameManager gameManager) {
		this.roomNum=roomNum;
		playerList=new ArrayList<>();
		playerList.add(user);
		roomTitle=title;
		this.dos=dos;
		this.gameManager=gameManager;
		state="RWAITING";
		user.setRoom(this);
		rnd=new Random();
		
		
	}
	
	synchronized public String exit(User user) {
		if(user.getOpponent()!=null) {
			System.out.println("room opponent있어서 지웠다.");
			user.getOpponent().setOpponent(null);
			user.setOpponent(null);
			System.out.println("room opponent다 지웠다.");
		}
		if(user.room!=null)user.room=null;
		playerList.remove(user);
		System.out.println("room playerlist에서 나 지워짐.");
		System.out.println("room playerlist player갯수 : "+playerList.size());
		user.setCurrentLocation("WAITING");
		
		if(playerList.size()<1) {
			System.out.println("room 사람이 0명이라서지워야 함.");
			return"WAITING:ROOM"+roomNum+":REMOVED";
			
		}else {
			System.out.println("room 사람이 1명 있어서 변경된 정보 보냄.");
			return"WAITING:ROOM"+roomNum+":CHANGED:"+playerList.get(0).getID();
		}
		
	}
	
	synchronized public void enter(User user) {
		playerList.add(user);
		System.out.println("room playerlist추가완료 59라인");
		user.setOpponent(playerList.get(0));
		playerList.get(0).setOpponent(user);
		state="READY";
		System.out.println("room enter됨. 53라인");
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
	
	
	
	
	
	
	
	
	
	
	
	
	

	public String getRoomTitle() {return roomTitle;}
	public ArrayList<User> getPlayerList() {return playerList;}
	public String getRoomNum() {return roomNum;}
	public String getState() {return state;	}
	
	
	public void setRoomTitle(String roomTitle) {this.roomTitle = roomTitle;}
	public void setPlayerList(ArrayList<User> playerList) {	this.playerList = playerList;}
	public void setRoomNum(String roomNum) {	this.roomNum = roomNum;}

	
	
	
	
	
}
