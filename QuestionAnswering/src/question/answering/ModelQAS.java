package question.answering;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianStemmer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class ModelQAS {

	private String[] wordType;
	private long startTime;
	private long finishTime;
	private int numIndexDoc;
	private String questionType;
	private String query = "";
	private TopDocs hits;
	private ObservableList<Answer> listAnswer;
	private String log = "log:";
	
	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log =  this.log + "\n" + log;
	}

	private ObservableList<Answer> listAnswer1;
	
	public ObservableList<Answer> getListAnswer1() {
		return listAnswer1;
	}

	public void setListAnswer1(ObservableList<Answer> listAnswer1) {
		this.listAnswer1 = listAnswer1;
	}

	private boolean checkBox;
	private boolean finish;
	
	private static ModelQAS modelQAS = new ModelQAS();
	
	private ModelQAS(){};
	
	public static ModelQAS getInstance(){
		return modelQAS;
	};
	
	
	
	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	public boolean isCheckBox() {
		return checkBox;
	}

	public void setCheckBox(boolean checkBox) {
		this.checkBox = checkBox;
	}

	ObservableList<Eval> listEval;
	
	public ObservableList<Eval> getListEval() {
		return listEval;
	}

	public void setListEval(ObservableList<Eval> listEval) {
		this.listEval = listEval;
	}

	public ObservableList<Answer> getListAnswer() {
		return listAnswer;
	}

	public void setListAnswer(ObservableList<Answer> listAnswer) {
		this.listAnswer = listAnswer;
	}

	DiceSimilarity diceSimilarity;
	
	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	private Document getDocument(File file) throws IOException {
		Document document = new Document();
		Field contentField = new Field(Constants.CONTENTS,
				new FileReader(file), Field.TermVector.YES);
		Field fileNameField = new Field(Constants.FILE_NAME, file.getName(),
				Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.YES);
		Field filePathField = new Field(Constants.FILE_PATH,
				file.getCanonicalPath(), Field.Store.YES,
				Field.Index.NOT_ANALYZED, Field.TermVector.YES);
		document.add(contentField);
		document.add(fileNameField);
		document.add(filePathField);
		return document;

	}

	
	public Task<?> createIndex() {
		return new Task<Object>() {

			@Override
			protected Object call() throws Exception {
				setLog("############### INDEXING DOCUMENTS ################\n");
				startTime = System.currentTimeMillis();
				
				
				Directory indexDirectory = FSDirectory.open(new File(
						Constants.INDEX_DIRECTORY));
				IndonesianAnalyzer analyzer = new IndonesianAnalyzer(
						Version.LUCENE_36);
				IndexWriterConfig config = new IndexWriterConfig(
						Version.LUCENE_36, analyzer);
				IndexWriter indexWriter = new IndexWriter(indexDirectory,
						config);

				File[] files = new File(Constants.DATA_DIRECTORY).listFiles();
				for (File file : files) {
					if (!file.isDirectory() && !file.isHidden()
							&& file.exists() && file.canRead()) {
						Document document = getDocument(file);
						indexWriter.addDocument(document);
						setLog("Indexing " + file.getCanonicalPath());

					}
				}
				numIndexDoc = indexWriter.numDocs();
				indexWriter.close();
				finishTime = System.currentTimeMillis();
				setLog("############### INDEXING COMPLETED #################\n");
				setLog("Indexing " + numIndexDoc + " files in "
						+ (finishTime - startTime) + " ms \n");
				return null;
			}
		};
	}



	public String getQuery() {
		return query;
	}


	public Task<?> analyzing(String question) {
		return new Task<Object>() {

			@Override
			protected Object call() throws Exception {
				wordType = null;
				numIndexDoc = 0;
				questionType = null;
				query = "";
			    hits = null;
				listAnswer = null;
				listAnswer1 = null;
				diceSimilarity = null;
				log = "";
				
				System.out.println("Start 1");
				startTime = System.currentTimeMillis();
				setLog("################## ANALYZING QUERY ##################");
				DatabaseConnections sqlite = DatabaseConnections.getInstance();
				String question1 = question.replace("?", "").toLowerCase();
				String[] arrayQuery = question1.split(" ");
				questionType = arrayQuery[0];
				setLog("Question Type: " + questionType );
				String queryFix = "";
				for (int i = 1; i < arrayQuery.length; i++) {
					queryFix = new StringBuilder(queryFix).append(
							arrayQuery[i] + " ").toString();
				}
				
				if(checkBox==true){
					arrayQuery = sqlite.getSynonym(queryFix).split(" ");
				
				}
				else{
					arrayQuery = queryFix.split(" ");
				
				}
				
				IndonesianStemmer stemmer = new IndonesianStemmer();
				String[] arrayQueryStemm = new String[arrayQuery.length];
				wordType = new String[arrayQuery.length];
				setLog("Word Type: ");
				for (int i = 0; i < arrayQuery.length; i++) {
					query = new StringBuilder(query)
							.append(arrayQuery[i] + " ").toString();
					//arrayQueryStemm[i] = stemmer.stemmedSingleWord(arrayQuery[i]);
				}
				
				wordType = sqlite.getPartOfSpeech(arrayQueryStemm);
				
				
				finishTime = System.currentTimeMillis();
				setLog("Query without Wordnet: " + queryFix );
				setLog("Query with Wordnet: " + query );
				setLog("############ ANALYZING COMPLETED ############");
				setLog("Analyzing in " + (finishTime - startTime) + " ms ");
				System.out.println("Finish 1");
				return null;
				
			}

		};
	}


	public Task<?> scoring(String queryScoring) {
		return new Task<Object>() {

			@Override
			protected Object call() throws Exception {
				System.out.println("Start 2");
				int i = 1;
				setLog("############ SCORING DOCUMENTS ###############\n");
				startTime = System.currentTimeMillis();
				Directory indexDirectory = FSDirectory.open(new File(
						Constants.INDEX_DIRECTORY));
				IndexReader indexReader = IndexReader.open(indexDirectory);
				IndexSearcher indexSearcher = new IndexSearcher(indexReader);
				QueryParser queryParser = new QueryParser(Version.LUCENE_36,
						Constants.CONTENTS, new IndonesianAnalyzer(
								Version.LUCENE_36));
				Query query = queryParser.parse(queryScoring);
				diceSimilarity = new DiceSimilarity(query);
				hits = indexSearcher.search(diceSimilarity,
						Constants.MAX_SEARCH);
				for (ScoreDoc scoreDoc : hits.scoreDocs) {
					Document doc = indexSearcher.doc(scoreDoc.doc);
					setLog(i + ".  Score: "
							+ Float.toString(scoreDoc.score) + "\n  File: "
							+ doc.get(Constants.FILE_PATH) + "\n");
				
					i++;
				}
				finishTime = System.currentTimeMillis();
				setLog("########### SCORING COMPLETED ############\n");
				setLog("Total " + hits.totalHits
						+ " documents found in " + (finishTime - startTime) + "ms \n");
				indexSearcher.close();
				System.out.println("Finish 2");
				return null;
			}

		};
	}
	
	public Task<?> filtering() throws IOException {
		return new Task<Object>() {

			@SuppressWarnings("resource")
			@Override
			protected Object call() throws Exception {
				System.out.println("Start 3");
				listEval = FXCollections.observableArrayList();
				listAnswer = FXCollections.observableArrayList();
				listAnswer1 = FXCollections.observableArrayList();
				query = query.toLowerCase();
				RuleBased ruleBased = new RuleBased(query, wordType,questionType);
				Directory indexDirectory = FSDirectory.open(new File(
						Constants.INDEX_DIRECTORY));
				IndexReader indexReader = IndexReader.open(indexDirectory);
				IndexSearcher indexSearcher = new IndexSearcher(indexReader);
				int count = 1;
				for (ScoreDoc scoreDoc : hits.scoreDocs) {
					Eval eval = new Eval();
					eval.setRank(count);
					count++;
					Document doc = indexSearcher.doc(scoreDoc.doc);
					String contentDoc = IOUtils.toString(new FileInputStream(
							new File(doc.get(Constants.FILE_PATH))), "UTF-8");
					eval.setFileName(doc.get(Constants.FILE_NAME));
					eval.setSimilarity(scoreDoc.score);
					contentDoc = contentDoc.toLowerCase();
					String[] arrayContent = contentDoc.split("(?<=[.!?])\\s* ");
					int[] arrayScore = new int[arrayContent.length];
					for (int i = 0; i < arrayContent.length; i++) {
						Answer answering = new Answer();
						int score = 0;
						score = ruleBased.wordMatch(score, arrayContent[i]);
						score = ruleBased.filtering(score, arrayContent[i]);
						if(i==0){
							eval.setTopScore(score);
						}
						else if(i>0){
							if(eval.getTopScore()<score){
								eval.setTopScore(score);
							}
						}
						arrayScore[i] = score;
						eval.setTotalScore(arrayScore[i]);
						answering.setContent(arrayContent[i]);
						answering.setScore(arrayScore[i]);
						answering.setFileName(doc.get(Constants.FILE_NAME));
						answering.setSimilarity(scoreDoc.score);
						listAnswer.add(answering);
					}
					listEval.add(eval);
				}
				Collections.sort(listAnswer,(o1, o2) -> {
					Integer x1 = ((Answer)o1).getScore();
					Integer x2 = ((Answer)o2).getScore();
					int comp = x1.compareTo(x2);
					if(comp!=0){
						return comp;
					}
					else{
						Float x3 = ((Answer)o1).getSimilarity();
						Float x4 = ((Answer)o2).getSimilarity();
						return x3.compareTo(x4);
					}
					
				});
			
				setLog("\n########################################## ANSWER ##########################################\n");
			
				int topAnswer = 1;
				for (int i = listAnswer.size() - 1; i >= 0; i--) {
					setLog( topAnswer + ". - Score: " + listAnswer.get(i).getScore()
							+ "\n" + "- Content: " + listAnswer.get(i).getContent()
							+ "\n" + "- File name: " + listAnswer.get(i).getFileName()
							+ "\n" + "- Similarity: " + listAnswer.get(i).getSimilarity()
							+ "\n");
					listAnswer.get(i).setRank(topAnswer);
					topAnswer++;
					//if (topAnswer == 30) {
						//break;
					//}

				}
				
				/*for (int i = listEval.size() - 1; i >= 0; i--) {
					System.out.println( i+1 +".- Score: " + listEval.get(i).getTotalScore()
							+ "\n" + "- File name: " + listEval.get(i).getFileName()
							+ "\n" + "- Similarity: " + listEval.get(i).getSimilarity()
							+ "\n" + "- Top Score: " + listEval.get(i).getTopScore()
							+ "\n\n");
			

				}*/
				indexSearcher.close();
				Collections.reverse(listAnswer);
				for (int i=0 ; i< listAnswer.size() ; i++) {
					//if(i<20){
						
						listAnswer1.add(listAnswer.get(i));
					//}
			

				}
				System.out.println("Finish 3");
				
				return null;
			}

		};
	}

	public String getAnswer(){
		return listAnswer.get(0).getContent();
	}
}

