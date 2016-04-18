package question.answering;

public class RuleBased {
	String query;
	String[] arrayQuery;
	String[] wordType;
	String questionType;
	DatabaseConnections sqlite;
	String[] kamus;

	public RuleBased(String query, String[] wordType, String questionType) {
		this.questionType = questionType;
		this.query = query;
		arrayQuery = query.split(" ");
		this.wordType = wordType;
		if (questionType.contains("kapan") || questionType.contains("dimana")
				|| questionType.contains("siapa")) {
			sqlite = DatabaseConnections.getInstance();
			kamus = new String[sqlite.getProperNoun(questionType).size()];
			kamus = sqlite.getProperNoun(questionType).toArray(kamus);
		}
	}

	public int filtering(int score, String contentDoc) {
		if (questionType.equals("kapan")) {
			score = ruleKapan(score, contentDoc);
		} else if (questionType.equals("dimana")) {
			score = ruleDimana(score, contentDoc);
		} else if (questionType.equals("mengapa")) {
			score = ruleMengapa(score, contentDoc);
		} else if (questionType.equals("siapa")) {
			score = ruleSiapa(score, contentDoc);
		} else if (questionType.equals("apa")) {
			score = ruleApa(score, contentDoc);
		}
		return score;
	}

	public int ruleKapan(int score, String contentDoc) {

		boolean boolSlamRule = false;
		boolean boolSlamKamus = false;
		boolean boolSlamQueryKamus = false;
		contentDoc = contentDoc.replace("(", "");
		contentDoc = contentDoc.replace(")", "");
		contentDoc = contentDoc.replace(",", "");
		contentDoc = contentDoc.replace(".", "");
		contentDoc = contentDoc.replace("/", "");
		contentDoc = contentDoc.replace(":", "");
		contentDoc = contentDoc.replace(";", "");
		String[] content = contentDoc.split(" ");
		for (String katakunci : Constants.RULE_KAPAN) {
			for (int i = 0; i < content.length; i++) {
				if (content[i].equals(katakunci)) {
					boolSlamRule = true;
				}
			}

		}
		for (String katakunci : kamus) {
			for (int i = 0; i < content.length; i++) {
				if (content[i].equals(katakunci)) {
					boolSlamKamus = true;
				}
			}
			String[] splitQuery = query.split(" ");
			for (int i = 0; i < splitQuery.length; i++) {
				if (splitQuery[i].equals(katakunci)) {
					boolSlamQueryKamus = true;
				}
			}
		}

		if (boolSlamRule == true && boolSlamKamus == true) {
			score += 20;
		} else if (boolSlamKamus == true || boolSlamQueryKamus == true) {
			score += 4;
		} else if (boolSlamRule == true) {
			score += 4;
		}
		return score;
	}

	public int ruleDimana(int score, String contentDoc) {
		boolean boolSlamRule = false;
		boolean boolSlamKamus = false;
		contentDoc = contentDoc.replace("(", "");
		contentDoc = contentDoc.replace(")", "");
		contentDoc = contentDoc.replace(",", "");
		contentDoc = contentDoc.replace(".", "");
		contentDoc = contentDoc.replace("/", "");
		contentDoc = contentDoc.replace(":", "");
		contentDoc = contentDoc.replace(";", "");
		String[] content = contentDoc.split(" ");
		for (String katakunci : Constants.RULE_DIMANA) {
			for (int i = 0; i < content.length; i++) {
				if (content[i].equals(katakunci)) {
					boolSlamRule = true;
				}
			}
		}
		for (String katakunci : kamus) {
			for (int i = 0; i < content.length; i++) {
				if (content[i].equals(katakunci)) {
					boolSlamKamus = true;
				}
			}
		}

		if (boolSlamRule == true && boolSlamKamus == true) {
			score += 20;
		} else if (boolSlamRule == true) {
			score += 3;
		} else if (boolSlamKamus == true) {
			score += 4;
		}

		return score;
	}

	public int ruleMengapa(int score, String contentDoc) {
		boolean boolSlamRule = false;
		contentDoc = contentDoc.replace("(", "");
		contentDoc = contentDoc.replace(")", "");
		contentDoc = contentDoc.replace(",", "");
		contentDoc = contentDoc.replace(".", "");
		contentDoc = contentDoc.replace("/", "");
		contentDoc = contentDoc.replace(":", "");
		contentDoc = contentDoc.replace(";", "");
		String[] content = contentDoc.split(" ");
		for (String katakunci : Constants.RULE_MENGAPA) {
			for (int i = 0; i < content.length; i++) {
				if (content[i].equals(katakunci)) {
					boolSlamRule = true;
				}
			}
		}

		if (boolSlamRule == true) {
			score += 3;
		}
		return score;
	}

	public int ruleSiapa(int score, String contentDoc) {
		boolean boolSlamQueryKamus = false;
		String[] splitQuery = query.split(" ");
		for (String katakunci : kamus) {
			for (int i = 0; i < splitQuery.length; i++) {
				if (splitQuery[i].equals(katakunci)) {
					boolSlamQueryKamus = true;
				}
			}

		}
		if (boolSlamQueryKamus == true) {
			score += 6;
		}
		return score;
	}

	public int ruleApa(int score, String contentDoc) {
		boolean boolSlamRule1A = false;
		boolean boolSlamRule1B = false;
		boolean boolSlamRule2A = false;
		boolean boolSlamRule2B = false;
		boolean boolSlamRule3 = false;
		contentDoc = contentDoc.replace("(", "");
		contentDoc = contentDoc.replace(")", "");
		contentDoc = contentDoc.replace(",", "");
		contentDoc = contentDoc.replace(".", "");
		contentDoc = contentDoc.replace("/", "");
		contentDoc = contentDoc.replace(":", "");
		contentDoc = contentDoc.replace(";", "");
		String[] content = contentDoc.split(" ");
		String[] splitQuery = query.split(" ");
		for (String katakunci : Constants.RULE_APA_SATU_A) {
			for (int i = 0; i < splitQuery.length; i++) {
				if (splitQuery[i].equals(katakunci)) {
					boolSlamRule1A = true;
				}
			}
		}
		for (String katakunci : Constants.RULE_APA_SATU_B) {
			for (int i = 0; i < content.length; i++) {
				if (content[i].equals(katakunci)) {
					boolSlamRule1B = true;
				}
			}
		}
		for (String katakunci : Constants.RULE_APA_DUA_A) {
			for (int i = 0; i < splitQuery.length; i++) {
				if (splitQuery[i].equals(katakunci)) {
					boolSlamRule2A = true;
				}
			}
		}
		for (String katakunci : Constants.RULE_APA_DUA_B_TIGA) {
			for (int i = 0; i < content.length; i++) {
				if (content[i].equals(katakunci)) {
					boolSlamRule2B = true;
				}
			}
			for (int i = 0; i < splitQuery.length; i++) {
				if (splitQuery[i].equals(katakunci)) {
					boolSlamRule3 = true;
				}
			}
		}

		if (boolSlamRule1A == true && boolSlamRule1B == true) {
			score += 6;
		} else if (boolSlamRule2A == true && boolSlamRule2B == true) {
			score += 20;
		} else if (boolSlamRule3 == true) {
			score += 6;
		}
		return score;
	}

	public int wordMatch(int score, String contentDoc) {
		contentDoc = contentDoc.replace("(", "");
		contentDoc = contentDoc.replace(")", "");
		contentDoc = contentDoc.replace(",", "");
		contentDoc = contentDoc.replace(".", "");
		contentDoc = contentDoc.replace("/", "");
		contentDoc = contentDoc.replace(":", "");
		contentDoc = contentDoc.replace(";", "");
		String[] content = contentDoc.split(" ");
		for (int i = 0; i < arrayQuery.length; i++) {
			for(int j=0;j<content.length;j++){
				if (content[j].equals(arrayQuery[i])) {
					if (wordType[i] != null && wordType[i].equals("verb")) {
						score += 6;
					} else {
						score += 3;
					}
				}
			}
			
		}
		return score;
	}

}
