package com.alextim.controller.dto;

import com.alextim.domain.User;
import com.alextim.security.GrantedAuthorityImpl;
import lombok.*;

import javax.validation.constraints.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data @NoArgsConstructor @RequiredArgsConstructor @AllArgsConstructor
public class UserDto {

    public static final String JSON_EXAMPLE = "{ \"username\" : \"IvanPetrov\", \"name\" : \"Ivan\", \"surname\" : \"Petrov\", \"email\" : \"petrov@gmail.com\", \"rawPassword\" : \"123\" }";

    @Setter(AccessLevel.NONE)
    private long id;

    @NotNull(message = "username cannot be null") @NotEmpty @NotBlank
    @NonNull
    private String username;

    @NotNull(message = "name cannot be null") @NotEmpty @NotBlank
    @NonNull
    private String name;

    @NotNull(message = "surname cannot be null") @NotEmpty @NotBlank
    @NonNull
    private String surname;

    @Email(message = "email does not match the format ") @NotNull(message = "email cannot be null") @NotEmpty @NotBlank
    @NonNull
    private String email;

    @NotNull(message = "rawPassword cannot be null") @NotEmpty @NotBlank @Size(min = 3)
    @NonNull
    private String rawPassword;

    private String[] authorities;

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                "***",
                user.getAuthorities().stream().map(GrantedAuthorityImpl::getAuthority).toArray(String[]::new));
    }
}
