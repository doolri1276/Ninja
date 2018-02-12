import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class NinjaDB {
	ArrayList<User> userList=new ArrayList<>();
	
	File dbFolder=new File("data");
	File dbFile = new File("data/userDB.txt");
	
	//JTextArea ServerA;
	
	
	
	public void fileCreate() {
		try {
			if(!dbFolder.exists()) {
				dbFolder.mkdir();
			}
			
			if(!dbFile.exists()) {
				dbFile=new File(dbFolder,"userDB.txt");
			}
			
		}catch(Exception e) {
			e.getStackTrace();
		}
	}
	
	public ArrayList<User> fileRead(){
		try {
			BufferedReader br=new BufferedReader(new FileReader(dbFile));
			if(dbFile.exists()) {
				String memRec;
				
				while((memRec=br.readLine())!=null) {
					String[] memInfo=memRec.split(":");
					
					User user=new User();
					user.setUserCode(memInfo[0]);
					user.setID(memInfo[1]);
					user.setPSW(memInfo[2]);
					user.setPlayedTimes(memInfo[3]);
					user.setWon(memInfo[4]);
					user.setLost(memInfo[5]);
					user.setWinRate(memInfo[6]);
					user.setRanking(memInfo[7]);
					user.setOnlineStatus(false);
					user.setCurrentLocation("nowhere");
					
					userList.add(user);
							
				}
				br.close();
			}else {
				System.out.println("파일 없음");
			}
		}catch(Exception e) {
			e.getStackTrace();
		}
		return userList;
	}
	
	public void fileWrite(ArrayList<User> list) {
		try {
			FileWriter fw=new FileWriter(dbFile);
			
			for(int i=0;i<list.size();i++) {
				fw.write(list.get(i).getDBWrite()+"\r\n");
			}
			
			fw.close();
			
		}catch(Exception e) {
			
		}
	}
	
	public User login(String id) {
		for(int i=0;i<userList.size();i++) {
			if(userList.get(i).getID().equals(id)) {
				return userList.get(i);
				
			}
		}
		return null;
	}
	
	public boolean checkPsw(String id,String psw) {
		for(int i=0;i<userList.size();i++) {
			if(userList.get(i).getID().equals(id)) {
				if(userList.get(i).getPSW().equals(psw)) return true;	
			}
		}
		return false;
	}
	
	public boolean isAvailable(String id,String psw) {
		
		for(int i=0;i<userList.size();i++) {
			if(userList.get(i).getID().equals(id)) return false;
		}
		
		userList.add(new User(id,psw));
		fileWrite(userList);
		
		//Calendar cal=Calendar.getInstance();
		//SimpleDateFormat sdf=new SimpleDateForm a
		return true;
		
	}
	
	public void signInUser(String id,String psw) {
		//userList.add(user);
		
	}
	
	public User getUserbyID(String id) {
		for(int i=0;i<userList.size();i++) {
			if(id.equals(userList.get(i).getID()))return userList.get(i);
		}
		
		return null;
	}
	
	
	
	
	
	
}
