package task2;
import java.util.Vector;
public class Resultmanagement {
	Vector<Integer> courseID = new Vector<>();
	Vector<String> coursename = new Vector<>();
	Vector<Student> std = new Vector<>();
	public void addcourse(String cname,int id) {
		this.courseID.add(id);
		this.coursename.add(cname);
	}
	public void addstudent(String name) {
		Student s = new Student(name);
        std.add(s);
	}
	public void displaysub() {
		for(int i=0;i<courseID.size();i++) {
			System.out.println(courseID.get(i)+" "+ coursename.get(i));
		}
	}
	public void searchstud(int id) {
		int idx=0;
		for(Student s : std) {
			if(s.getid()==id) {
				break;
			}
			idx++;
		}
		std.get(idx).getResult();
	}
}
