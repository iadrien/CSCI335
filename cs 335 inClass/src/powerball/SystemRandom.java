package powerball;

import java.util.Random;

public class SystemRandom {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Random randomNumber = new Random();
		int powerNumber;
		
		for (int i =0;i<5;i++){
			
			powerNumber = randomNumber.nextInt(69)+1;
			System.out.print(powerNumber+"-");
		}
		
		int powerBall = randomNumber.nextInt(26)+1;
		System.out.print("-"+powerBall);
	}

}
