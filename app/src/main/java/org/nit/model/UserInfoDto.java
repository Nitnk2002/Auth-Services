package org.nit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.nit.entities.UserInfo;


@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoDto extends UserInfo {

    @NonNull
    private String userName;
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private Long phoneNumber;
    @NonNull
    private String email;
}
