package com.example.gistcompetitioncnserver.config;

import com.example.gistcompetitioncnserver.config.filter.CustomAuthenticationFilter;
import com.example.gistcompetitioncnserver.config.filter.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration // loading setting info
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    public RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ADMIN > USER");
        return roleHierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/gistps/api/v1/login"); // set login url
        http.csrf().disable(); // need to know

        http.cors().configurationSource(corsConfigurationSource()); // CORS 설정

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // make stateless
        http.authorizeRequests().antMatchers("/gistps/api/v1/login/**", "/gistps/api/v1/user/token/refresh/**","/gistps/api/v1/user/registration/**").permitAll();
        http.authorizeRequests().antMatchers(GET, "/gistps/api/v1/post/**", "/gistps/api/v1/user/confirm/**", "/gistps/api/v1/user/registeration/**").permitAll();
        http.authorizeRequests().antMatchers(POST, "/gistps/api/v1/post").hasAnyAuthority("USER", "ADMIN");
//        http.authorizeRequests().antMatchers( "/gistps/api/v1/**").hasAnyAuthority("ADMIN");
        http.authorizeRequests().anyRequest().authenticated(); // Specify that URLs are allowed by any authenticated user.

        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean  //CORS 설정 빈
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("https://gist-petition-web-i8d9q7xe4-betterit.vercel.app/petitions");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}