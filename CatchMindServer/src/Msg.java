//ObjectStream

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.ImageIcon;

public class Msg implements Serializable{
	private static final long serialVersionUID = 1L;
	public String code;
	public String UserName;
	public String data;
	public ImageIcon img;
	public MouseEvent mouse_e;
	public int pen_size;
	public int room_id;
	public String room_name;
	//프로필이미지
	public ImageIcon profileImgIcon;
	// 추가
	public String mouse_type;
	public Color pen_color;
	public String shape_type;
	
	public Msg(String UserName,String code,String msg) {
		this.code=code;
		this.UserName=UserName;
		this.data=msg;
		
		this.mouse_type = "";
		this.shape_type = "free";
	}
	public void setRoomName(String room_name) {
		this.room_name=room_name;
	}
	public String getRoomName() {
		return this.room_name;
	}
	public void setRoom(int room_id) {
		this.room_id=room_id;
	}
	public int getRoom_id() {
		return this.room_id;
	}
	public void setRoom_id(int room_id) {
		this.room_id=room_id;
	}
	public void setProfileImgIcon(ImageIcon imgicon) {
		this.profileImgIcon=imgicon;
	}
	public ImageIcon getProfileImgIcon() {
		return profileImgIcon;
	}
}
