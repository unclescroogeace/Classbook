package uni.fmi.bachelors.students;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class StudentsAddEditForm {

	public JFrame frame;
	private JTextField tfFirstName;
	private JTextField tfLastName;
	private JTextField tfFacultyNumber;
	private JTextField tfEmail;
	private JComboBox<Object> comboBoxGender;
	private Student student;
	StudentsForm parent;

	/**
	 * Create the application.
	 */
	
	/**
	 * @wbp.parser.constructor
	 */
	public StudentsAddEditForm() {
		initialize();
	}
	
	public StudentsAddEditForm(Student student) {
		initialize();
		this.student = student;
		tfFirstName.setText(student.FirstName);
		tfLastName.setText(student.LastName);
		tfFacultyNumber.setText(student.FacultyNumber);
		tfEmail.setText(student.Email);
		if(student.gender == "F") {
			comboBoxGender.setSelectedIndex(1);
		}else {
			comboBoxGender.setSelectedIndex(0);
		}
		
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 293, 248);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblFirstName = new JLabel("First name");
		lblFirstName.setBounds(10, 11, 67, 14);
		frame.getContentPane().add(lblFirstName);
		
		JLabel lblLastName = new JLabel("Last name");
		lblLastName.setBounds(10, 36, 67, 14);
		frame.getContentPane().add(lblLastName);
		
		JLabel lblFacultyNumber = new JLabel("Faculty number");
		lblFacultyNumber.setBounds(10, 61, 87, 14);
		frame.getContentPane().add(lblFacultyNumber);
		
		JLabel lblGender = new JLabel("Gender");
		lblGender.setBounds(10, 117, 46, 14);
		frame.getContentPane().add(lblGender);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setBounds(10, 89, 46, 14);
		frame.getContentPane().add(lblEmail);
		
		tfFirstName = new JTextField();
		tfFirstName.setBounds(135, 8, 129, 20);
		frame.getContentPane().add(tfFirstName);
		tfFirstName.setColumns(10);
		
		tfLastName = new JTextField();
		tfLastName.setBounds(135, 33, 129, 20);
		frame.getContentPane().add(tfLastName);
		tfLastName.setColumns(10);
		
		tfFacultyNumber = new JTextField();
		tfFacultyNumber.setBounds(135, 58, 129, 20);
		frame.getContentPane().add(tfFacultyNumber);
		tfFacultyNumber.setColumns(10);
		
		String[] genders = {"M", "F"};
		comboBoxGender = new JComboBox<Object>(genders);
		comboBoxGender.setBounds(135, 114, 46, 20);
		frame.getContentPane().add(comboBoxGender);
		
		tfEmail = new JTextField();
		tfEmail.setBounds(135, 86, 129, 20);
		frame.getContentPane().add(tfEmail);
		tfEmail.setColumns(10);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(tfFirstName.getText().length() > 0
						&& tfLastName.getText().length() > 0
						&& tfFacultyNumber.getText().length() > 0
						&& tfEmail.getText().length() > 0) {
					
					if(student == null) {
						student = new Student();
					}
					
					student.FirstName = tfFirstName.getText();
					student.LastName = tfLastName.getText();
					student.FacultyNumber = tfFacultyNumber.getText();
					student.Email = tfEmail.getText();
					student.gender = (String)comboBoxGender.getSelectedItem();
					
					if(student.id != -1) {
						updateStudent(student);
					}else {
						insertStudent(student);
					}
					
					StudentsForm sf = new StudentsForm();
					sf.frmStudents.setVisible(true);
					frame.setVisible(false);
					frame.dispose();
				}
				else
				{
					System.out.println("Please fill all the fields!");
				}
			}
		});
		btnSave.setBounds(25, 167, 89, 23);
		frame.getContentPane().add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StudentsForm sf = new StudentsForm();
				sf.frmStudents.setVisible(true);
				frame.setVisible(false);
				frame.dispose();
			}
		});
		btnCancel.setBounds(161, 167, 89, 23);
		frame.getContentPane().add(btnCancel);
	}

	protected void insertStudent(Student student) {
		Connection conn = null;
		try {
			conn = getConnection();
			
			String query = "INSERT INTO STUDENTS (FIRSTNAME, LASTNAME, FACULTYNUMBER, EMAIL, GENDER) VALUES (?,?,?,?,?)";
			
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setString(1, student.FirstName);
			pst.setString(2, student.LastName);
			pst.setString(3, student.FacultyNumber);
			pst.setString(4, student.Email);
			pst.setString(5, student.gender);
			
			pst.execute();
			
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
	
	protected void updateStudent(Student student) {
		Connection conn = null;
		try {
			conn = getConnection();
			
			String query = "UPDATE STUDENTS SET FIRSTNAME = ?, LASTNAME = ?, EMAIL = ?, FACULTYNUMBER = ?, GENDER = ? WHERE ID = ?";
			
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setString(1, student.FirstName);
			pst.setString(2, student.LastName);
			pst.setString(3, student.Email);
			pst.setString(4, student.FacultyNumber);
			pst.setString(5, student.gender);
			pst.setLong(6, student.id);
			
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
}
