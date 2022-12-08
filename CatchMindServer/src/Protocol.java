
public interface Protocol {
	String Login = "100";
	String UpdateUserList="101";
	String PrivateMessage="103";
	String MakeRoom="203";
	String EnterRoom="202";
	String RoomChatting="211";
	String ExitRoom="900";
	String Logout = "999";
	String UpdateRoomList="207";
	String SuccessEnterRoom="209";
	String FailEnterRoom="208";
	String GameAlreadyStart="210";
	String Drawing="500";
	String RightAnswer="700";
	String StartGame="702";
	String FailStartGame="703";
	String ChangeColor="501";
	String ChangeShape="502";
	String SuccessStartGame="704";
	String UpdateUserScore="405";
	String UpdateUserProfile="407";
	String CorrectAnswer="212";
	String GetHint="701";
	String EndGame="800";
}
