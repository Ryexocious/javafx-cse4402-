package lab2;

class electriccar extends vehicle{
	public static int count=0;
	public double batterylevel=0;
	electriccar(String n,double p,double b){
		super(n,p,count++);
		this.batterylevel=b;
	}
	public void chargeBattery() {
		System.out.println("Charging battery to 100%...");
	}
	@Override
	public void startEngine() {
		System.out.println("Electric car engine starting...");
		
	}

	@Override
	public void stopEngine() {
		System.out.println("Electric car engine stopping...");
		
	}
	
}