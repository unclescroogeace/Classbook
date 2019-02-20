package uni.fmi.bachelors.subjects;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SubjectsAddEditForm {

	public JFrame frame;
	private JTextField tfSubjectName;
	private JComboBox<String> comboBox;
	private Subject subject;

	/**
	 * Create the application.
	 */
	/**
	 * @wbp.parser.constructor
	 */
	public SubjectsAddEditForm() {
		initialize();
	}
	

	public SubjectsAddEditForm(Subject subject) {
		initialize();
		this.subject = subject;
		tfSubjectName.setText(subject.subjectName);
		comboBox.setSelectedItem(subject.teacherFirstName + ' ' + subject.teacherLastName);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 326, 154);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblSubjectName = new JLabel("Subject name");
		lblSubjectName.setBounds(10, 11, 75, 14);
		frame.getContentPane().add(lblSubjectName);
		
		tfSubjectName = new JTextField();
		tfSubjectName.setBounds(120, 8, 176, 20);
		frame.getContentPane().add(tfSubjectName);
		tfSubjectName.setColumns(10);
		
		comboBox = new JComboBox<String>();
		comboBox.setBounds(120, 39, 176, 20);
		frame.getContentPane().add(comboBox);
		
		JLabel lblTeacher = new JLabel("Teacher");
		lblTeacher.setBounds(10, 36, 75, 14);
		frame.getContentPane().add(lblTeacher);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(tfSubjectName.getText().length() > 0
						&& comboBox.getSelectedIndex() >= 0) {
					
					if(subject == null) {
						subject = new Subject();
					}
					
					subject.subjectName = tfSubjectName.getText();
					subject.teacherId = getTeacherIdByFullName((String)comboBox.getSelectedItem());
					
					
					if(subject.id != -1) {
						updateSubject(subject);
					}else {
						insertSubject(subject);
					}
					
					SubjectsForm sf = new SubjectsForm();
					sf.frmSubjectsClassbook.setVisible(true);
					frame.setVisible(false);
					frame.dispose();
				}
				else
				{
					System.out.println("Please fill all the fields!");
				}
			}
		});
		btnSave.setBounds(34, 81, 89, 23);
		frame.getContentPane().add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SubjectsForm sf = new SubjectsForm();
				sf.frmSubjectsClassbook.setVisible(true);
				frame.setVisible(false);
				frame.dispose();
			}
		});
		btnCancel.setBounds(184, 81, 89, 23);
		frame.getContentPane().add(btnCancel);
		fillComboBox();
	}
	
	protected void insertSubject(Subject subject) {
		Connection conn = null;
		try {
			conn = getConnection();
			
			String query = "INSERT INTO SUBJECTS (NAME, TEACHERID) VALUES (?,?)";
			
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setString(1, subject.subjectName);
			pst.setLong(2, subject.teacherId);
			
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


	protected void updateSubject(Subject subject) {
		Connection conn = null;
		try {
			conn = getConnection();
			
			String query = "UPDATE SUBJECTS SET NAME = ?, TEACHERID = ? WHERE ID = ?";
			
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setString(1, subject.subjectName);
			pst.setLong(2, subject.teacherId);
			pst.setLong(3, subject.id);
			
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


	private Connection getConnection() throws SQLException{
		return DriverManager.getConnection(
				"jdbc:h2:~/classbookDB","sa","");
	}
	
	private void fillComboBox() {
		Connection conn = null;
		try {
			conn = getConnection();
			String query = "SELECT CONCAT(FIRSTNAME,' ' ,LASTNAME) AS NAME FROM TEACHERS";
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
	
	private long getTeacherIdByFullName(String fullName) {
		Connection conn = null;
		long teacherId = -1;
		
		try {
			conn = getConnection();
			
			String[] result = fullName.split(" ");
			
			String query = "SELECT ID FROM TEACHERS WHERE FIRSTNAME = ? AND LASTNAME = ?";
			
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setString(1, result[0]);
			pst.setString(2, result[1]);
			
			ResultSet rs = pst.executeQuery();			
			
			if(rs.first()){
				teacherId = rs.getLong(rs.findColumn("ID"));
				return teacherId;
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
		return teacherId;	
	}

}
