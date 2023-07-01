package zerobase.reserve.dto;

import lombok.*;
import zerobase.reserve.domain.Member;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {

    private Long id;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private String gender;

    private List<String> roles;

    public static MemberDto fromEntity(Member member){
        return MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .password(member.getPassword())
                .phoneNumber(member.getPhoneNumber())
                .gender(member.getGender())
                .roles(member.getRoles())
                .build();
    }
}
