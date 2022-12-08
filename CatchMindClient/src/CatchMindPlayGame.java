import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.sun.tools.javac.Main;
public class CatchMindPlayGame extends JFrame {
	private static final long serialVersionUID = 1L;
	private CatchMindLobby lobbyView;
	private JPanel contentPane;
	private Image background_img = makeImage("./rsc/하늘배경.png");
	private Image userImage = makeImage("./rsc/우는파란버섯.jpg");
	Image freeDrawingBtnImg = makeImage("./rsc/free_drawing.png");
	Image rectBtnImg = makeImage("./rsc/rect.png");
	Image ovalBtnImg = makeImage("./rsc/oval.png");
	Image lineBtnImg = makeImage("./rsc/line.png");
	Image redBtnImg = makeImage("./rsc/빨간색.png");
	Image blueBtnImg = makeImage("./rsc/파란색.png");
	Image GreenBtnImg = makeImage("./rsc/초록색.png");
	Image YellowBtnImg = makeImage("./rsc/노란색.png");
	Image BlackBtnImg = makeImage("./rsc/검은색.png");
	Image gameStartBtnImage = makeImage("./rsc/게임시작.png");
	Image gameroomExitBtnImage = makeImage("./rsc/방나가기.png");
	Image EndgameDialogImage = makeImage("./rsc/게임종료다이얼로그.png");
	Image gameroomExitBtnUpperImage= makeImage("./rsc/주황버섯_정지.png");
	//Image black
	Image eraserBtnImage = makeImage("./rsc/지우개.png");
	private JTextField txtInput;
	public JScrollPane chattingPane;
	public JTextPane txtRecord;
	// drawing
	private JPanel panel;
	private Image panelImage;
	private Image forShapeImage;
	private Graphics gc;
	private Graphics2D gc2 = null;
	private Graphics2D gc3 = null;
	private Color penColor = new Color(0, 0, 0);
	private int pen_size = 5; // minimum 5
	private Point oldCoord;
	private Point pressedCoord;
	private Point pressedCoordForLine;

	String userName = "temp"; // 호출 되면서 받아야 될 것
	int roomId;
	ImageIcon myprofile_img;
	
	private String shape_type = "free";
	
	
	//UserGUI 전역변수 선언
	ImagePanel userImagePanel1;
	JLabel userName1;
	JLabel userScore1;
	ImagePanel userImagePanel2;
	JLabel userName2;
	JLabel userScore2;
	ImagePanel userImagePanel3;
	JLabel userName3;
	JLabel userScore3;
	ImagePanel userImagePanel4;
	JLabel userName4;
	JLabel userScore4;
	
	///
	// 게임 요소
//	private TimerNum timerNum;
//	private Thread threadNum;
//	private final int GAME_COUNT = 15;

	private JLabel hintLabel;
	private JLabel answerLabel;
	private JPanel buttonPanel;
	//게임시작 나가기 버튼
	private JButton gameStartButton;
	private JButton gameroomExitButton;
	
	// 추가 - 그림 그리는 권한
	public boolean authorityOfDrawing = false;
	
	// view를 통해 통신
	public CatchMindPlayGame(CatchMindLobby view, int roomId) {
		userName = view.getUserName();
		lobbyView = view;
		this.roomId = roomId;
		setLocationRelativeTo(null); // 가운데 뜨도록
        setLayout(null);
        setResizable(false);
		setBounds(100,100,1200,810);

        setVisible(true);
        contentPane = new ImagePanel(background_img);
		setContentPane(contentPane);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		//contentPane = new ImagePanel(background_img); 시작 나가기 버튼 
		panel = new JPanel();
        panel.setBorder(new LineBorder(new Color(255, 255, 255)));
        panel.setBackground(Color.WHITE);
        panel.setBounds(270, 70, 630, 380);
        this.add(panel);
        userImagePanel1 = new ImagePanel(userImage);
    	userScore1=new JLabel("0");
    	userImagePanel2 = new ImagePanel(userImage);
    	userScore2=new JLabel("0");
    	userImagePanel3 = new ImagePanel(userImage);
    	userScore3=new JLabel("0");
    	userImagePanel4 = new ImagePanel(userImage);
    	userScore4=new JLabel("0");
    	//
    	
        gc = panel.getGraphics();
		drawingPanelGUI();
        setMouseEvent();

        chttingGUI();
		userGUI();
        answerGUI();
        hintGUI();
        
        repaint();
        // 타이머
        //timerNum = new TimerNum(this, GAME_COUNT);
        //threadNum = new Thread(timerNum);
        //threadNum.start();
        //this.add(timerNum);

    }
	
	
	public void setMouseEvent() {
		MyMouseEvent mouse = new MyMouseEvent();
		panel.addMouseMotionListener(mouse);
		panel.addMouseListener(mouse);
		MyMouseWheelEvent wheel = new MyMouseWheelEvent();
		panel.addMouseWheelListener(wheel);
	}
	
	public void answerGUI() {
		answerLabel = new JLabel();
		answerLabel.setBounds(360, 20, 240, 30);
		answerLabel.setFont(new Font("굴림체",Font.BOLD,30));
		this.add(answerLabel);
	}
  public void hintGUI() {
		hintLabel = new JLabel("hint part");
		hintLabel.setBounds(950, 30, 200, 30);
		hintLabel.setFont(new Font("굴림체",Font.BOLD, 20));
		this.add(hintLabel);

		JButton hintButton = new JButton("hint");
		hintButton.setBounds(900, 30, 40, 30);
		hintButton.setFont(new Font("굴림", Font.PLAIN, 20));
		this.add(hintButton);
		
		hintButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				requireHint();
			}
		});
  }
	public void userGUI() {
		// 유저
		//JLabel userImagePanel1 = new JLabel(new ImageIcon("./rsc/핑크빈_헤드셋.gif")); moving 이미지 만들 수 있음
		
		JLabel movepink = new JLabel(new ImageIcon("./rsc/작은_핑크빈_헤드셋.gif"));
		movepink.setBounds(950,610,150,150);
		this.add(movepink);
        ImageIcon gameStartButtonImageIcon=ChangeImageSizeToIcon(gameStartBtnImage, 150, 60);
        gameStartButton = new JButton(gameStartButtonImageIcon);
        Delete_btn_default_background(gameStartButton);
        gameStartButton.setBounds(950,520,150,80);
        gameStartButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		//게임시작 로직 구현, 우선 버튼의 클릭을 비활성으로 할건지는 다른 문제(일단 한번 클릭했을때 또 실행되면 꼬이게되니까 비활성화 활성화로 나누어야함)
        		//비활성화를 게임시작할때만 해야함
        		Msg cm = new Msg(userName, Protocol.StartGame, "");
        		cm.room_id = roomId;
        		lobbyView.SendObject(cm);
        	}
        });
        this.add(gameStartButton);
        //게임시작부분 끝
        //gameroomExitBtnUpperImage 정지 주황버섯
        JLabel movemushroom2=new JLabel(new ImageIcon("./rsc/주황버섯애니메이션130.gif"));
        movemushroom2.setBounds(30,620,130,130);
		this.add(movemushroom2);
		movemushroom2.setVisible(false);
        ImageIcon gameroomExitButtonUppweImageIcon=ChangeImageSizeToIcon(gameroomExitBtnUpperImage, 130, 130);
        JLabel movemushroom = new JLabel(gameroomExitButtonUppweImageIcon);
        movemushroom.setBounds(30,620,130,130);
        this.add(movemushroom);
        //게임룸 나가기 부분
        ImageIcon gameroomExitButtonImageIcon=ChangeImageSizeToIcon(gameroomExitBtnImage, 150, 60);
        gameroomExitButton = new JButton(gameroomExitButtonImageIcon);
        Delete_btn_default_background(gameroomExitButton);
        gameroomExitButton.setBounds(30,520,150,80);
        gameroomExitButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		//방나가는 로직 구현 세부- 방 인원 축소, client와 server의 각 user의 room_id를 lobby의 room_id인 0으로 설정해야함
        		Msg cm = new Msg(userName, "900", Integer.toString(lobbyView.getUser_room_id())); //client의 room_id를 전달 //나가고자하는 방을 알려준 것임
    	        lobbyView.SendObject(cm);
        	}
        });
        gameroomExitButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {} //딱히 구현안함
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) { //이미지 변경
				movemushroom.setVisible(false);
				movemushroom2.setVisible(true);
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				JButton b = (JButton)e.getSource();
				movemushroom2.setVisible(false);
				movemushroom.setVisible(true);
			}
		});
        this.add(gameroomExitButton);
	}
	
	public void drawingPanelGUI() {
        ImageIcon freeDrawingIcon=ChangeImageSizeToIcon(freeDrawingBtnImg, 30, 30); //자유형 아이콘
        ImageIcon rectIcon=ChangeImageSizeToIcon(rectBtnImg, 30, 30);// 정사각형 아이콘
        ImageIcon ovalIcon=ChangeImageSizeToIcon(ovalBtnImg, 30, 30); // 타원형 아이콘
        ImageIcon lineIcon=ChangeImageSizeToIcon(lineBtnImg, 30, 30); // 선 아이콘
        ImageIcon redIcon=ChangeImageSizeToIcon(redBtnImg,30,30);
        ImageIcon blueIcon=ChangeImageSizeToIcon(blueBtnImg,30,30);
        ImageIcon greenIcon=ChangeImageSizeToIcon(GreenBtnImg,30,30);
        ImageIcon yellowIcon=ChangeImageSizeToIcon(YellowBtnImg,30,30);
        ImageIcon blackIcon=ChangeImageSizeToIcon(BlackBtnImg,30,30);
        ImageIcon eraserIcon=ChangeImageSizeToIcon(eraserBtnImage,30,30);
        
		// 이미지 보관용
        panelImage = createImage(panel.getWidth(), panel.getHeight());
        gc2 = (Graphics2D) panelImage.getGraphics();
        gc2.setColor(panel.getBackground());
        gc2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
        //gc2.setColor(Color.BLACK);
        //gc2.drawRect(0, 0, panel.getWidth() - 1, panel.getHeight() - 1);

		forShapeImage = createImage(panel.getWidth(), panel.getHeight());
		gc3 = (Graphics2D) forShapeImage.getGraphics();
		gc3.setColor(panel.getBackground());
		gc3.fillRect(0,0, panel.getWidth(),  panel.getHeight());
        //gc3.setColor(Color.BLACK);
        //gc3.drawRect(0, 0, panel.getWidth() - 1, panel.getHeight() - 1);
		
        // todo 색깔 버튼, 도형 버튼 추가
		final int ColorAndShapeButtonHeight =460;
		final int ColorAndShapeButtonWidth  = 620;
		final int ColorRedStart=270; //빨간펜버튼 시작 x좌표
		final int ColorAndShapeButtonSize=40;//모양 색깔 버튼 크기 x,y;
        JButton redButton = new JButton(redIcon);
        redButton.setBounds(ColorRedStart, ColorAndShapeButtonHeight, ColorAndShapeButtonSize, ColorAndShapeButtonSize);
        JButton blueButton = new JButton(blueIcon);
        blueButton.setBounds(ColorRedStart+40, ColorAndShapeButtonHeight, ColorAndShapeButtonSize, ColorAndShapeButtonSize);
        JButton greenButton = new JButton(greenIcon);
        greenButton.setBounds(ColorRedStart+80, ColorAndShapeButtonHeight, ColorAndShapeButtonSize, ColorAndShapeButtonSize);
        JButton yellowButton = new JButton(yellowIcon);
        yellowButton.setBounds(ColorRedStart+120, ColorAndShapeButtonHeight, ColorAndShapeButtonSize, ColorAndShapeButtonSize);
        JButton blackButton = new JButton(blackIcon);
        blackButton.setBounds(ColorRedStart+160, ColorAndShapeButtonHeight, ColorAndShapeButtonSize, ColorAndShapeButtonSize);

        JButton freeDrawingButton = new JButton(freeDrawingIcon);
        freeDrawingButton.setBounds(ColorAndShapeButtonWidth-50, ColorAndShapeButtonHeight, ColorAndShapeButtonSize, ColorAndShapeButtonSize);
        freeDrawingButton.setBackground(Color.black);
        JButton rectButton = new JButton(rectIcon);
        rectButton.setBounds(ColorAndShapeButtonWidth, ColorAndShapeButtonHeight, ColorAndShapeButtonSize, ColorAndShapeButtonSize);
        rectButton.setBackground(Color.white);
        JButton ovalButton = new JButton(ovalIcon);
        ovalButton.setBounds(ColorAndShapeButtonWidth+50, ColorAndShapeButtonHeight, ColorAndShapeButtonSize, ColorAndShapeButtonSize);
        ovalButton.setBackground(Color.white);
        JButton lineButton = new JButton(lineIcon);
        lineButton.setBounds(ColorAndShapeButtonWidth+50, ColorAndShapeButtonHeight, ColorAndShapeButtonSize, ColorAndShapeButtonSize);
        lineButton.setBackground(Color.white);
        
        JButton eraserButton = new JButton(eraserIcon);
        Delete_btn_default_background(eraserButton);
        eraserButton.setBounds(ColorRedStart+200, ColorAndShapeButtonHeight, ColorAndShapeButtonSize, ColorAndShapeButtonSize);

        JButton clearButton = new JButton("clear");
        clearButton.setBounds(670, ColorAndShapeButtonHeight, 40, 40);
        clearButton.setFont(new Font("굴림", Font.PLAIN, 14));
        
        // 버튼이 눌러졌을 때, 자신의 컬러와 네트워크로 컬러를 변경해줌.
        redButton.addActionListener(new ButtonColorChangeListener(Color.RED, redButton));
        blueButton.addActionListener(new ButtonColorChangeListener(Color.BLUE, blueButton));
        greenButton.addActionListener(new ButtonColorChangeListener(Color.GREEN, greenButton));
        yellowButton.addActionListener(new ButtonColorChangeListener(Color.YELLOW, yellowButton));
        blackButton.addActionListener(new ButtonColorChangeListener(Color.BLACK, blackButton));
        eraserButton.addActionListener(new ButtonColorChangeListener(Color.WHITE, eraserButton));
        freeDrawingButton.addActionListener(new ButtonShapeChangeListener(freeDrawingButton, "free"));
        rectButton.addActionListener(new ButtonShapeChangeListener(rectButton, "rect"));
        ovalButton.addActionListener(new ButtonShapeChangeListener(ovalButton, "oval"));
        lineButton.addActionListener(new ButtonShapeChangeListener(lineButton, "line"));
        //버튼 뒷배경 제거
        Delete_btn_default_background(redButton);
        Delete_btn_default_background(blueButton);
        Delete_btn_default_background(greenButton);
        Delete_btn_default_background(yellowButton);
        Delete_btn_default_background(blackButton);
        
        //
        this.add(redButton);
        this.add(blueButton);
        this.add(greenButton);
        this.add(yellowButton);
        this.add(blackButton);
        this.add(freeDrawingButton);
        this.add(rectButton);
        this.add(ovalButton);
        this.add(lineButton);
        this.add(eraserButton);

	}
	
	class ButtonShapeChangeListener implements ActionListener {
		JButton mineButton;
		String shape_type;
		public ButtonShapeChangeListener(JButton b, String shape_type) {
			mineButton = b;
			this.shape_type = shape_type;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// 테두리 UI는 나중에
	        // mineButton.setBackground(Color.black); 
			if(authorityOfDrawing == true) {
		        Msg cm = new Msg(userName, Protocol.ChangeShape, "CHANGE SHAPE TYPE");
		        cm.room_id = roomId;
		        cm.shape_type = this.shape_type;
		        lobbyView.SendObject(cm);
			}
		}
	}
	
	
	class ButtonColorChangeListener implements ActionListener {
		Color changeColor;
		JButton mineButton;
		
		public ButtonColorChangeListener(Color c, JButton b) {
			changeColor = c;
			mineButton = b;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(authorityOfDrawing == true) {
		        Msg cm = new Msg(userName, Protocol.ChangeColor, "CHANGE COLOR");
		        cm.room_id = roomId;
		        cm.pen_color = changeColor;
		        
		        // 지우개는 freeDrawing으로 변경
		        if(changeColor.equals(Color.WHITE)) {
		        	cm.shape_type = "free";
		        	shape_type = "free";
		        }
		        
		        lobbyView.SendObject(cm);
			}
	        
	        // 테두리 UI는 나중에
	        // mineButton.setBackground(Color.black);
		}
	}
	
    public void receiveChangeColor(Color changeColor) {
        penColor = changeColor;
    }
    public void receiveChangeShape(String shapeType) {
        shape_type = shapeType;
        System.out.println("타입 변경 " + shapeType);
    }
    
    public void receiveFailStartGame() {
    	gameStartButton.setEnabled(true);
    }
	
	public void paint(Graphics g) {
		super.paint(g);
		// Image 영역이 가려졌다 다시 나타날 때 그려준다.
		gc.drawImage(panelImage, 0, 0, this);
	}
	
	
    public void chttingGUI() {
		// 채팅
		txtRecord = new JTextPane();
		txtRecord.setEditable(false);
		txtRecord.setFont(new Font("굴림체",Font.PLAIN,14));
		
		chattingPane = new JScrollPane();	
		chattingPane.setBounds(350, 520, 470, 180);
		chattingPane.setViewportView(txtRecord);
		chattingPane.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.add(chattingPane);
		
		
		// 채팅 입력 field
		txtInput = new JTextField();
		txtInput.setBounds(350, 710, 470, 30);
		txtInput.setFont(new Font("굴림체",Font.PLAIN,14));
		txtInput.setColumns(10);
		//채팅 입력 로직
		txtInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = txtInput.getText();
				lobbyView.SendRoomMessage(msg,lobbyView.getUser_room_id());
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				//승민
			}
		});
		//
		this.add(txtInput);
    }
    // 수신
	public void receiveDrawingEvent(Msg cm) {
		pen_size = cm.pen_size;
		gc2.setColor(cm.pen_color);
		
		// 선택한 그릴 모양에 따른 동작
		switch(cm.shape_type) {
		case "free":
			freeDrawing(cm);
			break;
		case "rect":
			shapeDrawing(cm);
			break;
		case "oval":
			shapeDrawing(cm);
			break;
		case "line":
			shapeDrawing(cm);
			break;
		}
	}
	
	public void receiveAuthorityOfDrawing(String data) {
		gameStartButton.setEnabled(false);
		// 그림판 전체 지우기
        gc2.setColor(panel.getBackground());
        gc2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		gc.drawImage(panelImage, 0, 0, panel);
		
		// 색깔, 도형, 펜 굵기 초기화
		this.penColor = Color.black;
		this.shape_type = "free";
		this.pen_size = 5;
		
		if(data.equals("deprive")) { // 그림 그리는 권한 빼앗기+채팅가능
			txtInput.setEnabled(true);
			authorityOfDrawing = false;
			answerLabel.setVisible(false);
		}
		else { 						 // 권한 주고, 정답 보여주기+채팅불가능
			txtInput.setEnabled(false);
			authorityOfDrawing = true;
			answerLabel.setVisible(true);
			answerLabel.setText("정답 : " + data);
		}
	}
	
	public void freeDrawing(Msg cm) {
		MouseEvent e = cm.mouse_e;
		if(cm.mouse_type.equals("pressed")) {
			oldCoord = e.getPoint();
		}

		gc2.setColor(penColor);
		gc2.setStroke(new BasicStroke(cm.pen_size, BasicStroke.CAP_ROUND, 0));
		gc2.drawLine((int)oldCoord.getX(), (int)oldCoord.getY(), e.getX(), e.getY());
		gc.drawImage(panelImage, 0, 0, panel);
		oldCoord = e.getPoint();

		gc3.drawImage(panelImage, 0, 0, panel);
	}
	
	public void shapeDrawing(Msg cm) {
		MouseEvent e = cm.mouse_e;
		// 마우스 타입에 따른 동작
		switch(cm.mouse_type) {
		case "dragged":
			makeShape(pressedCoord, e.getPoint(), cm.shape_type);
			break;
		case "pressed":
			gc3.drawImage(forShapeImage, 0, 0, panel);
			pressedCoord = e.getPoint();
			pressedCoordForLine = e.getPoint();
			break;
		case "released":
			makeShape(pressedCoord, e.getPoint(), cm.shape_type);
			gc3.drawImage(panelImage, 0, 0, panel);
			break;
		case "clicked":
			pressedCoord = e.getPoint();
		}
	}
	public void endDialog(Msg msg){//승민
		MyDialog end_dialog = new MyDialog(this,"end",msg.data+"님이 이겼습니다!");
		end_dialog.setBounds(pen_size, pen_size, 330, 200);
		end_dialog.setVisible(true);
	}
	public void setScore(Msg msg) {
		String[] args=msg.data.split(" ");
		if(args[1].matches("1")) {
			userScore1.setText(args[0]);
			repaint();
		}
		if(args[1].matches("2")) {
			userScore2.setText(args[0]);
			repaint();
		}
		if(args[1].matches("3")) {
			userScore3.setText(args[0]);
			repaint();
		}
		if(args[1].matches("4")) {
			userScore4.setText(args[0]);
			repaint();
		}
	}
    public void setProfile(Msg msg) {//프로필 그리는 함수
    	repaint();
    	if(msg.UserName.matches("")) //널대비
    		msg.UserName="etc";
    		if(msg.data.matches("1")) {
    			if(!(userImagePanel1==null))
    	    		this.remove(userImagePanel1);
    			if(!(userName1==null))
    				this.remove(userName1);
    			userName1 = new JLabel(msg.UserName);
    			if(!(msg.profileImgIcon==null)) { 
    			Image userImage = msg.getProfileImgIcon().getImage();
    			userImagePanel1 = new ImagePanel(userImage);
    			userImagePanel1.setBounds(30, 70, 100, 100);
    			this.add(userImagePanel1);
    			userName1.setFont(new Font("굴림체",Font.BOLD,20));
    			userName1.setBounds(150, 75, 150, 35);
    			this.add(userName1);
    			userScore1.setFont(new Font("굴림체",Font.BOLD,20));
    			userScore1.setBounds(150, 115, 150, 35);
    			this.add(userScore1);
    			}
    			repaint();
    		}
    		else if(msg.data.matches("2")) {
    			if(!(userImagePanel2==null))
    	    		this.remove(userImagePanel2);
    			if(!(userName2==null))
    				this.remove(userName2);
    			if(!(msg.profileImgIcon==null)) { 
    			Image userImage2 = msg.getProfileImgIcon().getImage();
    			userImagePanel2 = new ImagePanel(userImage2);
    			userImagePanel2.setBounds(30, 200, 100, 100);
    			this.add(userImagePanel2);
    			userName2 = new JLabel(msg.UserName);
    			userName2.setFont(new Font("굴림체",Font.BOLD,20));
    			userName2.setBounds(150, 205, 150, 35);
    			this.add(userName2);
    			userScore2.setFont(new Font("굴림체",Font.BOLD,20));
    			userScore2.setBounds(150, 245, 150, 35);
    			this.add(userScore2);
    			}
    			repaint();
    			
    		}
    		else if(msg.data.matches("3")) {
    			if(!(userImagePanel3==null))
    	    		this.remove(userImagePanel3);
    			if(!(userName3==null))
    				this.remove(userName3);
    			if(!(msg.profileImgIcon==null)) { 
    			Image userImage3 = msg.getProfileImgIcon().getImage();
    			userImagePanel3 = new ImagePanel(userImage3);
    			userImagePanel3.setBounds(950, 70, 100, 100);
    			this.add(userImagePanel3);
    			userName3 = new JLabel(msg.UserName);
    			userName3.setFont(new Font("굴림체",Font.BOLD,20));
    			userName3.setBounds(1070, 75, 150, 35);
    			this.add(userName3);
    			userScore3.setFont(new Font("굴림체",Font.BOLD,20));
    			userScore3.setBounds(1070, 115, 150, 35);
    			this.add(userScore3);
    			}
    			repaint();
    		}
    		else if(msg.data.matches("4")){ 
    			if(!(userImagePanel4==null))
    	    		this.remove(userImagePanel4);
    			if(!(userName4==null))
    				this.remove(userName4);
    			if(!(msg.profileImgIcon==null)) { 
    			Image userImage4 = msg.getProfileImgIcon().getImage();
    			userImagePanel4 = new ImagePanel(userImage4);
    			userImagePanel4.setBounds(950, 200, 100, 100);
    			this.add(userImagePanel4);
    			userName4 = new JLabel(msg.UserName);
    			userName4.setFont(new Font("굴림체",Font.BOLD,20));
    			userName4.setBounds(1070, 205, 150, 35);
    			this.add(userName4);
    			userScore4.setFont(new Font("굴림체",Font.BOLD,20));
    			userScore4.setBounds(1070, 245, 150, 35);
    			this.add(userScore4);
    			}
    			repaint();
    		}
    }
	// 송신
	public void sendDrawingEvent(MouseEvent e, String mouse_type, String shape_tpye) {
		// 권한이 있는 사람만 보낼 수 있음.
		if(authorityOfDrawing == true) {
			Msg cm = new Msg(userName, Protocol.Drawing, "DRAWING");
			cm.mouse_e = e;
			cm.pen_size = pen_size;
			cm.mouse_type = mouse_type;
			cm.room_id = roomId;
			cm.shape_type = shape_tpye;
			lobbyView.SendObject(cm);
		}
	}

    class MyMouseWheelEvent implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// TODO Auto-generated method stub
			if (e.getWheelRotation() < 0) { // 위로 올리는 경우 pen_size 증가
				if (pen_size < 20)
					pen_size++;
			} else {
				if (pen_size > 5)
					pen_size--;
			}
		}
	}
	
	// 자신의 것에 그리지 않고 오직 수신해서만 그리기 
	class MyMouseEvent implements MouseListener, MouseMotionListener {
		
		@Override
		public void mouseDragged(MouseEvent e) {
			sendDrawingEvent(e, "dragged", shape_type);
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			sendDrawingEvent(e, "clicked", shape_type);
		}
		@Override
		public void mousePressed(MouseEvent e) {
			sendDrawingEvent(e, "pressed", shape_type);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			switch(shape_type) {
			case "rect":
				sendDrawingEvent(e, "released", "rect");
				break;
			case "oval":
				sendDrawingEvent(e, "released", "oval");
				break;
			case "line":
				sendDrawingEvent(e, "released", "line");
				break;
			}
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
	}
    
	public void makeShape(Point pressedCoord, Point releasedCoord, String shape_type) {
		// 두 x값 중 작은 값을 구한다. (기준)
		// 두 y값 중 작은 값을 구한다. (기준)
		int x = (int) Math.min(pressedCoord.getX(), releasedCoord.getX());
		int y = (int) Math.min(pressedCoord.getY(), releasedCoord.getY());

		// 두 사이의 길이(절대값)을 구한다.
		int width = (int) Math.abs(pressedCoord.getX() - releasedCoord.getX());
		int height = (int) Math.abs(pressedCoord.getY() - releasedCoord.getY());
		
		// 사각형을 그린다.
		// - 도형을 그리기 전의 도화지를 계속 로드한다.
		gc2.drawImage(forShapeImage, 0, 0, panel);
		gc2.setColor(penColor);
		gc2.setStroke(new BasicStroke(pen_size, BasicStroke.CAP_ROUND, 0));
		// - 그곳에 새로운 좌표로 도형을 그린다.
		switch(shape_type) {
		case "rect":
			gc2.drawRect(x, y, width, height);
			break;
		case "oval":
			gc2.drawOval(x, y, width, height);
			break;
		case "line":
			gc2.drawLine((int) pressedCoordForLine.getX(), (int) pressedCoordForLine.getY(),
					(int) releasedCoord.getX(), (int) releasedCoord.getY());
		}
		// - 지금껏 그린 이미지를 본 panel에 덮어쓴다.
		gc.drawImage(panelImage, 0, 0, panel);
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
	public ImageIcon ChangeImageSizeToIcon(Image img,int x,int y) {
		Image ChangeSizeImg = img.getScaledInstance(x, y, Image.SCALE_SMOOTH);
		ImageIcon changeSizeIcon = new ImageIcon(ChangeSizeImg);
		return changeSizeIcon;
	} //이미지 아이콘 사이즈 변경 함수
	public void Delete_btn_default_background(JButton btn) {
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setOpaque(false);
	}//이미지 버튼 만들때 백그라운드 지우는 함수 로직 구현해둠
	
	
	// 정답 수신
	public void receiveAnswer(Msg cm) {
		String answer = cm.data;
		answerLabel.setText("정답 : " + answer);
	}
	
//	// 힌트 송신
	public void requireHint() {
        Msg cm = new Msg(userName, Protocol.GetHint, "");
        cm.room_id = roomId;
		lobbyView.SendObject(cm);
	}
	
//	// 힌트 수신
	public void receiveHint(String hint) {
		hintLabel.setText("힌트 : " + hint);
	}
	public void gamestartbtnoff() {
		gameStartButton.setEnabled(false);
	}
	public class MyDialog extends JDialog{
		ImagePanel dialog_img = new ImagePanel(EndgameDialogImage);
		JButton okButton = new JButton("확인");
		JLabel user_msg = new JLabel();
		public MyDialog(JFrame frame,String title,String message) {
			super(frame,title);
			setLayout(null);
			user_msg.setText(message);
			user_msg.setFont(new Font("굴림체",Font.BOLD,20));
			user_msg.setBounds(90, 35, 300, 60); 
			okButton.setBounds(100, 110, 100, 30);
			dialog_img.setBounds(0, 0, 80, 150);
			setSize(330,200);
			this.add(okButton);
			this.add(user_msg);
			this.add(dialog_img);
			repaint();
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					Msg cm = new Msg(userName, "900", Integer.toString(lobbyView.getUser_room_id())); //client의 room_id를 전달 //나가고자하는 방을 알려준 것임
	    	        lobbyView.SendObject(cm);
				}
			});
			
		}
	}
}


