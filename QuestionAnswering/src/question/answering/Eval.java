package question.answering;

public class Eval{
	private int totalScore;
	private String fileName;
	private float similarity;
	private int topScore;
	private int rank;
	
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(int totalScore) {
		this.totalScore += totalScore;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public float getSimilarity() {
		return similarity;
	}
	public void setSimilarity(float similarity) {
		this.similarity = similarity;
	}
	public int getTopScore() {
		return topScore;
	}
	public void setTopScore(int topScore) {
		this.topScore = topScore;
	}
}

