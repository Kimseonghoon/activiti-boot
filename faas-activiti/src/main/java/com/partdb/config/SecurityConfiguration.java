package com.partdb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.partdb.security.CustomFailureHandler;
import com.partdb.security.CustomSuccessHandler;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring()
//            .antMatchers(HttpMethod.OPTIONS, "/**")
//            .antMatchers("/app/**/*.{js,html}")
//            .antMatchers("/bower_components/**")
//            .antMatchers("/i18n/**")
//            .antMatchers("/content/**")
//            /*.antMatchers("/swagger-ui/index.html")*/
//            .antMatchers("/test/**");
//    }
    
    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
		http
		//.addFilterBefore(new CORSFilter(), ChannelProcessingFilter.class)
        	.authorizeRequests() 
        	.anyRequest().permitAll()
        	.and()
//        .formLogin()
//        	.loginPage("/login")
//        	.permitAll()        
//        	.and()
        .csrf().disable();
    }*/
	
	@Autowired
	@Qualifier("myUserDetailsService")
	UserDetailsService userDetailsService;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		//auth.inMemoryAuthentication().withUser("test").password("1234").roles("ADMIN");
		//auth.inMemoryAuthentication().withUser("user").password("1234").roles("USER");
	}
	
	@Autowired
	CustomFailureHandler authenticationFailureHandler;
	
	@Autowired
	CustomSuccessHandler successHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    http
	    	.authorizeRequests()
	    	.antMatchers("/admin/**")
	    	.access("hasRole('ROLE_ADMIN')")
		.and()
			.formLogin().loginPage("/login")
			.failureUrl("/login?error")
			//.failureHandler(authenticationFailureHandler)
			.successHandler(successHandler)
			.usernameParameter("email")
			.passwordParameter("password")
			.and()
			.logout().logoutSuccessUrl("/login?logout")
		.and()
			.exceptionHandling().accessDeniedPage("/login")
	    .and()
			.csrf();
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
}
