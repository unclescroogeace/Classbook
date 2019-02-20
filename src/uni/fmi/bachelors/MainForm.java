package uni.fmi.bachelors;

import java.awt.EventQueue;

import javax.swing.JFrame;

import uni.fmi.bachelors.students.StudentsForm;
import uni.fmi.bachelors.subjects.SubjectsForm;
import uni.fmi.bachelors.teachers.TeachersForm;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class MainForm extends JFrame{

	public JFrame frmMainClassbook;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm window = new MainForm();
					window.frmMainClassbook.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainForm() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMainClassbook = new JFrame();
		frmMainClassbook.setTitle("Main - Classbook");
		frmMainClassbook.setBounds(100, 100, 342, 192);
		frmMainClassbook.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMainClassbook.getContentPane().setLayout(null);
		
		JButton btnSubjects = new JButton("Subjects");
		btnSubjects.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SubjectsForm subf = new SubjectsForm();
				subf.frmSubjectsClassbook.setVisible(true);
				frmMainClassbook.setVisible(false);
			}
		});
		btnSubjects.setBounds(25, 87, 125, 45);
		frmMainClassbook.getContentPane().add(btnSubjects);
		
		JButton btnStudents = new JButton("Students");
		btnStudents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				StudentsForm sf = new StudentsForm();
				sf.frmStudents.setVisible(true);
				frmMainClassbook.setVisible(false);
			}
		});
		btnStudents.setBounds(25, 11, 125, 45);
		frmMainClassbook.getContentPane().add(btnStudents);
		
		JButton btnTeachers = new JButton("Teachers");
		btnTeachers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TeachersForm tf = new TeachersForm();
				tf.frmTeachersClassbook.setVisible(true);
				frmMainClassbook.setVisible(false);
			}
		});
		btnTeachers.setBounds(176, 11, 125, 45);
		frmMainClassbook.getContentPane().add(btnTeachers);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmMainClassbook.dispatchEvent(new WindowEvent(frmMainClassbook, WindowEvent.WINDOW_CLOSING));
			}
		});
		btnExit.setBounds(176, 87, 125, 45);
		frmMainClassbook.getContentPane().add(btnExit);
	}
}
