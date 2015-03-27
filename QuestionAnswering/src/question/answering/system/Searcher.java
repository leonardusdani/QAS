package question.answering.system;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {
	IndexSearcher indexSearcher;
	QueryParser queryParser;
	Query query;
	
	public Searcher(String indexDirectoryPath) throws IOException{
		Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath));
		IndexReader indexReader = IndexReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
		queryParser = new QueryParser(Version.LUCENE_36,"contents",new IndonesianAnalyzer(Version.LUCENE_36));	

	}
	
	public TopDocs search(String searchQuery) throws IOException,ParseException{
		
		
		query = queryParser.parse(searchQuery);
		System.out.println(query);
		DiceSimilarity diceSimilarity = new DiceSimilarity(query);
		return indexSearcher.search(diceSimilarity, 100);
	}
	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException,IOException{
		Explanation explanation = indexSearcher.explain(query, scoreDoc.doc);
		System.out.println(explanation.toString());
		return indexSearcher.doc(scoreDoc.doc);
	}
	public void close() throws IOException{
		indexSearcher.close();
	}
	
}
