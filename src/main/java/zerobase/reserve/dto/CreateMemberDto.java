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

        public Member createMember(List<String> roles){
            return Member.builder()
                    .name(this.name)
                    .password(this.password)
                    .email(this.email)
                    .gender(this.gender)
                    .phoneNumber(this.phoneNumber)
                    .roles(roles)
                    .build();
        }
    }
}
