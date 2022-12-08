//JavaObjServer.java ObjectStream 기반 채팅 Server

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class CatchMindServer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;

	private ServerSocket socket; // 서버소켓
	private Socket client_socket; // accept() 에서 생성된 client 소켓
	private Vector UserVec = new Vector(); // 연결된 사용자를 저장할 벡터
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Vector<GameRoom> GameRoomVec = new Vector<GameRoom>(); // 생성된 방을 저장할 벡터
	private int gived_room_id=1; //서버가 room에게 부여할 room_id; 초기값은 1, room이 생성될때마다 +1된 room_id가 room에게 할당된다
	//private Vector LobbyUserVec = new Vector(); //로비에 대기중인 유저를 저장할 벡터 단순히 접속유저에 대한 정보만 가지고 있다.
	private int endScore=20; //게임이 끝나기 직전 스코어
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CatchMindServer frame = new CatchMindServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CatchMindServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AppendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
				txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
					AppendText("새로운 참가자 from " + client_socket);
					// User 당 하나씩 Thread 생성
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // 새로운 참가자 배열에 추가
					new_user.start(); // 만든 객체의 스레드 실행
					AppendText("현재 참가자 수 " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}
	//중간 퇴장 처리
	public GameRoom ExitRoom(Msg msg) { //msg의 roomid와 같은 room을 벡터에서 찾고, 방 나가는 처리 해주고.
		for(int j=0;j<GameRoomVec.size();j++) {
			GameRoom gmroom = (GameRoom) GameRoomVec.elementAt(j);
			if(Integer.toString(gmroom.room_id).equals(msg.data)) { //data와 room_id가 같으면
				gmroom.exitRoom(msg.UserName); //처리를 해줌
				if(gmroom.getRoomUserCount()==0) { //다 나가서 유저가 없는경우
					GameRoomVec.removeElement(gmroom); //벡터에서 룸제거
				}		
				return gmroom;
			}
		}
		return null; // 이 경우 예외처리 그냥 안 해줌.. ㅋ
	}

	public void AppendText(String str) {
		// textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(Msg msg) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("code = " + msg.code + "\n");
		textArea.append("id = " + msg.UserName + "\n");
		textArea.append("data = " + msg.data + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// User 당 생성되는 Thread
	// Read One 에서 대기 -> Write All
	class UserService extends Thread {
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;

		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector user_vc;
		public String UserName = "";
		public String UserStatus;
		public int roomId;
		public UserService(Socket client_socket) {
			// TODO Auto-generated constructor stub
			// 매개변수로 넘어온 자료 저장
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			try {
				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());
			} catch (Exception e) {
				AppendText("userService error");
			}
		}
		public void EnterRoom(Msg msg) { //msg형태는 방 이름 자체만 전달함
			//room을 들어가는 처리
			//data가 방이름 인원, 최대인원으로 정해져있으니까 첫번째 인자에 인원수 추가//일단 방이름으로 구분하고 나중에 아이디 사용
			String room_id=msg.data; //만들때는 부여할 room_id가 없어서 기존 makeroom에 enterroom기능도 추가했다.
			for(int i=0;i<GameRoomVec.size();i++) {
				GameRoom gmroom = (GameRoom) GameRoomVec.elementAt(i);
				if(room_id.equals(Integer.toString(gmroom.getRoom_id()))) {
					int j = gmroom.enterRoom(msg); //입장 사용자 이름
					if(j==-1) //인원이 꽉차서 입장을 못했을 경우엔
					{
						WriteOne("fail",Protocol.FailEnterRoom); //208 방 입장 실패
					}
					else if(j==-2) {//게임이 시작중일때 -2를 리턴한다
						WriteOne("alreadystart",Protocol.GameAlreadyStart);
					}
					else {
						Msg temp = new Msg(UserName,Protocol.SuccessEnterRoom,"");
						temp.room_id=gmroom.getRoom_id();
						WriteOneObject(temp);
						
						// 2. room에 들어갈 때 유저 자신이 어느 방에 있는지 알기 위해 저장
						this.roomId = temp.room_id;
						SendUpdateProfileMsg(msg,gmroom);
						
					}
					break;
				}
			}
		}//EnterRoom 끝
		public void MakeAndEnterRoom(Msg msg) {
			GameRoom new_gameroom = new GameRoom(msg.room_name,msg.UserName);
			new_gameroom.room_id=gived_room_id; //고유 방번호 부여
			new_gameroom.enterRoom(msg);
			gived_room_id++;
			GameRoomVec.add(new_gameroom);
			Msg temp = new Msg(UserName,Protocol.SuccessEnterRoom,"");//usercount로 프로필 순번정함
			temp.room_id=new_gameroom.room_id;
			temp.setProfileImgIcon(msg.getProfileImgIcon());
			WriteOneObject(temp);
			//WriteOne("update",Protocol.UpdateUserProfile);
			//방만드는걸 실패할 경우도 고려하긴해야하지만 극히 드무니까 생략
			for(int l=0;l<new_gameroom.max_user;l++) { //방을 만들때 전체유저 그냥 점수 초기화
				new_gameroom.RoomUserScoreVec.add(0);
			}
			//room에 들어갈때 유저 자신이 어느방에 있는지 알기 위해 저장
			this.roomId=new_gameroom.room_id;
			SendUpdateProfileMsg(msg,new_gameroom);
			
		}
		public void SendUpdateProfileMsg(Msg msg,GameRoom gmroom) {
			for(int k=0;k<user_vc.size();k++) { //해당 room아이디 유저들에 대해서  roomid순서대로 보내야함
				UserService user = (UserService) user_vc.elementAt(k);
				if(user.roomId==gmroom.getRoom_id()) { //이름이 같지 않다는 가정하에 room유저에게 각각
					for(int i=0;i<gmroom.getRoomUserCount();i++) { //room user전체를
					Msg updateprofile = new Msg(gmroom.RoomUserVec.elementAt(i),Protocol.UpdateUserProfile,Integer.toString(i+1)); //room유저의 이름과 프로필 목록 순서를 보여줌
					updateprofile.setProfileImgIcon(gmroom.RoomUserImageIconVec.elementAt(i));
					user.WriteOneObject(updateprofile);
					}
				}
			}
		}
		public void SendUpdateProfileMsg2(Msg msg,GameRoom gmroom) {
			int tempint=0;
			for(int k=0;k<user_vc.size();k++) { //해당 room아이디 유저들에 대해서  roomid순서대로 보내야함
				UserService user = (UserService) user_vc.elementAt(k);
				if(user.roomId==gmroom.getRoom_id()) { //이름이 같지 않다는 가정하에 room유저에게 각각
					for(int i=0;i<gmroom.getRoomUserCount();i++) { //room user전체를
					Msg updateprofile = new Msg(gmroom.RoomUserVec.elementAt(i),Protocol.UpdateUserProfile,Integer.toString(i+1)); //room유저의 이름과 프로필 목록 순서를 보여줌
					updateprofile.setProfileImgIcon(gmroom.RoomUserImageIconVec.elementAt(i));
					user.WriteOneObject(updateprofile);
					tempint=i;
					}
					Msg temp=new Msg("",Protocol.UpdateUserProfile,Integer.toString(tempint+2));
					user.WriteOneObject(temp);
				}
			}
		}
		public void Login() {
			AppendText("새로운 참가자 " + UserName + " 입장.");
			WriteOne("Welcome to CatchMindGame\n","200");
			WriteOne(UserName + "님 환영합니다.\n","200"); // 연결된 사용자에게 정상접속을 알림
			String msg = "[" + UserName + "]님이 입장 하였습니다.\n";
			WriteOthers(msg); // 아직 user_vc에 새로 입장한 user는 포함되지 않았다.
		}
		public void UpdateLobbyUserList() { //대기실 유저 업데이트, 대기실 입장 프로토콜 101
			String msg="접속 중인 유저\nㅡㅡㅡㅡㅡㅡ";
			for (int i = 0; i < user_vc.size(); i++) { //
				UserService user_name = (UserService) user_vc.elementAt(i);
				msg += "\n"+user_name.UserName;
			}
			WriteAll(msg,Protocol.UpdateUserList);
			
		}
		public void UpdateRoomList() {
			//room리스트의 변경내용을 보내는 부분
			String msg="";
			for (int i = 0; i < GameRoomVec.size(); i++) { //room갯수만큼 보내주고
				GameRoom gameroom = (GameRoom) GameRoomVec.elementAt(i);
				msg+=gameroom.getRoom_id()+" "+gameroom.getRoomName()+" "+Integer.toString(gameroom.getRoomUserCount())+" "+Integer.toString(gameroom.max_user)+"\n";
			}
			WriteAll(msg,Protocol.UpdateRoomList);
			
		}

		public void Logout() {
			String msg = "[" + UserName + "]님이 퇴장 하였습니다.\n";
			UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
			WriteAll(msg,"200"); // 나를 제외한 다른 User들에게 전송
			AppendText("사용자 " + "[" + UserName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
		}
		
		public void gameStart(Msg msg) {
			for(int i=0; i<GameRoomVec.size(); i++) {
				GameRoom room = GameRoomVec.elementAt(i);
				// 메세지를 보낸 room이 어디인지 찾기 (권한을 줄 유저뿐 아니라 권한을 뺏을 유저도 찾아야 하므로 필요)
				if(msg.UserName.equals(room.RoomUserVec.elementAt(0))) { //room의 유저중 첫번째 유저가 게임을 시작함
				if(msg.room_id == room.room_id) {
					// 방에 첫 번째 저장되어 있는 유저에게 그림 그리는 권한을 우선적으로 부여
					room.drawingUser = room.RoomUserVec.elementAt(0);
					// 두 명 이상이어야 게임 시작 가능 
					if(room.getRoomUserCount() >= 2) {
						room.isStarted = true;
						room.changeAnswer();
						authorizeDrawing(room.room_id, room.drawingUser, room.currentAnswer);
						//게임을 시작할때 score초기화
						break;
					}
					else {
						failGameStart(room.drawingUser);
					}
				}//if(msg.room_id==room.room_id);
				}
			}
			
			msg.getRoom_id();
		}
		
		public void authorizeDrawing(int roomId, String drawingUser, String answer) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				// roomId가 같은 user를 먼저 찾고
				if(user.roomId == roomId) {
					// 유저 이름(식별기능)을 통해 유저를 찾고, 그림 그리는 권한을 부여한다.
					if(user.UserName.equals(drawingUser)) {
						Msg msg = new Msg(drawingUser, Protocol.StartGame, answer);
						user.WriteOneObject(msg);
					}
					else {
						Msg msg = new Msg(user.UserName, Protocol.StartGame, "deprive");
						user.WriteOneObject(msg);
					}
				}
			}
		}
		
		public void failGameStart(String userName) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if(user.UserName.equals(userName)) {
					Msg msg = new Msg(userName, Protocol.FailStartGame, "");
					user.WriteOneObject(msg);
					break;
				}
			}
		}
		
		// 추가
		public void drawing(Msg msg) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				// 자신의 roomId와 통신 받은 room_id가 같을 곳에만 write(send)한다.
				if(user.roomId == msg.room_id) {
					System.out.println(user.roomId + "room_id");
					user.WriteOneObject(msg);
				}
			}
		}
		
		// TODO 맞췄을 떄, 그림판 전체 지우는 기능과 그리는 권한 없을 떄, 색깔 변경이나 도형 변경도 막아주기
		public void chattingRoom(Msg cm) {
			//내부에 room채팅 구현 // 우선 먼저 게임이 시작한 경우 값을 비교함. 이때 서버에서 그리는 client에게 답을 전달해주고 채팅을 항상 비교하는 것이 좋을듯
			//정답비교부분
			
			//
			for(int i=0; i<user_vc.size();i++) { //동일한 roomuser들에게 전달
				UserService user = (UserService) user_vc.elementAt(i);
				if(cm.room_id==user.roomId){
					// 비교 연산
					Msg temp = new Msg(cm.UserName,Protocol.RoomChatting,cm.data); //누가 보냈는지 username과 문자데이터 전송
					user.WriteOneObject(temp);
					compareAnswer(cm.room_id, cm.data, cm.UserName);
				}
			}
		}
		private void compareAnswer(int roomId, String msg, String rightUserName) {
			for(int i=0; i<GameRoomVec.size(); i++) {
				GameRoom room = GameRoomVec.elementAt(i);
				// 메세지를 보낸 room이 어디인지 찾기 
				if(roomId == room.room_id) {
					// 정답을 맞췄을 경우 맞춘 사람이 drawingUser가 되고, 정답을 바꿔준다.
					if(room.currentAnswer.equals(msg)) { //맞춘 경우 -> 맞췄음을 알리고, 점수추가 각각의 프로토콜로 알려줄 것임
						//전체 유저 중 해당 유저들에게 전부 송신하는 부분(ex : rightUserName 님이 정답을 맞추셨습니다!)
						for(int j=0;j<user_vc.size();j++) {
							UserService user = (UserService) user_vc.elementAt(j);
							if(user.roomId==roomId) {
								Msg celebrity = new Msg(user.UserName,Protocol.CorrectAnswer,rightUserName);
								user.WriteOneObject(celebrity);
								
							}
						}
						//정답을 맞춘사람의 점수갱신
						for(int k=0;k<room.RoomUserVec.size();k++) { //room 전체 유저중 //이름 중복은 고려하지않음
							if(rightUserName.matches(room.RoomUserVec.elementAt(k))) {//정답자의
								room.RoomUserScoreVec.setElementAt(room.RoomUserScoreVec.elementAt(k)+10, k);//기존 점수의 +10을 해준 것임
								for(int l=0;l<user_vc.size();l++) {//전체유저중 해당룸의 유저
									UserService user = (UserService) user_vc.elementAt(l);
									if(user.roomId==roomId) {
										Msg updatescore = new Msg(user.UserName,Protocol.UpdateUserScore,Integer.toString(room.RoomUserScoreVec.elementAt(k))+" "+Integer.toString(k+1));
										//유저 벡터의 점수+" "+순서
										user.WriteOneObject(updatescore);
									}
									if(room.RoomUserScoreVec.elementAt(k)>endScore) {
										Msg EndGame = new Msg(user.UserName,Protocol.EndGame,rightUserName); //단순히 게임의 종료를 알려줌
										user.WriteOneObject(EndGame);
									}
								}
							}
						}
						//
						room.changeAnswer();
						room.drawingUser = rightUserName;
						authorizeDrawing(roomId, room.drawingUser, room.currentAnswer);
						break;
					}
				}
			}
		}
		//- 힌트 보내기
		private void sendHint(Msg msg) {
			StringBuilder hint = new StringBuilder("");
		// 힌트 만들어주고
		for(GameRoom room : GameRoomVec) {
				if(msg.room_id == room.room_id&&room.isStarted) {
					// ex 바나나 -> 바○○
					hint.append(room.currentAnswer.charAt(0));
					for(int i=0; i<room.currentAnswer.length()-1; i++) 
						hint.append('O');				
					break;
				}
			}
			
			// 같은 방의 유저들에게 힌트 보내주기
			for(int i=0; i<user_vc.size();i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if(msg.room_id==user.roomId) {
					Msg temp = new Msg(msg.UserName,Protocol.GetHint, hint.toString()); //누가 보냈는지 username과 문자데이터 전송
					user.WriteOneObject(temp);
				}
			}
		}
		
		//중간에 나갔을 떄 처리
		//중간에 나갔을 떄 처리(drawingUser 하고, 정답 바꿔주기)
		private void changeDrawingUser(GameRoom room) {
			// 권한을 다시 바꿔주기(그림판 지우는 것은 CatchMindPlayGame 부분에서 실행
			room.drawingUser = room.RoomUserVec.elementAt(0);
			room.changeAnswer();
			authorizeDrawing(room.room_id, room.drawingUser, room.currentAnswer);
		
		}
		
		// TODO 중간에 나갔을 떄 처리(drawingUser 하고, 정답 바꿔주기)

		// 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteAll(String str,String protocol) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.UserStatus == "O")
					user.WriteOne(str,protocol);
			}
		}
		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.UserStatus == "O")
					user.WriteOneObject(ob);
			}
		}

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.UserStatus == "O")
					user.WriteOne(str,"200");
			}
		}

		// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
		public byte[] MakePacket(String msg) {
			byte[] packet = new byte[BUF_LEN];
			byte[] bb = null;
			int i;
			for (i = 0; i < BUF_LEN; i++)
				packet[i] = 0;
			try {
				bb = msg.getBytes("euc-kr");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (i = 0; i < bb.length; i++)
				packet[i] = bb[i];
			return packet;
		}

		// UserService Thread가 담당하는 Client 에게 1:1 전송 프로토콜 200
		public void WriteOne(String msg,String protocol) {
			try {
				Msg obcm = new Msg("SERVER", protocol, msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		public void WriteOneObject(Object ob) {
			try {
			    oos.writeObject(ob);
			} 
			catch (IOException e) {
				AppendText("oos.writeObject(ob) error");		
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout();
			}
		}
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					Object obcm = null;
					String msg = null;
					Msg cm = null;
					if (socket == null)
						break;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					if (obcm == null)
						break;
					if (obcm instanceof Msg) {
						cm = (Msg) obcm;
						AppendObject(cm); //101 프로토콜은 클랑
					} else
						continue;
					if (cm.code.matches(Protocol.Login)) { //cm은 msg
						UserName = cm.UserName;
						UserStatus = "O"; // Online 상태
						Login();
						UpdateLobbyUserList();
						UpdateRoomList();
					}else if(cm.code.matches(Protocol.PrivateMessage)) { //쪽지 클라이언트로부터 수신    103은 수신, 104는 전달    &로 유저이름 구분
						String[] args=cm.data.split("&");
						for(int i =0; i<user_vc.size();i++) {
							UserService user = (UserService) user_vc.elementAt(i);
							if(user.UserName.matches(args[0])) {//args[0] == 수신자 이름
								user.WriteOne(cm.UserName+"&"+args[1],Protocol.PrivateMessage);
							}
						}
					}
					else if (cm.code.matches("200")) {
						msg = String.format("[%s] %s", cm.UserName, cm.data);
						AppendText(msg); // server 화면에 출력
						String[] args = msg.split(" "); // 단어들을 분리한다.
						if (args[1].matches("/exit")) {
							Logout();
							break;
						} else { // 일반 채팅 메시지
							//WriteAll(msg + "\n"); // Write All
							WriteAllObject(cm);
						}
					}else if(cm.code.matches(Protocol.MakeRoom)) { //room을 만들 때
						//room 처리하는 부분
						MakeAndEnterRoom(cm);  //room을 만들고
						UpdateRoomList(); //변경사항을 client로 전송
						
					}else if(cm.code.matches(Protocol.EnterRoom)) {//room에 유저가 입장할 때
						EnterRoom(cm); //room에 입장하고
						UpdateRoomList();//변경사항을 client로 전송
					}
					else if(cm.code.matches(Protocol.RoomChatting)) {//room 채팅
						chattingRoom(cm);
					}
					else if(cm.code.matches(Protocol.ExitRoom)) {
						for(int i =0; i<user_vc.size();i++) {
							UserService user = (UserService) user_vc.elementAt(i);
							if(user.UserName.equals(cm.UserName)){
								GameRoom room = ExitRoom(cm);
								SendUpdateProfileMsg2(cm,room); //인원수만큼 보내주고
								//만약 그림 그리는 권한을 가진 유저가 나갔다면
								if(room.isStarted&&room.drawingUser.equals(cm.UserName) && room.RoomUserVec.size() != 0)
									changeDrawingUser(room);
								user.WriteOne("방나가기성공함", Protocol.ExitRoom); //이건 단순히 나갔다는것을 알려줌
								user.roomId=0;
							}
							
						}
						UpdateRoomList();
					}//ExitRoom
					else if (cm.code.matches(Protocol.Logout)) { // logout message 처리
						Logout();
						break;
					} else if (cm.code.matches(Protocol.StartGame)) { // , 500, ... 기타 object는 모두 방송한다.
						gameStart(cm);
					} else if(cm.code.matches(Protocol.GetHint)) {
						sendHint(cm);
					}
					else {
						//WriteAllObject(cm);
						drawing(cm);
					}
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
//						dos.close();
//						dis.close();
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // 에러가난 현재 객체를 벡터에서 지운다
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			} // while
		} // run
	}

}

