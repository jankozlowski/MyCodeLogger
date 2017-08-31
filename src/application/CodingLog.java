package application;

import java.util.Date;
import java.util.List;

public class CodingLog {

	private String duration;
	private List<Integer> charCount;
	private String project;
	private String language;
	private int charSum;
	private Date creationDate;
	

	
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public int getCharSum() {
		return charSum;
	}
	public void setCharSum(int charSum) {
		this.charSum = charSum;
	}
	public List<Integer> getCharCount() {
		return charCount;
	}
	
	public void setCharCount(List<Integer> charCount) {
		this.charCount = charCount;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}
