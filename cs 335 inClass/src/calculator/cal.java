package calculator;

public class cal {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double hw;
		double test;
		double midterm;
		double finalexam;
		hw = (84.0 + 98 + 97 + 97 + 93 + 90 + 97 + 100 + 100 + 77 + 94)/11;
		System.out.println(hw);
		test = (98.0 + 98 + 100 + 100  + 80 + 100)/6;
		System.out.println(test);
		midterm = 98;
		System.out.println(midterm);
		finalexam = 93 - 0.25*hw - 0.25*test - 0.25*midterm;
		finalexam = finalexam / 25;
		System.out.println(finalexam);
	}

}
