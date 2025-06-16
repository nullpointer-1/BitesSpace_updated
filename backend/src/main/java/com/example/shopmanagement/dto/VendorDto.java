package com.example.shopmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorDto {
    public VendorDto(Long id, @NotBlank(message = "Vendor name is required") String name,
			@NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email,
			@NotBlank(message = "Username is required") String username, String password,
			@NotBlank(message = "Contact number is required") String contactNumber,
			@NotNull(message = "Active status is required") Boolean active) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.username = username;
		this.password = password;
		this.contactNumber = contactNumber;
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	private Long id;

    @NotBlank(message = "Vendor name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Username is required")
    private String username;

    private String password;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @NotNull(message = "Active status is required")
    private Boolean active;

    public VendorDto(String username, String name, String email) {
        this.username=username;
        this.name=name;
        this.email=email;
    }
}