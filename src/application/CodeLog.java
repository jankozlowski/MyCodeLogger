package application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CodeLog {

	private String duration;
	private Map<Integer,Integer> charCount;
	private Project project;
	private Set<Language> language;
	private int charSum;
	private List<Instant> clickDate;
	private Instant recordStart;

	
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}


	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public int getCharSum() {
		return charSum;
	}
	public void setCharSum(int charSum) {
		this.charSum = charSum;
	}

	public Map<Integer, Integer> getCharCount() {
		return charCount;
	}
	public void setCharCount(Map<Integer, Integer> charCount) {
		this.charCount = charCount;
	}
	public Set<Language> getLanguage() {
		return language;
	}
	public void setLanguage(Set<Language> language) {
		this.language = language;
	}

	public List<Instant> getClickDate() {
		return clickDate;
	}
	public void setClickDate(List<Instant> clickDate) {
		this.clickDate = clickDate;
	}
	public Instant getRecordStart() {
		return recordStart;
	}
	public void setRecordStart(Instant recordStart) {
		this.recordStart = recordStart;
	}

	

}
