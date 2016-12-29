package de.kreth.clubhelperbackend.config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class ClubhelperAuthenticationProvider implements AuthenticationProvider {

	private Logger log = LoggerFactory.getLogger(ClubhelperAuthenticationProvider.class);
	private PreparedStatement stmGroups;

	@Autowired
	public void setDataSource(DataSource source) throws SQLException {
		stmGroups = source.getConnection()
				.prepareStatement("select groupDef.name groupname from person \n"
						+ "	left join persongroup on persongroup.person_id = person._id\n"
						+ "    left join groupDef on persongroup.group_id = groupDef._id\n"
						+ "where person.username = ? and person.password = ?");
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
		String password = authentication.getCredentials().toString();

		log.debug("Searching Login for name=\"" + name + "\"");

		List<GrantedAuthority> grantedAuths;

		try {
			grantedAuths = getRoles(name, password);
		} catch (SQLException e) {
			throw new AuthenticationCredentialsNotFoundException("Sql error on authentication", e);
		}

		if (grantedAuths.isEmpty()) {
			log.info("No valid login group found for \"" + name + "\"");
			return null;
		} else {
			Authentication auth = new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
			log.info("Login groups found for \"" + name + "\": " + grantedAuths);
			return auth;
		}
	}

	private List<GrantedAuthority> getRoles(String name, String password) throws SQLException {

		List<GrantedAuthority> grantedAuths = new ArrayList<>();
		stmGroups.setString(1, name);
		stmGroups.setString(2, password);
		ResultSet rs = stmGroups.executeQuery();

		while (rs.next()) {
			grantedAuths.add(new SimpleGrantedAuthority(rs.getString("groupname")));
		}

		return grantedAuths;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
