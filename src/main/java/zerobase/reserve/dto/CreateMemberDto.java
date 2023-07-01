package zerobase.reserve.dto;

import lombok.*;
import zerobase.reserve.domain.Member;

import java.util.List;

public class CreateMemberDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignIn{
        private String email;
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignUp{
        private String email;
        private String password;
        private String name;
        private String phoneNumber;
        private String gender;

        private List<String> roles;

        public Member toEntity(){
            return Member.builder()
                    .name(this.name)
                    .gender(this.gender)
                    .phoneNumber(this.phoneNumber)
                    .email(this.email)
                    .password(this.password)
                    .roles(this.roles)
                    .build();
        }
    }
}
