package application;

public class Project {

	private long projectId;
	private String name;
	private boolean selected;
	private boolean compleated;

	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public boolean isCompleated() {
		return compleated;
	}
	public void setCompleated(boolean compleated) {
		this.compleated = compleated;
	}
	
	
}
