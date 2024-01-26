package com.pedramero.sms.pmsms.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document("Users")
public class User {

    @Id
    String id;
    String firstName;
    String lastName;
    String username;
    String email;
    Date birthDate;
    UserRole role;
    String imageFileObjectId;
    String password;

}
