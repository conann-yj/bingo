package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class BingoGame {
	static JPanel panelNorth; // 상단 화면
	static JPanel panelCenter; // 게임 화면
	static JLabel labelMessage;
	static JButton[] buttons = new JButton[16];
	static String[] images = { "food01.png", "food02.png", "food03.png", "food04.png", "food05.png", "food06.png",
			"food07.png", "food08.png", "food01.png", "food02.png", "food03.png", "food04.png", "food05.png",
			"food06.png", "food07.png", "food08.png" }; //빙고 화면 추가
	
	static int openCount = 0;
	static int buttonIndexSave1 = 0; //첫번째 카드 클릭!
	static int buttonIndexSave2 = 0; //두번째 카드 클릭!
	static Timer timer;
	static int tryCount = 0;
	static int successCount = 0;
	
	static class MyFrame extends JFrame implements ActionListener{
		public MyFrame(String title) {
			super(title);
			this.setLayout(new BorderLayout());
			this.setSize(400, 500);
			this.setVisible(true);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			initUI(this); // 화면구성
			mixCard(); //음식카드 섞기
			
			this.pack(); //여백 정리
		}
		
		//같은그림 또는 다른그림 나올 때 효과음 
		static void playSound(String fileName) {
			File file = new File("./wav/"+fileName);
			if(file.exists()) {
				try {
					AudioInputStream stream = AudioSystem.getAudioInputStream(file);
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}else {
				System.out.println("소리가 들리지 않습니다!");
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.println("Button Clicked!");
			
			if(openCount == 2) {
				return;
			}
			
			JButton btn = (JButton) e.getSource();
			int index = getButtonIndex(btn);
			//System.out.println("Button Index: "+ index);
			
			btn.setIcon(changeImage(images[index]));
			
			openCount++;
			if(openCount == 1) { //첫번째 카드
				buttonIndexSave1 = index;
			}else if(openCount == 2){ //두번째 카드
				buttonIndexSave2 = index;
				tryCount++; //두번째 클릭할 때 횟수 추가
				labelMessage.setText("Find Same Food! " + "Try " + tryCount);
				
				//같은 그림인지 아닌지 판단
				boolean isBingo = checkCard(buttonIndexSave1, buttonIndexSave2);
				if(isBingo == true) {
					playSound("bingo.wav");
					openCount = 0;
					successCount++;
					if(successCount == 8) {
						labelMessage.setText("Game Over " + "Try " + tryCount);
					}
				}else {
					backQuestion();
					
				}
			}
		}
		
		public void backQuestion() {
			//그림 누르고 기다리는 타임(초)
			timer = new Timer(1000,new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Timer.");
					
					playSound("fail.wav");
					openCount = 0;
					buttons[buttonIndexSave1].setIcon(changeImage("suggestion.png"));
					buttons[buttonIndexSave2].setIcon(changeImage("suggestion.png"));
					timer.stop();
				}
			});
			timer.start();
			
			
		}
		
		public boolean checkCard(int index1, int index2) {
			if(index1 == index2) {
				return false;
			}
			if(images[index1].equals(images[index2])) {
				return true;
			}else {
				return false;
			}
		}
		
		
		public int getButtonIndex(JButton btn) {
			int index = 0;
			for(int i=0; i<16; i++) {
				if(buttons[i] == btn) {
					index = i;
				}
			}
			return index;
		}
	}
	
	static void mixCard() {
		Random rand = new Random();
		for(int i=0; i<1000; i++) {
			int random = rand.nextInt(15) + 1; //1~15
			
			String temp = images[0];
			images[0] = images[random];
			images[random] = temp;
		}
	}
	

	static void initUI(MyFrame myFrame) {
		//상단 화면 구성
		panelNorth = new JPanel();
		panelNorth.setPreferredSize(new Dimension(400,100));
		panelNorth.setBackground(Color.PINK);
		labelMessage = new JLabel("Find Same Food! "+" Try 0");
		labelMessage.setPreferredSize(new Dimension(400,100));
		labelMessage.setForeground(Color.GRAY);
		labelMessage.setHorizontalAlignment(JLabel.CENTER);
		panelNorth.add(labelMessage);
		myFrame.add("North",panelNorth);
		
		//게임 화면 구성
		panelCenter = new JPanel();
		panelCenter.setLayout(new GridLayout(4,4));
		panelCenter.setPreferredSize(new Dimension(400,400));
		for(int i=0; i<16; i++) {
			buttons[i] = new JButton();
			buttons[i].setPreferredSize(new Dimension(100,100));
			buttons[i].setIcon(changeImage("suggestion.png"));
			buttons[i].addActionListener(myFrame); //버튼 클릭
			panelCenter.add(buttons[i]);
		}
		myFrame.add("Center", panelCenter);
	}

	static ImageIcon changeImage(String fileName) {
		ImageIcon icon = new ImageIcon("./img/" + fileName);
		Image originImage = icon.getImage();
		Image changedImage = originImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		ImageIcon icon_new = new ImageIcon(changedImage);
		return icon_new;
		
	}
	
	public static void main(String[] args) {
		new MyFrame("B.I.N.G.O ! Bingo Game");

	}



}
