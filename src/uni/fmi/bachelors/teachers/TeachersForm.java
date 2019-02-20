package uni.fmi.bachelors.teachers;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import uni.fmi.bachelors.MainForm;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class TeachersForm {

	public JFrame frmTeachersClassbook;
	private JTextField tfFirstName;
	private JTextField tfLastName;
	private JTextField tfEmployeeNumber;
	private JTable tableTeachers;
	private JCheckBox chckbxFemale;
	private JCheckBox chckbxMale;
	/**
	 * Create the application.
	 */
	public TeachersForm() {
		initialize();
		fillTableData();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTeachersClassbook = new JFrame();
		frmTeachersClassbook.setTitle("Teachers - Classbook");
		frmTeachersClassbook.setBounds(100, 100, 698, 367);
		frmTeachersClassbook.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTeachersClassbook.getContentPane().setLayout(null);
		
		JLabel lblFirstName = new JLabel("First name");
		lblFirstName.setBounds(10, 11, 86, 14);
		frmTeachersClassbook.getContentPane().add(lblFirstName);
		
		JLabel lblLastName = new JLabel("Last name");
		lblLastName.setBounds(106, 11, 86, 14);
		frmTeachersClassbook.getContentPane().add(lblLastName);
		
		JLabel lblEmployeeNumber = new JLabel("Employee number");
		lblEmployeeNumber.setBounds(202, 11, 110, 14);
		frmTeachersClassbook.getContentPane().add(lblEmployeeNumber);
		
		JLabel lblGender = new JLabel("Gender");
		lblGender.setBounds(323, 9, 46, 14);
		frmTeachersClassbook.getContentPane().add(lblGender);
		
		tfFirstName = new JTextField();
		tfFirstName.setBounds(10, 36, 86, 20);
		frmTeachersClassbook.getContentPane().add(tfFirstName);
		tfFirstName.setColumns(10);
		
		tfLastName = new JTextField();
		tfLastName.setBounds(106, 36, 86, 20);
		frmTeachersClassbook.getContentPane().add(tfLastName);
		tfLastName.setColumns(10);
		
		tfEmployeeNumber = new JTextField();
		tfEmployeeNumber.setBounds(202, 36, 110, 20);
		frmTeachersClassbook.getContentPane().add(tfEmployeeNumber);
		tfEmployeeNumber.setColumns(10);
		
		chckbxMale = new JCheckBox("Male");
		chckbxMale.setBounds(323, 33, 65, 23);
		frmTeachersClassbook.getContentPane().add(chckbxMale);
		
		chckbxFemale = new JCheckBox("Female");
		chckbxFemale.setBounds(390, 33, 87, 23);
		frmTeachersClassbook.getContentPane().add(chckbxFemale);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchingByCriteria();
			}
		});
		btnSearch.setBounds(487, 11, 86, 49);
		frmTeachersClassbook.getContentPane().add(btnSearch);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 67, 563, 250);
		frmTeachersClassbook.getContentPane().add(panel);
		panel.setLayout(null);
		
		tableTeachers = new JTable();
		tableTeachers.setBounds(0, 0, 565, 250);
		panel.add(tableTeachers);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TeachersAddEditForm taef = new TeachersAddEditForm();
				taef.frame.setVisible(true);
				frmTeachersClassbook.setVisible(false);
				frmTeachersClassbook.dispose();
			}
		});
		btnAdd.setBounds(583, 67, 89, 23);
		frmTeachersClassbook.getContentPane().add(btnAdd);
		
		JButton btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = tableTeachers.getSelectedRow();
				long id = (long)tableTeachers.getValueAt(row, 0);
				editTeacher(id);
			}
		});
		btnEdit.setBounds(583, 101, 89, 23);
		frmTeachersClassbook.getContentPane().add(btnEdit);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = tableTeachers.getSelectedRow();
				long id = (long)tableTeachers.getValueAt(row, 0);
				
				deleteTeacher(id);	
				fillTableData();
			}
		});
		btnDelete.setBounds(583, 135, 89, 23);
		frmTeachersClassbook.getContentPane().add(btnDelete);
		
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainForm mf = new MainForm();
				mf.frmMainClassbook.setVisible(true);
				frmTeachersClassbook.setVisible(false);
			}
		});
		btnBack.setBounds(583, 169, 89, 23);
		frmTeachersClassbook.getContentPane().add(btnBack);
		
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tfFirstName.setText("");
				tfLastName.setText("");
				tfEmployeeNumber.setText("");
				chckbxMale.setSelected(false);
				chckbxFemale.setSelected(false);
				fillTableData();
			}
		});
		btnReset.setBounds(583, 11, 89, 47);
		frmTeachersClassbook.getContentPane().add(btnReset);
	}
	
	protected void deleteTeacher(long id) {
		Connection conn = null;
		try {
			conn = getConnection();
			
			String query = "DELETE FROM TEACHERS WHERE ID=?";
			
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
	
	protected void editTeacher(long id) {
		Teacher teacher = getTeacherById(id);		
		
		if(teacher != null){
			TeachersAddEditForm saef = new TeachersAddEditForm(teacher);
			saef.frame.setVisible(true);
			frmTeachersClassbook.setVisible(false);
			frmTeachersClassbook.dispose();
		}
	}
	
	
	private Teacher getTeacherById(long id) {
		Connection conn = null;
		Teacher teacher = null;
		
		try {
			conn = getConnection();
			
			String query = "SELECT ID, FIRSTNAME, LASTNAME, EMAIL, EMPLOYEENUMBER, GENDER FROM TEACHERS WHERE ID = ?";
			
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setLong(1, id);
			
			ResultSet rs = pst.executeQuery();			
			
			if(rs.first()){
				teacher = new Teacher();
				
				teacher.id = rs.getLong(rs.findColumn("ID"));
				teacher.firstName = rs.getString(rs.findColumn("FIRSTNAME"));
				teacher.lastName = rs.getString(rs.findColumn("LASTNAME"));
				teacher.email = rs.getString(rs.findColumn("EMAIL"));
				teacher.employeeNumber = rs.getString(rs.findColumn("EMPLOYEENUMBER"));
				teacher.gender = rs.getString(rs.findColumn("GENDER"));
				
				return teacher;
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
	
	protected void searchingByCriteria() {
		Connection conn = null;
		try {
			conn = getConnection();
			//SELECT S.ID, S.FIRSTNAME, S.LASTNAME, S.FACULTYNUMBER, S.GENDER, S.EMAIL, AVG(G.GRADE) FROM STUDENTS AS S JOIN GRADES AS G ON G.STUDENTID = S.ID GROUP BY S.ID
			String query = "SELECT ID, FIRSTNAME, LASTNAME, EMPLOYEENUMBER, GENDER, EMAIL FROM TEACHERS "
					+ "WHERE FIRSTNAME LIKE ? AND LASTNAME LIKE ? AND EMPLOYEENUMBER LIKE ? AND GENDER LIKE ?";
			
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
			
			if(tfEmployeeNumber.getText().length() > 0) {
				pst.setString(3, "%" + tfEmployeeNumber.getText() + "%");
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
			ArrayList<Teacher> teachers = new ArrayList<>();
			
			Teacher teacher;
			
			while(rs.next()){
				teacher = new Teacher();
				
				teacher.id = rs.getLong(rs.findColumn("ID"));
				teacher.firstName = rs.getString(rs.findColumn("FIRSTNAME"));
				teacher.lastName = rs.getString(rs.findColumn("LASTNAME"));
				teacher.email = rs.getString(rs.findColumn("EMAIL"));
				teacher.employeeNumber = rs.getString(rs.findColumn("EMPLOYEENUMBER"));
				teacher.gender = rs.getString(rs.findColumn("GENDER"));
				
				teachers.add(teacher);
			}
			
			
			DefaultTableModel model = new DefaultTableModel(
					new String[]{"id", "firstname", "lastname", "employeeNumber", "gender", "email"}, 
					teachers.size());
			
			for(Teacher t: teachers){
				Object[] row = new Object[6];
				row[0] = t.id;
				row[1] = t.firstName;
				row[2] = t.lastName;
				row[3] = t.employeeNumber;
				row[4] = t.gender;
				row[5] = t.email;
				
				model.addRow(row);
			}
			
			tableTeachers.setModel(model);
			
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
	
	public void fillTableData(){
		Connection conn = null;
		try {
			conn = getConnection();
			//SELECT S.ID, S.FIRSTNAME, S.LASTNAME, S.FACULTYNUMBER, S.GENDER, S.EMAIL, AVG(G.GRADE) FROM STUDENTS AS S JOIN GRADES AS G ON G.STUDENTID = S.ID GROUP BY S.ID
			String query = "SELECT ID, FIRSTNAME, LASTNAME, EMPLOYEENUMBER, GENDER, EMAIL FROM TEACHERS";
			PreparedStatement pst = conn.prepareStatement(query);
			
			ResultSet rs = pst.executeQuery();
			ArrayList<Teacher> teachers = new ArrayList<>();
			
			Teacher teacher;
			
			while(rs.next()){
				teacher = new Teacher();
				
				teacher.id = rs.getLong(rs.findColumn("ID"));
				teacher.firstName = rs.getString(rs.findColumn("FIRSTNAME"));
				teacher.lastName = rs.getString(rs.findColumn("LASTNAME"));
				teacher.email = rs.getString(rs.findColumn("EMAIL"));
				teacher.employeeNumber = rs.getString(rs.findColumn("EMPLOYEENUMBER"));
				teacher.gender = rs.getString(rs.findColumn("GENDER"));
				
				teachers.add(teacher);
			}
			
			
			DefaultTableModel model = new DefaultTableModel(
					new String[]{"id", "firstname", "lastname", "employeeNumber", "gender", "email"}, 
					teachers.size());
			
			for(Teacher t: teachers){
				Object[] row = new Object[6];
				row[0] = t.id;
				row[1] = t.firstName;
				row[2] = t.lastName;
				row[3] = t.employeeNumber;
				row[4] = t.gender;
				row[5] = t.email;
				
				model.addRow(row);
			}
			
			tableTeachers.setModel(model);
			
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
