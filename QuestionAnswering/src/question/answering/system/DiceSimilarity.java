package question.answering.system;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.function.CustomScoreProvider;
import org.apache.lucene.search.function.CustomScoreQuery;
@SuppressWarnings("serial")
public class DiceSimilarity extends CustomScoreQuery{
	private Query query;
	public DiceSimilarity(Query query){
		super(query);
		this.query = query;
	}
	public CustomScoreProvider getCustomScoreProvider(final IndexReader reader){
		return new CustomScoreProvider(reader){
			public float customScore(int doc,float subQueryScore,float valSrcScore) throws IOException{
				TermFreqVector freqVector = reader.getTermFreqVector(doc, "contents");
				TermFreqVector freqVector1 = reader.getTermFreqVector(doc, "filename");
				int freqs[] = freqVector.getTermFrequencies();
				Set<Term> terms = new HashSet<>();
				query.extractTerms(terms);
				System.out.println(terms.toString());
				float dice=0;
				float total = 0;
				float lengthQuery = 0;
				float lengthDoc = 0;
				for(Term term : terms){
					lengthQuery++;
					int index = freqVector.indexOf(term.text());
					if(index != -1){
						total += freqs[index];
					}
				}
				for(int freqq : freqs){
					lengthDoc += freqq;
				}
				System.out.println("filename : " + freqVector1.toString());
				System.out.println("terms in doc: " + lengthDoc);
				System.out.println("terms in query: " + lengthQuery);
				System.out.println("terms in doc and query: " + total);
				dice = (2*total)/(lengthDoc+lengthQuery);
				System.out.println("Similarity: " + dice);
				return dice;
			}
		};
	}

}
