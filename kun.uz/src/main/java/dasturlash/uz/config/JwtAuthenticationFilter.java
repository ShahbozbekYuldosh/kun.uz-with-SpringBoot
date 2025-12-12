package dasturlash.uz.config;

import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.repository.ProfileRepository;
import dasturlash.uz.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return Arrays.stream(SecurityConfig.AUTH_WHITELIST)
                .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            try {
                username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);
                Integer id = jwtUtil.extractUserId(token);

                // DB dan profilni o‘qiymiz
                Optional<ProfileEntity> profileOpt = profileRepository.findByUsernameAndVisibleTrue(username);
                if (profileOpt.isPresent()) {
                    ProfileEntity profile = profileOpt.get();

                    // Request attribute ga qo‘shamiz
                    request.setAttribute("profile", profile);
                    logger.info("Profile set in request: " + profile.getUsername());

                    // Authentication-ga qo‘shamiz
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(profile, null, authorities);
                    authToken.setDetails(id);
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                } else {
                    logger.warn("Profile not found for username: " + username);
                }

            } catch (Exception e) {
                logger.warn("JWT noto‘g‘ri: " + e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }

}
