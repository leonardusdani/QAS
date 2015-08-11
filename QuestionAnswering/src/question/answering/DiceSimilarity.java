package question.answering;

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
public class DiceSimilarity extends CustomScoreQuery {
	private Query query;
	ModelQAS model;
	
	public DiceSimilarity(Query query) {
		super(query);
		this.query = query;
		model = ModelQAS.getInstance();
	}

	@Override
	public CustomScoreProvider getCustomScoreProvider(final IndexReader reader) {
		return new CustomScoreProvider(reader) {
			@Override
			public float customScore(int doc, float subQueryScore,
					float valSrcScore) throws IOException {
				TermFreqVector freqVector = reader.getTermFreqVector(doc,
						Constants.CONTENTS);
				TermFreqVector freqVector1 = reader.getTermFreqVector(doc,Constants.FILE_NAME);
				int freqs[] = freqVector.getTermFrequencies();
				Set<Term> terms = new HashSet<>();
				query.extractTerms(terms);
				model.setLog("-Doc: " + freqVector.toString());
				model.setLog("-Query: " + terms.toString());
				float dice = 0;
				float total = 0;
				float lengthQuery = 0;
				float lengthDoc = 0;
				for (Term term : terms) {
					lengthQuery++;
					int index = freqVector.indexOf(term.text());
					if (index != -1) {
						total += freqs[index];
					}
				}
				for (int freqq : freqs) {
					lengthDoc += freqq;
				}
				model.setLog("-Filename : " + freqVector1.toString());
				model.setLog("-Terms in doc: " + lengthDoc);
				model.setLog("-Terms in query: " + lengthQuery);
				model.setLog("-Terms in doc and query: " + total);
				dice = (2 * total) / (lengthDoc + lengthQuery);
				model.setLog("-Similarity: " + dice + "\n");
				return dice;
			}
		};
	}

}
