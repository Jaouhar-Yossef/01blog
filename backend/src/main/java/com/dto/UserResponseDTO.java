package com.dto;

public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String role;
    private String tokeString;


    public UserResponseDTO() {}
    public UserResponseDTO(Long id, String username, String email, String role , String tokeString)  {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.tokeString = tokeString;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String gettokeString() { return this.tokeString; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setTokeString(String tokeString) { this.tokeString = tokeString; }

}
