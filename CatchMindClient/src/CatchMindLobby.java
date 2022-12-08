import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class CatchMindLobby extends JFrame {
	//스크롤팬 안에 테이블을 넣는 방식도 가능할듯
	//메세지 처리는 전부 로비에서
	//drawImage(패널 복사)
	//insert component
	private static final long serialVersionUID = 1L;
	private Frame user_profile_img_load_frame; //프로필 사진 탐색
	private FileDialog fd; //프로필 사진 다이얼로그
	private String UserName;
	private JButton MakeRoomButton; //방만들기 버튼
	private JButton EnterRoomButton; // 방 입장 버튼
	private JButton ExitLobbyButton; //대기실 나가기 버튼
	private JButton LobbyMsgSendButton; // 로비 채팅 보내기
	private JButton MailBoxButton; //귓속말보내기 (우편함)
	private static final int BUF_LEN = 128;
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private JTextPane textrecord;  //채팅 기록(스크롤팬에 부착)
	private JTextPane LobbyUserListArea;//대기 유저 리스트 표시 팬
	private JTextPane messengertextrecord; //쪽지함 저장
	private String messengerrecordStr="";
	private JTextField txtInput; // 채팅 입력 창
	private ImagePanel profilePanel; //우측 하단 프로필 영역
	private JScrollPane RoomListScrollPanel; //대기방 표시  //DefaultTableModel 사용해보기
	private JPanel RoomListPanel;
	private JButton profileUserImagePanelButton; //상단 프로필 사진 영역
	private JTable RoomListTable;
	private ImagePanel LobbyMainPanel; //로비 전체 패널에 이미지 넣고 위에 다시 패널 올릴 수 있음 //버튼 위 버튼도 가능
	private JOptionPane MakeRoomDialog; //방만들기 다이얼로그
	private JDialog FailEnterRoom;
	private Vector RoomList_btn = new Vector(); //room 버튼 저장 벡터
	private DefaultTableModel RoomList_DefaultTableModel;
	private int room_id=0; //클라이언트당 룸아이디 배정 lobby=0;
	public CatchMindLobby lobby;
	
	
	Image LobbyBackground_img=makeImage("./rsc/헤네시스배경.jpg");
	Image profile_panel_img=makeImage("./rsc/프로필판.png");
	Image profile_user_default_img=makeImage("./rsc/기본유저이미지_더벅머리.png");
	Image EnterRoom_btn_img=makeImage("./rsc/입장하기_파란버섯.png");
	Image MakeRoom_btn_img=makeImage("./rsc/방만들기_주황버섯.png");
	Image ExitLobby_btn_img=makeImage("./rsc/뿔버섯나가기.png");
	Image FailEnterRoom_img=makeImage("./rsc/인원꽉참1.png");
	Image Send_chat_button_img=makeImage("./rsc/보내기버튼.png");
	Image Messenger_check_button_img=makeImage("./rsc/초록버섯우편함.png");
	Image Messenger_check_button_img2=makeImage("./rsc/초록버섯우편함갱신.png");
	Image AlreadyGameStartDialogImage = makeImage("./rsc/게임중_입장불가.png");
	Image profile_user_select_img=makeImage("./rsc/기본유저이미지_더벅머리.png");
	Image YouMustSelectRoom_img=makeImage("./rsc/방을선택하세요.png");
	ImageIcon profile_user_select_imgicon=new ImageIcon("./rsc/기본유저이미지_더벅머리.png");
	String[][]RoomList_entire_status; //방정보 이차원배열 필요없을듯
	private String RoomList_Str[];
	private JFrame Mainframe;
	
	CatchMindPlayGame playgameroom; //게임룸
	public String getUserName() {
		return this.UserName;
	}
	public int getUser_room_id() {
		return this.room_id;
	}
	public void Update_Room_List_Panel() {
		
	}
	private ImageIcon ImageIcon(String string) {
		// TODO Auto-generated method stub
		return null;
	}
	public void Delete_btn_default_background(JButton btn) {
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setOpaque(false);
	}
	public ImageIcon ChangeImageSizeToIcon(Image img,int x,int y) {
		Image ChangeSizeImg = img.getScaledInstance(x, y, Image.SCALE_SMOOTH);
		ImageIcon changeSizeIcon = new ImageIcon(ChangeSizeImg);
		return changeSizeIcon;
	}
	public CatchMindLobby(String username, String ip_addr, String port_no) {
		lobby=this;
		Mainframe=this;
		RoomListPanel = new JPanel();//이미지 패널은 힘듬
		UserName=username; //username 지정
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null); //frame 배치관리자 삭제
		setBounds(300,300,950,680);
		//room table
		String RoomListheader[]= {"방 번호", "방이름","참가인원","최대인원"};
		Object[][] data = new Object[][] {};
		////////
		//ImageIcon Main_up_imgicon=ChangeImageSizeToIcon(Main_up_icon,120,100);
		setIconImage(makeImage("./rsc/메인아이콘.jpg"));
		setTitle("캐치마인드");
		RoomList_DefaultTableModel = new DefaultTableModel(data,RoomListheader) {
			public boolean isCellEditable(int rowIndex,int mColIndex) { //셀 수정 불가
				return false;
			}
		};
		RoomListTable = new JTable(RoomList_DefaultTableModel); //버튼은 부착이되는데 스크롤팬에 테이블이 안붙는중
		////크기변경
		RoomListTable.setRowHeight(40); // 테이블 행 높이 설정
		RoomListTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		RoomListTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		RoomListTable.getColumnModel().getColumn(2).setPreferredWidth(30);
		RoomListTable.getColumnModel().getColumn(3).setPreferredWidth(30);
		RoomListTable.setPreferredScrollableViewportSize(new Dimension(620,300)); //스크롤가능하게 만들어준거임
		RoomListScrollPanel = new JScrollPane(RoomListTable);
		/////크기 변경파트
		RoomListTable.getTableHeader().setReorderingAllowed(false); //열 이동 불가
		RoomListTable.getTableHeader().setResizingAllowed(false); //열 크기 조절 불가능
		RoomListScrollPanel.setBounds(10,10,660,340); //크기 660 380
		RoomListScrollPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		//this.getContentPane().add(RoomListScrollPanel); //this setLayout으로 패널 분리
		RoomListScrollPanel.setLayout(null);
		//
		RoomListPanel.add(new JScrollPane(RoomListTable));//기존에 만들어준 JScrollPane 필요 없을듯 
		RoomListPanel.setBounds(30, 50, 620, 330);
		Mainframe.add(RoomListPanel);
		//testframe.add(RoomListScrollPanel);
		LobbyMainPanel = new ImagePanel(LobbyBackground_img);
		LobbyMainPanel.setBounds(0,0,950,680);
		RoomListScrollPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.add(LobbyMainPanel);
		LobbyMainPanel.setLayout(null);
		JScrollPane LobbyChatRecordScrollPane = new JScrollPane();//채팅기록
		LobbyChatRecordScrollPane.setBounds(170,400,500,180);
		LobbyMainPanel.add(LobbyChatRecordScrollPane);
		
		JScrollPane LobbyUserListScrollPane = new JScrollPane(); //대기실 입장 유저 리스트
		LobbyUserListScrollPane.setBounds(10,400,150,220);
		LobbyMainPanel.add(LobbyUserListScrollPane);
		
		messengertextrecord=new JTextPane(); //메신저 저장
		messengertextrecord.setEditable(false);
		messengertextrecord.setFont(new Font("굴림체",Font.PLAIN,14));
		
		textrecord = new JTextPane();
		textrecord.setEditable(false);
		textrecord.setFont(new Font("굴림체",Font.PLAIN,14));
		LobbyChatRecordScrollPane.setViewportView(textrecord);
		LobbyUserListArea = new JTextPane();
		LobbyUserListArea.setEditable(false);
		LobbyUserListArea.setFont(new Font("굴림체",Font.PLAIN,14));
		LobbyUserListScrollPane.setViewportView(LobbyUserListArea);
		txtInput = new JTextField();
		txtInput.setBounds(170,590,410, 40);
		txtInput.setFont(new Font("굴림체",Font.PLAIN,17));
		LobbyMainPanel.add(txtInput);
		txtInput.setColumns(10);
		MakeRoomDialog = new JOptionPane();
		
		ImageIcon EnterRoom_btn_imgicon=ChangeImageSizeToIcon(EnterRoom_btn_img,120,100);
		ImageIcon MakeRoom_btn_imgicon=ChangeImageSizeToIcon(MakeRoom_btn_img,120,100);
		MakeRoomButton = new JButton(MakeRoom_btn_imgicon);
		MakeRoomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String CreationRoomName=""; //String을 전달해주는 방법이 없음
				CreationRoomName = MakeRoomDialog.showInputDialog(LobbyMainPanel,"방 이름","방 만들기",3);
				if(!(CreationRoomName==null)) {
					//msg로 room만드는걸 보내고, roomlist를 그려주는 함수를 다시 호출하면 됨.
					Msg msg = new Msg(UserName,Protocol.MakeRoom,CreationRoomName); //make room protocol전송
					msg.setProfileImgIcon(profile_user_select_imgicon);
					msg.setRoomName(CreationRoomName);
					SendObject(msg);
				}
			}
		});
		
		MakeRoomButton.setBounds(670,80,120,100);
		LobbyMainPanel.add(MakeRoomButton);
		
		EnterRoomButton = new JButton(EnterRoom_btn_imgicon);
		EnterRoomButton.setBounds(800,80,120,100);
		EnterRoomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//table selected시에 보내기
				if(!(RoomListTable.getSelectedRow()==-1)){//선택된게없을때 -1리턴
				String data=RoomList_Str[RoomListTable.getSelectedRow()]; //여기에 table.getrow로 string문자열 보내주기  0부터 시작
				String data2[]=data.split(" ");
				Msg msg = new Msg(UserName,Protocol.EnterRoom,data2[0]);
				msg.setProfileImgIcon(profile_user_select_imgicon);
				SendObject(msg);
				}
				else {
					EnterRoomFail_Dialog fail_dialog = new EnterRoomFail_Dialog(Mainframe,"이런!","방을 선택해야합니다!");
					fail_dialog.SetDialogImage(YouMustSelectRoom_img);
					fail_dialog.setBounds(600, 600, 300, 200);
					fail_dialog.setVisible(true);
				}
			}
		});
		LobbyMainPanel.add(EnterRoomButton);
		ImageIcon Messenger_check_button_img1=ChangeImageSizeToIcon(Messenger_check_button_img,120,90);
		MailBoxButton = new JButton(Messenger_check_button_img1);
		MailBoxButton.setBounds(670,200,120,90);
		MailBoxButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//귓속말구현
				MessengerDialog messanger = new MessengerDialog(Mainframe,"쪽지");
				messanger.setBounds(600,600,235,400);
				messanger.setVisible(true);
				System.out.println("귓속말");
			}
		});
		Delete_btn_default_background(MailBoxButton);
		LobbyMainPanel.add(MailBoxButton);
		
		Delete_btn_default_background(MakeRoomButton);
		Delete_btn_default_background(EnterRoomButton);
		ImageIcon send_chat_btn_imgicon=ChangeImageSizeToIcon(Send_chat_button_img,80,40);
		LobbyMsgSendButton = new JButton(send_chat_btn_imgicon);
		LobbyMsgSendButton.setBounds(590,590,80,40);
		Delete_btn_default_background(LobbyMsgSendButton);
		LobbyMainPanel.add(LobbyMsgSendButton);
		
		ImageIcon ExitLobby_btn_imgicon1=ChangeImageSizeToIcon(ExitLobby_btn_img,120,90);
		ExitLobbyButton = new JButton(ExitLobby_btn_imgicon1);
		Delete_btn_default_background(ExitLobbyButton);
		ExitLobbyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Msg msg = new Msg(UserName, Protocol.Logout,"Bye");
				SendObject(msg);
				CatchMindClientMain Main = new CatchMindClientMain(); //room을 나갈때 다시 메인 화면으로 복귀
				setVisible(false);
			}
		});
		ExitLobbyButton.setBounds(800,200,120,90);
		LobbyMainPanel.add(ExitLobbyButton);
		ImageIcon profile_user_default_img_Icon=ChangeImageSizeToIcon(profile_user_default_img,80,100);
		profileUserImagePanelButton = new JButton(profile_user_default_img_Icon);
		profileUserImagePanelButton.setBounds(750,420,100,100);
		Delete_btn_default_background(profileUserImagePanelButton);
		profileUserImagePanelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				user_profile_img_load_frame =new Frame("프로필 사진 변경");
				fd=new FileDialog(user_profile_img_load_frame,"프로필 사진 선택",FileDialog.LOAD);
				fd.setVisible(true);
				ImageIcon profile_user_select_imgicon_init = new ImageIcon(fd.getDirectory()+fd.getFile());
				if(fd.getDirectory()==null||fd.getFile()==null) {
					profile_user_select_imgicon_init = new ImageIcon("./rsc/기본유저이미지_더벅머리.png");
				}
				profile_user_select_img=profile_user_select_imgicon_init.getImage();
				//구한 이미지를 송신하기 위해 전역변수로 선언한 sendobject용 image객체인 profile_user_select_imgicon에 담아 놓는다.
				profile_user_select_imgicon = ChangeImageSizeToIcon(profile_user_select_img,100,100);
				profileUserImagePanelButton.setIcon(profile_user_select_imgicon);
			}
		});
		profileUserImagePanelButton.addMouseListener(new MouseListener() {
			JButton gear_img;
			@Override
			public void mouseClicked(MouseEvent e) {} //딱히 구현안함
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) { //이미지 변경 파트
				 gear_img = new JButton();
				 gear_img.setBounds(0,0,20,20);
				 Image img=makeImage("./rsc/톱니바퀴.png");
				 ImageIcon img2 = ChangeImageSizeToIcon(img,20,20);
				 Delete_btn_default_background(gear_img);
				 gear_img.setIcon(img2);
				 profileUserImagePanelButton.add(gear_img);
				 gear_img.setVisible(true);
				 
			}

			@Override
			public void mouseExited(MouseEvent e) {
				gear_img.setVisible(false);
			}
		});
		LobbyMainPanel.add(profileUserImagePanelButton);
		setVisible(true);
		
		profilePanel = new ImagePanel(profile_panel_img);
		profilePanel.setBounds(690,370,220,240);
		profilePanel.setLayout(null); //프로필 배치...lable로 해야할듯
		///프로필 이름
		
		JTextField profile_bottom_name = new JTextField(this.UserName);
		profile_bottom_name.setOpaque(false);
		profile_bottom_name.setBorder(null);
		profile_bottom_name.setFont(new Font("굴림체",Font.BOLD,18));
		profile_bottom_name.setHorizontalAlignment(JTextField.CENTER);
		profile_bottom_name.setForeground(Color.white);
		profile_bottom_name.setBounds(20, 190, 180, 30);
		profile_bottom_name.setEditable(false);
		profilePanel.add(profile_bottom_name);
		///
		LobbyMainPanel.add(profilePanel);
		setVisible(true);
		
		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());
			
			Msg obcm = new Msg(UserName,Protocol.Login,"hello");
			SendObject(obcm);
			
			ListenNetwork net = new ListenNetwork();
			net.start();

			TextSendAction textsendaction = new TextSendAction();
			txtInput.addActionListener(textsendaction);
			LobbyMsgSendButton.addActionListener(textsendaction);
		} catch(NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AppendText("connect error",textrecord);
		}
	}
	public Image makeImage(String furl){
		Image img;
		Toolkit tk=Toolkit.getDefaultToolkit();
		img=tk.getImage(furl);
		try {
			//여기부터
			MediaTracker mt = new MediaTracker(this);
			mt.addImage(img, 0);
			mt.waitForID(0);
			//여기까지, getImage로 읽어들인 이미지가 로딩이 완료됐는지 확인하는 부분
		} catch (Exception ee) {
			ee.printStackTrace();
			return null;
		}	
		return img;
	}
	public void UpdateMessengerStr(String msg) { //메신저 기록 
		messengertextrecord.setText("");
		msg=msg.trim(); //앞 뒤 blank와 \n 제거
		int len = messengertextrecord.getDocument().getLength(); //textrecord가 아닌 userlist에 출력
		StyledDocument doc = messengertextrecord.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
		try {
			doc.insertString(doc.getLength(), msg+"\n", left);
			messengertextrecord.requestFocus();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void UpdateLobbyListText(String msg) { //로비 유저 리스트 업데이트
		LobbyUserListArea.setText("");
		msg=msg.trim(); //앞 뒤 blank와 \n 제거
		int len = LobbyUserListArea.getDocument().getLength(); //textrecord가 아닌 userlist에 출력
		StyledDocument doc = LobbyUserListArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
		try {
			doc.insertString(doc.getLength(), msg+"\n", left);
			LobbyUserListArea.requestFocus();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void AppendText(String msg,JTextPane textrecord) {
		msg=msg.trim(); //앞 뒤 blank와 \n 제거
		int len = textrecord.getDocument().getLength(); //이따 수정할 것. textrecord가 아닌 userlist에 출력해볼 것
		StyledDocument doc = textrecord.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
	    doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg+"\n", left );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		len = textrecord.getDocument().getLength();
		textrecord.setCaretPosition(len);
	}
	class TextSendAction implements ActionListener { //로비 채팅 action
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == LobbyMsgSendButton || e.getSource() == txtInput) {
				String msg = null;
				msg = txtInput.getText();
				SendMessage(msg);
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				/*if (msg.contains("/exit")) // 종료 처리
					System.exit(0);*/
			}
		}
	}
	public void sendprivateMessage(String msg) { //프로토콜 "
		try {
			Msg obcm = new Msg(UserName, Protocol.PrivateMessage, msg);
			
			oos.writeObject(obcm);
		} catch (IOException e) {
			// AppendText("dos.write() error");
			AppendText("oos.writeObject() error",textrecord);
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
		
	}
	public void SendMessage(String msg) {
		try {
			Msg obcm = new Msg(UserName, "200", msg);
			
			oos.writeObject(obcm);
		} catch (IOException e) {
			AppendText("oos.writeObject() error",textrecord);
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}
	public void SendRoomMessage(String msg,int room_id) { //room 채팅 프로토콜 211 , 방번호를 인자로 받아 전송
		try {
			System.out.println(msg);
			Msg obcm = new Msg(UserName, Protocol.RoomChatting, msg);
			obcm.setRoom_id(room_id);
			oos.writeObject(obcm);
		} catch (IOException e) {
			AppendText("oos.writeObject() error",textrecord);
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}
	public void SendObject(Object ob) {
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
			AppendText("SendObject Error",textrecord);
		}
	}
	public void AppendTextR(String msg, JTextPane textrecord) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.	
		StyledDocument doc = textrecord.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setForeground(right, Color.BLUE);	
	    doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		try {
			doc.insertString(doc.getLength(),msg+"\n", right );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len = textrecord.getDocument().getLength();
		textrecord.setCaretPosition(len);
	}
	public void AppendTextCorrect(String msg, JTextPane textrecord) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.	
		StyledDocument doc = textrecord.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_CENTER);
		StyleConstants.setForeground(right, Color.RED);	
	    doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		try {
			doc.insertString(doc.getLength(),msg+"\n", right );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len = textrecord.getDocument().getLength();
		textrecord.setCaretPosition(len);
	}
	public class EnterRoomFail_Dialog extends JDialog{
		ImagePanel dialog_img = new ImagePanel(FailEnterRoom_img);
		JButton okButton = new JButton("확인");
		JLabel warning_msg = new JLabel();
		ImagePanel contentPane;
		public EnterRoomFail_Dialog(JFrame frame,String title,String message) {
			super(frame,title);
			setLayout(null);
			warning_msg.setText(message);
			warning_msg.setFont(new Font("굴림체",Font.BOLD,14));
			warning_msg.setBounds(90, 35, 150, 30); 
			dialog_img.setBounds(30, 30, 50, 50);
			okButton.setBounds(100, 100, 100, 30);
			add(warning_msg);
			add(dialog_img);
			add(okButton);
			setSize(300,200);
			contentPane = new ImagePanel(FailEnterRoom_img);
			this.setContentPane(contentPane);
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			
		}
		public void SetDialogImage(Image img) { //다이얼로그의 이미지 채우기
			this.remove(contentPane);
			ImagePanel newcontentPane = new ImagePanel(img);
			this.setContentPane(newcontentPane);
		}
		public void SetDialogSize(int width,int height)//다이얼로그의 크기 재 설정, bound위치는 고정
		{
			
		}
	}
	public class MessengerDialog extends JDialog{
		JButton sendmessenger_btn = new JButton("쪽지보내기");
		JButton mailbox_closebutton = new JButton("닫기");
		JLabel recvmsg = new JLabel();
		JLabel sendmsg = new JLabel();
		JLabel username = new JLabel();
		JScrollPane MessengerScrollPane = new JScrollPane(); //대기실 입장 유저 리스트
		JTextField sendfield = new JTextField();
		JTextField usertxtinput = new JTextField();
		public MessengerDialog(JFrame frame,String title) {
			super(frame,title);
			setLayout(null);
			sendfield.setBounds(10,285,200, 30);
			sendfield.setFont(new Font("굴림체",Font.PLAIN,17));
			add(sendfield);
			usertxtinput.setBounds(100,245,100,30);
			usertxtinput.setFont(new Font("굴림체",Font.PLAIN,17));
			add(usertxtinput);
			sendfield.setColumns(30);
			usertxtinput.setColumns(10);
			sendmessenger_btn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					String user=null;
					user=usertxtinput.getText();
					if(user=="") {
						//유저가 없음
						System.out.println("잘못된 유저이름!");
					}
					else{String msg = null;
					msg = sendfield.getText();
					msg=user+"&"+msg;
					sendprivateMessage(msg);
					sendfield.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
					sendfield.requestFocus();
					}
				}
			});
			mailbox_closebutton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					messengerrecordStr="";
					ImageIcon Messenger_check_button_img1=ChangeImageSizeToIcon(Messenger_check_button_img,120,90);
					MailBoxButton.setIcon(Messenger_check_button_img1);
					setVisible(false);
				}
			});
			messengertextrecord.setText("");
			MessengerScrollPane.setViewportView(messengertextrecord);
			messengertextrecord.setText(messengerrecordStr);
			recvmsg.setText("받은 쪽지");
			recvmsg.setFont(new Font("굴림체",Font.BOLD,14));
			recvmsg.setBounds(10, 0, 100, 20); 
			sendmsg.setText("보낼 쪽지");
			sendmsg.setFont(new Font("굴림체",Font.BOLD,14));
			sendmsg.setBounds(10, 220, 100, 20);
			username.setText("받는 유저");
			username.setBounds(10,250,100,20);
			username.setFont(new Font("굴림체", Font.BOLD,14));
			
			sendmessenger_btn.setBounds(10, 320, 100, 30);
			mailbox_closebutton.setBounds(120, 320, 60, 30);
			MessengerScrollPane.setBounds(10,20,200,200);
			add(recvmsg);
			add(sendmsg);
			add(username);
			add(sendmessenger_btn);
			add(mailbox_closebutton);
			add(MessengerScrollPane);
			setSize(300,200);
			
		}
	} //MessengerDialog
	class ListenNetwork extends Thread { //서버로부터 메세지를 수신하고 처리
		public void run() {
			while (true) {
				try {

					Object obcm = null;
					String msg = null;
					Msg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof Msg) {
						cm = (Msg) obcm;
						if(cm.UserName.matches("SERVER")) { //Server의 데이터일 경우 표시하지 않음
							msg = String.format("%s", cm.data);
						}
						else {
							msg = String.format("[%s]\n%s", cm.UserName, cm.data);
						}
					} else
						continue;
					switch (cm.code) {
					case Protocol.UpdateUserList:
						UpdateLobbyListText(msg);
						break;
					case Protocol.PrivateMessage:
						String args[]=cm.data.split("&");
						messengerrecordStr += "보낸유저 : "+args[0]+" "+args[1];
						ImageIcon Messenger_check_new_btn_imgicon=ChangeImageSizeToIcon(Messenger_check_button_img2,120,90);
						MailBoxButton.setIcon(Messenger_check_new_btn_imgicon);
						break;
					case "200": // chat message
						if (cm.UserName.equals(UserName))
							AppendTextR(msg,textrecord); // 내 메세지는 우측에
						else
							AppendText(msg,textrecord);
						break;
					case Protocol.SuccessEnterRoom: //방에 정상입장했음을 알림 (이때 클라이언트에게 room_id로  서버가 전달, room에 들어갔다는 flag는 필요한지?)
						room_id=cm.room_id;
						lobby.setVisible(false);
						playgameroom = new CatchMindPlayGame(lobby,room_id);//room번호 전달 이 방식이 아닌 cm의 room_id를 client의 room_id에 저장하는 것임 모든 drawing 코드는 room_id를 기반으로 한다
						//playgameroom.setProfile(cm);전체 유저에게 업데이트를 지시해야함.
						break;
					case Protocol.FailEnterRoom: //인원초과로 방 입장에 실패한 경우
						EnterRoomFail_Dialog fail_enter_room_excess = new EnterRoomFail_Dialog(Mainframe,"ㅜㅜ","인원이 꽉 찼어요"); //대충써놓은거임
						fail_enter_room_excess.setBounds(600, 600, 300, 200);
						fail_enter_room_excess.setVisible(true);
						break;
					case Protocol.GameAlreadyStart:
						EnterRoomFail_Dialog fail_enter_room_already_start = new EnterRoomFail_Dialog(Mainframe,"잠깐!","");
						fail_enter_room_already_start.SetDialogImage(AlreadyGameStartDialogImage);
						fail_enter_room_already_start.setBounds(600, 600, 300, 200);
						fail_enter_room_already_start.setVisible(true);
						break;
					case Protocol.UpdateRoomList: //room내용 업데이트 default table model을 이용한다. ->새로 로그인한 사람들에게도 보여줘야한다.207
						if(!(RoomList_Str==null)) {//문자열이 아예 없을때를 고려함 +..(row를 전부삭제하고 나서 다시 그리는 과정도 들어감)
							for(int i=0;i<=RoomList_Str.length-1;i++) {
								RoomList_DefaultTableModel.removeRow(0); //일단 지우고 //위의 행(0)부터 지워짐
							} //에러 생기는거 고려
						}
						String temp_RoomList[]=msg.split("\\n");//방정보 한줄씩 구분 \\n로 개행문자구분. 임시 배열사용
						//(row를 전부삭제하고 나서 다시 추가하는 과정 (update RoomList)
						if(!(temp_RoomList==null)) {
						for(int j=0;j<=temp_RoomList.length-1;j++)
						RoomList_DefaultTableModel.addRow(temp_RoomList[j].split(" ")); //room에 대한 것
						}
						RoomList_Str=temp_RoomList;
						//i를 행으로 나머지를 열로 써 여기서 모델을 만드는 것 로그인할때도 roomlist는 나와야하니까 똑같이 302를 받는다. //그땐 로그인한 사람에게만 302를 보낸다.
						break;
					case Protocol.RoomChatting: //roomchatting을 받는중
						if (cm.UserName.equals(UserName))
							AppendTextR(msg,playgameroom.txtRecord); // 내 메세지는 우측에 //게임룸 채팅창에 보내기
						else
							AppendText(msg,playgameroom.txtRecord);		//룸채팅 각 클라이언트 room에게 보내는것이 문제임
						break;
					case "400": // Image 첨부 원래 300->400으로 임시변경
						if (cm.UserName.equals(UserName))
							AppendText("[" + cm.UserName + "]",textrecord);
						else
							AppendText("[" + cm.UserName + "]",textrecord);
						//AppendImage(cm.img);
						
						break;
					case Protocol.Drawing: // Mouse Event 수신
						playgameroom.receiveDrawingEvent(cm);
						break;
					case Protocol.ChangeColor: // change pen color 수신
						playgameroom.receiveChangeColor(cm.pen_color);
						break;
					case Protocol.ChangeShape: // change shape type 수신
						playgameroom.receiveChangeShape(cm.shape_type);
						break;
					case Protocol.RightAnswer:
						playgameroom.receiveAnswer(cm);
						break;
					case Protocol.GetHint:
						playgameroom.receiveHint(cm.data);
						break;
					case Protocol.StartGame: // 그림 그리는 권한 부여 및 회수
						playgameroom.receiveAuthorityOfDrawing(cm.data);
						break;
					case Protocol.SuccessStartGame:
						playgameroom.gamestartbtnoff();
						break;
					case Protocol.FailStartGame:
						playgameroom.receiveFailStartGame();
						break;
					case Protocol.UpdateUserProfile:// 업데이트 요청시 프로필 이미지 변경
						playgameroom.setProfile(cm);
						break;
					case Protocol.CorrectAnswer:
						AppendTextCorrect(cm.data+"님이 정답을 맞추셨습니다!",playgameroom.txtRecord);
						break;
					case Protocol.UpdateUserScore:
						playgameroom.setScore(cm);
						break;
					case Protocol.EndGame: //게임 종료
						playgameroom.endDialog(cm);
						break;
					case Protocol.ExitRoom: //exitroom성공시
						playgameroom.setVisible(false);
						lobby.setVisible(true);
						room_id=0; //room_id로비로 복귀
						break;
					}
				} catch (IOException e) {
					AppendText("ois.readObject() error",textrecord);
					try {
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}
	}
}