import java.awt.Color;
import java.awt.Font;
import java.util.Vector;

import javax.swing.JLabel;

class TimerNum extends JLabel implements Runnable {
	int width = 75, height = 75;
	int x = 200, y = 150;
	
	int second;
	
	private Vector<String> answers = new Vector<String>();
	private String answer;
	private final int GAME_COUNT;
	private CatchMindPlayGame game;
	public TimerNum(CatchMindPlayGame game, int second) {
		this.GAME_COUNT = second;
		initialAnswers();
		setOpaque(true);
		setBounds(750, 30, 100, 100);
		setForeground(Color.BLUE);
		setText(second + "");
		setFont(new Font("맑은고딕", Font.PLAIN, 50));
		setHorizontalAlignment(JLabel.CENTER);
		
		this.second = second;
		this.game = game;
		changeAnswer();
	}

	@Override
	public void run() {
		// 초기 정답 설정
		sendChangeAnswer();
		while (true) {
			try {
				Thread.sleep(1000);	// 1초
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (second > 0) {
				second -= 1;		// 1초씩 줄어듦
				setText(second + "");
			} else {
				changeAnswer();
				second = GAME_COUNT;
				sendChangeAnswer();
			}
		}
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
	
	private void changeAnswer()  {
		if(answers.size() < 0) {
			// TODO answers를 다 썼을 때 예외 처리
		}
		int randomIndex = (int) Math.random() * answers.size();
		this.answer = answers.remove(randomIndex);
	}
	
	public String getHint() {
		return this.answer.substring(0, 1);
	}
	
	public String getAnswer() {
		return this.answer;
	}
	
	private void sendChangeAnswer() {
		//game.sendAnswer(answer);
	}
	
}