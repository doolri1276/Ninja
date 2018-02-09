import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Main_Server extends JFrame {
	
	//화면 꾸미는 멤버객체들
	private MainPanel mainPanel;
	
	private Image img_icon;
	private int icon_x,icon_y,icon_w,icon_h;
	private Image img_bg;
	
	//화면 사이즈
	private int width,height;
	
	//DB관련 멤버
	private NinjaDB DBManager;
	
	
	private ServerSocket mainServer=null;

	
	private ArrayList<User> userList;
	private ArrayList<UserThread> onlineUserList;//온라인인 아이들 여기에 존재하지..
	
	private JTextArea serverA, room1A,room2A,room3A,room4A;
	private GameManager gameManager;
	
	private ArrayList<Room> roomList;
	
	
	
	
	
	
	
	public void serverAappend(String msg) {
		serverA.append(msg+"\n");
		serverA.setCaretPosition(serverA.getText().length());
	}
	
	public void roomAppend(String roomNum,String msg) {
		switch(roomNum) {
		case("ROOM1"):
			room1Aappend(msg);
			break;
		case("ROOM2"):
			room2Aappend(msg);
			break;
		case("ROOM3"):
			room3Aappend(msg);
			break;
		case("ROOM4"):
			room4Aappend(msg);
			break;
		
		}
	}
	
	public void room1Aappend(String msg) {
		room1A.append(msg+"\n");
		room1A.setCaretPosition(room1A.getText().length());
	}
	
	public void room2Aappend(String msg) {
		room2A.append(msg+"\n");
		room2A.setCaretPosition(room2A.getText().length());
	}
	
	public void room3Aappend(String msg) {
		room3A.append(msg+"\n");
		room3A.setCaretPosition(room3A.getText().length());
	}
	
	public void room4Aappend(String msg) {
		room4A.append(msg+"\n");
		room4A.setCaretPosition(room4A.getText().length());
	}
	
	public Main_Server() {
		
		
		DBManager=new NinjaDB();
		
		DBManager.fileCreate();
		//System.out.println("파일을 만들었다.");
		DBManager.fileRead();
		//System.out.println("파일을 읽었다.");
		
		roomList=new ArrayList<>();
		gameManager=new GameManager(roomList);

		setTitle("Ninja Game");
		setSize(800, 600);
		setLocation(0, 100);
		//setLayout(null);
		
		ImageIcon imgicon=new ImageIcon("images/ninja_red256.png");
		imgicon=new ImageIcon(imgicon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH));
		JLabel icon=new JLabel(imgicon);
		add(icon,BorderLayout.NORTH);
		
		mainPanel=new MainPanel();
		
		
		add(mainPanel,BorderLayout.CENTER);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		
		//서버시작
		new MainServerThread().start();
		
		
		
		
		
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				super.windowClosing(arg0);
				DBManager.fileWrite(DBManager.userList);
				
				for(int i=0;i<onlineUserList.size();i++) {
					
						try {
							if(onlineUserList.get(i).dos!=null)
							onlineUserList.get(i).dos.close();
							
							if(onlineUserList.get(i).getMe().getMySocket()!=null)
								onlineUserList.get(i).getMe().getMySocket().close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}
				Main_Server.this.dispose();
			}
		
		});
		
		
		
	}

	public static void main(String[] args) {
		new Main_Server();

	}
	
	class MainPanel extends JPanel{
		
		public MainPanel() {
			setLayout(new GridLayout(0, 3));
			
			JPanel p1=new JPanel();
			p1.setLayout(new BorderLayout());
			JTextField text =new JTextField("                               [ServerField]");
			text.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			text.setEditable(false);
			text.setBackground(Color.WHITE);
			
			serverA=new JTextArea();
			serverA.setEditable(false);
			JScrollPane pane=new JScrollPane(serverA);
			
			p1.add(text,BorderLayout.NORTH);
			p1.add(pane,BorderLayout.CENTER);
			add(p1);
			
			
			
			
			
			JPanel p2=new JPanel();
			p2.setLayout(new GridLayout(2, 0));
			text=new JTextField("                               [Room 1]");
			text.setEditable(false);
			text.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			text.setBackground(Color.WHITE);
			room1A=new JTextArea();
			p1=new JPanel(new BorderLayout());
			p1.add(text,BorderLayout.NORTH);
			pane=new JScrollPane(room1A);
			p1.add(pane);
			p2.add(p1);
			
			text=new JTextField("                               [Room 2]");
			text.setEditable(false);
			text.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			text.setBackground(Color.WHITE);
			room2A=new JTextArea();
			p1=new JPanel(new BorderLayout());
			p1.add(text,BorderLayout.NORTH);
			pane=new JScrollPane(room2A);
			p1.add(pane);
			p2.add(p1);
			
			JPanel p3=new JPanel();
			p3.setLayout(new GridLayout(2, 0));
			text=new JTextField("                               [Room 3]");
			text.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			text.setEditable(false);
			text.setBackground(Color.WHITE);
			room3A=new JTextArea();
			p1=new JPanel(new BorderLayout());
			p1.add(text,BorderLayout.NORTH);
			pane=new JScrollPane(room3A);
			p1.add(pane);
			p3.add(p1);
			
			text=new JTextField("                               [Room 4]");
			text.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			text.setEditable(false);
			text.setBackground(Color.WHITE);
			room4A=new JTextArea();
			p1=new JPanel(new BorderLayout());
			p1.add(text,BorderLayout.NORTH);
			pane=new JScrollPane(room4A);
			p1.add(pane);
			p3.add(p1);

			add(p2);
			add(p3);
			
			//serverA.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			//room1A.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			//room2A.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			//room3A.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			//room4A.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		
		
		
	}
	
	class MainServerThread extends Thread{
		
		@Override
		public void run() {
			//서버오픈
			try {
				mainServer=new ServerSocket(12345);

				userList=new ArrayList<>();
				onlineUserList=new ArrayList<>();
				while(true) {
					serverAappend("[SERVER_INFO] 현재 인원 : "+onlineUserList.size());
					Socket socket=mainServer.accept();
					InetAddress ia=socket.getInetAddress();
					serverAappend("[SERVER_ENTER] "+(onlineUserList.size()+1)+" 번째 손님 입장 하셨습니다.");
					//serverA.append("["+(socketList.size()+1)+"] address : "+ia.getHostAddress()+"\n");

					UserThread ut=new UserThread(socket);//여기 손질해야한다.
					ut.start();
					onlineUserList.add(ut);
					
				}

			}catch(Exception e) {}
		}
		
	}
	
	public void removeUser() {
		for(int i=onlineUserList.size()-1;i>=0;i--) {
			UserThread ut=onlineUserList.get(i);
			if(!ut.getMe().getOnlineStatus()) {
	
				serverAappend("[SERVER_EXIT] "+ut.getIpAddress()+"님께서 퇴장하셨습니다.");
				
				onlineUserList.remove(i);
				
				serverAappend("[SERVER_INFO] 현재 인원 : "+onlineUserList.size());
			}
		}
	}
	
	public void stopThread() {
		
		synchronized (this) {
			this.notify();
		}
		
	}
	
	class UserThread extends Thread{
		String ipAddress;
		Socket mySocket;
		DataInputStream dis;
		DataOutputStream dos;
		User me;
		Room room;
		
		boolean user_isRun;
		
		public User getMe() {return me;	}
		public String getIpAddress() {	return ipAddress;}
		
		public UserThread(Socket socket) {
			mySocket=socket;
			ipAddress=socket.getInetAddress().getHostAddress();
			user_isRun=true;

		}
		
		
		
		@Override
		public void run() {
			try {
				
				dis=new DataInputStream(mySocket.getInputStream());
				new ReceiveThread().start();
			}catch(Exception e) {
				
				e.printStackTrace();
			}
		}
		
		
		class ReceiveThread extends Thread{
			String[] msg;
			String returnmsg="";
			boolean isRun;
			@Override
			public void run() {
				isRun=true;
				serverAappend("["+ipAddress+"] 객체 생성완료");
				byte[] buff=new byte[100];
				while(isRun) {
					try {
						//serverAappend(ipAddress+"받기를 기다리는 중입니다.");
						
						String str=dis.readUTF();
						

						
						msg=str.split(":");
						
						//serverAappend(mySocket.getInetAddress()+" ");

						//serverAappend(ipAddress+"  "+str+"을 전송받았습니다.");
						serverAappend("["+ipAddress+"_RECEIVE] 배열 : "+Arrays.toString(msg));
						
						
						if(msg[0].equals("LOGIN")) caseLogin();
						else if(msg[0].equals("GAME")) caseGame();
						else if(msg[0].equals("SIGNIN")) caseSignin();
						else if(msg[0].equals("WAITING")) caseWaiting();
						else if(msg[0].startsWith("ROOM")) {

							int num =Integer.parseInt(msg[0].charAt(4)+"");
							caseRoom1234(num);
						}
//						else if(msg[0].equals("ROOM3")) caseRoom3();
//						else if(msg[0].equals("ROOM4")) caseRoom4();
						else if(msg[0].equals("EXIT")) {
							me.setOnlineStatus(false);
							
							if(!me.currentLocation.equals("WAITING")) {
								String nummm=me.currentLocation.charAt(4)+"";
								String changed=gameManager.exitRoom(me,nummm);
								sendChatMsg(changed,"WAITING");
								
							}
							
							sendDeadMsg();
							me.setCurrentLocation("nowhere");
							try {
								dis.close();
								dos.close();
								mySocket.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							isRun=false;
							user_isRun=false;
							
							removeUser();
							
							
						}
						
						
					}catch(Exception e) {
						
						e.getStackTrace();
					}
					
				}
				
	
				
			}
			
			synchronized public void caseGame() {
				if(msg[1].equals("READY")) {
					
					caseGameReady();
					
				}else if(msg[1].equals("REQUEST")) {
					if(msg[2].equals("ITEM")) {
						roomAppend(me.getCurrentLocation(), "[GAME] Item Request");
						//String item=room.getRndItem();//GAME:ITEM:X위치:Y위치:아이템종류
						String item=getRndItem();
						roomAppend(me.getCurrentLocation(), "[GAME] "+item);
						try {
							dos.writeUTF(item);
							dos.flush();
							sendOpoMsg(item);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					}
				}else if(msg[1].equals("FIRSTPICK")) {
					
					caseGameFirstPick(msg[2],msg[3]);	
					
				}else if(msg[1].equals("OPTIMER")) {
					sendOpoMsg("GAME:OPTIMER:"+msg[2]);
				}else if(msg[1].equals("MOVE")) {
					roomAppend(me.currentLocation, "[GAME] "+me.getID()+"위치 : ["+msg[2]+"] ["+msg[3]+"]");
					sendOpoMsg("GAME:MOVE:"+msg[2]+":"+msg[3]+":"+msg[4]);
				}else if(msg[1].equals("ATTACK")) {
					if(msg[2].equals("SKIP")) {
						sendOpoMsg("GAME:ATTACK:SKIP:SKIP:"+msg[4]);
					}else if(msg[2].equals("ONE")){
						sendOpoMsg("GAME:ATTACK:ONE:"+msg[3]+":"+msg[4]+":"+msg[5]+":"+msg[6]);
					}else if(msg[2].equals("TWO")) {
						sendOpoMsg("GAME:ATTACK:TWO:"+msg[3]+":"+msg[4]+":"+msg[5]+":"+msg[6]+":"+msg[7]+":"+msg[8]);
					}
					
				}else if(msg[1].equals("ITEM")) {
					if(msg[2].equals("FOUND")) {
						sendOpoMsg("GAME:ITEM:FOUND:"+msg[3]+":"+msg[4]);
					}
				}else if(msg[1].equals("OVER")){
					room.isPlaying=false;
					try {
						me.setIsReady(false);
						me.getOpponent().setIsReady(false);
						me.wonTimesPlus();
						me.getOpponent().lostTimesPlus();
						dos.writeUTF("GAME:OVER:WIN");
						sendOpoMsg("GAME:OVER:LOST");
					}catch(Exception e) {
						e.printStackTrace();
					}
				}else if(msg[1].equals("EXIT")) {
						sendOpoMsg("GAME:EXIT");
						me.lostTimesPlus();
						me.getOpponent().wonTimesPlus();
						
						me.getOpponent().wonTimesPlus();
						me.lostTimesPlus();
						me.getOpponent().setOpponent(null);
						me.setOpponent(null);

				}
			}
			
			synchronized public void caseGameReady() {
				if(msg[2].equals("TRUE")) {
					
					
					me.isReady=true;
					roomAppend(me.currentLocation, "[SERVER] "+me.getID()+" READY");
					sendOpoMsg("GAME:READY:TRUE:"+me.id);
					if(me.getOpponent()==null) {
						
					}else if(room.setReady()) {
						roomAppend(me.currentLocation, "[GAME] == 게임 시작 ==");
						try {
							
							int rnd=new Random().nextInt(2);
							if(rnd==0) {
								me.playedTimesPlus();
								me.opponent.playedTimesPlus();
								room.isPlaying=true;
								roomAppend(me.currentLocation, "[GAME] 선공 : "+me.getID());
								dos.writeUTF("GAME:START:FIRST");
								dos.flush();
								sendOpoMsg("GAME:START:LAST");
							}else {
								roomAppend(me.currentLocation, "[GAME] 선공 : "+me.getOpponent().getID());
								dos.writeUTF("GAME:START:LAST");
								dos.flush();
								sendOpoMsg("GAME:START:FIRST");
							}
							
							
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					}
				}else {
					roomAppend(me.currentLocation, "[SERVER] "+me.getID()+"레디취소");
					try {
						me.getOpponent().getDos().writeUTF("GAME:READY:FALSE:"+me.id);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			synchronized public void caseGameFirstPick(String x,String y) {
				roomAppend(me.currentLocation, "[GAME] "+me.getID()+"위치 : ["+x+"] ["+y+"]");
				me.itemReady=true;
				me.setXY(x, y);
				if(me.opponent.getItemReady()) {
					me.itemReady=false;
					me.getOpponent().setItemReady(false);
					sendOpoMsg("GAME:FIRSTPICK:"+me.getX()+":"+me.getY());
					sendMsg("GAME:FIRSTPICK:"+me.getOpponent().getX()+":"+me.getOpponent().getY());
					
				}
			}
			
			public void caseLogin(){
				String id=msg[2];
				String psw=msg[3];
				serverAappend("["+ipAddress+"] LOGIN:CHECK ID ["+id+"] PSW ["+psw+"]");
				me=DBManager.login(id);
				if(me!=null) {
					if(me.onlineStatus) {returnmsg="LOGIN:FAIL:ONLINE";}
					else if(!DBManager.checkPsw(id,psw)) {
						serverAappend("["+ipAddress+"] LOGIN:FAIL:WRONGPSW");
						returnmsg="LOGIN:FAIL:WRONGPSW";
						sendMsg(returnmsg);
						return;
					}
					else {
						returnmsg="LOGIN:SUCCESS:"+me.getDBWrite();
						serverAappend("["+ipAddress+"] LOGIN:SUCCESS ->"+me.getID());
						
						ipAddress=me.id;
						me.setMySocket(mySocket);
						me.setOnlineStatus(true);
						me.setCurrentLocation("WAITING");
						sendMsg(returnmsg);
						
						serverAappend("["+me.getID()+"] WAITING으로 LO이동");
						userList.add(me);
						//sendChatMsg(gameManager.checkRoomState());
//						msg[0]="WAITING";
//						sendChatMsg(gameManager.checkRoomState());
//						serverAappend(gameManager.checkRoomState());
//						System.out.println("game manager serverA에 쌓음");
						
						return;
						}
					
				}else {
					serverAappend("["+ipAddress+"] LOGIN:FAIL:NOID");
					returnmsg="LOGIN:FAIL:NOID";sendMsg(returnmsg);
					sendMsg(returnmsg);
					return;
				}
				
			

			}
			
			public String getRndItem() {
				Random rnd=new Random();
				String iteminfo="GAME:ITEM:";
				
				int x=rnd.nextInt(5);
				int y=rnd.nextInt(5);
				
				int item=rnd.nextInt(10);
				iteminfo+=x+":"+y+":"+item;
				
				return iteminfo;
				
				
			}
			
			public void caseSignin() {

				String id=msg[2];
				String psw=msg[3];
				serverAappend("["+ipAddress+"] SIGNIN:CHECK ID ["+id+"] PSW ["+psw+"]");
				
				if(DBManager.isAvailable(id,psw)) {
					serverAappend("["+ipAddress+"] SIGNIN SUCCESS");
					returnmsg="SIGNIN:SUCCESS:";
				}else {
					serverAappend("["+ipAddress+"] SIGNIN FAIL");
					returnmsg="SIGNIN:FAIL:";
				}
				
				sendMsg(returnmsg);
				returnmsg="";
			}
		
			public void caseWaiting() {
				String location=msg[0];
				String command=msg[1];
				
				//serverAappend("wait에 들어옴"+command);
				
				if(command.equals("CHAT")) {
					String id=msg[2];
					String message=msg[3];
					serverAappend("[CHAT_"+id+"] "+message);
					String msg=location+":"+command+":"+id+":"+message;
					sendChatMsg(msg);
					
					
					
					
				}else if(command.equals("ONLINE")) {
					//serverAappend("온라인에 들어왔다.");
					String msg=location+":"+"ONLINE";
					for(int i=0;i<onlineUserList.size();i++) {
						UserThread ut=onlineUserList.get(i);
						msg+=":"+ut.getIpAddress();//아이디 목록들 여기에 추가될 예정임.
						
						//serverAappend("확인한 곳:"+ut.getIpAddress());
					}
					//serverAappend(msg);
					sendChatMsg(msg);
					//sendChatMsg(gameManager.checkRoomState());
					
					
					//serverAappend(gameManager.checkRoomState());
					//System.out.println("game manager serverA에 쌓음");
				}else if(msg[1].equals("ROOMS")) {
					try {
						for(int i=0;i<gameManager.getRoomList().size();i++) {
							Room r=gameManager.getRoomList().get(i);
							if(r.playerList.size()==2) {
								dos.writeUTF("WAITING:ROOM"+r.roomNum+":FULL:"+r.playerList.get(0).getID()+":"+r.playerList.get(1).getID()+":"+r.getRoomTitle());
								dos.flush();
							}else if(r.playerList.size()==1) {
								dos.writeUTF("WAITING:ROOM"+r.roomNum+":CHANGED:"+r.playerList.get(0).getID()+":---:"+r.getRoomTitle());
							}
						}
					}catch(Exception e) {
						
					}
				}
					
				
				
				
			}
			

			
			public void caseRoom1234(int roomNumber) {
				String command=msg[1];//CHAT
				
				if(msg[1].equals("CHAT")) {
					roomAppend(me.getCurrentLocation(),"[CHAT_"+me.id+"] " +msg[3]);
					
					
					sendChatMsg("ROOM:"+command+":"+me.id+":"+msg[3]);
					roomAppend(me.getCurrentLocation(),"[CHAT_"+me.id+"] " +msg[3]+"위치:"+me.getCurrentLocation());
					//msg[0]="WAITING";
				}else if(msg[1].equals("CREATE")) {
					caseRoomCreate(roomNumber,msg[3]);
				}else if(msg[1].equals("ENTER")) {
					caseRoomEnter(msg[0]);
				}else if(msg[1].equals("EXIT")) {
					caseRoomExit(roomNumber);
					
					
				}
	
			}
			
			synchronized public void caseRoomCreate(int roomNumber,String title){
				if(gameManager.createCheck(roomNumber)) {
					
					
					gameManager.createRoom(me,title,dos,roomNumber);
					
					try {
						me.setCurrentLocation("ROOM"+roomNumber);
						
						dos.writeUTF("ROOM0:ROOM"+roomNumber+":"+title);//만드는데 성공했고 그 번호는 num번이란다.
						dos.flush();
						String roomNum="ROOM"+roomNumber;
						roomAppend(roomNum,"["+roomNum+"_CREATE] "+me.id+"님께서 "+roomNumber+"번방 개설");
						dos.writeUTF("ROOM:CHAT:SERVER:"+me.id+"님께서 입장하셨습니다.");
						dos.flush();
						dos.writeUTF("ROOM:CHAT:SERVER:다음 사람이 입장할때 까지 대기합니다.");
						dos.flush();
						sendChatMsg("WAITING:"+roomNum+":CREATED:"+me.id+":"+title,"WAITING");
						room=me.getRoom();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
				}else {
					try {
						dos.writeUTF("ROOM0:FAIL");//만드는데 실패했다. 이미 4개의 방이 꽉차있어.
						dos.flush();
						String m=gameManager.getRoomMsg(roomNumber);
						dos.writeUTF(m);
						dos.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			synchronized public void caseRoomEnter(String m) {
				String enter=gameManager.enterRoom(me,m);
				
				try {
					me.setCurrentLocation(m);
					dos.writeUTF("ROOM:ENTER:"+m+":"+me.room.getRoomTitle()+":"+me.getOpponent().getID());
					dos.flush();
					
					roomAppend(m,"[SERVER_ENTER] "+me.id+"님 "+me.room.getRoomNum()+"번방 입장");
					//System.out.println("입장까지 씀. 635");
					//me.getOpponent().getDos().writeUTF("ROOM:OPOENTER:"+me.id);//이부분 수정함.
					//System.out.println("입장한 사실을 알림. 637");
					sendChatMsg(enter,"WAITING");
					
					sendOpoMsg("ROOM:OPOENTER:"+me.getID()+":NULL");
					//System.out.println("main opomsg사용해본다.");
					sendChatMsg("ROOM:CHAT:SERVER:"+me.id+"님께서 입장하셨습니다.");
					
					sendChatMsg("ROOM:CHAT:SERVER:준비 버튼을 눌러주세요.");
					//sendChatMsg("ROOM:READY:"+me.getID());
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			synchronized public void caseRoomExit(int roomNumber) {
				String changed=gameManager.exitRoom(me,roomNumber+"");
				roomAppend("ROOM"+roomNumber,"[ROOM"+roomNumber+"_EXIT] "+me.getID()+ "님께서 퇴장하셨습니다.");
				if(changed.endsWith("REMOVED")) {
					roomAppend("ROOM"+roomNumber, "[ROOM"+roomNumber+"_DELETE] "+roomNumber+"번 방이 폐쇄되었습니다.");
					roomAppend("ROOM"+roomNumber, "===================================");
				}
				sendChatMsg(changed,"WAITING");
				me.setIsReady(false);
				
			}
			
			public void sendChatMsg(String message) {
				//받은 메세지 온라인이고 방마다 다 보내는 거 해야한다.
				
				for(int i=0;i<onlineUserList.size();i++) {
					UserThread user=onlineUserList.get(i);
					//serverAappend(user.getID()+"쪽을 확인중입니다.");
					//serverAappend(user.getOnlineStatus()+"  "+user.getCurrentLocation());
					if(!user.getMe().getOnlineStatus()) continue;
						
					
					if(user.getMe().getCurrentLocation().equals(msg[0])) {
						//serverAappend("보내려구 합니다.");
						sendMsg(message, user.getMe().getMySocket());
						serverAappend("["+me.getID()+"_SEND] "+user.getMe().getID()+" 전송 ["+message+"]");
					}
					
				}
				
			}
			
			public void sendChatMsg(String message,String location) {
				//받은 메세지 온라인이고 방마다 다 보내는 거 해야한다.
				
				for(int i=0;i<onlineUserList.size();i++) {
					UserThread user=onlineUserList.get(i);
					//serverAappend(user.getMe().getID()+"쪽을 확인중입니다.");
					//serverAappend(user.getMe().getOnlineStatus()+"  "+user.getMe().getCurrentLocation());
					if(!user.getMe().getOnlineStatus()) continue;
						
					
					if(user.getMe().getCurrentLocation().equals(location)) {
						//serverAappend("보내려구 합니다.");
						sendMsg(message, user.getMe().getMySocket());
						serverAappend("롤로"+user.getMe().getID()+"에 "+message+"를 보냈습니다.");
					}
					
				}
				
			}
			
			public void sendDeadMsg() {
				msg[0]=me.getCurrentLocation();
				me.setOnlineStatus(false);
				String message=msg[0]+":"+"CHAT"+":"+"SERVER"+":"+ipAddress+"님께서 퇴장하셨습니다.";
				serverAappend(message);
				
				
				
				
				
				String str=msg[0]+":"+"ONLINE";
				for(int i=0;i<onlineUserList.size();i++) {
					UserThread ut=onlineUserList.get(i);
					if(!ut.getMe().onlineStatus)continue;
					str+=":"+ut.getIpAddress();//아이디 목록들 여기에 추가될 예정임.
					serverAappend("확인한 곳:"+ut.getIpAddress());
				}
				serverAappend(str);
				sendChatMsg(str);
			
				
				for(int i=0;i<onlineUserList.size();i++) {
					User user=onlineUserList.get(i).getMe();
					
					//if(!user.getOnlineStatus()) continue;

					if(user.getCurrentLocation().equals(msg[0])) {
						serverAappend(user.getID()+"에 보냄"+str);
						sendMsg(message, user.getMySocket());
						sendMsg(str,user.getMySocket());
					}
					
				}
				
				
				
				
			}
		
			
		}
		
		
		public void sendMsg(String msg) {
			new Thread() {
				@Override
				public void run() {
					try {
				dos=new DataOutputStream(mySocket.getOutputStream());
				dos.writeUTF(msg);
				dos.flush();
				
					}catch(Exception e) {}
				}
			}.start();
		}
		
		public void sendMsg(String msg,Socket so) {
			new Thread() {
				@Override
				public void run() {
					try {
						DataOutputStream dos=new DataOutputStream(so.getOutputStream());
						dos.writeUTF(msg);
						dos.flush();
						
					}catch(Exception e) {}
				}
			}.start();
		}
		
		public void sendOpoMsg(String msg) {
			new Thread() {
				@Override
				public void run() {
					try {
						DataOutputStream dos=new DataOutputStream(me.getOpponent().mySocket.getOutputStream());
						
						dos.writeUTF(msg);
						dos.flush();
						System.out.println("main opo에 메세지 보내졌음 946"+msg);
						roomAppend(me.getCurrentLocation(), me.getOpponent().getID()+"Opo :"+msg);
						
					}catch(Exception e) {}
				}
			}.start();
		}
		
	}
	
	

	
	
}








