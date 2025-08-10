package task2;
import java.util.HashMap;
import java.util.Map;
public class Student {
	public static int id2=1;
	int id;
	String name;
	Map<Integer, Integer> courseMarks = new HashMap<>();
	Student (String sname){
		this.id=id2++;
		this.name=sname;
	}
	public int getid() {
		return id;
	}
	public String getname() {
		return name;
	}
	public void assignMarks(int courseId, int marks) {
        courseMarks.put(courseId, marks);
    }
    public void getResult() {
    	System.out.println("format is courseid \t marks");
    	for(Map.Entry<Integer, Integer> entry : courseMarks.entrySet()) {
    		int a=entry.getKey();
    		int b=entry.getValue();
    		System.out.println(a+" "+b);
    	}
    }
}