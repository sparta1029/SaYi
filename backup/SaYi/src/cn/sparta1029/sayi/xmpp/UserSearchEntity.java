package cn.sparta1029.sayi.xmpp;

public class UserSearchEntity {
	private String userName,name,email;
	public UserSearchEntity(String userName,String name,String email) {
		this.userName=userName;
		this.name=name;
		this.email=email;
	}
	
	public String getUserName(){
		return userName;
	}
	public String getName(){
		return name;
	}
	public String getEmail(){
		return email;
	}
	
}
