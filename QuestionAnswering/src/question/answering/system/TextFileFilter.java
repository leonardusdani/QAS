package question.answering.system;
import java.io.*;

public class TextFileFilter implements FileFilter {
	public boolean accept(File pathname)
	{
		return pathname.getName().toLowerCase().endsWith(".txt");
	}
	

}
