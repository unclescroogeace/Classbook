package uni.fmi.bachelors.grades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import uni.fmi.bachelors.students.Student;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GradeAddEditForm {

	public JFrame frame;
	private JTextField tfGrade;
	private JComboBox<String> comboBox;
	private Student student;
	private JLabel labelStudentData;
	private Grade grade;

	/**
	 * Create the application.
	 */
	public GradeAddEditForm(Student student, Grade grade) {
		initialize();
		this.student = student;
		labelStudentData.setText(student.getStudentFullName());
		this.grade = grade;
		comboBox.setSelectedItem(grade.subjectName);
		tfGrade.setText(String.valueOf(grade.grade));
	}
	
	public GradeAddEditForm(Student student) {
		initialize();
		this.student = student;
		labelStudentData.setText(student.getStudentFullName());
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 391, 177);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblGrade = new JLabel("Grade");
		lblGrade.setBounds(10, 76, 55, 14);
		frame.getContentPane().add(lblGrade);
		
		JLabel lblGradeFor = new JLabel("Grade for : ");
		lblGradeFor.setBounds(10, 11, 69, 14);
		frame.getContentPane().add(lblGradeFor);
		
		labelStudentData = new JLabel("");
		labelStudentData.setBounds(89, 11, 272, 14);
		frame.getContentPane().add(labelStudentData);
		
		JLabel lblSubject = new JLabel("Subject");
		lblSubject.setBounds(10, 51, 55, 14);
		frame.getContentPane().add(lblSubject);
		
		comboBox = new JComboBox<String>();
		comboBox.setBounds(75, 48, 286, 20);
		frame.getContentPane().add(comboBox);
		
		tfGrade = new JTextField();
		tfGrade.setBounds(75, 73, 22, 20);
		frame.getContentPane().add(tfGrade);
		tfGrade.setColumns(10);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				double g = Double.parseDouble(tfGrade.getText());
				if(comboBox.getSelectedIndex() >= 0
						&& g >= 2.0 
						&& g <= 6.0) {
					
					if(grade == null) {
						grade = new Grade();
					}
					
					grade.grade = Double.parseDouble(tfGrade.getText());
					grade.subjectName = comboBox.getSelectedItem().toString();
					
					if(grade.id != -1) {
						updateGrade(grade);
					}else {
						insertGrade(grade);
					}
					
					GradesForm gf = new GradesForm(student);
					gf.frmGradesClassbook.setVisible(true);
					frame.setVisible(false);
					frame.dispose();
				}
			}
		});
		btnSave.setBounds(140, 103, 89, 23);
		frame.getContentPane().add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.dispose();
				GradesForm gf = new GradesForm(student);
				gf.frmGradesClassbook.setVisible(true);
			}
		});
		btnCancel.setBounds(260, 103, 89, 23);
		frame.getContentPane().add(btnCancel);
		
		fillSubjects();
	}
	
	protected void insertGrade(Grade grade) {
		Connection conn = null;
		try {
			conn = getConnection();
			long subjectId = -1;
			
			String queryGetSubjectId = "SELECT ID FROM SUBJECTS WHERE NAME = ?";

			PreparedStatement pst = conn.prepareStatement(queryGetSubjectId);
			pst.setString(1, comboBox.getSelectedItem().toString());
			ResultSet rs = pst.executeQuery();
			
			if(rs.first()){
				subjectId = rs.getLong(rs.findColumn("ID"));
			}
			
			String query = "INSERT INTO GRADES (STUDENTID, SUBJECTID, GRADE) VALUES (?,?,?)";
			
			PreparedStatement pst2 = conn.prepareStatement(query);
			
			pst2.setLong(1, student.getStudentId());
			pst2.setLong(2, subjectId);
			pst2.setDouble(3, grade.grade);
			
			pst2.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	protected void updateGrade(Grade grade) {
		Connection conn = null;
		try {
			conn = getConnection();
			long subjectId = -1;
			
			String queryGetSubjectId = "SELECT ID FROM SUBJECTS WHERE NAME = ?";

			PreparedStatement pst = conn.prepareStatement(queryGetSubjectId);
			pst.setString(1, comboBox.getSelectedItem().toString());
			ResultSet rs = pst.executeQuery();
			
			if(rs.first()){
				subjectId = rs.getLong(rs.findColumn("ID"));
			}
			
			String query = "UPDATE GRADES SET GRADE = ?, SUBJECTID = ? WHERE ID = ?";
			
			PreparedStatement pst2 = conn.prepareStatement(query);
			pst2.setDouble(1, grade.grade);
			pst2.setLong(2, subjectId);
			pst2.setLong(3, grade.id);
			
			pst2.executeUpdate();
			
		} catch (SQLException e) {			
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

	private Connection getConnection() throws SQLException{
		return DriverManager.getConnection(
				"jdbc:h2:~/classbookDB","sa","");
	}
	
	private void fillSubjects() {
		Connection conn = null;
		try {
			conn = getConnection();
			String query = "SELECT ID, NAME FROM SUBJECTS";
			PreparedStatement pst = conn.prepareStatement(query);
			
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()){
				comboBox.addItem(rs.getString("NAME"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}
}
