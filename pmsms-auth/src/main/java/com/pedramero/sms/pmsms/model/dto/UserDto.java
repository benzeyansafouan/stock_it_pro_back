package com.pedramero.sms.pmsms.model.dto;

import com.pedramero.sms.pmsms.model.UserRole;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {

    String id;
    String firstName;
    String lastName;
    String username;
    String email;
    Date birthDate;
    UserRole role;
    byte[] userImage;
    String password;
    String personalPin;
}
