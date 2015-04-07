package question.answering.system;

import java.io.*;
import java.util.Observable;

import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class Indexer extends Observable {
	private IndexWriter writer;
	private String filePath;
	
	public void displayChanged(){
		setChanged();
		notifyObservers();
	}
	
	public Indexer(String indexDirectoryPath) throws IOException
	{
		Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath));
	    IndonesianAnalyzer analyzer = new IndonesianAnalyzer(Version.LUCENE_36);
	    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,analyzer);
		writer = new IndexWriter(indexDirectory,config);
		
	}
	public void close() throws CorruptIndexException, IOException
	{
		writer.close();
	}
	private Document getDocument(File file) throws IOException
	{
		Document document = new Document();
		Field contentField = new Field("contents",new FileReader(file),Field.TermVector.YES);
		Field fileNameField = new Field("filename",file.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES);
		Field filePathField = new Field("filepath",file.getCanonicalPath(),Field.Store.YES,Field.Index.NOT_ANALYZED,Field.TermVector.YES);
		document.add(contentField);
		document.add(fileNameField);
		document.add(filePathField);
		return document;
		
	}
	
	public String getFilePath(){
		return filePath;
	}
	
	public void indexFile(File file) throws IOException, InterruptedException{
		filePath = file.getCanonicalPath();
		displayChanged();
		Document document = getDocument(file);
		writer.addDocument(document);
	}
	public int createIndex(String dataDirPath, FileFilter filter) throws IOException, InterruptedException{
		File[] files = new File(dataDirPath).listFiles();
		for(File file :files){
			if(!file.isDirectory()
				&& !file.isHidden()
				&& file.exists()
				&& file.canRead()
				&& filter.accept(file))
				{
					indexFile(file);
					
				}
		}
		return writer.numDocs();
	}
}
	
