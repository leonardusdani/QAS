package question.answering.system;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class MainWindow implements Observer {
	
	Observable observable;
	private JFrame frame;
	private JTextField textField;
	String indexDir = "D:\\Lucene\\Index";
	String dataDir = "D:\\Lucene\\Data";
	Searcher searcher;
	Indexer indexer;
	JTextArea textArea;
	private JScrollPane scrollPane;
	JScrollBar vbar;

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
		JButton btnNewButton_1 = new JButton("Index");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					createIndex();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnNewButton_1.setBounds(234, 105, 89, 23);
		frame.getContentPane().add(btnNewButton_1);	
		textField = new JTextField();
		textField.setToolTipText("enter keyword here");
		textField.setBounds(234, 74, 203, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		JButton btnNewButton = new JButton("Search");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					Filter filter = new Filter(textField.getText());
					search(filter.getQuery());
				}catch(IOException e){
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(348, 105, 89, 23);
		frame.getContentPane().add(btnNewButton);
		JLabel lblQuestionAnsweringSystem = new JLabel("Question Answering System");
		lblQuestionAnsweringSystem.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblQuestionAnsweringSystem.setHorizontalAlignment(SwingConstants.CENTER);
		lblQuestionAnsweringSystem.setBounds(207, 32, 265, 31);
		frame.getContentPane().add(lblQuestionAnsweringSystem);
		scrollPane = new JScrollPane();
		scrollPane.setBounds(27, 149, 646, 249);
		frame.getContentPane().add(scrollPane);
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setLineWrap(true);
		vbar = scrollPane.getVerticalScrollBar();	
	}
	
	public void search(String searchQuery) throws IOException, ParseException{
		searcher = new Searcher(indexDir);
		textArea.append("################################ SEARCHING DOCUMENTS ###################################\n");
		long startTime = System.currentTimeMillis();
		TopDocs hits = searcher.search(searchQuery);
		long endTime = System.currentTimeMillis();
		int i =0;
		for(ScoreDoc scoreDoc : hits.scoreDocs){
			Document doc = searcher.getDocument(scoreDoc);
			i++; 
			textArea.append(i+".  Score: " + Float.toString(scoreDoc.score) +"\n  File: "+doc.get("filepath")+"\n");
			vbar.setValue(vbar.getMaximum());
	        vbar.paint(vbar.getGraphics());
	        textArea.scrollRectToVisible(textArea.getVisibleRect());
	        textArea.paint(textArea.getGraphics());
	        String theString2 = IOUtils.toString(new FileInputStream(new File(doc.get("filepath"))), "UTF-8");
	        System.out.println(theString2);
		}
		textArea.append("################################ SEARCHING COMPLETED ###################################\n");
		textArea.append("  Total " + hits.totalHits + " documents found in "+(endTime - startTime)+"ms \n");
	}
	public void createIndex() throws IOException, InterruptedException{
		indexer = new Indexer(indexDir);
		indexer.addObserver(this);
		textArea.append("################################# INDEXING DOCUMENTS ####################################\n");
		int numIndexed;
		long startTime = System.currentTimeMillis();
		numIndexed  = indexer.createIndex(dataDir,new TextFileFilter());
		long endTime  = System.currentTimeMillis();
		indexer.close();
		textArea.append("################################# INDEXING COMPLETED ####################################\n");
		textArea.append("  Indexing " + numIndexed + " files in "+(endTime-startTime)+" ms \n");
	}

	@Override
	public void update(Observable obs, Object arg1) {
		if(obs instanceof Indexer){
			Indexer indexer = (Indexer) obs;
			textArea.append("  Indexing "+indexer.getFilePath()+"\n");
	        vbar.setValue(vbar.getMaximum());
	        vbar.paint(vbar.getGraphics());
	        textArea.scrollRectToVisible(textArea.getVisibleRect());
	        textArea.paint(textArea.getGraphics());
		}
		// TODO Auto-generated method stub
		
	}
	
}
