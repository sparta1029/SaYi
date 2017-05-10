package cn.sparta1029.sayi.xmpp;

public class UserEntity {
	public String account;
	public String password;
	public String nickname;
	public String email;
	public UserEntity(String account, String password,String nickname,String email) {
		this.account = account;
		this.password = password;
		this.nickname = nickname;
		this.email = email;
	}
}
