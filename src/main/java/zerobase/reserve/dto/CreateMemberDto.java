package zerobase.reserve.dto;

import lombok.*;
import zerobase.reserve.domain.Gender;
import zerobase.reserve.domain.Member;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class CreateMemberDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignIn{
        @NotEmpty
        private String email;
        @NotEmpty
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignUp{

        @NotEmpty
        private String email;
        @NotEmpty
        private String password;
        @NotEmpty
        private String name;
        @NotEmpty
        private String phoneNumber;
        @NotEmpty
        private String gender;

        public Member createMember(List<String> roles){
            Gender genderData;

            if (this.gender.equals("Female")){
                genderData = Gender.FEMALE;
            } else{
                genderData = Gender.MALE;
            }

            return Member.builder()
                    .name(this.name)
                    .password(this.password)
                    .email(this.email)
                    .gender(genderData)
                    .phoneNumber(this.phoneNumber)
                    .roles(roles)
                    .build();
        }
    }
}
