package question.answering.system;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.io.IOException;

import javax.swing.JScrollPane;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class MainWindow {

	private JFrame frame;
	private JTextField textField;
	JTextArea textArea;
	String indexDir = "D:\\Lucene\\Index";
	String dataDir = "D:\\Lucene\\Data";
	Indexer indexer;
	Searcher searcher;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 714, 449);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
	
		
		
		textField = new JTextField();
		textField.setToolTipText("eneter keyword here");
		textField.setBounds(234, 74, 203, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Enter");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					createIndex();
					search(textField.getText());
				}catch(IOException e){
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(285, 105, 89, 23);
		frame.getContentPane().add(btnNewButton);
		
		JLabel lblQuestionAnsweringSystem = new JLabel("Question Answering System");
		lblQuestionAnsweringSystem.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblQuestionAnsweringSystem.setHorizontalAlignment(SwingConstants.CENTER);
		lblQuestionAnsweringSystem.setBounds(207, 32, 265, 31);
		frame.getContentPane().add(lblQuestionAnsweringSystem);
		
		textArea = new JTextArea();
		textArea.setForeground(Color.BLACK);
		textArea.setBackground(Color.WHITE);
		textArea.setBounds(67, 141, 564, 245);
		frame.getContentPane().add(textArea);

		JScrollPane scroller = new JScrollPane(textArea);
		scroller.setLocation(80, 139);
		scroller.setSize(543, 250);

	    frame.getContentPane().add(scroller);		
		
		
	}
	
	public void search(String searchQuery) throws IOException, ParseException{
		searcher = new Searcher(indexDir);
		long startTime = System.currentTimeMillis();
		TopDocs hits = searcher.search(searchQuery);
		long endTime = System.currentTimeMillis();
		textArea.append(hits.totalHits + " documents found. Time: "+(endTime - startTime)+"\n");
		for(ScoreDoc scoreDoc : hits.scoreDocs){
			Document doc = searcher.getDocument(scoreDoc);
			textArea.append("File: "+doc.get("filepath")+"\n");
		}
	}
	public void createIndex() throws IOException{
		indexer = new Indexer(textArea, indexDir);
		int numIndexed;
		long startTime = System.currentTimeMillis();
		numIndexed  = indexer.createIndex(dataDir,new TextFileFilter());
		long endTime  = System.currentTimeMillis();
		indexer.close();
		textArea.append("Indexing " + numIndexed + " file in "+(endTime-startTime)+" ms \n");
	}
}
