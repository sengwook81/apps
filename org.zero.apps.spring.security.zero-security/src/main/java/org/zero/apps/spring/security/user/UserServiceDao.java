package org.zero.apps.spring.security.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserServiceDao  implements UserDetailsService{

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		if("user".equals(username)) {
			
			List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
			auths.add(new SimpleGrantedAuthority("ROLE_USER"));
			auths.add(new SimpleGrantedAuthority("ROLE_Admin"));
			return new UserDetailInfo(username,"password",auths);
		}
		
		return null;
	}
	
}
