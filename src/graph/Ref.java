package graph;

import java.io.Serializable;

public class Ref implements Serializable {

	private static final long serialVersionUID = 2903064202001923035L;
	private String URI;
	private String displayName;
	private String knowledgeBase;
	private double weight;
	
	
	public Ref(String uri, String displayName, String knowledgeBase, double weight) {
		URI = uri;
		this.displayName = displayName;
		this.knowledgeBase = knowledgeBase;
		this.weight = weight;
	}


	public String getURI() {
		return URI;
	}


	public String getDisplayName() {
		return displayName;
	}


	public String getKnowledgeBase() {
		return knowledgeBase;
	}
	
	public double getWeight(){
		return weight;
	}
	
	
}
