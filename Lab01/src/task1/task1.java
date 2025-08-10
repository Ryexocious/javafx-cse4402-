package task1;
import java.util.Random;
import java.util.Scanner;
public class task1 {

	public static void main(String[] args) {
		 Random rand = new Random();
		 Scanner scanner = new Scanner ( System.in );
		 int a= rand.nextInt(100);
		 int b=-1;
		 while(b!=a) {
			 System.out.println("Enter your choice?");
			 b=scanner.nextInt();
			 scanner.nextLine();
			 if(b<a) {
				 System.out.println("your guess is lower try again.");
			 }
			 else if(b>a) {
				 System.out.println("your guess is higher try again.");
			 }
		 }
		 System.out.println("you guessed it correct the number was " + a);

	}

}
