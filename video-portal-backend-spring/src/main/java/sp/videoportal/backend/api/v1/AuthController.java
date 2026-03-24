package sp.videoportal.backend.api.v1;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sp.videoportal.backend.dto.CredentialsDto;
import sp.videoportal.backend.dto.UserDto;
import sp.videoportal.backend.service.UserAuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final String SESSION_USER_ID = "userId";

    private final UserAuthService authService;

    public AuthController(UserAuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        // Calling this endpoint forces token generation and sets the XSRF-TOKEN cookie
        return Map.of("token", token.getToken());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody CredentialsDto credentials, HttpSession session) {
        Optional<UserDto> user = authService.login(credentials);
        if (user.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        session.setAttribute(SESSION_USER_ID, user.get().getId());
        return ResponseEntity.ok(user.get());
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        Object rawUserId = session.getAttribute(SESSION_USER_ID);
        if (!(rawUserId instanceof Long userId)) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        return authService
                .me(userId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).body("Not authenticated"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CredentialsDto credentials) {
        return authService
                .register(credentials)
                .<ResponseEntity<?>>map(created -> ResponseEntity.status(201).body(created))
                .orElseGet(() -> ResponseEntity.status(409).body("Username already taken"));
    }
}
