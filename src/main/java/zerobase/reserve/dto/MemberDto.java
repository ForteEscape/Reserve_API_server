package zerobase.reserve.dto;

import lombok.*;
import zerobase.reserve.domain.Gender;
import zerobase.reserve.domain.Member;

import java.util.List;

/**
 * 회원 정보 반환 DTO
 */
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
    private Gender gender;

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
