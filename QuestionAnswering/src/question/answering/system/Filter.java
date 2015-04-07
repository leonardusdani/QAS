package question.answering.system;

public class Filter {
	
	String jenisPertanyaan;
	String query = "";
	
	public Filter(String query){
		query = query.replace("?", "").toLowerCase();
		String[] arrayQuery = query.split(" ");
		jenisPertanyaan = arrayQuery[0];
		for(int i=1;i<arrayQuery.length;i++){
			this.query = new StringBuilder(this.query).append(arrayQuery[i]+" ").toString();
		}
		System.out.println("Jenis pertanyaan: " + jenisPertanyaan);
		System.out.println("query: " + this.query);
	}
	
	public String getQuery(){
		return this.query.toString();
	}

}
