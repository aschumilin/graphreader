package graph;

import java.io.Serializable;

public class Role implements Serializable{

	private static final long serialVersionUID = -5058931256439107155L;
	
	

	private String role;


	
	public Role(String role) {
		super();
		this.role = role;
	}



	public String getRole() {
		return role;
	}
	
	
	public String toString(){
		
		return role;
	}
	

	
	
}
