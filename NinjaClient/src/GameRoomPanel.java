import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import itempack.ADoubleAttack;
import itempack.AHold;
import itempack.AMultiAttack;
import itempack.APowerUp;
import itempack.ASeeThroughAll;
import itempack.AttackItem;
import itempack.Item;
import itempack.PEnergy;
import itempack.PMoveUp;
import itempack.PSeeThrough;
import itempack.PShield;
import itempack.PTeleport;
import itempack.PassiveItem;

public class GameRoomPanel extends JPanel{
	private String roomNum;
	private String roomTitle;
	private ArrayList<User> playerList;
	private User me; User opponent;
	
	
	
	private Main_Client main_Client;
	private DataOutputStream dos;
	
	private JTextArea roomChat;
	private JTextField tf_chat;
	private JButton btn_send;
	private JTextArea onlineMembers;
	private JButton btn_waiting;
	private JButton btn_ready;
	
	
	private DrawingThread drawingThread;
	
	
	private int height,width;
	
	private Image imgBackground;
	
	private Image myImg;
	private int myX,myY;//내 이미지의 좌표
	private int myW,myH;//내 이미지의 절반 넓이, 절반 높이
	private int myRoomX,myRoomY;
	
	private Image openedDoor;
	private Image closedDoor;
	private Image itemDoor;
	private int doorX,doorY;
	
	private Image opImage;
	private int opX,opY;
	private int opW,opH;
	
	Image strAttack;
	Image strMove;
	Image strAttackSkip;
	Image strMoveSkip;
	Image strAItems;
	Image strMItems;
	Image startImg;
	Image morae;
	Image [] num;
	Image number;
	Image smallMe;
	Image smallOp;
	Image smallMetu;
	Image item;
	Image itemopa;
	Image dropItem;
	Image sign;
	
	
	
	int dropX,dropY;
	int dropDX,dropDY;
	
	private Room[][] rooms;
	
	int myHp,opHp;
	
	ArrayList<AttackItem> myAttackItems;
	ArrayList<PassiveItem> myPassiveItems;
	boolean myTurn;
	boolean attackable;
	boolean movable;
	boolean attackClicking;
	boolean moveClicking;
	boolean itemDropTime;
	boolean isAlive;
	boolean startImgshow;
	boolean roomspickable;
	boolean secondItem;
	boolean attacked;
	boolean moved;
	boolean gameRunning;
	
	boolean gameOverShow;
	
	
	boolean isRunning;
	boolean opTimerRunning;
	boolean myTimerRunning;
	
	boolean gameOver;
	
	int attackPower;
	
	
	
	
	
	
	public void setOpponent(String id) {
		System.out.println("id를 받았다."+id+" grp 69라인");
		this.opponent = new User(id);
		System.out.println("오포낸트 설정을 완료했다. grp71라인");
		
		
		me.setOpponent(opponent);
		System.out.println("내 오포낸트로 지정");
		opponent.setOpponent(me);
		System.out.println("상대의 오포낸트를 나로 지정");
	}
	
	public void setGameStart(boolean b) {
		
		
		myTurn=b;
		isAlive=true;
		attackable=false;
		movable=false;
		gameRunning=true;
		
		
		//checkHP죽는지 확인하는거있어야 함...
		
		
		
	}
	
	public void pickMyPlace() {
		boolean tmpTurn=myTurn;
		myTurn=true;
		pickableAll();
		roomspickable=true;
		myTimerRunning=true;
		new Thread() {
			@Override
			public void run() {
				try {
					Room pickedRoom;
					for(int i=19;i>-1;i--) {
						if(!myTimerRunning)return;
						pickedRoom=checkPicked();
						if(pickedRoom!=null) {
							pickedRoom.meExist=true;
							me.room=pickedRoom;
							myTurn=tmpTurn;
							roomspickable=false;
							myTimerRunning=false;
							disPickableAll();
							myRoomX=pickedRoom.getXpos();
							myRoomY=pickedRoom.getYpos();
							dos.writeUTF("GAME:FIRSTPICK:"+myRoomX+":"+myRoomY);
							dos.flush();
							
							return;
						}//눌렸는지 체크
						
						number=num[i/2];
						//그림 바꾸고
						Thread.sleep(500);
						//한번 잠들고
					}
					
					disPickableAll();
					myTimerRunning=false;
					Random rnd = new Random();
					int x=rnd.nextInt(5);
					int y=rnd.nextInt(5);
					pickedRoom=rooms[x][y];
					pickedRoom.meExist=true;
					me.room=pickedRoom;
					myTurn=tmpTurn;
					dos.writeUTF("GAME:FIRSTPICK:"+pickedRoom.getXpos()+":"+pickedRoom.getYpos());
					dos.flush();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		
	}
	
	public Room checkPicked(){
		for(int i=0;i<5;i++) {
			for(int j=0;j<5;j++) {
				if(rooms[i][j].isPicked) {
					rooms[i][j].isPicked=false;
					return rooms[i][j];
				}
			}
		}
		return null;
	}
	
	public void disPickedAll() {
		for(int i=0;i<5;i++) {
			for(int j=0;j<5;j++) {
				rooms[i][j].isPicked=false;
				
			}
		}
	}
	
	
	public void pickableAll() {
		for(int i=0;i<5;i++) {
			for(int j=0;j<5;j++) {
				rooms[i][j].pickable=true;
			}
		}
	}
	
	public void disPickableAll() {
		for(int i=0;i<5;i++) {
			for(int j=0;j<5;j++) {
				rooms[i][j].pickable=false;
				
			}
		}
	}
	
	public void resetRoom() {
		for(int i=0;i<5;i++) {
			for(int j=0;j<5;j++) {
				rooms[i][j].pickable=false;
				rooms[i][j].isPicked=false;
				rooms[i][j].isOpened=false;
				rooms[i][j].setItem(null);
				rooms[i][j].meExist=false;
				rooms[i][j].opExist=false;
				rooms[i][j].itemExist=false;
			}
		}
		me.setRoom(null);
		opponent.setRoom(null);
	}
	
	public void setOpoLocation(int x,int y) {
		for(int i=0;i<5;i++)
			for(int j=0;j<5;j++)
				rooms[j][i].opExist=false;
		rooms[y][x].opExist=true;
	}
	
	public void doMyTurn() {
		secondItem=true;
		myTurn=true;
		moveClicking=false;
		attackClicking=false;
		
		requestItem();
		
	}
	
	
	public void changeMyHp(int num) {
		myHp+=num;
	}
	
	public void changeOpHp(int num) {
		opHp+=num;
	}
	
	public void startShow() {
		startImgshow=true;
		System.out.println("보이게 변경했다.");
		
		
		try {
			System.out.println("잠이든다..");
			Thread.sleep(3000);
			System.out.println("잠이 깼다!");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startImgshow=false;
	}
	

	
	void requestItem() {
		try {
			
			Thread.sleep(1000);
			dos.writeUTF("GAME:REQUEST:ITEM");
			dos.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void putItem(int xi,int yi,int type) {
		itemDropTime=true;
		Item item=new Item();
		switch(type) {
		case 0:item=new APowerUp();break;
		case 1:item=new AHold();break;
		case 2:item=new ADoubleAttack();break;
		case 3:item=new AMultiAttack();break;
		case 4:item=new ASeeThroughAll();break;
		case 5:item=new PShield();break;
		case 6:item=new PMoveUp();break;
		case 7:item=new PTeleport();break;
		case 8:item=new PEnergy();break;
		case 9:item=new PSeeThrough();break;	
		}
		rooms[xi][yi].setItemExist(true);
		rooms[xi][yi].addItem(item);
		if(myTurn) {
			if(secondItem) {
				secondItem=false;
				requestItem();
			}else {
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				attackable=true;
				movable=true;
				roomspickable=false;
				doMyAction();
			}
		}
		
	}
	
	public void itemCheck() {
		System.out.println("아이템 체크하러 들어옴");
		
		if(me.room.itemExist) {
			System.out.println("아이템 있다.");
			
			Item i=me.room.getItem();
			if(i instanceof AttackItem) {
				if(myAttackItems.size()==3) return;
				myAttackItems.add((AttackItem)i);
				me.room.setItem(null);
				me.room.setItemExist(false);
				System.out.println("attack하나 먹었다.");
			}else {
				if(myPassiveItems.size()==3) return;
				myPassiveItems.add((PassiveItem)i);
				me.room.setItem(null);
				me.room.setItemExist(false);
				System.out.println("passive하나 먹었다.");
			}
			try {
				dos.writeUTF("GAME:ITEM:FOUND:"+me.room.getXpos()+":"+me.room.getYpos());
				dos.flush();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("지나감.");
	}
	
	public void doMyAction(){
		

		System.out.println("내 액션 수행");
		
		itemCheck();
		
		
		myTimerRunning=true;
		roomspickable=false;
		System.out.println("시간 시작된다.");
		TimerThread t=new TimerThread();
		t.start();
				
		
		
		
		
		
		
		
		
	}
	
	
	
	class TimerThread extends Thread{
			Room pickedRoom;
		@Override
		public void run() {
			try {
				
			
				
				for(int i=19;i>-1;i--) {
					
					pickedRoom=checkPicked();
					if(pickedRoom!=null) {
						
						if(movable) {
							
							me.room.meExist=false;
							me.room=pickedRoom;
							pickedRoom.meExist=true;
							roomspickable=false;
							myRoomX=me.room.getXpos();
							myRoomY=me.room.getYpos();
							moveClicking=false;
							movable=false;
							moved=true;
							myTimerRunning=false;
							
							if(attacked) {
								dos.writeUTF("GAME:MOVE:"+myRoomX+":"+myRoomY+":DONE");
								dos.flush();
								myTurn=false;
								moved=false;
								attacked=false;
								break;
							}else {
								dos.writeUTF("GAME:MOVE:"+myRoomX+":"+myRoomY+":STILL");
								dos.flush();
								attackable=true;
								doMyAction();
								break;
							}
						}if(attackable) {
							pickedRoom.isOpened=true;
							attacked=true;
							attackable=false;
							attackClicking=false;
							myTimerRunning=false;
							attackPower=30;
							
							if(pickedRoom.meExist)changeMyHp(-(int)(attackPower*0.7));
							if(pickedRoom.opExist)changeOpHp(-attackPower);
							if(opHp<=0) {
								
								opTimerRunning=false;
								myTurn=false;
								movable=false;
								attackable=false;
								moveClicking=false;
								attackClicking=false;
								roomspickable=false;
								dos.writeUTF("GAME:OVER");
								dos.flush();
								
							}else {
							
							
								if(moved) {
									dos.writeUTF("GAME:ATTACK:ONE:"+pickedRoom.getXpos()+":"+pickedRoom.getYpos()+":"+attackPower+":DONE");
									dos.flush();
									myTurn=false;
									moved=false;
									attacked=false;
									
									if(opHp<=0) {
										dos.writeUTF("GAME:OVER");
										dos.flush();
									}
	
								}else {
									dos.writeUTF("GAME:ATTACK:ONE:"+pickedRoom.getXpos()+":"+pickedRoom.getYpos()+":"+attackPower+":STILL");
									dos.flush();
									movable=true;
									doMyAction();
									if(opHp<=0) {
										dos.writeUTF("GAME:OVER");
										dos.flush();
									}
									
								}
							}
								
						}
						
						
					}
					
					
					if(i%2==0) {
						
						number=num[i/2];//그림 바꾸고
						//상대에게 내 시간을 보내주고.
						System.out.println("그림 바꿨다."+i/2);
						dos.writeUTF("GAME:OPTIMER:"+i/2);
						dos.flush();
					}
					
					if(!myTimerRunning) {
						break;
					}
					Thread.sleep(500);
				}
				
				//안눌렸으면.. 할 일.
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void gameOvered(String winlost) {
		opTimerRunning=false;
		myTimerRunning=false;
		myTurn=false;
		gameRunning=false;
		
		if(winlost.equals("LOST"))myHp=0;
		else opHp=0;
		sign=Toolkit.getDefaultToolkit().getImage("images/"+winlost+".png");
		gameOverShow=true;
		

		try {
			System.out.println("잠이든다..");
			Thread.sleep(5000);
			System.out.println("잠이 깼다!");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resetGame();
		
		gameOverShow=false;
		
		
	}
	
	public void resetGame() {
		myHp=100;
		opHp=100;
		
		btn_ready.setText("READY");
		me.setIsReady(false);

		resetRoom();
		
	}
	
	public void skippedMoving() {
		try {
			myTimerRunning=false;
			
			if(attacked) {
				myTurn=false;
				moved=false;
				attacked=false;
				dos.writeUTF("GAME:MOVE:SKIP:SKIP:DONE");
				dos.flush();
				
			}else {
				dos.writeUTF("GAME:MOVE:SKIP:SKIP:STILL");
				attackable=true;
				doMyAction();
			}
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void skippedAttacking() {
		try {
			myTimerRunning=false;
			roomspickable=false;
			attackClicking=false;
			if(moved) {
				myTurn=false;
				moved=false;
				attacked=false;
				dos.writeUTF("GAME:ATTACK:SKIP:SKIP:DONE");
				dos.flush();
			}else {
				movable=true;
				attacked=true;
				dos.writeUTF("GAME:ATTACK:SKIP:SKIP:STILL");
				dos.flush();
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void checkMovablePlaces() {
		int movableArea=1;
		int x=myRoomX; int y=myRoomY;
		System.out.println("내 룸위치 : "+myRoomX+"과 "+myRoomY);
		if(x-movableArea>-1) {
			rooms[y][x-movableArea].pickable=true;
			//System.out.println("배열로 따지면 위치["+y+"]["+(x-movableArea)+"] 이 배열의 xpos,ypos"+rooms[y][x-movableArea].Xpos+"  "+rooms[y][x-movableArea].Ypos );
			if(y-movableArea>-1)rooms[y-movableArea][x-movableArea].pickable=true;
			if(y+movableArea<5)rooms[y+movableArea][x-movableArea].pickable=true;
			//System.out.println("여기 바뀐거 맞나?"+rooms[y+movableArea][x-movableArea].Xpos+"  "+rooms[y-movableArea][x-movableArea].Ypos+"위치.."+rooms[y-movableArea][x-movableArea].pickable);
			
		}
		if(y-movableArea>-1)rooms[y-movableArea][x].pickable=true;
		if(y+movableArea<5)rooms[y+movableArea][x].pickable=true;
		//System.out.println("정아래 pickable? "+rooms[y+movableArea][x-movableArea].pickable);
		if(x+movableArea<5) {
			
			rooms[y][x+movableArea].pickable=true;
			
			if(y-movableArea>-1)rooms[y-movableArea][x+movableArea].pickable=true;
			
			if(y+movableArea<5)rooms[y+movableArea][x+movableArea].pickable=true;
			
		}
		
		
		
		
		
	}
	

	
	
	
	public void moveItemDrop(){
		int wh=32;
		dropX+=dropDX;
		dropY+=dropDY;
		if(dropX>width/2) {
			dropX=-40; dropY=100;
			dropDX=10;  dropDY=0;
			itemDropTime=false;
			//if(myTurn) makeMyMove();
		}
		
		if(dropX>width/4) {dropDX=10;dropDY=5;}
		else if(dropX>width/3) {dropDX=7;dropDY=7;}
		else if(dropX>width/2) {dropDX=5;dropDY=10;}
		
		
		
	}
	
	public void gotAttacked(int x,int y,int power) {
		Room r=rooms[y][x];
		r.isOpened=true;
		if(r.meExist) {
			changeMyHp(-power);
		}
		
		if(r.opExist) {
			changeOpHp(-(int)(power*0.7));
		}
		
	}
	
	
	

	public GameRoomPanel(String roomNum, String title, User me,int width,int height,DataOutputStream dos,Main_Client main_Client) {
		
		playerList=new ArrayList<>();
		playerList.add(me);
		roomTitle=title;
		this.roomNum=roomNum;
		this.width=width;
		this.height=height;
		this.dos=dos;
		me.setCurrentLocation(roomNum);
		this.main_Client=main_Client;
		doorX=146;doorY=135;
		
		myHp=100;
		opHp=100;
		
		myTurn=true;
		attackable=true;
		movable=true;
		
		rooms=new Room[5][5];
		
		int xxx=114,yyy=135;
		
		for(int i=0;i<5;i++) {
			rooms[i]=new Room[5];
			xxx=114;
			for(int j=0;j<5;j++) {
				rooms[i][j]=new Room(i,j,xxx,yyy);
				System.out.println("새로운 방을 넣었다. y="+i+", x="+j);
				xxx+=70;
			}
			yyy+=70;
		}
		
		myAttackItems=new ArrayList<>();
		myPassiveItems=new ArrayList<>();
		
		
		this.me=me;
		
		setLayout(new BorderLayout());
		Toolkit toolkit=Toolkit.getDefaultToolkit();
		myImg=toolkit.getImage("images/ninja_redbody.png");
		
		myW=32; myH=64;
		myImg=myImg.getScaledInstance(myW*2, myH*2, Image.SCALE_SMOOTH);
		myX=520;
		myY=410;
		
		smallMe=myImg.getScaledInstance(32, 64, Image.SCALE_SMOOTH);
		
		imgBackground=toolkit.getImage("images/sky_bg.jpg");
		imgBackground=imgBackground.getScaledInstance(width, height-200, Image.SCALE_SMOOTH);
		openedDoor=toolkit.getImage("images/opened.png");
		openedDoor=openedDoor.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
		
		closedDoor=toolkit.getImage("images/closed.png");
		closedDoor=closedDoor.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
		
		itemopa=toolkit.getImage("images/itemopa.png");
		
		opImage=toolkit.getImage("images/ninja_bluebody.png");
		opImage=opImage.getScaledInstance(64, 128, Image.SCALE_SMOOTH);
		opX=52;  opY=410;
		opW=32;  opH=64;
		
		smallOp=opImage.getScaledInstance(32, 64, Image.SCALE_SMOOTH);
		
		smallMetu=toolkit.getImage("images/ninjaopa.png");
		smallMetu=smallMetu.getScaledInstance(32, 64, Image.SCALE_SMOOTH);
		
		itemDoor=toolkit.getImage("images/item.png");
		itemDoor=itemDoor.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
		
		
		strAttack=toolkit.getImage("images/strattack.png");
		strAttack=strAttack.getScaledInstance(91, 29, Image.SCALE_SMOOTH);
		
		strMove=toolkit.getImage("images/strmove.png");
		strMove=strMove.getScaledInstance(91, 29, Image.SCALE_SMOOTH);
		
		strAttackSkip=toolkit.getImage("images/skipattack.png");
		strAttackSkip=strAttackSkip.getScaledInstance(91, 29, Image.SCALE_SMOOTH);
		
		strMoveSkip=toolkit.getImage("images/skipmove.png");
		strMoveSkip=strMoveSkip.getScaledInstance(91, 29, Image.SCALE_SMOOTH);
		
		strAItems=toolkit.getImage("images/strAItems.png");
		strAItems=strAItems.getScaledInstance(42, 29, Image.SCALE_SMOOTH);
		
		strMItems=toolkit.getImage("images/strMItems.png");
		strMItems=strMItems.getScaledInstance(42, 29, Image.SCALE_SMOOTH);
		
		startImg=toolkit.getImage("images/start.png");
		
		morae=toolkit.getImage("images/morae.png");
		
		dropItem=itemDoor;
		dropX=-40; dropY=100;
		dropDX=10;  dropDY=0;
		
		num=new Image[10];
		for(int i=0;i<10;i++) {
			num[i]=toolkit.getImage("images/num"+(i+1)+".png").getScaledInstance(32, 32, Image.SCALE_SMOOTH);
		}
		
		
		
		
		JPanel chatPlace=new JPanel();
		chatPlace.setLayout(new BorderLayout());
		chatPlace.setPreferredSize(new Dimension(width, 200));
	
		roomChat=new JTextArea();
		roomChat.setEditable(false);
		JScrollPane pane=new JScrollPane(roomChat);
		tf_chat=new JTextField();
		roomChat.setBackground(Color.black);
		roomChat.setForeground(Color.white);
		
		JPanel p=new JPanel();
		p.setLayout(new BorderLayout());
		p.add(pane,BorderLayout.CENTER);
		
		JPanel p1=new JPanel();
		p1.setLayout(new BorderLayout());
		p1.add(tf_chat, BorderLayout.CENTER);
		btn_send=new JButton("SEND");
		p1.add(btn_send,BorderLayout.EAST);
		
		p.add(p1,BorderLayout.SOUTH);
		
		tf_chat.setForeground(Color.WHITE);
		tf_chat.setBackground(Color.DARK_GRAY);
		btn_send.setBackground(Color.GRAY);
		btn_send.setForeground(Color.WHITE);
		
		chatPlace.add(p,BorderLayout.CENTER);
		
		onlineMembers=new JTextArea();
		
		onlineMembers.setBackground(Color.DARK_GRAY);
		onlineMembers.setForeground(Color.WHITE);
		
		onlineMembers.setText("   [현재 온라인인 멤버들]\n");
		onlineMembers.append( "-----------------------------------\n");
		onlineMembers.append("  "+me.getID()+" [방장]\n");
		
		
		
		
		
		
		ScrollPane sp=new ScrollPane();
		
		//sp.set
		
		JPanel spp=new JPanel();
		spp.setLayout(new BorderLayout());
		sp.add(onlineMembers);
		
		spp.add(sp,BorderLayout.CENTER);
		spp.setPreferredSize(new Dimension(160, 0));
		JPanel btns=new JPanel(new GridLayout(2, 0));
		
		btn_ready=new JButton("READY");
		btns.add(btn_ready);
		
		btn_ready.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String msg;
				
				if(btn_ready.getText().startsWith("게임"))return;
				if(me.getIsReady()) {
					btn_ready.setText("READY");
					roomChat.append("[SERVER] READY취소 하였습니다.\n");
					msg="GAME:READY:FALSE:"+me.getID();
					me.setIsReady(false);
				}else {
					msg="GAME:READY:TRUE:"+me.getID();
					roomChat.append("[SERVER] READY 하였습니다.\n");
					me.setIsReady(true);
					btn_ready.setText("READY 완료!");
				}
				try {
					dos.writeUTF(msg);
					dos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		});
		
		
		btn_waiting=new JButton("나가기");
		btn_waiting.setLocation(50, 50);
	
		//korea.add(btn_waiting);
		btn_waiting.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				
				try {
					if(gameRunning) {
						dos.writeUTF("GAME:EXIT");
						dos.flush();
						gameRunning=false;
					}
					dos.writeUTF(me.getCurrentLocation()+":EXIT");
					dos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//내가 있는 장소를 waiting으로 변경, 이걸 서버도 알아야 함.
				//더불어 패널 변경.
				me.setCurrentLocation("WAITING");
				myTurn=false;
				attackable=false;
				movable=false;
				
				attackClicking=false;
				moveClicking=false;
				itemDropTime=false;
				isAlive=false;
				startImgshow=false;
				roomspickable=false;
				
				isRunning=false;
				opTimerRunning=false;
				myTimerRunning=false;
				main_Client.changePanel();
				try {
					dos.writeUTF("WAITING:ONLINE");
					dos.writeUTF("WAITING:ROOMS");
					dos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		btns.add(btn_waiting);
		spp.add(btns,BorderLayout.SOUTH);
		
		
		chatPlace.add(spp,BorderLayout.EAST);
		onlineMembers.setEditable(false);

		
		add(chatPlace,BorderLayout.SOUTH);
		
		btn_send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				roomSent();
			}
		});
		
		tf_chat.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) 	roomSent();
			}
			@Override
			public void keyPressed(KeyEvent arg0) {}
		});
		
		drawingThread=new DrawingThread();
		drawingThread.start();
		
	}

	public void roomSent() {
		if(tf_chat.getText()==null||tf_chat.getText().length()==0)return;
		
		//System.out.println(me.getID());
		String msg=roomNum+":CHAT:"+me.getID()+":"+tf_chat.getText();
		try {
			dos.writeUTF(msg);
			dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tf_chat.setText("");
		System.out.println(msg+"를 서버에 전송했습니다.");
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		//화면 다 지운다.
		g.clearRect(0, 0, width, height-200);
		
		//배경이미지를 그린다.
		g.drawImage(imgBackground, 0, 0, this);
		
		
		
		g.setColor(Color.WHITE);
		g.fillRect(109, 130, 356, 356);
		
		
		
		for(int i=0;i<5;i++) {
			for(int j=0;j<5;j++) {
				Room room=rooms[i][j];
				if(room.isOpened) {
					g.drawImage(openedDoor, room.getX(), room.getY(), this);
					
					if(room.meExist&&room.opExist) {
						g.drawImage(smallOp, room.getX()+30, room.getY(), this);
						g.drawImage(smallMe, room.getX()+2, room.getY(), this);
						
					}else if(room.meExist) {
						g.drawImage(smallMe, room.getX()+16, room.getY(), this);
					}else if(room.opExist) {
						g.drawImage(smallOp, room.getX()+16, room.getY(), this);
					}
					
					if(room.itemExist) {
						g.drawImage(itemDoor, room.getX(), room.getY(), this);
					}
					
				}else {
					g.drawImage(closedDoor, room.getX(), room.getY(), this);
					
					if(room.meExist) {
					g.drawImage(smallMetu, room.getX()+16, room.getY(), this);
					
						if(room.itemExist) {
							g.drawImage(itemopa, room.getX(), room.getY(), this);
						}
					}
					
					
					
					
					
				}
				
				//System.out.println("room"+i+" "+j+" : " +(doorX-32)+","+(doorY-32)+"에 그림");
				
				//System.out.println(room.pickable);
				if(room.pickable) {
					g.setColor(Color.CYAN);
					g.fillRect(room.getX()-1, room.getY(), 66, 4);
					g.fillRect(room.getX()-1, room.getY()+60, 66, 4);
					g.fillRect(room.getX()-1, room.getY(), 4, 64);
					g.fillRect(room.getX()+62, room.getY(), 4, 64);
					
				}
				
			}
			
			
		}
		
		
		g.drawImage(myImg, myX-myW, myY-myH, this);
		g.drawImage(opImage, opX-opW, opY-opH, this);
		
		g.setColor(Color.WHITE);
		g.fillRoundRect(13, 470, 80, 16,7, 7);
		g.fillRoundRect(480, 470, 80, 16,7, 7);
		
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(17, 474, 72, 8);
		g.fillRect(484, 474, 72, 8);
		
		if((int)(opHp*72/100)>0) {
			g.setColor(new Color(0x1B75BB));
			g.fillRect(17, 474, (int)(opHp*72/100), 8);
		}
		if((int)(myHp*72/100)>0) {
			g.setColor(new Color(0xCE2027));
			g.fillRect(484, 474, (int)(myHp*72/100), 8);
		}
		
		g.setColor(new Color(0x422C15));
		g.fillRect(17, 510, 97, 35);
		g.fillRect(129, 510, 97, 35);
		g.fillRect(237, 510, 156, 35);
		g.fillRect(403, 510, 156, 35);
		
		Color c=new Color(0x975E1F);
		
		
		
		if(attackable)g.setColor(c);
		else g.setColor(Color.GRAY);
		g.fillRect(20, 513, 91, 29);
		
		if(movable)g.setColor(c);
		else g.setColor(Color.GRAY);
		g.fillRect(132, 513, 91, 29);
		
		if(myTurn)	g.setColor(c);
		else g.setColor(Color.GRAY);
		g.fillRect(240, 513, 150, 29);
		g.fillRect(406, 513, 150, 29);
		
		g.setColor(Color.WHITE);
		if(attackClicking) {
			g.drawImage(strAttackSkip, 20, 513,this);
		}else {
			g.drawImage(strAttack, 20, 513, this);
		}
		if(moveClicking) {
			g.drawImage(strMoveSkip, 132, 513, this);
		}else {
			g.drawImage(strMove, 132, 513, this);
		}
		
		g.drawImage(strAItems, 240, 512, this);
		
		g.drawImage(strMItems, 406, 512, this);
		
		int xx=288,yy=515;
		g.setColor(new Color(0xBE954F));
		for(int i=0;i<3;i++) {
			g.fillRect(xx, yy, 25, 25);
			xx+=35;
		}
		
		xx=454;
		for(int i=0;i<3;i++) {
			g.fillRect(xx, yy, 25, 25);
			xx+=35;
		}
		
		if(startImgshow) {
			g.drawImage(startImg, 60, 180,this);
			System.out.println("스타트 이미지 그렷다!!");
		}
		
		if(opTimerRunning) {
			g.drawImage(morae, 25, 285, this);
			g.drawImage(number, 36, 256, this);
		}
			
		if(myTimerRunning) {	
			g.drawImage(morae, 493, 285, this);
			g.drawImage(number, 504, 256, this);
		}
		
		if(itemDropTime) {
			g.drawImage(dropItem, dropX-32, dropY-32, this);
		}
		
		if(gameOverShow) {
			g.drawImage(sign, 60, 180, this);
			
		}
		
		
	}
	
	class DrawingThread extends Thread{
		@Override
		public void run() {
			isRunning=true;
			while(isRunning) {
				try {
					
					if(itemDropTime) {
						
						
						moveItemDrop();
					}
					//할일하고!
					repaint();
					//System.out.println("그렸다.");
					Thread.sleep(20);
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			
			
		}
	}
	
	public JTextField getTf_chat() {	return tf_chat;}
	public JTextArea getRoomChat() {	return roomChat;}
	public JButton getBtn_ready() {	return btn_ready;}
	public Room[][] getRooms() {	return rooms;}
	
	
}
