package uni.fmi.bachelors.teachers;

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

public class TeachersAddEditForm {

	public JFrame frame;
	private JTextField tfFirstName;
	private JTextField tfLastName;
	private JTextField tfEmail;
	private JTextField tfEmployeeNumber;
	private Teacher teacher;
	private JComboBox<Object> comboBoxGender;

	/**
	 * Create the application.
	 */

	/**
	 * @wbp.parser.constructor
	 */

	public TeachersAddEditForm() {
		initialize();
	}

	public TeachersAddEditForm(Teacher teacher) {
		initialize();
		this.teacher = teacher;
		tfFirstName.setText(teacher.firstName);
		tfLastName.setText(teacher.lastName);
		tfEmployeeNumber.setText(teacher.employeeNumber);
		tfEmail.setText(teacher.email);
		if (teacher.gender == "F") {
			comboBoxGender.setSelectedIndex(1);
		} else {
			comboBoxGender.setSelectedIndex(0);
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 304, 228);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblFirstName = new JLabel("First name");
		lblFirstName.setBounds(10, 11, 126, 14);
		frame.getContentPane().add(lblFirstName);

		JLabel lblLastName = new JLabel("Last name");
		lblLastName.setBounds(10, 36, 126, 14);
		frame.getContentPane().add(lblLastName);

		JLabel lblEmail = new JLabel("Email");
		lblEmail.setBounds(10, 61, 126, 14);
		frame.getContentPane().add(lblEmail);

		JLabel lblEmployeeNumber = new JLabel("Employee number");
		lblEmployeeNumber.setBounds(10, 86, 126, 14);
		frame.getContentPane().add(lblEmployeeNumber);

		JLabel lblGender = new JLabel("Gender");
		lblGender.setBounds(10, 111, 126, 14);
		frame.getContentPane().add(lblGender);

		tfFirstName = new JTextField();
		tfFirstName.setBounds(146, 8, 129, 20);
		frame.getContentPane().add(tfFirstName);
		tfFirstName.setColumns(10);

		tfLastName = new JTextField();
		tfLastName.setBounds(146, 33, 129, 20);
		frame.getContentPane().add(tfLastName);
		tfLastName.setColumns(10);

		tfEmail = new JTextField();
		tfEmail.setBounds(146, 58, 129, 20);
		frame.getContentPane().add(tfEmail);
		tfEmail.setColumns(10);

		tfEmployeeNumber = new JTextField();
		tfEmployeeNumber.setBounds(146, 83, 129, 20);
		frame.getContentPane().add(tfEmployeeNumber);
		tfEmployeeNumber.setColumns(10);

		String[] genders = { "M", "F" };
		comboBoxGender = new JComboBox<Object>(genders);
		comboBoxGender.setBounds(146, 108, 43, 20);
		frame.getContentPane().add(comboBoxGender);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(tfFirstName.getText().length() > 0
						&& tfLastName.getText().length() > 0
						&& tfEmployeeNumber.getText().length() > 0
						&& tfEmail.getText().length() > 0) {
					
					if(teacher == null) {
						teacher = new Teacher();
					}
					
					teacher.firstName = tfFirstName.getText();
					teacher.lastName = tfLastName.getText();
					teacher.employeeNumber = tfEmployeeNumber.getText();
					teacher.email = tfEmail.getText();
					teacher.gender = (String)comboBoxGender.getSelectedItem();
					
					if(teacher.id != -1) {
						updateTeacher(teacher);
					}else {
						insertTeacher(teacher);
					}
					
					TeachersForm tf = new TeachersForm();
					tf.frmTeachersClassbook.setVisible(true);
					frame.setVisible(false);
					frame.dispose();
				}
				else
				{
					System.out.println("Please fill all the fields!");
				}
				
			}
		});
		btnSave.setBounds(31, 151, 89, 23);
		frame.getContentPane().add(btnSave);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TeachersForm tf = new TeachersForm();
				tf.frmTeachersClassbook.setVisible(true);
				frame.setVisible(false);
				frame.dispose();
			}
		});
		btnCancel.setBounds(165, 151, 89, 23);
		frame.getContentPane().add(btnCancel);
	}
	
	private Connection getConnection() throws SQLException{
		return DriverManager.getConnection(
				"jdbc:h2:~/classbookDB","sa","");
	}

	protected void insertTeacher(Teacher teacher) {
		Connection conn = null;
		try {
			conn = getConnection();
			
			String query = "INSERT INTO TEACHERS (FIRSTNAME, LASTNAME, EMPLOYEENUMBER, EMAIL, GENDER) VALUES (?,?,?,?,?)";
			
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setString(1, teacher.firstName);
			pst.setString(2, teacher.lastName);
			pst.setString(3, teacher.employeeNumber);
			pst.setString(4, teacher.email);
			pst.setString(5, teacher.gender);
			
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

	protected void updateTeacher(Teacher teacher) {
		Connection conn = null;
		try {
			conn = getConnection();
			
			String query = "UPDATE TEACHERS SET FIRSTNAME = ?, LASTNAME = ?, EMAIL = ?, EMPLOYEENUMBER = ?, GENDER = ? WHERE ID = ?";
			
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setString(1, teacher.firstName);
			pst.setString(2, teacher.lastName);
			pst.setString(3, teacher.email);
			pst.setString(4, teacher.employeeNumber);
			pst.setString(5, teacher.gender);
			pst.setLong(6, teacher.id);
			
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
