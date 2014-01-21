package org.zero.apps.spring.security.user;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserDetailInfo extends User {

	private static final long serialVersionUID = 8656196271547348571L;

	public UserDetailInfo(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);

	}
	
}