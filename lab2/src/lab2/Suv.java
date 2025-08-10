package lab2;
import java.util.Vector;

class Suv extends vehicle{
	public static int count=0;
	public Vector<String> offroadModes= new Vector<String>();
	public Suv(String name, double price) {
		super(name, price,count++);
		offroadModes.add("sand");
		offroadModes.add("snow");
		offroadModes.add("mud");
	}
	public void listofoffroadmodes() {
		for(String it: offroadModes) {
			System.out.println(it);
		}
	}
	@Override
	public void startEngine() {
		System.out.println("SUV car engine starting...");
		
	}

	@Override
	public void stopEngine() {
		System.out.println("SUV car engine stopping...");
		
	}
	
}
