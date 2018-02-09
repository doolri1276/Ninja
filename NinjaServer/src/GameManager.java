import java.io.DataOutputStream;
import java.util.ArrayList;

public class GameManager {
	
	private ArrayList<User> onlineUserList;
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
		Room room=new Room(roomNumber+"",user,msg,dos,this);
		roomList.add(room);
		return;//roomNum은 ROOM4형태
		
	}
	
	synchronized public void removeRoom(Room room) {
		roomList.remove(room);
	}
	
	public String checkRoomState() {
		
		System.out.println("check roomstate입장");
		String msg="WAITING:ROOMSTATE";
		
		for(int i=0;i<roomList.size();i++) {
			
			System.out.println(i+"개 쌓았음");
			System.out.println("state : "+roomList.get(i).getState());
			System.out.println("player1 : "+roomList.get(i).getPlayerList().get(0).getID());
				msg+=":"+roomList.get(i).getState()+":"+roomList.get(i).getPlayerList().get(0).getID();
				System.out.println("player2 null인가요? : " + (roomList.get(i).getPlayerList().size()==1));
				if(roomList.get(i).getPlayerList().size()==1) msg+=":NULL";
				else msg+=roomList.get(i).getPlayerList().get(1).getID();
		
		}
		
		System.out.println("처음에 다해서 null도 쌓는데로 들어옴");
		msg+=":EMPTY:NULL:NULL";
		msg+=":EMPTY:NULL:NULL";
		msg+=":EMPTY:NULL:NULL";
		msg+=":EMPTY:NULL:NULL";
		

		return msg;
	}
	
	synchronized public String exitRoom(User user,String location) {
		System.out.println("=============================exitRoom하러 들어왔다.");
		String exit="";
		for(int i=0;i<roomList.size();i++) {
			System.out.println(roomList.get(i).getRoomNum()+"와 "+location+"  ==========");
			if((roomList.get(i).getRoomNum()).equals(location)) {
				System.out.println("gm 같은 공간 확인완료!"+roomList.get(i).getRoomNum()+"    "+location);
				
				exit=roomList.get(i).exit(user);
				
				if(roomList.get(i).getPlayerList().size()<1) {
					System.out.println("gm 아 사람이 너무 적음.. 없어짐!");
					roomList.remove(i);
					System.out.println("gm 방이 지워졌습니다. "+roomList.size());
					
					
				}
				
				break;
			}
		}
		
		System.out.println("gm 그래서 결국 만들어진 문구 : "+exit);
		return exit;
		
	}
	
	public String enterRoom(User user,String location) {
		System.out.println("gm 유저 "+user+" 지역 "+location +"78라인");
		String enter="";
		for(int i=0;i<roomList.size();i++) {
			if(("ROOM"+roomList.get(i).getRoomNum()).equals(location)) {
				System.out.println("gm 같은 룸을 찾음 82라인 ");
				roomList.get(i).enter(user);
				
				enter="WAITING:"+location+":FULL:"+roomList.get(i).getPlayerList().get(0).getID()+":"+user.getID();
				System.out.println("gm enter에 해당 문구 입력됨  86라인");
				
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
