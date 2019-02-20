package uni.fmi.bachelors.students;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import uni.fmi.bachelors.MainForm;
import uni.fmi.bachelors.grades.GradesForm;

import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class StudentsForm {

	public JFrame frmStudents;
	private JTextField tfFirstName;
	private JTextField tfLastName;
	private JTextField tfFacultyNumber;
	private JTable tableStudents;
	private JCheckBox chckbxMale;
	private JCheckBox chckbxFemale;

	/**
	 * Create the application.
	 */
	public StudentsForm() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmStudents = new JFrame();
		frmStudents.setTitle("Students - Classbook");
		frmStudents.setBounds(100, 100, 699, 415);
		frmStudents.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmStudents.getContentPane().setLayout(null);
		
		JLabel lblFirstName = new JLabel("First name");
		lblFirstName.setBounds(10, 11, 65, 14);
		frmStudents.getContentPane().add(lblFirstName);
		
		JLabel lblLastName = new JLabel("Last name");
		lblLastName.setBounds(121, 11, 57, 14);
		frmStudents.getContentPane().add(lblLastName);
		
		JLabel lblFacultyNumber = new JLabel("Faculty Number");
		lblFacultyNumber.setBounds(230, 11, 80, 14);
		frmStudents.getContentPane().add(lblFacultyNumber);
		
		JLabel lblGender = new JLabel("Gender");
		lblGender.setBounds(337, 11, 46, 14);
		frmStudents.getContentPane().add(lblGender);
		
		tfFirstName = new JTextField();
		tfFirstName.setBounds(10, 36, 86, 20);
		frmStudents.getContentPane().add(tfFirstName);
		tfFirstName.setColumns(10);
		tfFirstName.setText("");
		
		tfLastName = new JTextField();
		tfLastName.setBounds(121, 36, 86, 20);
		frmStudents.getContentPane().add(tfLastName);
		tfLastName.setColumns(10);
		
		tfFacultyNumber = new JTextField();
		tfFacultyNumber.setBounds(230, 36, 86, 20);
		frmStudents.getContentPane().add(tfFacultyNumber);
		tfFacultyNumber.setColumns(10);
		
		chckbxMale = new JCheckBox("Male");
		chckbxMale.setBounds(337, 35, 57, 23);
		frmStudents.getContentPane().add(chckbxMale);
		
		chckbxFemale = new JCheckBox("Female");
		chckbxFemale.setBounds(396, 35, 65, 23);
		frmStudents.getContentPane().add(chckbxFemale);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchingByCriteria();
			}
		});
		btnSearch.setBounds(473, 11, 89, 47);
		frmStudents.getContentPane().add(btnSearch);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 67, 552, 297);
		frmStudents.getContentPane().add(panel);
		panel.setLayout(null);
		
		tableStudents = new JTable();
		tableStudents.setBounds(0, 0, 552, 297);
		panel.add(tableStudents);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StudentsAddEditForm saef = new StudentsAddEditForm();
				saef.frame.setVisible(true);
				frmStudents.setVisible(false);
				frmStudents.dispose();
			}
		});
		btnAdd.setBounds(568, 101, 105, 23);
		frmStudents.getContentPane().add(btnAdd);
		
		JButton btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = tableStudents.getSelectedRow();
				long id = (long)tableStudents.getValueAt(row, 0);
				editStudent(id);
			}
		});
		btnEdit.setBounds(568, 139, 105, 23);
		frmStudents.getContentPane().add(btnEdit);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = tableStudents.getSelectedRow();
				long id = (long)tableStudents.getValueAt(row, 0);
				
				deleteStudent(id);	
				fillTableData();
			}
		});
		btnDelete.setBounds(568, 173, 105, 23);
		frmStudents.getContentPane().add(btnDelete);
		
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainForm mf = new MainForm();
				mf.frmMainClassbook.setVisible(true);
				frmStudents.setVisible(false);
			}
		});
		btnBack.setBounds(568, 207, 105, 23);
		frmStudents.getContentPane().add(btnBack);
		
		JButton btnViewGrades = new JButton("View grades");
		btnViewGrades.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = tableStudents.getSelectedRow();
				long id = (long)tableStudents.getValueAt(row, 0);
				ViewStudentGrades(id);
			}
		});
		btnViewGrades.setBounds(568, 67, 105, 23);
		frmStudents.getContentPane().add(btnViewGrades);
		
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tfFirstName.setText("");
				tfLastName.setText("");
				tfFacultyNumber.setText("");
				chckbxMale.setSelected(false);
				chckbxFemale.setSelected(false);
				fillTableData();
			}
		});
		btnReset.setBounds(568, 11, 105, 47);
		frmStudents.getContentPane().add(btnReset);
		
		fillTableData();
	}

	protected void searchingByCriteria() {
		Connection conn = null;
		try {
			conn = getConnection();
			//SELECT S.ID, S.FIRSTNAME, S.LASTNAME, S.FACULTYNUMBER, S.GENDER, S.EMAIL, AVG(G.GRADE) FROM STUDENTS AS S JOIN GRADES AS G ON G.STUDENTID = S.ID GROUP BY S.ID
			String query = "SELECT ID, FIRSTNAME, LASTNAME, FACULTYNUMBER, GENDER, EMAIL FROM STUDENTS "
					+ "WHERE FIRSTNAME LIKE ? AND LASTNAME LIKE ? AND FACULTYNUMBER LIKE ? AND GENDER LIKE ?";
			
			PreparedStatement pst = conn.prepareStatement(query);
			
			if(tfFirstName.getText().length() > 0) {
				pst.setString(1, "%" + tfFirstName.getText() + "%");
			}else {
				pst.setString(1, "%");
			}
			
			if(tfLastName.getText().length() > 0) {
				pst.setString(2, "%" + tfLastName.getText() + "%");
			}else {
				pst.setString(2, "%");
			}
			
			if(tfFacultyNumber.getText().length() > 0) {
				pst.setString(3, "%" + tfFacultyNumber.getText() + "%");
			}else {
				pst.setString(3, "%");
			}
			
			if((chckbxMale.isSelected() && chckbxFemale.isSelected()) 
					|| (!chckbxMale.isSelected() && !chckbxFemale.isSelected())) {
				pst.setString(4, "%");
			}else if(chckbxMale.isSelected()) {
				pst.setString(4, "M");
			}else {
				pst.setString(4, "F");
			}
			
			ResultSet rs = pst.executeQuery();
			ArrayList<Student> students = new ArrayList<>();
			
			Student student;
			
			while(rs.next()){
				student = new Student();
				
				student.id = rs.getLong(rs.findColumn("ID"));
				student.FirstName = rs.getString(rs.findColumn("FIRSTNAME"));
				student.LastName = rs.getString(rs.findColumn("LASTNAME"));
				student.Email = rs.getString(rs.findColumn("EMAIL"));
				student.FacultyNumber = rs.getString(rs.findColumn("FACULTYNUMBER"));
				student.gender = rs.getString(rs.findColumn("GENDER"));
				
				students.add(student);
			}
			
			
			DefaultTableModel model = new DefaultTableModel(
					new String[]{"id", "firstname", "lastname", "facultynumber", "gender", "email"}, 
					students.size());
			
			for(Student s: students){
				Object[] row = new Object[6];
				row[0] = s.id;
				row[1] = s.FirstName;
				row[2] = s.LastName;
				row[3] = s.FacultyNumber;
				row[4] = s.gender;
				row[5] = s.Email;
				
				model.addRow(row);
			}
			
			tableStudents.setModel(model);
			
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

	protected void ViewStudentGrades(long id) {
		Student student = getStudentById(id);
		
		if(student != null) {
			GradesForm gf = new GradesForm(student);
			gf.frmGradesClassbook.setVisible(true);
			frmStudents.setVisible(false);
			frmStudents.dispose();
		}
	}

	protected void deleteStudent(long id) {
		Connection conn = null;
		try {
			conn = getConnection();
			
			String query = "DELETE FROM STUDENTS WHERE ID=?";
			
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

	protected void editStudent(long id) {
		Student student = getStudentById(id);		
		
		if(student != null){
			StudentsAddEditForm saef = new StudentsAddEditForm(student);
			saef.frame.setVisible(true);
			frmStudents.setVisible(false);
			frmStudents.dispose();
		}
	}
	
	private Connection getConnection() throws SQLException{
		return DriverManager.getConnection(
				"jdbc:h2:~/classbookDB","sa","");
	}
	
	private Student getStudentById(long id) {
		Connection conn = null;
		Student student = null;
		
		try {
			conn = getConnection();
			
			String query = "SELECT ID, FIRSTNAME, LASTNAME, EMAIL, FACULTYNUMBER, GENDER FROM STUDENTS WHERE ID = ?";
			
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setLong(1, id);
			
			ResultSet rs = pst.executeQuery();			
			
			if(rs.first()){
				student = new Student();
				
				student.id = rs.getLong(rs.findColumn("ID"));
				student.FirstName = rs.getString(rs.findColumn("FIRSTNAME"));
				student.LastName = rs.getString(rs.findColumn("LASTNAME"));
				student.Email = rs.getString(rs.findColumn("EMAIL"));
				student.FacultyNumber = rs.getString(rs.findColumn("FACULTYNUMBER"));
				student.gender = rs.getString(rs.findColumn("GENDER"));
				
				return student;
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
	
	public void fillTableData(){
		Connection conn = null;
		try {
			conn = getConnection();
			//SELECT S.ID, S.FIRSTNAME, S.LASTNAME, S.FACULTYNUMBER, S.GENDER, S.EMAIL, AVG(G.GRADE) FROM STUDENTS AS S JOIN GRADES AS G ON G.STUDENTID = S.ID GROUP BY S.ID
			String query = "SELECT ID, FIRSTNAME, LASTNAME, FACULTYNUMBER, GENDER, EMAIL FROM STUDENTS";
			PreparedStatement pst = conn.prepareStatement(query);
			
			ResultSet rs = pst.executeQuery();
			ArrayList<Student> students = new ArrayList<>();
			
			Student student;
			
			while(rs.next()){
				student = new Student();
				
				student.id = rs.getLong(rs.findColumn("ID"));
				student.FirstName = rs.getString(rs.findColumn("FIRSTNAME"));
				student.LastName = rs.getString(rs.findColumn("LASTNAME"));
				student.Email = rs.getString(rs.findColumn("EMAIL"));
				student.FacultyNumber = rs.getString(rs.findColumn("FACULTYNUMBER"));
				student.gender = rs.getString(rs.findColumn("GENDER"));
				
				students.add(student);
			}
			
			
			DefaultTableModel model = new DefaultTableModel(
					new String[]{"id", "firstname", "lastname", "facultynumber", "gender", "email"}, 
					students.size());
			
			for(Student s: students){
				Object[] row = new Object[6];
				row[0] = s.id;
				row[1] = s.FirstName;
				row[2] = s.LastName;
				row[3] = s.FacultyNumber;
				row[4] = s.gender;
				row[5] = s.Email;
				
				model.addRow(row);
			}
			
			tableStudents.setModel(model);
			
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
