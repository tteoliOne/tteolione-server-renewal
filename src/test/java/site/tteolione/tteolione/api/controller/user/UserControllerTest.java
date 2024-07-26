package site.tteolione.tteolione.api.controller.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import site.tteolione.tteolione.ControllerTestSupport;
import site.tteolione.tteolione.api.controller.user.request.DupNicknameReq;
import site.tteolione.tteolione.api.controller.user.request.DuplicateLoginIdReq;
import site.tteolione.tteolione.config.exception.Code;

import static org.junit.jupiter.api.Assertions.*;
//참고문헌 https://velog.io/@jmjmjmz732002/Springboot-Junit5-%EC%BB%A8%ED%8A%B8%EB%A1%A4%EB%9F%AC-%ED%85%8C%EC%8A%A4%ED%8A%B8-401-%EC%97%90%EB%9F%AC%EB%A5%BC-%EB%A7%88%EC%A3%BC%EC%B3%A4%EB%8B%A4
class UserControllerTest extends ControllerTestSupport {


    @DisplayName("회원가입시 로그인 아이디 중복 테스트 통과")
    @Test
    @WithMockUser
    void duplicateLoginId() throws Exception {
        // given
        DuplicateLoginIdReq request = DuplicateLoginIdReq.builder()
                .loginId("test123")
                .build();
        // when
        BDDMockito.when(userService.duplicateLoginId(Mockito.anyString()))
                .thenReturn(true);

        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/check/login-id")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("사용가능한 아이디입니다."));
    }

    @DisplayName("회원가입시 로그인 아이디 중복체크할 때 로그인 아이디에 null 값")
    @Test
    @WithMockUser
    void duplicateNullLoginId() throws Exception {
        // given
        DuplicateLoginIdReq request = DuplicateLoginIdReq.builder()
                .build();
        // when
        BDDMockito.when(userService.duplicateLoginId(Mockito.anyString()))
                .thenReturn(true);

        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/check/login-id")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 로그인Id를 적어주세요."));
    }

    @DisplayName("회원가입시 로그인 아이디 중복체크할 때 로그인 아이디 유효성 검사 실패")
    @Test
    @WithMockUser
    void duplicateValidateLoginId() throws Exception {
        // given
        DuplicateLoginIdReq request = DuplicateLoginIdReq.builder()
                .loginId("123123123123")
                .build();
        // when
        BDDMockito.when(userService.duplicateLoginId(Mockito.anyString()))
                .thenReturn(true);

        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/check/login-id")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("id는 소문자 하나이상있어야하고, 6자~20자여야합니다."));
    }

    @DisplayName("회원가입시 로그인 아이디 중복체크할 때 로그인 아이디 글자수 초과")
    @Test
    @WithMockUser
    void duplicateEmptyLoginId() throws Exception {
        // given
        DuplicateLoginIdReq request = DuplicateLoginIdReq.builder()
                .loginId("abcdefghijk123123123123")
                .build();
        // when
        BDDMockito.when(userService.duplicateLoginId(Mockito.anyString()))
                .thenReturn(true);

        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/check/login-id")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("id는 소문자 하나이상있어야하고, 6자~20자여야합니다."));
    }

    @DisplayName("닉네임 중복 테스트 통과")
    @Test
    @WithMockUser
    void duplicateNickname() throws Exception {
        // given
        DupNicknameReq request = DupNicknameReq.builder()
                .nickname("test12")
                .build();
        // when
        BDDMockito.when(userService.existByNickname(Mockito.anyString()))
                .thenReturn(false);

        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/check/nickname")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("사용가능한 닉네임입니다."));
    }

    @DisplayName("닉네임 중복 체크 빈 문자열")
    @Test
    @WithMockUser
    void duplicateNicknameEmpty() throws Exception {
        // given러
        DupNicknameReq request = DupNicknameReq.builder()
                .nickname("")
                .build();
        // when
        BDDMockito.when(userService.existByNickname(Mockito.anyString()))
                .thenReturn(true);

        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/check/nickname")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("닉네임은 2글자 이상 6글자 이하이여야 합니다."));
    }
}