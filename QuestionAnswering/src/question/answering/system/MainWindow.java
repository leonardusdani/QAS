package question.answering.system;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.io.IOException;

public class MainWindow {

	private JFrame frame;
	private JTextField textField;
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
		
		JTextArea textArea = new JTextArea();
		textArea.setForeground(Color.BLACK);
		textArea.setBackground(Color.WHITE);
		textArea.setBounds(67, 141, 564, 245);
		frame.getContentPane().add(textArea);
		
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
					
				}
				catch(ParseException e){
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
	}
	public void createIndex() throws IOException{
		indexer = new Indexer(indexDir);
		int numIndexed;
		long startTime = System.currentTimeMillis();
		numIndexed  = indexer.createIndex(dataDir,new TextFileFilter());
		long endTime  = System.currentTimeMillis();
		indexer.close();
		System.out.println(numIndexed + "File indexed, time taken: "+(endTime-startTime)+" ms");
		
	}
	public void search(String searchQuery) throws IOException,ParseException{
		searcher = new Searcher(indexDir);
		long startTime  = System.currentTimeMillis();
		TopDocs hits = searcher.search(searchQuery);
		long endTime = System.currentTimeMillis();
		System.out.println(hits.totalHits + " document found. Time: "+(endTime-startTime));
		for(ScoreDoc scoreDoc : hits.scoreDocs){
			Document doc = searcher.getDocument(scoreDoc);
			System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
		}
		searcher.close();
	}
}
