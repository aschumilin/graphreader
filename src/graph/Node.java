package graph;

import java.io.Serializable;

public class Node extends Argument implements Serializable {
	

	private static final long serialVersionUID = 6961741340587011057L;
	
	
	private String type; // e.g. word or entity_person/organization.. or entity_other

	
	public Node(String id, String type, String displayName, String mention) {
		super(id, displayName, mention);	
		this.type = type;
	}
	
	@Override
	public boolean isPredicate() {
		return false;
	}
	@Override
	public boolean isRoot(){
		return false;
	}


	public String getType() {
		return type;
	}


	public String toString(){
		if(type.equals("PREDICATE")){
			return "[" + super.getId() +"->" + super.getDisplayName() + "]";
		}else{
			return "[" + super.getMention() +"] "+ super.getRefs().get(0).getURI() + " (" + super.getRefs().get(0).getWeight() + ")";
		}
		
	}
	
	
}


