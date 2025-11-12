package rs.raf.demo.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    private Integer id;
    @NotNull(message = "Title field is required")
    @NotEmpty(message = "Title field is required")
    private String email;
    private String password;
    @NotNull(message = "Title field is required")
    @NotEmpty(message = "Title field is required")
    private String firstName;
    @NotNull(message = "Title field is required")
    @NotEmpty(message = "Title field is required")
    private String lastName;
    private UserType userType;
    private Active status;


    public User(int userId, String firstName, String lastName, String email, String password, String userType, String status) {
        this.id = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.userType = UserType.valueOf(userType);
        this.status = Active.valueOf(status);
    }
}

