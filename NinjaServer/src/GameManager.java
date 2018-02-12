import java.io.DataOutputStream;
import java.util.ArrayList;

public class GameManager {
	
	private ArrayList<Room> roomList;
	
	public GameManager(ArrayList<Room> roomList) {
		
		this.roomList=roomList;
		
	}
	
	synchronized public boolean createCheck(int num) {
		if(roomList.size()==4) {
			return false;
		}
		for(int i=0;i<roomList.size();i++) {
			if(num+""==roomList.get(i).getRoomNum()) return false;
		}

		return true;
	}
	
	synchronized public void createRoom(User user,String msg,DataOutputStream dos,int roomNumber) {
		Room room=new Room(roomNumber+"",user,msg);
		roomList.add(room);
		return;//roomNum은 ROOM4형태
		
	}
	
	synchronized public void removeRoom(Room room) {
		roomList.remove(room);
	}
	
	synchronized public String exitRoom(User user,String location) {

		String exit="";
		for(int i=0;i<roomList.size();i++) {

			if((roomList.get(i).getRoomNum()).equals(location)) {
				
				exit=roomList.get(i).exit(user);
				
				if(roomList.get(i).getPlayerList().size()<1) {

					roomList.remove(i);
					
				}
				break;
			}
		}
		
		return exit;
		
	}
	
	public String enterRoom(User user,String location) {

		String enter="";
		for(int i=0;i<roomList.size();i++) {
			if(("ROOM"+roomList.get(i).getRoomNum()).equals(location)) {

				roomList.get(i).enter(user);
				
				enter="WAITING:"+location+":FULL:"+roomList.get(i).getPlayerList().get(0).getID()+":"+user.getID();
				
				break;
			}
		}
		return enter;
		
		
		
	}
	
	public ArrayList<Room> getRoomList() {
		return roomList;
	}
	
	
	public String getRoomMsg(int number) {
		for(int i=0;i<roomList.size();i++) {
			Room r=roomList.get(i);
			if(r.getRoomNum().equals(number+"")) {
				if(r.getPlayerList().size()==2) {
					return "WAITING:ROOM"+number+":FULL:"+r.getPlayerList().get(0).getID()+":"+r.getPlayerList().get(1).getID()+":"+r.getRoomTitle();
				}else {
					return "WAITING:ROOM"+number+":CHANGED:"+r.getPlayerList().get(0).getID()+":---:"+r.getRoomTitle();
				}
				
				
			}
		}
		return "WAITING:ROOM"+number+":CHANGED:---:---";
		
	}
	
	
}
