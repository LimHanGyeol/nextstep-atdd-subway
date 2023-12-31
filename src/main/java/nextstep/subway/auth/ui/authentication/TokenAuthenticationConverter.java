package nextstep.subway.auth.ui.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.subway.auth.domain.AuthenticationToken;
import nextstep.subway.auth.dto.TokenRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class TokenAuthenticationConverter implements AuthenticationConverter {

    private final ObjectMapper objectMapper;

    public TokenAuthenticationConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public AuthenticationToken convert(HttpServletRequest request) {
        try {
            TokenRequest tokenRequest = objectMapper.readValue(request.getInputStream(), TokenRequest.class);
            String principal = tokenRequest.getEmail();
            String credentials = tokenRequest.getPassword();
            return new AuthenticationToken(principal, credentials);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
