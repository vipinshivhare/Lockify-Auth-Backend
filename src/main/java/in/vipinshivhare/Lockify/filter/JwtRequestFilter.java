package in.vipinshivhare.Lockify.filter;

import in.vipinshivhare.Lockify.service.AppUserDetailsService;
import in.vipinshivhare.Lockify.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_URLS = List.of(
            "/",               // root
            "/health",         // health
            "/login",
            "/register",
            "/send-reset-otp",
            "/reset-password",
            "/logout"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Normalize path to be independent of context-path and allow OPTIONS preflight
        String requestUri = request.getRequestURI(); // e.g. /api/v1.0/register or /register
        String contextPath = request.getContextPath(); // e.g. /api/v1.0
        String rawPath = requestUri;
        if (contextPath != null && !contextPath.isEmpty() && requestUri.startsWith(contextPath)) {
            rawPath = requestUri.substring(contextPath.length()); // -> /register
        }
        final String normalizedPath = rawPath;

        // Always allow CORS preflight requests to pass through
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Allow public endpoints (match exact or suffix to be safe)
        boolean isPublic = PUBLIC_URLS.contains(normalizedPath) ||
                PUBLIC_URLS.stream().anyMatch(pub -> normalizedPath.endsWith(pub));

        if(isPublic){
            filterChain.doFilter(request,response);
            return;
        }

        String jwt = null;
        String email = null;

        //1. check the authorization header
        final String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            jwt = authorizationHeader.substring(7);
        }
        // 3. we need to validate the token and set the security context

        if(jwt != null){
            email = jwtUtil.extractEmail(jwt);
            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);
                if(jwtUtil.validateToken(jwt, userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}








