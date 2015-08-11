package question.answering;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class DatabaseConnections {

	Connection c;
	Statement stmt;
	ResultSet rs;
	ModelQAS model;
	String bahasa;
	public String getBahasa() {
		return bahasa;
	}

	public void setBahasa(String bahasa) {
		this.bahasa = bahasa;
	}

	String grade;
	
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	private static DatabaseConnections databaseConnections = new DatabaseConnections();
	
	
	public static DatabaseConnections getInstance(){
		return databaseConnections;
	};

	private DatabaseConnections() {
		model = ModelQAS.getInstance();
		c = null;
		stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:qas.db");
			c.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	public String getSynonym(String query) {
		
		
		//LANGUAGE='I'
		String[] arrayQuery = query.split(" ");

		for (String word : arrayQuery) {
			try {
				stmt = c.createStatement();
				rs = stmt
						.executeQuery("SELECT * FROM WORDNET WHERE (CODE IN (SELECT CODE FROM WORDNET WHERE WORD='"
								+ word
								+ "')) AND ("+bahasa+") AND ("+grade+") AND (WORD!='"
								+ word + "');");
				while (rs.next()) {
					if (!query.contains(rs.getString("WORD"))) {
						query = new StringBuilder(query).append(
								rs.getString("WORD") + " ").toString();
					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return query;

	}
	
	public List<String> getProperNoun(String wordType){
			List<String> listProperNoun = new ArrayList<String>();
			try {
				stmt = c.createStatement();
				rs = stmt.executeQuery("SELECT "+ wordType + " FROM PROPERNOUN;");
				while (rs.next()) {
					if(!listProperNoun.contains(rs.getString(wordType)) && (rs.getString(wordType))!=null){
						listProperNoun.add(rs.getString(wordType));
					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		return listProperNoun;
	} 
	
	public String[] getPartOfSpeech(String[] query){
		String[] wordType = new String[query.length];
		
		for (int i=0;i<query.length;i++) {
			try {
				stmt = c.createStatement();
				rs = stmt.executeQuery("SELECT VERB FROM PARTOFSPEECH WHERE VERB='"+ query[i] +"';");
				while (rs.next()) {
					if (rs.getString("VERB") != null) {
						wordType[i] = "verb";
						model.setLog("- " + query[i] + ": (verb)");
					}
				}
				

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		/*try {
			stmt = c.createStatement();
			rs = stmt.executeQuery("SELECT VERB FROM PARTOFSPEECH;");
			while (rs.next()) {
				for(int i=0;i<query.length;i++){
					if(query[i].equals(rs.getString("VERB"))){
						
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		return wordType;
	}
	
}
