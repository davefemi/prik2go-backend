package nl.davefemi.prik2go.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.exceptions.ErrorBody;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {
    private final SessionFactory sessionFactory;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer")) {
            String token = header.substring(7);
            try {
                Claims claims = sessionFactory.parseToken(token);
                String user = claims.getSubject();
                String role = claims.get("ROLE", String.class);
                UUID userId =
                        request.getHeader("user") != null
                                ? UUID.fromString(request.getHeader("user"))
                                : null;
                UUID tokenId =
                        request.getHeader("tokenId") != null
                                ? UUID.fromString(request.getHeader("user"))
                                : null;
                if (user != null) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken
                                    (user, null,
                                            List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
            catch (Exception e){
                setResponseError(response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setResponseError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        ErrorBody error = new ErrorBody();
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.setTitle("Token expired");
        error.setMessage("Please re-authenticate");
        response.getWriter().print(new ObjectMapper().writeValueAsString(error));
    }
}
