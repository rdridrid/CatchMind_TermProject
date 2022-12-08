import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.sun.tools.javac.Main;
public class CatchMindClientMain extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private ImagePanel LoginPane;
	private RoundJTextField txtUserName;
	private String ip_addr = "127.0.0.1";
	private String port_no = "30000";
	Image background_img=makeImage("./rsc/메인화면.jpg");
	Image Enter_Lobby_img=makeImage("./rsc/시작하기 초기.png");
	Image Exit_btn_img=makeImage("./rsc/게임종료 초기.png");
	Image Pressed_Enter_Lobby_img=makeImage("./rsc/시작하기 누르기.png");
	Image Pressed_Exit_btn_img=makeImage("./rsc/게임종료 누르기.png");
	Login_music login_music;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CatchMindClientMain frame = new CatchMindClientMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	class Login_music extends Thread {
		public int flag=0;
		public void run(){
		{
			while(flag==0) { //스레드를 직접 stop하는건 위험함
				File a = new File("./rsc/메이플스토리_로그인.wav");
				try{
					AudioInputStream b = AudioSystem.getAudioInputStream(a);
					Clip c = AudioSystem.getClip();
					c.open(b);
					c.start();
					Thread.sleep(c.getMicrosecondLength()/1000);
					}catch(Exception ee){ //오류처리를 해주긴 해야하는데
						ee.printStackTrace();
						return;
					}
				}
			}
		}
	}//class Login_music
	public CatchMindClientMain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //종료시 프로세스도 함께 종료
		setIconImage(makeImage("./rsc/메인아이콘.jpg"));
		setTitle("캐치마인드");
		setSize(950,680);
		setLocationRelativeTo(null);
		setLayout(null);
		setVisible(true);
		setResizable(false);
		contentPane = new ImagePanel(background_img);
		setBounds(300,300,950,680);
		setContentPane(contentPane);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		txtUserName = new RoundJTextField(1);
		txtUserName.setHorizontalAlignment(SwingConstants.CENTER);
		txtUserName.setBounds(430, 350, 120, 40);
		txtUserName.setText("NAME");
		txtUserName.setFont(new Font("굴림체",Font.BOLD,19));
		contentPane.add(txtUserName);
		txtUserName.setColumns(10);
		ImageIcon First_Enter_btn_img = ChangeImageSizeToIcon(Enter_Lobby_img,160,70);
		ImageIcon First_Exit_btn_img= ChangeImageSizeToIcon(Exit_btn_img,160,70);
		ImageIcon Second_Enter_btn_img = ChangeImageSizeToIcon(Pressed_Enter_Lobby_img,160,70);
		ImageIcon Second_Exit_btn_img= ChangeImageSizeToIcon(Pressed_Exit_btn_img,160,70);
		JButton btngameStart = new JButton(First_Enter_btn_img); //게임시작버튼
		btngameStart.setBorderPainted(false);
		btngameStart.setContentAreaFilled(false);
		btngameStart.setOpaque(false);
		
		JButton btngameExit = new JButton(First_Exit_btn_img);
		//
		//btngameExit.setIcon(changeSizeIcon); //setIcon으로 동적 변경
		btngameExit.setBorderPainted(false);
		btngameExit.setContentAreaFilled(false);
		btngameExit.setOpaque(false);
		btngameStart.setBounds(700,480,160,70);
		btngameExit.setBounds(700,550,160,70);
		contentPane.add(btngameStart);
		contentPane.add(btngameExit);
		Myaction action = new Myaction();
		btngameStart.addActionListener(action);
		btngameStart.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {} //딱히 구현안함
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) { //이미지 변경 파트
				 JButton b = (JButton)e.getSource();
			     b.setIcon(Second_Enter_btn_img);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				JButton b = (JButton)e.getSource();
			     b.setIcon(First_Enter_btn_img);
			}
		});
		//login_music = new Login_music();  //로그인 음악 구현. 스레드 종료를 아직 제대로 구현 못함
		//login_music.start();
		
		btngameExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				setVisible(false);
			}
		});
		btngameExit.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {} //딱히 구현안함
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) { //이미지 변경 파트
				 JButton b = (JButton)e.getSource();
			     b.setIcon(Second_Exit_btn_img);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				JButton b = (JButton)e.getSource();
			     b.setIcon(First_Exit_btn_img);
			}
		});
		txtUserName.addActionListener(action);
		
		
	}
	class Myaction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String username=txtUserName.getText().trim();
			CatchMindLobby lobby = new CatchMindLobby(username,ip_addr,port_no);
			setVisible(false);
		}
	}
	class MyMouseListener implements MouseListener{
		@Override
	    public void mouseClicked(MouseEvent e) {
	    }
	    @Override
	    public void mousePressed(MouseEvent e) {
	    }
	    @Override
	    public void mouseReleased(MouseEvent e) {
	    }
	    @Override//마우스가 버튼 안으로 들어오면 빨간색으로 바뀜
	    public void mouseEntered(MouseEvent e) {
	    	ImageIcon changeSizeIcon = ChangeImageSizeToIcon(Enter_Lobby_img,160,70);
			ImageIcon changeSizeIcon2= ChangeImageSizeToIcon(Exit_btn_img,160,70);
	        JButton b = (JButton)e.getSource();
	        b.setIcon(changeSizeIcon2);
	    }
		@Override
		public void mouseExited(MouseEvent e) {
			 JButton b = (JButton)e.getSource();
		        b.setBackground(Color.YELLOW);
			
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
	public ImageIcon ChangeImageSizeToIcon(Image img,int x,int y) {
		Image ChangeSizeImg = img.getScaledInstance(x, y, Image.SCALE_SMOOTH);
		ImageIcon changeSizeIcon = new ImageIcon(ChangeSizeImg);
		return changeSizeIcon;
	}

}