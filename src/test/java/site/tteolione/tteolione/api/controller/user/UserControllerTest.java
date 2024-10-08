package site.tteolione.tteolione.api.controller.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import site.tteolione.tteolione.ControllerTestSupport;
import site.tteolione.tteolione.GenerateMockToken;
import site.tteolione.tteolione.WithMockCustomAccount;
import site.tteolione.tteolione.api.controller.user.request.*;
import site.tteolione.tteolione.api.service.user.request.FindServiceLoginIdReq;
import site.tteolione.tteolione.api.service.user.request.FindServicePasswordReq;
import site.tteolione.tteolione.api.service.user.request.VerifyServiceLoginIdReq;
import site.tteolione.tteolione.api.service.user.request.VerifyServicePasswordReq;
import site.tteolione.tteolione.api.service.user.response.VerifyLoginIdRes;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.config.exception.GeneralException;
import site.tteolione.tteolione.common.util.SecurityUserDto;
import site.tteolione.tteolione.common.util.SecurityUtils;

//참고문헌 https://velog.io/@jmjmjmz732002/Springboot-Junit5-%EC%BB%A8%ED%8A%B8%EB%A1%A4%EB%9F%AC-%ED%85%8C%EC%8A%A4%ED%8A%B8-401-%EC%97%90%EB%9F%AC%EB%A5%BC-%EB%A7%88%EC%A3%BC%EC%B3%A4%EB%8B%A4
class UserControllerTest extends ControllerTestSupport {


    @DisplayName("회원가입시 로그인 아이디 중복 테스트 통과")
    @Test
    @WithMockUser
    void duplicateLoginId_Success() throws Exception {
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
    void duplicate_Failure_NullLoginId() throws Exception {
        // given
        DuplicateLoginIdReq request = DuplicateLoginIdReq.builder()
                .build();
        // when
        BDDMockito.when(userService.duplicateLoginId(Mockito.anyString()))
                .thenReturn(true);

        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/check/login-id")
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
    void duplicate_Failure_ValidateLoginId() throws Exception {
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
    void duplicate_Failure_EmptyLoginId() throws Exception {
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
    void duplicateNickname_Success() throws Exception {
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
    void duplicate_Failure_EmptyNickname() throws Exception {
        // given
        DupNicknameReq request = DupNicknameReq.builder()
                .nickname("")
                .build();
        // when
        BDDMockito.when(userService.existByNickname(Mockito.anyString()))
                .thenReturn(true);

        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/check/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("닉네임은 2글자 이상 6글자 이하이여야 합니다."));
    }

    @DisplayName("닉네임 변경 - 성공")
    @Test
    @WithMockCustomAccount
    void changeNickname_Success() throws Exception {
        // given
        String newNickname = "새로운닉네임";

        ChangeNicknameReq request = ChangeNicknameReq.builder()
                .nickname(newNickname)
                .build();

        SecurityUserDto userDto = SecurityUtils.getUser();

        // when
        BDDMockito.doNothing().when(userService).changeNickname(userDto, request.toServiceRequest());

        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/v2/users/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .headers(GenerateMockToken.getToken())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("정상적으로 닉네임이 변경되었습니다."));
    }

    @DisplayName("닉네임 길이 유효성 실패(2글자 이상 6글자 이하) - 실패")
    @Test
    @WithMockCustomAccount
    void changeNickname_Failure_LengthNickname() throws Exception {
        // given
        String newNickname = "1234567";

        ChangeNicknameReq request = ChangeNicknameReq.builder()
                .nickname(newNickname)
                .build();

        SecurityUserDto userDto = SecurityUtils.getUser();

        // when
        BDDMockito.doNothing().when(userService).changeNickname(userDto, request.toServiceRequest());

        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/v2/users/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .headers(GenerateMockToken.getToken())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("닉네임은 2글자 이상 6글자 이하이여야 합니다."));
    }

    @DisplayName("회원의 닉네임 변경이 기존의 것 일치할 때 - 실패")
    @Test
    @WithMockCustomAccount
    void changeNickname_Failure_EqualsToOriginNickname() throws Exception {
        // given
        String newNickname = "새로운닉네임";

        ChangeNicknameReq request = ChangeNicknameReq.builder()
                .nickname(newNickname)
                .build();

        SecurityUserDto userDto = SecurityUtils.getUser();

        // when
        BDDMockito.doThrow(new GeneralException(Code.EQUALS_NICKNAME)).when(userService).changeNickname(userDto, request.toServiceRequest());

        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/v2/users/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .headers(GenerateMockToken.getToken())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.EQUALS_NICKNAME.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(Code.EQUALS_NICKNAME.getMessage()));
    }

    @DisplayName("회원의 닉네임 변경이 다른 회원들의 것과 일치할 때- 실패")
    @Test
    @WithMockCustomAccount
    void changeNickname_Failure_ExistByNickname() throws Exception {
        // given
        String newNickname = "새로운닉네임";

        ChangeNicknameReq request = ChangeNicknameReq.builder()
                .nickname(newNickname)
                .build();

        SecurityUserDto userDto = SecurityUtils.getUser();

        // when
        BDDMockito.doThrow(new GeneralException(Code.EXIST_NICKNAME)).when(userService).changeNickname(userDto, request.toServiceRequest());

        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/v2/users/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .headers(GenerateMockToken.getToken())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.EXIST_NICKNAME.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(Code.EXIST_NICKNAME.getMessage()));
    }

    @DisplayName("아이디 찾기 - 성공")
    @Test
    @WithMockCustomAccount
    void findLoginId_Success() throws Exception {
        // given
        String username = "테스터";
        String email = "test123@naver.com";

        FindLoginIdReq request = FindLoginIdReq.builder()
                .username(username)
                .email(email)
                .build();

        // when
        BDDMockito.when(userService.findLoginId(Mockito.any(FindServiceLoginIdReq.class)))
                .thenReturn("이메일 인증코드 발송에 성공했습니다.");

        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/find/login-id")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("이메일 인증코드 발송에 성공했습니다."));
    }

    @DisplayName("아이디 찾기시 유저네임 빈 문자열 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void findLoginId_Failure_EmptyUsername() throws Exception {
        // given
        String username = "";
        String email = "test123@naver.com";

        FindLoginIdReq request = FindLoginIdReq.builder()
                .username(username)
                .email(email)
                .build();

        // when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/find/login-id")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 이름을 적어주세요."));
    }

    @DisplayName("아이디 찾기시 이메일 빈 문자열 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void findLoginId_Failure_EmptyEmail() throws Exception {
        // given
        String username = "테스터";
        String email = "";

        FindLoginIdReq request = FindLoginIdReq.builder()
                .username(username)
                .email(email)
                .build();

        // when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/find/login-id")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 이메일을 적어주세요."));
    }

    @DisplayName("아이디 찾기시 이메일 빈 문자열 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void findLoginId_Failure_RegexEmail() throws Exception {
        // given
        String username = "테스터";
        String email = "test123123";

        FindLoginIdReq request = FindLoginIdReq.builder()
                .username(username)
                .email(email)
                .build();

        // when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/find/login-id")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이메일 형식이 맞지 않습니다."));
    }

    @DisplayName("아이디 찾기 검증 - 성공")
    @Test
    @WithMockCustomAccount
    void verifyLoginId_Success() throws Exception {
        // given
        String loginId = "test123";
        String username = "테스터";
        String email = "test123@naver.com";
        String authCode = "test123";

        VerifyLoginIdReq request = VerifyLoginIdReq.builder()
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        //when
        VerifyLoginIdRes response = VerifyLoginIdRes.from(loginId);

        BDDMockito.when(userService.verifyLoginId(Mockito.any(VerifyServiceLoginIdReq.class)))
                .thenReturn(response);

        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/verify/login-id")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.loginId").value(loginId));
    }

    @DisplayName("아이디 찾기 검증 시 유저네임 빈문자열 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void verifyLoginId_Failure_NotBlank_Username() throws Exception {
        // given
        String username = "";
        String email = "test123@naver.com";
        String authCode = "test123";

        VerifyLoginIdReq request = VerifyLoginIdReq.builder()
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/verify/login-id")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 이름을 적어주세요."));
    }

    @DisplayName("아이디 찾기 검증 시 인증번호 7글자가 아닐때 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void verifyLoginId_Failure_NotLengthSeven_Username() throws Exception {
        // given
        String username = "테스터";
        String email = "test123@naver.com";
        String authCode = "test";

        VerifyLoginIdReq request = VerifyLoginIdReq.builder()
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/verify/login-id")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("인증코드는 7자리입니다."));
    }

    @DisplayName("아이디 찾기 검증 시 이메일 형식이 맞지 않을때 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void verifyLoginId_Failure_RegexEmail() throws Exception {
        // given
        String username = "테스터";
        String email = "test123";
        String authCode = "test123";

        VerifyLoginIdReq request = VerifyLoginIdReq.builder()
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/verify/login-id")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이메일 형식이 맞지 않습니다."));
    }

    @DisplayName("비밀번호 찾기 - 성공")
    @Test
    @WithMockCustomAccount
    void findPassword_Success() throws Exception {
        // given
        String loginId = "test123";
        String username = "테스터";
        String email = "test123@naver.com";

        FindPasswordReq request = FindPasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .build();

        // when
        BDDMockito.when(userService.findPassword(Mockito.any(FindServicePasswordReq.class)))
                .thenReturn("이메일 인증코드 발송에 성공했습니다.");

        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/find/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("이메일 인증코드 발송에 성공했습니다."));
    }

    @DisplayName("비밀번호 찾기 시 로그인Id regex 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void findPassword_Failure_Regex_LoginId() throws Exception {
        // given
        String loginId = "";
        String username = "테스터";
        String email = "test123@naver.com";

        FindPasswordReq request = FindPasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .build();

        // when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/find/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("id는 소문자 하나이상있어야하고, 6자~20자여야합니다."));
    }

    @DisplayName("비밀번호 찾기시 유저네임 빈 문자열 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void findPassword_Failure_EmptyUsername() throws Exception {
        // given
        String loginId = "test123";
        String username = "";
        String email = "test123@naver.com";

        FindPasswordReq request = FindPasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .build();

        // when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/find/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 이름을 적어주세요."));
    }

    @DisplayName("아이디 찾기시 이메일 regex 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void findPassword_Failure_RegexEmail() throws Exception {
        // given
        String loginId = "test123";
        String username = "테스터";
        String email = "test123123";

        FindPasswordReq request = FindPasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .build();

        // when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/find/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이메일 형식이 맞지 않습니다."));
    }

    @DisplayName("비밀번호 찾기 검증 - 성공")
    @Test
    @WithMockCustomAccount
    void verifyPassword_Success() throws Exception {
        // given
        String loginId = "test123";
        String username = "테스터";
        String email = "test123@naver.com";
        String authCode = "test123";

        VerifyPasswordReq request = VerifyPasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        //when
        String response = "비밀번호 검증 성공";

        BDDMockito.when(userService.verifyPassword(Mockito.any(VerifyServicePasswordReq.class)))
                .thenReturn(response);

        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/verify/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(response));
    }

    @DisplayName("비밀번호 찾기 검증 시 유저네임 빈문자열 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void verifyPassword_Failure_NotBlank_Username() throws Exception {
        // given
        String loginId = "test123";
        String username = "";
        String email = "test123@naver.com";
        String authCode = "test123";

        VerifyPasswordReq request = VerifyPasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/verify/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 이름을 적어주세요."));
    }

    @DisplayName("비밀번호 찾기 검증 시 인증번호 7글자가 아닐때 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void verifyPassword_Failure_NotLengthSeven_Username() throws Exception {
        // given
        String loginId = "test123";
        String username = "테스터";
        String email = "test123@naver.com";
        String authCode = "test1234";

        VerifyPasswordReq request = VerifyPasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/verify/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("인증코드는 7자리입니다."));
    }

    @DisplayName("아이디 찾기 검증 시 이메일 형식이 맞지 않을때 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void verifyPassword_Failure_RegexEmail() throws Exception {
        // given
        String loginId = "test123";
        String username = "테스터";
        String email = "test123";
        String authCode = "test123";

        VerifyPasswordReq request = VerifyPasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/verify/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이메일 형식이 맞지 않습니다."));
    }

    @DisplayName("비밀번호 찾기 검증 시 로그인Id가 6글자 미만일때 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void verifyPassword_Failure_Less_Than_6Length_LoginId() throws Exception {
        // given
        String loginId = "test1";
        String username = "테스터";
        String email = "test123@naver.com";
        String authCode = "test123";

        VerifyPasswordReq request = VerifyPasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/verify/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("id는 소문자 하나이상있어야하고, 6자~20자여야합니다."));
    }

    @DisplayName("비밀번호 찾기 검증 시 로그인Id가 21글자 초과일때 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void verifyPassword_Failure_Exceeding_20Length_LoginId() throws Exception {
        // given
        String loginId = "test12345678910111213";
        String username = "테스터";
        String email = "test123@naver.com";
        String authCode = "test123";

        VerifyPasswordReq request = VerifyPasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/verify/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("id는 소문자 하나이상있어야하고, 6자~20자여야합니다."));
    }

    @DisplayName("비밀번호 찾기 검증 시 로그인Id가 소문자가 하나도 없을때 예외처리 - 실패")
    @Test
    @WithMockCustomAccount
    void verifyPassword_Failure_NotExist_Low_Character() throws Exception {
        // given
        String loginId = "123456";
        String username = "테스터";
        String email = "test123@naver.com";
        String authCode = "test123";

        VerifyPasswordReq request = VerifyPasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        //when
        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/users/verify/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("id는 소문자 하나이상있어야하고, 6자~20자여야합니다."));
    }
}