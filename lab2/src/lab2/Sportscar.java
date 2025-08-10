package lab2;

class Sportscar extends vehicle {
	public static int count=0;
	public double topspeed=0;
	Sportscar(String name, double price,double topspeed) {
		super(name, price,count++);
		this.topspeed=topspeed;
	}
	public void boost() {
		System.out.println("Activating boost! Top speed: "+topspeed+ "km/h’’");
	}
	@Override
	public void startEngine() {
		System.out.println("Sports car engine starting...");
		
	}

	@Override
	public void stopEngine() {
		System.out.println("Sports car engine stopping...");
		
	}
	
}
