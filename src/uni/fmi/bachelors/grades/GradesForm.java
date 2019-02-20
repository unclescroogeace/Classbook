package uni.fmi.bachelors.grades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import uni.fmi.bachelors.students.Student;
import uni.fmi.bachelors.students.StudentsForm;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GradesForm {

	public JFrame frmGradesClassbook;
	private JTable tableGrades;
	private Student student;
	private JLabel lblStudentName;
	

	/**
	 * Create the application.
	 */
	public GradesForm(Student student) {
		initialize();
		this.student = student;
		lblStudentName.setText(student.getStudentFullName() + " ( " + student.getFacultyNumber() + " )");
		fillTableData(student.getStudentId());
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmGradesClassbook = new JFrame();
		frmGradesClassbook.setTitle("Grades - Classbook");
		frmGradesClassbook.setBounds(100, 100, 424, 332);
		frmGradesClassbook.getContentPane().setLayout(null);
		
		JLabel lblGradesFor = new JLabel("Grades for : ");
		lblGradesFor.setBounds(10, 11, 70, 14);
		frmGradesClassbook.getContentPane().add(lblGradesFor);
		
		lblStudentName = new JLabel("");
		lblStudentName.setBounds(90, 11, 308, 14);
		frmGradesClassbook.getContentPane().add(lblStudentName);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(10, 31, 388, 219);
		frmGradesClassbook.getContentPane().add(panel);
		
		tableGrades = new JTable();
		tableGrades.setBounds(0, 0, 388, 219);
		panel.add(tableGrades);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GradeAddEditForm gaef = new GradeAddEditForm(student);
				gaef.frame.setVisible(true);
				frmGradesClassbook.setVisible(false);
				frmGradesClassbook.dispose();
			}
		});
		btnAdd.setBounds(10, 261, 89, 23);
		frmGradesClassbook.getContentPane().add(btnAdd);
		
		JButton btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = tableGrades.getSelectedRow();
				long id = (long)tableGrades.getValueAt(row, 0);
				editGrade(id);
			}
		});
		btnEdit.setBounds(109, 261, 89, 23);
		frmGradesClassbook.getContentPane().add(btnEdit);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = tableGrades.getSelectedRow();
				long id = (long)tableGrades.getValueAt(row, 0);
				
				deleteStudent(id);	
				fillTableData(student.getStudentId());
			}
		});
		btnDelete.setBounds(208, 261, 89, 23);
		frmGradesClassbook.getContentPane().add(btnDelete);
		
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmGradesClassbook.setVisible(false);
				StudentsForm sf = new StudentsForm();
				sf.frmStudents.setVisible(true);
				frmGradesClassbook.dispose();
			}
		});
		btnBack.setBounds(307, 261, 89, 23);
		frmGradesClassbook.getContentPane().add(btnBack);
	}
	
	protected void deleteStudent(long id) {
		Connection conn = null;
		try {
			conn = getConnection();
			
			String query = "DELETE FROM GRADES WHERE ID=?";
			
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setLong(1, id);
			
			pst.executeUpdate();
			
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

	protected void editGrade(long id) {
		Grade grade = getGradeById(id);
		
		if(grade != null){
			GradeAddEditForm gaef = new GradeAddEditForm(student, grade);
			gaef.frame.setVisible(true);
			frmGradesClassbook.setVisible(false);
			frmGradesClassbook.dispose();
		}
	}
	
	private Grade getGradeById(long id) {
		Connection conn = null;
		Grade grade = null;
		
		try {
			conn = getConnection();
			
			String query = "SELECT S.NAME, G.GRADE FROM GRADES AS G JOIN SUBJECTS AS S ON S.ID = G.SUBJECTID WHERE G.ID = ?";
			
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setLong(1, id);
			
			ResultSet rs = pst.executeQuery();
			
			if(rs.first()){
				grade = new Grade();
				
				grade.id = id;
				grade.subjectName = rs.getString(rs.findColumn("NAME"));
				grade.grade = rs.getDouble(rs.findColumn("GRADE"));
				
				return grade;
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
		return null;
	}

	private Connection getConnection() throws SQLException{
		return DriverManager.getConnection(
				"jdbc:h2:~/classbookDB","sa","");
	}
	
	public void fillTableData(long id){
		Connection conn = null;
		try {
			conn = getConnection();
			//SELECT G.ID, G.GRADE, S.NAME FROM GRADES AS G JOIN SUBJECTS AS S ON G.SUBJECTID = S.ID WHERE G.STUDENTID = 3
			String query = "SELECT G.ID, G.GRADE, S.NAME FROM GRADES AS G JOIN SUBJECTS AS S ON G.SUBJECTID = S.ID WHERE G.STUDENTID = ?";
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setLong(1, id);
			
			ResultSet rs = pst.executeQuery();
			ArrayList<Grade> grades = new ArrayList<>();
			
			Grade grade;
			
			while(rs.next()){
				grade = new Grade();
				
				grade.id = rs.getLong(rs.findColumn("ID"));
				grade.subjectName = rs.getString(rs.findColumn("NAME"));
				grade.grade = rs.getDouble(rs.findColumn("GRADE"));
				
				grades.add(grade);
			}
			
			
			DefaultTableModel model = new DefaultTableModel(
					new String[]{"id", "subjectName", "grade"}, 
					grades.size());
			
			for(Grade g: grades){
				Object[] row = new Object[3];
				row[0] = g.id;
				row[1] = g.subjectName;
				row[2] = g.grade;
				
				model.addRow(row);
			}
			
			tableGrades.setModel(model);
			
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
