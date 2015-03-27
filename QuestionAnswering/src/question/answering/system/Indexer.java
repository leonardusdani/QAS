package question.answering.system;

import java.io.*;

import javax.swing.JTextArea;

import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class Indexer {
	private JTextArea textArea;
	private IndexWriter writer;
	
	public Indexer(JTextArea textArea, String indexDirectoryPath) throws IOException
	{
		
		this.textArea = textArea;
		Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath));
	    IndonesianAnalyzer analyzer = new IndonesianAnalyzer(Version.LUCENE_36);
		//StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
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
	public void indexFile(File file) throws IOException{
		textArea.append("Indexing "+file.getCanonicalPath()+"\n");
		Document document = getDocument(file);
		writer.addDocument(document);
	}
	public int createIndex(String dataDirPath, FileFilter filter) throws IOException{
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
	
