package uni.fmi.bachelors.subjects;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import uni.fmi.bachelors.MainForm;
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

public class SubjectsForm {

	public JFrame frmSubjectsClassbook;
	private JTextField tfTeacherFirstName;
	private JTextField tfSubjectName;
	private JTextField tfTeacherLastName;
	private JButton btnSearch;
	private JPanel panel;
	private JTable tableSubjects;
	private JButton btnAdd;
	private JButton btnEdit;
	private JButton btnDelete;
	private JButton btnBack;
	private JButton btnReset;

	/**
	 * Create the application.
	 */
	public SubjectsForm() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSubjectsClassbook = new JFrame();
		frmSubjectsClassbook.setTitle("Subjects - Classbook");
		frmSubjectsClassbook.setBounds(100, 100, 642, 361);
		frmSubjectsClassbook.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSubjectsClassbook.getContentPane().setLayout(null);
		
		JLabel lblTeacherFirstName = new JLabel("Teacher first name");
		lblTeacherFirstName.setBounds(10, 11, 129, 14);
		frmSubjectsClassbook.getContentPane().add(lblTeacherFirstName);
		
		JLabel lblSubjectName = new JLabel("Subject name");
		lblSubjectName.setBounds(288, 11, 129, 14);
		frmSubjectsClassbook.getContentPane().add(lblSubjectName);
		
		tfTeacherFirstName = new JTextField();
		tfTeacherFirstName.setBounds(10, 36, 129, 20);
		frmSubjectsClassbook.getContentPane().add(tfTeacherFirstName);
		tfTeacherFirstName.setColumns(10);
		
		tfSubjectName = new JTextField();
		tfSubjectName.setBounds(288, 36, 129, 20);
		frmSubjectsClassbook.getContentPane().add(tfSubjectName);
		tfSubjectName.setColumns(10);
		
		JLabel lblTeacherLastName = new JLabel("Teacher last name");
		lblTeacherLastName.setBounds(149, 11, 129, 14);
		frmSubjectsClassbook.getContentPane().add(lblTeacherLastName);
		
		tfTeacherLastName = new JTextField();
		tfTeacherLastName.setBounds(149, 36, 129, 20);
		frmSubjectsClassbook.getContentPane().add(tfTeacherLastName);
		tfTeacherLastName.setColumns(10);
		
		btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchingByCriteria();
			}
		});
		btnSearch.setBounds(427, 11, 89, 47);
		frmSubjectsClassbook.getContentPane().add(btnSearch);
		
		panel = new JPanel();
		panel.setBounds(10, 67, 506, 244);
		frmSubjectsClassbook.getContentPane().add(panel);
		panel.setLayout(null);
		
		tableSubjects = new JTable();
		tableSubjects.setBounds(0, 0, 506, 244);
		panel.add(tableSubjects);
		
		btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SubjectsAddEditForm saef = new SubjectsAddEditForm();
				saef.frame.setVisible(true);
				frmSubjectsClassbook.setVisible(false);
				frmSubjectsClassbook.dispose();
			}
		});
		btnAdd.setBounds(526, 67, 89, 23);
		frmSubjectsClassbook.getContentPane().add(btnAdd);
		
		btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = tableSubjects.getSelectedRow();
				long id = (long)tableSubjects.getValueAt(row, 0);
				editSubject(id);
			}
		});
		btnEdit.setBounds(526, 101, 89, 23);
		frmSubjectsClassbook.getContentPane().add(btnEdit);
		
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = tableSubjects.getSelectedRow();
				long id = (long)tableSubjects.getValueAt(row, 0);
				
				deleteSubject(id);	
				fillTableData();
			}
		});
		btnDelete.setBounds(526, 135, 89, 23);
		frmSubjectsClassbook.getContentPane().add(btnDelete);
		
		btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainForm mf = new MainForm();
				mf.frmMainClassbook.setVisible(true);
				frmSubjectsClassbook.setVisible(false);
				frmSubjectsClassbook.dispose();
			}
		});
		btnBack.setBounds(526, 169, 89, 23);
		frmSubjectsClassbook.getContentPane().add(btnBack);
		
		btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tfTeacherFirstName.setText("");
				tfTeacherLastName.setText("");
				tfSubjectName.setText("");
				fillTableData();
			}
		});
		btnReset.setBounds(526, 11, 89, 47);
		frmSubjectsClassbook.getContentPane().add(btnReset);
		
		fillTableData();
	}
	
	protected void searchingByCriteria() {
		Connection conn = null;
		try {
			conn = getConnection();
			//SELECT S.ID, S.FIRSTNAME, S.LASTNAME, S.FACULTYNUMBER, S.GENDER, S.EMAIL, AVG(G.GRADE) FROM STUDENTS AS S JOIN GRADES AS G ON G.STUDENTID = S.ID GROUP BY S.ID
			String query = "SELECT S.ID, T.FIRSTNAME, T.LASTNAME, S.NAME, S.TEACHERID FROM SUBJECTS AS S JOIN TEACHERS AS T "
					+ "ON T.ID = S.TEACHERID WHERE T.FIRSTNAME LIKE ? AND T.LASTNAME LIKE ? AND NAME LIKE ?";
			
			PreparedStatement pst = conn.prepareStatement(query);
			
			if(tfTeacherFirstName.getText().length() > 0) {
				pst.setString(1, "%" + tfTeacherFirstName.getText() + "%");
			}else {
				pst.setString(1, "%");
			}
			
			if(tfTeacherLastName.getText().length() > 0) {
				pst.setString(2, "%" + tfTeacherLastName.getText() + "%");
			}else {
				pst.setString(2, "%");
			}
			
			if(tfSubjectName.getText().length() > 0) {
				pst.setString(3, "%" + tfSubjectName.getText() + "%");
			}else {
				pst.setString(3, "%");
			}
			
			ResultSet rs = pst.executeQuery();
			ArrayList<Subject> subjects = new ArrayList<>();
			
			Subject subject;
			
			while(rs.next()){
				subject = new Subject();
				
				subject.id = rs.getLong(rs.findColumn("ID"));
				subject.teacherFirstName = rs.getString(rs.findColumn("FIRSTNAME"));
				subject.teacherLastName = rs.getString(rs.findColumn("LASTNAME"));
				subject.teacherId = rs.getLong(rs.findColumn("TEACHERID"));
				subject.subjectName = rs.getString(rs.findColumn("NAME"));
				
				subjects.add(subject);
			}
			
			
			DefaultTableModel model = new DefaultTableModel(
					new String[]{"id", "teacherFirstName", "teacherLastName", "subjectName"}, 
					subjects.size());
			
			for(Subject s: subjects){
				Object[] row = new Object[4];
				row[0] = s.id;
				row[1] = s.teacherFirstName;
				row[2] = s.teacherLastName;
				row[3] = s.subjectName;
				
				model.addRow(row);
			}
			
			tableSubjects.setModel(model);
			
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

	protected void deleteSubject(long id) {
		Connection conn = null;
		try {
			conn = getConnection();
			
			String query = "DELETE FROM SUBJECTS WHERE ID=?";
			
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

	protected void editSubject(long id) {
		Subject subject = getSubjectById(id);		
		
		if(subject != null){
			SubjectsAddEditForm saef = new SubjectsAddEditForm(subject);
			saef.frame.setVisible(true);
			frmSubjectsClassbook.setVisible(false);
			frmSubjectsClassbook.dispose();
		}
		
	}
	
	private Subject getSubjectById(long id) {
		Connection conn = null;
		Subject subject = null;
		
		try {
			conn = getConnection();
			
			String query = "SELECT T.FIRSTNAME, T.LASTNAME, S.NAME, S.TEACHERID FROM SUBJECTS AS S JOIN TEACHERS AS T ON T.ID = S.TEACHERID WHERE S.ID = ?";
			
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setLong(1, id);
			
			ResultSet rs = pst.executeQuery();			
			
			if(rs.first()){
				subject = new Subject();
				
				subject.id = id;
				subject.subjectName = rs.getString(rs.findColumn("NAME"));
				subject.teacherFirstName = rs.getString(rs.findColumn("FIRSTNAME"));
				subject.teacherLastName = rs.getString(rs.findColumn("LASTNAME"));
				subject.teacherId = rs.getLong(rs.findColumn("TEACHERID"));
				
				return subject;
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
	
	public void fillTableData(){
		Connection conn = null;
		try {
			conn = getConnection();
			
			String query = "SELECT S.ID, T.FIRSTNAME, T.LASTNAME, S.NAME, S.TEACHERID FROM SUBJECTS AS S JOIN TEACHERS AS T ON T.ID = S.TEACHERID";
			
			PreparedStatement pst = conn.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			
			ArrayList<Subject> subjects = new ArrayList<>();
			
			Subject subject;
			
			while(rs.next()){
				subject = new Subject();
				
				subject.id = rs.getLong(rs.findColumn("ID"));
				subject.subjectName = rs.getString(rs.findColumn("NAME"));
				subject.teacherFirstName = rs.getString(rs.findColumn("FIRSTNAME"));
				subject.teacherLastName = rs.getString(rs.findColumn("LASTNAME"));
				subject.teacherId = rs.getLong(rs.findColumn("TEACHERID"));
				
				subjects.add(subject);
			}
			
			
			DefaultTableModel model = new DefaultTableModel(
					new String[]{"id", "subjectName", "teacherFirstName", "teacherLastName"}, 
					subjects.size());
			
			for(Subject s: subjects){
				Object[] row = new Object[4];
				row[0] = s.id;
				row[3] = s.subjectName;
				row[1] = s.teacherFirstName;
				row[2] = s.teacherLastName;
				
				model.addRow(row);
			}
			
			tableSubjects.setModel(model);
			
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
