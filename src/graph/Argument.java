package graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Argument implements Serializable {


	private static final long serialVersionUID = 1189089332581539650L;
	
	private String id;
	private String mention;
	private String displayName;
	private ArrayList<Ref> references;
	
	
	public Argument(String id, String displayName, String mention) {
		super();
		this.id = id;
		this.mention = mention;
		this.displayName = displayName;
		references = new ArrayList<Ref>();
	}
	
	
	public abstract boolean isRoot();

	public abstract boolean isPredicate();
	
	
	public String getId(){
		return id;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getMention(){
		return mention;
	}

	@Override
	public boolean equals(Object obj) {
		return ((Argument)obj).getId().equals(this.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}


	public void addRef(Ref r){
		references.add(r);
	}
	public ArrayList<Ref> getRefs() {
		return references;
	}
	
}
