package lab2;
import java.util.Scanner;
public class task2 {
	public double[] add(double a[],double b[],int n) {
		double res[]=new double[n];
		for(int i=0;i<n;i++) {
			res[i]=a[i]+b[i];
		}
		return res;
	}
	public double dot(double a[],double b[],int n) {
		double res=0;
		for(int i=0;i<n;i++) {
			res+=a[i]*b[i];
		}
		return res;
	}
	public double[] Cross(double a[],double b[],int n) {
		if(n!=3) {
			return new double[] {0};
		}
		return new double[] {
	            a[1] * b[2] - a[2] * b[1],
	            a[2] * b[0] - a[0] * b[2],
	            a[0] * b[1] - a[1] * b[0]
	    };
	}
	public double angle(double a[],double b[],int n) {
		double s=dot(a,b,n);
		double a2=0,b2=0;
		for(int i=0;i<n;i++) {
			a2+=a[i]*a[i];
		}
		for(int i=0;i<n;i++) {
			b2+=b[i]*b[i];
		}
		a2=Math.sqrt(a2);
		b2=Math.sqrt(b2);
		double res=s/(a2*b2);
		res=Math.acos(res);
		return res;
	}
	public static void main(String[] args) {
		int dimension;
		Scanner scanner = new Scanner ( System.in );
		dimension=scanner.nextInt();
		double a[]=new double[dimension],b[]=new double[dimension];
		for(int i=0;i<dimension;i++) {
			a[i]=scanner.nextDouble();
		}
		for(int i=0;i<dimension;i++) {
			b[i]=scanner.nextDouble();
		}
		
	}

}
