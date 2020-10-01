package de.kreth.clubhelperbackend.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class ClubhelperAuthenticationProvider implements AuthenticationProvider, UserDetailsService {

	private final Logger log = LoggerFactory.getLogger(ClubhelperAuthenticationProvider.class);
	private final DataSource dataSource;

	public ClubhelperAuthenticationProvider(DataSource dataSource) throws SQLException {
		this.dataSource = dataSource;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
		String password = authentication.getCredentials().toString();

		log.debug("Searching Login for name=\"" + name + "\"");

		try {
			List<GrantedAuthority> grantedAuths = getRoles(name, password);

			if (grantedAuths.isEmpty()) {
				log.warn("No valid login group found for \"" + name + "\"");
				return null;
			} else {
				Authentication auth = new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
				log.info("Login groups found for \"" + name + "\": " + grantedAuths);
				return auth;
			}

		} catch (SQLException e) {
			log.error("Sql error on authentication", e);
			throw new AuthenticationCredentialsNotFoundException("Sql error on authentication", e);
		}
	}

	private List<GrantedAuthority> getRoles(String name, String password) throws SQLException {

		List<GrantedAuthority> grantedAuths = new ArrayList<>();

		try (Connection connection = dataSource.getConnection()) {

			PreparedStatement stmGroups = connection.prepareStatement("select groupdef.name groupname from person \n"
					+ "	left join persongroup on persongroup.person_id = person.id\n"
					+ " left join groupdef on persongroup.group_id = groupdef.id\n"
					+ " where person.username = ? and person.password = ?");
			stmGroups.setString(1, name);
			stmGroups.setString(2, password);
			ResultSet rs = stmGroups.executeQuery();

			while (rs.next()) {
				String groupName = rs.getString("groupname");
				if (groupName != null) {
					grantedAuths.add(createAuthority(groupName));
				}
			}

		}
		return grantedAuths;
	}

	private GrantedAuthority createAuthority(String roleName) {
		return new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("getting Userdetails for username " + username);

		PreparedStatement stmUser = null;

		try (Connection connection = dataSource.getConnection()) {

			stmUser = connection
					.prepareStatement("select person.password password, groupdef.name groupname from person \n"
							+ "	left join persongroup on persongroup.person_id = person.id\n"
							+ " left join groupdef on persongroup.group_id = groupdef.id\n"
							+ " where person.username = ?");
			stmUser.setString(1, username);
			ResultSet rs = stmUser.executeQuery();

			List<GrantedAuthority> grantedAuths = new ArrayList<>();
			String password = null;
			while (rs.next()) {
				grantedAuths.add(createAuthority(rs.getString("groupname")));
				password = rs.getString("password");
			}

			if (password == null) {
				throw new UsernameNotFoundException("No user found matching " + username);
			} else {
				return new User(username, password, grantedAuths);
			}

		} catch (SQLException e) {
			throw new UsernameNotFoundException("error executing " + stmUser, e);
		}
	}

}
