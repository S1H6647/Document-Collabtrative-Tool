package com.project.realtimedoccollab.domain.websocket;

import com.project.realtimedoccollab.auth.jwt.JwtUtil;
import com.project.realtimedoccollab.auth.user.UserPrincipal;
import com.project.realtimedoccollab.user.User;
import com.project.realtimedoccollab.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        log.debug("WebSocket handshake attempt from {}", request.getRemoteAddress());

        // localhost:8080/ws?token=eyh...
        // Step 1: Get the raw URI and extract query string
        URI uri = request.getURI();
        String query = uri.getQuery();

        if (query == null || !query.contains("token=")) {
            log.warn("WebSocket handshake rejected - no token in query params");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        // Step 2: Parse the token value from query string
        String token = extractToken(query);

        if (token == null || token.isBlank()) {
            log.warn("WebSocket handshake rejected - token is blank");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        // Step 3: Validate the JWT and extract the username (email)
        try {
            String email = jwtUtil.extractEmail(token);

            if (email == null) {
                log.warn("WebSocket handshake rejected - could not extract email from the token");
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }

            // Step 4: Load the user to verify they still exist and token is valid
            User user = userRepository.findUserByEmail(email)
                    .orElse(null);

            if (user == null) {
                log.warn("WebSocket handshake rejected - user not found for email: {}", email);
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }

            UserPrincipal principal = UserPrincipal.from(user);

            if (!jwtUtil.validateToken(token, principal)) {
                log.warn("WebSocket handshake rejected — token invalid or expired for: {}", email);
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }

            // Step 5: Store identity in session attributes — this travels with the connection
            attributes.put("userId", user.getId());
            attributes.put("email", user.getEmail());

            log.debug("WebSocket handshake accepted for user: {} (id={})", email, user.getId());
            return true; // Connection opens
        } catch (Exception e) {
            log.error("WebSocket handshake rejected — token validation threw exception", e);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            @Nullable Exception exception) {

        // Nothing to do after handshake
        // This method exists in the interface — must be overridden but can be empty
    }

    // Helper
    private String extractToken(String query) {
        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                return param.substring("token=".length());
            }
        }
        return null;
    }

    /* Note:
     * First, the attributes map is per-session — each connected client has their own isolated map,
     * so storing userId there is safe even with thousands of concurrent connections.
     * Second, you're doing a userRepository call at handshake time — this is intentional and correct.
     * It's a one-time cost per connection, not per message, so it's not a performance concern.
     */
}
