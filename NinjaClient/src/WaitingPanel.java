import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class WaitingPanel extends JPanel{



	private int width;
	private int height;
	private Main_Client main_Client;
	
	private ImageIcon banner;
	private JTextArea mainChat;
	private JTextField tf_chat;
	private JTextArea onlineMembers;
	private JButton btn_send;
	private JLabel i_id,i_line,i_pt,i_won,i_lost,i_rate,i_rank;
	private JFrame j;
	
	private JButton[] roomb;
	private JLabel[] state;
	private JLabel[] player1;
	private JLabel[] player2;
	
	private User me;
	
	private DataOutputStream dos;
	
	
	
	public JTextField getTf_chat() {return tf_chat;	}
	public JTextArea getMainChat() {return mainChat;}
	public JTextArea getOnlineMembers() {return onlineMembers;}
	public JButton[] getRoomb() {return roomb;}
	public JLabel[] getState() {return state;}
	public JLabel[] getPlayer1() {return player1;}
	public JLabel[] getPlayer2() {return player2;}
	public JFrame getJ() {	return j;}
	
	
	public void setState(int num,String s) {
		System.out.println("setState에 들어왔다.");
		System.out.println(state[num].getText());
		state[num].setText(s);}
	public void setPlayer1(int num,String s) {player1[num].setText(s);}
	public void setPlayer2(int num,String s) {player2[num].setText(s);}
	public void setRoomb(int num,String s) {roomb[num].setText(s);}
	
	
	public WaitingPanel(int width,int height,Main_Client m) {
		
		this.width=width;
		this.height=height;
		this.main_Client=m;
		me=main_Client.getMe();
		me.setCurrentLocation("WAITING");
		dos=main_Client.getDos();
		
		roomb=new JButton[4];
		state=new JLabel[4];
		player1=new JLabel[4];
		player2=new JLabel[4];
		
		setLayout(new BorderLayout());
	
		banner=new ImageIcon(new ImageIcon("images/ninja_banner.png").getImage().getScaledInstance(width, 128, Image.SCALE_SMOOTH));
		JLabel bannerLabel =new JLabel(banner);
		bannerLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		add(bannerLabel,BorderLayout.NORTH);
		
		JPanel chatPlace=new JPanel();
		chatPlace.setLayout(new BorderLayout());
		chatPlace.setPreferredSize(new Dimension(width, 250));
	
		mainChat=new JTextArea();
		mainChat.setEditable(false);
		JScrollPane pane=new JScrollPane(mainChat);
		tf_chat=new JTextField();
		mainChat.setBackground(Color.BLACK);
		mainChat.setForeground(Color.white);
		
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
		
		
		
		
		ScrollPane sp=new ScrollPane();
		sp.setPreferredSize(new Dimension(160, 0));
		
		sp.add(onlineMembers);
		chatPlace.add(sp,BorderLayout.EAST);
		onlineMembers.setEditable(false);
		
		add(chatPlace,BorderLayout.SOUTH);
		
		JPanel myInfoPanel=new JPanel();
		
		myInfoPanel.setLayout(new BoxLayout(myInfoPanel, BoxLayout.Y_AXIS));
		myInfoPanel.setPreferredSize(new Dimension(170, 0));
		myInfoPanel.setBackground(Color.DARK_GRAY);
		myInfoPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		
		Font infoFont=new Font("한컴 윤고딕 230", Font.PLAIN,20);
		JLabel empty=new JLabel("   ");
		i_id=new JLabel("["+me.getID()+"]");
		i_id.setForeground(Color.WHITE);
		i_id.setFont(infoFont);
		i_id.setAlignmentX(0.5f);
		myInfoPanel.add(empty);
		myInfoPanel.add(i_id);
		empty=new JLabel("   ");
		myInfoPanel.add(empty);
		
		i_line=new JLabel("------");
		i_line.setForeground(Color.WHITE);
		i_line.setFont(infoFont);
		i_line.setAlignmentX(0.5f);
		myInfoPanel.add(i_line);
		empty=new JLabel("   ");
		myInfoPanel.add(empty);
		
		infoFont=new Font("한컴 윤고딕 230", Font.PLAIN,15);
	
		i_pt=new JLabel("플레이 횟수 : "+me.getPlayedTimes());
		i_pt.setForeground(Color.WHITE);
		i_pt.setFont(infoFont);
		i_pt.setAlignmentX(0.5f);
		myInfoPanel.add(i_pt);
		empty=new JLabel("   ");
		myInfoPanel.add(empty);
		
		i_won=new JLabel("승리 횟수 : "+me.getWon());
		i_won.setForeground(Color.WHITE);
		i_won.setAlignmentX(0.5f);
		i_won.setFont(infoFont);
		myInfoPanel.add(i_won);
		empty=new JLabel("   ");
		myInfoPanel.add(empty);
		
		
		i_lost=new JLabel("패배 횟수 : "+me.getLost());
		i_lost.setForeground(Color.WHITE);
		i_lost.setAlignmentX(0.5f);
		i_lost.setFont(infoFont);
		myInfoPanel.add(i_lost);
		empty=new JLabel("   ");
		myInfoPanel.add(empty);
		
		i_rate=new JLabel("승    률 : "+me.getWinRate());
		i_rate.setForeground(Color.WHITE);
		i_rate.setAlignmentX(0.5f);
		i_rate.setFont(infoFont);
		myInfoPanel.add(i_rate);
		empty=new JLabel("   ");
		myInfoPanel.add(empty);
		
		i_rank=new JLabel("랭    킹 : "+me.getRanking());
		i_rank.setForeground(Color.WHITE);
		i_rank.setAlignmentX(0.5f);
		i_rank.setFont(infoFont);
		myInfoPanel.add(i_rank);
		empty=new JLabel("   ");
		myInfoPanel.add(empty);
					
		add(myInfoPanel,BorderLayout.WEST);
		
		JPanel centerPanel=new JPanel();
		centerPanel.setLayout(new GridLayout(2, 2));
		
		for(int i=0;i<roomb.length;i++) {
			roomb[i]=new JButton("[빈방] 방 개설하기");
			state[i]=new JLabel("");
			player1[i]=new JLabel("");
			player2[i]=new JLabel("");
		}
		
		
		
		System.out.println("ddddddddddddddddddddddddddddddddd");
		RoomPanel room1P=new RoomPanel(roomb[0],state[0],player1[0],player2[0]);
		RoomPanel room2P=new RoomPanel(roomb[1],state[1],player1[1],player2[1]);
		RoomPanel room3P=new RoomPanel(roomb[2],state[2],player1[2],player2[2]);
		RoomPanel room4P=new RoomPanel(roomb[3],state[3],player1[3],player2[3]);
		System.out.println("ddddddddddddddddddddddddddddddddd");
		
		centerPanel.add(room1P);
		centerPanel.add(room2P);
		centerPanel.add(room3P);
		centerPanel.add(room4P);
		
		add(centerPanel,BorderLayout.CENTER);
		
		
		
		btn_send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mainChatSent();
			}
		});
		
		
		
		tf_chat.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) 	mainChatSent();
			}
			@Override
			public void keyPressed(KeyEvent arg0) {}
		});
		roomb[0].addActionListener(new btnListener(1));
		roomb[1].addActionListener(new btnListener(2));
		roomb[2].addActionListener(new btnListener(3));
		roomb[3].addActionListener(new btnListener(4));
		
		try {
			dos.writeUTF("WAITING:CHAT:SERVER:"+me.getID()+"님께서 입장하셨습니다.");
			dos.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	
	
	
	class btnListener implements ActionListener {
		int num;
		JTextField roomtitle;
	
		public btnListener(int num) {
			// TODO Auto-generated constructor stub
			this.num=num;
		}
		
		public void actionPerformed(ActionEvent e) {
			if(((JButton)e.getSource()).getText().startsWith("[빈방]")) {
				j=new JFrame();
				j.setTitle("방 개설하기");
				j.setSize(300,150);
				j.setLocation(400,300);
				JPanel panel=new JPanel();
				panel.setLayout(new GridLayout(3,1));
				panel.setLayout(new GridLayout(3, 1));
				
				JLabel title=new JLabel(num+"번 방제목을 입력해주세요.");
				title.setHorizontalAlignment(JLabel.CENTER);
				title.setFont(title.getFont().deriveFont(15.0F));
				panel.add(title);
				
				JPanel tmp=new JPanel();
				roomtitle=new JTextField(20);
				tmp.add(roomtitle);
				panel.add(tmp);
				
				tmp=new JPanel();
				JButton make=new JButton(num+"번 방 만들기");

				tmp.add(make);
				panel.add(tmp);
				j.add(panel);
				j.setVisible(true);
				
				make.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {btnClicked();}
				});
				
				roomtitle.addKeyListener(new KeyListener() {
					public void keyTyped(KeyEvent arg0) {}
					public void keyReleased(KeyEvent e) {if(e.getKeyCode()==KeyEvent.VK_ENTER)btnClicked();	}
					public void keyPressed(KeyEvent arg0) {}
				});
				
			}else if(((JButton)e.getSource()).getText().startsWith("[대기]")) {
				try {
					dos.writeUTF("ROOM"+num+":ENTER:"+me.getID());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
		}
		
		public void btnClicked() {
			if(roomtitle.getText()==null||roomtitle.getText().length()==0) {
				JOptionPane.showMessageDialog(null, "방 제목을 입력해주세요");
			}else {
				
				String title=roomtitle.getText();
				try {
					
					dos.writeUTF("ROOM"+num+":CREATE:"+me.getID()+":"+title);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}
		}
		
	}

	
	public void setInfo() {
		i_id.setText(	" "+me.getID()+"\n\n"+
						" 플레이 횟수 : "+me.getPlayedTimes()+"\n"+
						" 승리    횟수 : "+me.getWon()+"\n"+
						" 패배    횟수 : "+me.getLost()+"\n"+
						" 승          률 : "+me.getWinRate()+"\n"+
						" 랭          킹 : "+me.getWon()+"\n");
	}
	
	public void closeJFrame() {
		getJ().dispose();
	}
	
	
	
	public void mainChatSent() {
		if(tf_chat.getText()==null||tf_chat.getText().length()==0)return;
		
		System.out.println(me.getID());
		String msg="WAITING:CHAT:"+me.getID()+":"+tf_chat.getText();
		try {
			dos.writeUTF(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tf_chat.setText("");
		System.out.println(msg+"를 서버에 전송했습니다.");
	}
	
	class RoomPanel extends JPanel{
		
		public RoomPanel(JButton btn,JLabel state,JLabel pl1,JLabel pl2) {
			
			setBackground(Color.BLACK);
			setBorder(BorderFactory.createLineBorder(Color.WHITE));
			setLayout(new BorderLayout());
			
			add(btn,BorderLayout.SOUTH);
			
			Font f=new Font("한컴 윤고딕 230", Font.PLAIN,20);
			
			JPanel p1=new JPanel();
			JLabel tmp=new JLabel("           ");
			p1.add(tmp);
			
			p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
			state.setText("빈   방");
			state.setAlignmentX(0.5F);
			state.setFont(f);
			p1.add(state);
			
			tmp=new JLabel("           ");
			p1.add(tmp);
			
			f=new Font("한컴 윤고딕 230",Font.PLAIN,15);
			tmp.setText("------------");
			tmp.setAlignmentX(0.5F);
			tmp.setFont(f);
			p1.add(tmp);
			
			tmp=new JLabel("           ");
			p1.add(tmp);
			
			pl1.setText("---");
			pl1.setAlignmentX(0.5F);
			pl1.setFont(f);
			p1.add(pl1);
			
			tmp=new JLabel("           ");
			p1.add(tmp);
			
			pl2.setText("---");
			pl2.setAlignmentX(0.5F);
			pl2.setFont(f);
			p1.add(pl2);
			
			add(p1,BorderLayout.CENTER);
			
			
		}
	}
	
	
	
	
}
