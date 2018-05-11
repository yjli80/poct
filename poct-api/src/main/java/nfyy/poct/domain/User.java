package nfyy.poct.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@NotNull
	@Column(unique=true, nullable=false)
	private String username;
	
	@JsonIgnore
	private String password;
	@JsonIgnore
	private boolean passwordRaw = true;

	@ManyToMany
	@JoinTable(name="USER_ROLES")
	private Set<Role> roles;
	
	@NotNull
	private String name;
	private String gender;
	
	@JsonFormat(shape=Shape.STRING, pattern="yyyy-MM-dd")
	private Date birthDate;
	
	private String mobile;
	private String email;
	
	public void setRawPassword(String rawPassword) {
		this.passwordRaw = true;
		this.password = rawPassword;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isPasswordRaw() {
		return passwordRaw;
	}
	public void setPasswordRaw(boolean passwordRaw) {
		this.passwordRaw = passwordRaw;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	public void addRole(Role role) {
		if (this.roles == null) {
			this.roles = new HashSet<Role>();
		}
		this.roles.add(role);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", passwordRaw=" + passwordRaw
				+ ", roles=" + roles + ", name=" + name + ", gender=" + gender + ", birthDate=" + birthDate
				+ ", mobile=" + mobile + ", email=" + email + "]";
	}
}
