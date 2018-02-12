import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;




public class Main_Client extends JFrame {
	
	//네트워크
	private Socket mySocket;
	
	private MainPanel mainPanel;
	private LoginPanel loginPanel;
	private SigninFrame signInFrame;
	private WaitingPanel waitingPanel;
	private ReceiveThread receiveThread;

	
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private Image img_icon;
	private int icon_x,icon_y,icon_w,icon_h;
	private Image img_bg;
	
	private int width,height;
	
	private User me;
	
	
	private GameRoomPanel gameRoomPanel;
	JTextArea roomChat;
	
	
	
	
	public Socket getMySocket() {return mySocket;}
	public User getMe() {		return me;}
	public DataOutputStream getDos() {return dos;}
	
	
	
	public Main_Client() {
		setTitle("Ninja Game");
		setSize(600, 800);
		setLocation(300, 100);
		setLayout(new BorderLayout());
		setResizable(false);
		
		mainPanel=new MainPanel();
		mainPanel.setLayout(null);
		
		loginPanel=new LoginPanel();
		
		Toolkit toolkit=Toolkit.getDefaultToolkit();
		img_icon=toolkit.getImage("images/ninja_red256.png");
		img_icon=img_icon.getScaledInstance(128, 128, Image.SCALE_SMOOTH);
		icon_w=64;
		icon_h=64;
		
		
		mainPanel.add(loginPanel);
		add(mainPanel);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		
		//네트워크 연결
		try {
			mySocket=new Socket("127.0.0.1",12345);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		receiveThread=new ReceiveThread();

		
		receiveThread.start();

		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				
				super.windowClosing(e);
				if(gameRoomPanel!=null)	gameRoomPanel.isRunning=false;
				try {
					if(dos!=null) {
					dos.writeUTF("EXIT");
					dos.flush();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//스레드 종료작업
				receiveThread.stopThread();
				//프레임창 종료
				Main_Client.this.dispose();
				
			}
		});
		
		addMouseListener(new MouseAdapter() {
			int mX,mY;
			@Override
			public void mouseReleased(MouseEvent e) {
				if(me==null)return;
				if(!me.getCurrentLocation().startsWith("ROOM")) return;
				mX=e.getX()-10;
				mY=e.getY()-30;
				
				if(gameRoomPanel==null)return;
				
				if(gameRoomPanel.myTurn) {
					if(!gameRoomPanel.gameRunning) return;
					System.out.println("myturn이라 선택가능");
					if(gameRoomPanel.roomspickable) {//방선택이 가능한 경우
					
						for(int i=0;i<5;i++) {
							for(int j=0;j<5;j++) {
								Room room=gameRoomPanel.getRooms()[i][j];
								if(room.pickable&&room.checkLocation(mX, mY)) {
									System.out.println("방이 선택됬다! "+room.Xpos+"  "+room.Ypos);
									gameRoomPanel.disPickableAll();
									room.isPicked=true;
									return;
								}
								
							}
						}
						if(mX>17&&mX<114&&mY>510&&mY<545&&gameRoomPanel.attackClicking) {
							System.out.println("skip attack을 눌렀다.");
							gameRoomPanel.skippedAttacking();
 
							return;
						}else if(gameRoomPanel.moveClicking&&mX>129&&mX<226&&mY>510&&mY<545) {//움직였던 경우.
							System.out.println("skipmove를 눌렀다.");
							gameRoomPanel.skippedMoving();
							return;
						}
						
					}else if(gameRoomPanel.attackable||gameRoomPanel.movable) {

						if(gameRoomPanel.attackable&&mX>17&&mX<114&&mY>510&&mY<545) {
							System.out.println("어택버튼을 눌렀다.");
								gameRoomPanel.attackClicking=true;
								gameRoomPanel.movable=false;
								gameRoomPanel.roomspickable=true;
								gameRoomPanel.pickableAll();
								return;
						
						}else if(gameRoomPanel.movable&&mX>129&&mX<226&&mY>510&&mY<545) {
							System.out.println("move버튼을 눌렀다.");
							gameRoomPanel.moveClicking=true;
							gameRoomPanel.attackable=false;
							gameRoomPanel.roomspickable=true;
							gameRoomPanel.checkMovablePlaces();
							return;
						}
						
						
					}
					
				}
			}
		});
		

	}

	public static void main(String[] args) {
		new Main_Client();

	}
	
	class MainPanel extends JPanel{
		
		
		
		@Override
		protected void paintComponent(Graphics g) {
			if(width==0||height==0) {
				width=getWidth();
				height=getHeight();
				
			}
			
			g.drawImage(img_icon, width/2-icon_w, 150,this);
		}
		
	}
	
	class LoginPanel extends JPanel{
		
		JLabel id,psw;
		JTextField tf_id;
		JPasswordField tf_psw;
		JButton signIn,login;
		
		
		public LoginPanel() {
			setLayout(new GridLayout(5, 0));
			setBounds(170,300,250,200);
			//setBackground(Color.YELLOW);
			setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
			
			id=new JLabel("   ID ");
			//id.setBounds(100, 50, 40, 20);
			id.setFont(id.getFont().deriveFont(15.0F));
			add(id);
			
			JPanel tmp=new JPanel();
			
			tf_id=new JTextField(20);
			tmp.add(tf_id);
			add(tmp);
			
			psw=new JLabel("   PASSWORD ");
			psw.setFont(psw.getFont().deriveFont(15.0F));
			add(psw);
			
			tmp=new JPanel();
			tf_psw=new JPasswordField(20);
			tmp.add(tf_psw);
			add(tmp);
			
			
			tmp=new JPanel();
			signIn=new JButton("Sign in");
			signIn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					signInFrame=new SigninFrame();
					
				}
			});
			tmp.add(signIn);
			JLabel blank=new JLabel("     ");
			tmp.add(blank);
			login=new JButton("Log in");
			tmp.add(login);
			add(tmp);
			//tmp.setBounds(arg0, arg1, arg2, arg3);
			
			//로그인 버튼에 능력 추가
			login.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					loginBtnClicked();	
				}
			});
			
			tf_psw.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					if(e.getKeyCode()==KeyEvent.VK_ENTER) loginBtnClicked();
				}
				
				@Override
				public void keyPressed(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
			
			
			
		}
		
		public void loginBtnClicked() {

			if(tf_id.getText().length()==0||tf_psw.getText().length()==0) {
				JOptionPane.showMessageDialog(null, "입력칸이 빈칸입니다.");
				return;
			}
			
			String id=tf_id.getText();
			String psw=tf_psw.getText();
			
			String msg="LOGIN:CHECK:"+id+":"+psw;

			try {
				dos.writeUTF(msg);
				dos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		

		}
		
		
		public JTextField getTf_id() {
			return tf_id;
		}
		
		public JPasswordField getTf_psw() {
			return tf_psw;
		}
		
		
		
	}
	
	
	
	
	
	class ReceiveThread extends Thread{
		String[] msg;
		
		boolean isRun=true;
		
		@Override
		public void run() {
			try {
				dis=new DataInputStream(mySocket.getInputStream());
				dos=new DataOutputStream(mySocket.getOutputStream());
				
				while(isRun) {
	
					msg=dis.readUTF().split(":");
										
					if(msg[0].equals("LOGIN")) caseLogin();
					else if(msg[0].equals("SIGNIN")) caseSignin();
					else if(msg[0].equals("WAITING")) caseWaiting();
					else if(msg[0].equals("ROOM0")) caseRoom0();
					else if(msg[0].equals("ROOM")) caseRoom();
					else if(msg[0].equals("GAME")) caseGame();
					
						
					
					//msg=null;
				}//while
				
				dos.close();
				
			}catch(Exception e) {}			
			
		}
		
		public void stopThread() {
			isRun= false;
			synchronized (this) {
				this.notify();
			}
			
		}
		
		
		public void caseGame() {
			if(msg[1].equals("READY")) {
				if(msg[2].equals("TRUE")) {
					me.opponent.setIsReady(true);
					roomChat.append("[SERVER] "+msg[3]+"님께서 READY되었습니다.\n");
				}else {
					me.opponent.setIsReady(false);
					roomChat.append("[SERVER] "+msg[3]+"님께서 READY취소 하였습니다.\n");
				}
				
				
			}else if(msg[1].equals("START")) {
				
				gameRoomPanel.getBtn_ready().setText("게임 진행중");
				
				gameRoomPanel.startShow();
				
			
				me.playedTimesPlus();
				if(msg[2].equals("FIRST")) {
					gameRoomPanel.setGameStart(true);
				}else {
					gameRoomPanel.setGameStart(false);
				}
				gameRoomPanel.pickMyPlace();
				
			}else if(msg[1].equals("ITEM")) {
				if(msg[2].equals("FOUND")) {
					int x=Integer.parseInt(msg[3]);
					int y=Integer.parseInt(msg[4]);
					gameRoomPanel.getRooms()[y][x].setItem(null);
					gameRoomPanel.getRooms()[y][x].setItemExist(false);
				}else {
					int x=Integer.parseInt(msg[2]);
					int y=Integer.parseInt(msg[3]);
					int type=Integer.parseInt(msg[4]);
					gameRoomPanel.putItem(x,y,type);
				}
				
			}else if(msg[1].equals("FIRSTPICK")) {
				int ox=Integer.parseInt(msg[2]);
				int oy=Integer.parseInt(msg[3]);
				gameRoomPanel.setOpoLocation(ox,oy);
				if(gameRoomPanel.myTurn)
					gameRoomPanel.doMyTurn();
				
			}else if(msg[1].equals("OPTIMER")) {
				if(msg[2].equals("9")) {
					gameRoomPanel.opTimerRunning=true;
				}
				int n=Integer.parseInt(msg[2]);
				gameRoomPanel.number=gameRoomPanel.num[n];
				if(n==0) {
					gameRoomPanel.opTimerRunning=false;
				}
			}else if(msg[1].equals("MOVE")) {
				gameRoomPanel.opTimerRunning=false;
				if(msg[2].equals("SKIP")) {
					if(msg[4].equals("DONE")) {
						gameRoomPanel.myTurn=true;
						gameRoomPanel.doMyTurn();
					}
				}else {
					int x=Integer.parseInt(msg[2]);
					int y=Integer.parseInt(msg[3]);
					gameRoomPanel.setOpoLocation(x, y);
					if(msg[4].equals("DONE")) {
						gameRoomPanel.myTurn=true;
						gameRoomPanel.doMyTurn();
					}
				}
			}else if(msg[1].equals("ATTACK")) {
				gameRoomPanel.opTimerRunning=false;
				if(msg[2].equals("SKIP")) {
					if(msg[4].equals("DONE")) {
						gameRoomPanel.myTurn=true;
						gameRoomPanel.doMyTurn();
					}
				}else if(msg[2].equals("ONE")) {
					int x=Integer.parseInt(msg[3]);
					int y=Integer.parseInt(msg[4]);
					int power=Integer.parseInt(msg[5]);
					gameRoomPanel.gotAttacked(x, y, power);
					if(msg[6].equals("DONE")) {
						gameRoomPanel.myTurn=true;
						gameRoomPanel.doMyTurn();
					}
					
				}
			}else if(msg[1].equals("OVER")) {
				
				gameRoomPanel.gameOvered(msg[2]);
			}else if(msg[1].equals("EXIT")) {
				
			}
		}
		
		synchronized public void caseLogin() {
			
			if(msg[1].equals("SUCCESS")) {

				User user=new User();
				user.setUserCode(msg[2]);
				user.setID(msg[3]);
				user.setPSW(msg[4]);
				
				user.setPlayedTimes(msg[5]);
				user.setWon(msg[6]);
				user.setLost(msg[7]);
				
				me=user;

				JOptionPane.showMessageDialog(null, "로그인에 성공했습니다.");
				me.setCurrentLocation("WAITING");

				changePanel();

			}else if(msg[1].equals("FAIL")) {

				loginPanel.getTf_id().setText("");
				loginPanel.getTf_psw().setText("");
				if(msg[2].equals("ONLINE"))JOptionPane.showMessageDialog(null, "해당 아이디는 이미 로그인 된 상태입니다.");
				else if(msg[2].equals("NOID"))JOptionPane.showMessageDialog(null, "해당 아이디는 존재하지 않습니다.\r\n회원가입을 해주세요.");
				else if(msg[2].equals("WRONGPSW"))JOptionPane.showMessageDialog(null, "잘못된 비밀번호입니다.");
				else JOptionPane.showMessageDialog(null, "로그인에 실패했습니다.\r\n아이디나 비밀번호를 확인해주세요.");
			
			}
			
		}
		
		public void caseSignin() {
			if(msg[1].equals("SUCCESS")) {
				JOptionPane.showMessageDialog(null, "회원가입에 성공 했습니다.");
				signInFrame.dispose();
			}else {
				JOptionPane.showMessageDialog(null, "회원가입에 실패했습니다.");
			}
			
		}
		
		public void caseWaiting() {

			
			if(msg[1].equals("CHAT")) caseChat();
			else if(msg[1].equals("ONLINE")) {
				JTextArea online=waitingPanel.getOnlineMembers();
				online.setText("   [현재 온라인인 멤버들]\n");
				online.append( "-----------------------------------\n");
				for(int i=2;i<msg.length;i++) {

					online.append("  "+msg[i]+"\n");
				}
				online.append( "-----------------------------------\n");
				
			}else if(msg[1].equals("ROOM")) {
				if(msg[2].equals("CREATED")) {
					for(int i=0;i<waitingPanel.getState().length;i++) {
						if(waitingPanel.getState()[i].getText().equals("빈   방")) {
							waitingPanel.setState(i, msg[4]);
							waitingPanel.setPlayer1(i, msg[3]);
							waitingPanel.setRoomb(i, "[모집중] 입장 하기");
							
						}
					}
				}
			}else if(msg[1].equals("ROOM1")) {
				caseRoom1234(0);
			}else if(msg[1].equals("ROOM2")) {
				caseRoom1234(1);
			}else if(msg[1].equals("ROOM3")) {
				caseRoom1234(2);
			}else if(msg[1].equals("ROOM4")) {
				caseRoom1234(3);
			}
			
			
			
		}
		
		public void caseChat() {//메세지를 받았는데 채팅의 명령어인경우
			String id=msg[2];
			String message=msg[3];
			
			JTextArea mainChat=waitingPanel.getMainChat();
			String msg=" ["+id+"] "+message;
			
			mainChat.append(msg+"\n");
			mainChat.setCaretPosition(mainChat.getText().length());
			
		}
		
		public void caseRoom0() {
			String state=msg[1];
			String title=msg[2];
			if(state.equals("FAIL")) {
				JOptionPane.showMessageDialog(null, "방 만들기에 실패하였습니다.");
			}else {
				me.setCurrentLocation(state);
				
				JOptionPane.showMessageDialog(null, "방 만들기에 성공하였습니다.");
				
				waitingPanel.closeJFrame();
				changeRoom(msg[1],msg[2]);
				
				
			}
		}
		
		public void caseRoom() {
			
			String id=msg[2];
			String message=msg[3];
			
			if(msg[1].equals("CHAT")) {
				
				String msg=" ["+id+"] "+message;
				
				roomChat.append(msg+"\n");
				roomChat.setCaretPosition(roomChat.getText().length());
			}else if(msg[1].equals("ENTER")) {//들어갔을때 이뤄져야 할 일들
				changeRoom(msg[2],msg[3]);
				gameRoomPanel.setOpponent(msg[4]);
				
			}else if(msg[1].equals("OPOENTER")) {

				gameRoomPanel.setOpponent(msg[2]);
				
			}else if(msg[1].equals("READY")) {
			
				if(msg[2].equals("TRUE")) {
					
					roomChat.append("[SERVER] "+msg[3]+"님께서 레디하였습니다.\n");
					roomChat.setCaretPosition(roomChat.getText().length());
					
				}else {
					roomChat.append("[SERVER] "+msg[3]+"님께서 레디 취소하였습니다.\n");
					roomChat.setCaretPosition(roomChat.getText().length());
				}
			}
		}
		
		synchronized public void caseRoom1234(int num) {
			if(msg[2].equals("CREATED")) {
				waitingPanel.setState(num, msg[4]);
				waitingPanel.setPlayer1(num, msg[3]);
				waitingPanel.setRoomb(num, "[대기] 입장 하기");
			}else if(msg[2].equals("REMOVED")) {
				waitingPanel.setState(num, "빈   방");
				waitingPanel.setPlayer1(num, "---");
				waitingPanel.setPlayer2(num, "---");
				waitingPanel.setRoomb(num, "[빈방] 방 개설하기");
			}else if(msg[2].equals("CHANGED")) {
	
				waitingPanel.setPlayer1(num, msg[3]);
				waitingPanel.setPlayer2(num, "---");
				waitingPanel.setRoomb(num, "[대기] 입장 하기");
				if(msg[5]!=null) {
					waitingPanel.setState(num, msg[5]);
				}
			}else if(msg[2].equals("FULL")) {
				waitingPanel.setPlayer1(num, msg[3]);
				waitingPanel.setPlayer2(num, msg[4]);
				waitingPanel.setRoomb(num, "[만실] 입장불가");
				if(msg[5]!=null) {
					waitingPanel.setState(num, msg[5]);
				}
			}
		}
		
		
		
	}//class ReceieveThread............
	

	
	class SigninFrame extends JFrame {
		
		SigninPanel signinPanel;		
		
		
		public SigninFrame() {
			setTitle("Sign In");
			setSize(400, 300);
			setLocation(400, 300);
			setLayout(null);


			signinPanel=new SigninPanel();
			
			
			
			
			add(signinPanel);
			setVisible(true);
		}
		
		class SigninPanel extends JPanel{
			
			JTextField tf_id;
			JPasswordField tf_psw;
			JButton back,signin;
			
			public SigninPanel() {
				setLayout(new GridLayout(5, 0));
				setBounds(70,35,250,200);
				setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
				
				JLabel id,psw;
				
				
				id=new JLabel("   ID ");
				id.setFont(id.getFont().deriveFont(15.0F));
				//id.setBorder(BorderFactory.createLineBorder(Color.WHITE));
				add(id);
				
				JPanel tmp=new JPanel();
				
				tf_id=new JTextField(20);
				tmp.add(tf_id);
				add(tmp);
				
				psw=new JLabel("   PASSWORD ");
				psw.setFont(psw.getFont().deriveFont(15.0F));
				add(psw);
				
				tmp=new JPanel();
				tf_psw=new JPasswordField(20);
				tmp.add(tf_psw);
				add(tmp);
				
				
				tmp=new JPanel();
				back=new JButton("Reset");
				back.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						tf_id.setText("");
						tf_psw.setText("");
						
						
					}
				});
				tmp.add(back);
				JLabel blank=new JLabel("     ");
				tmp.add(blank);
				signin=new JButton("Sign in");
				signin.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						signinBtnClicked();
						
						
						
					}
				});
				
				tmp.add(signin);
				add(tmp);
				
				tf_psw.addKeyListener(new KeyListener() {
					@Override
					public void keyTyped(KeyEvent arg0) {}
					@Override
					public void keyReleased(KeyEvent e) {
						if(e.getKeyCode()==KeyEvent.VK_ENTER) signinBtnClicked();
					}
					@Override
					public void keyPressed(KeyEvent arg0) {}
				});
				
			}
			
			public void signinBtnClicked() {
				if(tf_id.getText().length()==0||tf_psw.getText().length()==0) {
					JOptionPane.showMessageDialog(null, "입력칸이 빈칸입니다.");
					return;
				}
				
				String msg="SIGNIN:CHECK:"+tf_id.getText()+":"+tf_psw.getText();
				try {
					dos.writeUTF(msg);
					dos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
			
			
		}

	}
	
	void changePanel() {
		
		getContentPane().removeAll();
		
		waitingPanel=new WaitingPanel(width,height,this);

		add(waitingPanel,BorderLayout.CENTER);
		if(gameRoomPanel!=null)	gameRoomPanel.isRunning=false;

		revalidate();
		repaint();

		try {
			dos.writeUTF("WAITING:ONLINE");
			dos.flush();
			dos.writeUTF("WAITING:ROOMS");
			dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		if(waitingPanel.getOnlineMembers().getText().length()==0) {
			try {
				dos.writeUTF("WAITING:ONLINE");
				dos.flush();
			}catch(Exception e) {
				
			}
		}
		
	}
	
	void changeRoom(String num,String title) {

		getContentPane().removeAll();

		gameRoomPanel=new GameRoomPanel(num,title,me,width,height,dos,this);

		
		add(gameRoomPanel,BorderLayout.CENTER);
		
		revalidate();
		repaint();

		roomChat=gameRoomPanel.getRoomChat();
		
		
	}
	
	
	
}
















