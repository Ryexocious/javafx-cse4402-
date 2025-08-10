package lab2;

abstract class vehicle {
	protected String name;
    protected double price;
    protected int ModelNumber;

    public vehicle(String name, double price, int ModelNumber) {
        this.name = name;
        this.price = price;
        this.ModelNumber = ModelNumber;
    }

    public String getModelNumber() {
        return name + " - " + ModelNumber;
    }

    public abstract void startEngine();
    public abstract void stopEngine();
}
