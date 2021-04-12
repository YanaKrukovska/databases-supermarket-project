package ua.edu.ukma.supermarket.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ua.edu.ukma.supermarket.persistence.service.EmployeeService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    EmployeeService employeeService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/", "/resources/**", "/h2-console/**", "/css/**",
                        "/webjars/**", "/login").permitAll()
                .antMatchers("/сashier").hasRole("CASHIER")
                .antMatchers("/manager", "/customer/basic", "/employee/cashiers",
                        "/category/all", "/employee/contacts", "/product/all/from-category",
                        "/store-products/from-product", "/receipt/detailed",
                        "/receipt/detailed/from-employee", "/add-product", "/add-category",
                        "/add-employee", "/add-receipt", "/add-customer",
                        "/edit-category", "/edit-product", "/edit-store-product", "/edit-employee"
                ).hasRole("MANAGER")
                .antMatchers("/**").hasAnyRole("CASHIER", "MANAGER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .failureForwardUrl("/login-processing")
                .loginProcessingUrl("/login-processing")
                .defaultSuccessUrl("/employee", true)
                .permitAll();

        httpSecurity.headers().frameOptions().disable();
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(employeeService).passwordEncoder(bCryptPasswordEncoder());
    }
}