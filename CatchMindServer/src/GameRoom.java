import java.awt.Image;
import java.util.Vector;

import javax.swing.ImageIcon;

public class GameRoom {
	private static final long serialVersionUID = 1L;
	public int room_id; //방 고유번호
	public int max_user=4; //최대 방 인원 수
	private int user_count=0;
	public Vector<String> RoomUserVec = new Vector<String>(); //room user list // 추가: userName(String)값 추가 + public 교체
	public Vector<ImageIcon> RoomUserImageIconVec = new Vector<ImageIcon>();
	public Vector<Integer> RoomUserScoreVec = new Vector<Integer>();//user점수

	private String roomOwner;
	private String room_name;
	public boolean isStarted = false; // 게임 시작 flag
	private Vector<String> answers = new Vector<String>(); // gameRoom에서 answers 관리
	public String currentAnswer = "";
	public String drawingUser;
	
	public GameRoom(String room_name,String user) {
		this.room_name=room_name;
		this.roomOwner=user;
		for(int i=0;i<this.max_user;i++) {
			this.RoomUserScoreVec.add(0); //게임 생성시 모든 유저의 점수 0으로 초기화
		}
		initialAnswers();
	}
	public int enterRoom(Msg msg) {
		if(!isStarted) { //게임이 시작하지 않을때
		if(max_user>user_count) {
		this.RoomUserVec.add(msg.UserName);
		this.RoomUserImageIconVec.add(msg.getProfileImgIcon()); //이미지도 추가
		this.user_count++;
		return this.user_count;
		}
		else
			return -1; // 입장실패시 -1 리턴
		}
		else return -2; //게임이 시작한경우 -2리턴
	}
	public String exitRoom(String username) { //String -> UserService
		for(int i=0; i<this.RoomUserVec.size();i++) {
			if(this.RoomUserVec.elementAt(i).matches(username)) {
				this.RoomUserVec.remove(i);
				this.RoomUserImageIconVec.remove(i);
			}
		}
		//this.RoomUserVec.remove(username);
		this.user_count--;
		return username;
	}
	public String getRoomName() {
		return this.room_name;
	}
	public int getRoomUserCount() {
		return this.user_count;
	}
	public int getRoom_id() {
		return this.room_id;
	}
	
	private void initialAnswers() {
		answers.add("사과");
		answers.add("배");
		answers.add("자전거");
		answers.add("바나나");
		answers.add("계란");
		answers.add("우유");
		answers.add("건전지");
		answers.add("카레");
		answers.add("콜라");
	}
	
	public void changeAnswer()  {
		if(answers.size() <= 0) {
			// TODO answers를 다 썼을 때 예외 처리
		}
		int randomIndex = (int) (Math.random() * answers.size());
		this.currentAnswer = answers.remove(randomIndex);
	}
	
}