package uni.fmi.bachelors.students;

public class Student {
	String FirstName;
	String LastName;
	String FacultyNumber;
	String gender;
	String Email;
	long id = -1;
	
	public String getStudentFullName() {
		return FirstName + " " + LastName;
	}
	
	public long getStudentId() {
		return id;
	}
	
	public String getFacultyNumber() {
		return FacultyNumber;
	}
}
