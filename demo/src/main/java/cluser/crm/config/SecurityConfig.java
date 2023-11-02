package cluser.crm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * @Description: Security Configuration Class
 */
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableWebSecurity
public class    SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${apiUrl}")
    private String apiUrl;

    @Autowired
    public SecurityConfig(AuthenticationSuccessHandler authenticationSuccessHandler) {
        /**
         * Used for custom redirect after login
         */
    }


    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    /**
     * @Description: Security path configuration
     * @param http -
     * @throws Exception -
     */
    @Override

    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                //Tickets
                .antMatchers("/files/users/{mainId}/clients/{subUserId}/**")
                .access("(hasAnyAuthority('ROLE_super_admin','ROLE_admin','ROLE_TicketsFull','ROLE_TicketsView','ROLE_demo_user') or @userFolderSecurity.isOwnId(authentication,#subUserId) and @userFolderSecurity.isMainId(authentication,#mainId))")

                //Procedure
                .antMatchers("/files/users/{mainId}/procedures/**")
                .access("hasAnyAuthority('ROLE_admin','ROLE_ProceduresFull','ROLE_ProceduresView') and @userFolderSecurity.isMainId(authentication,#mainId) or hasAnyAuthority('ROLE_super_admin','ROLE_demo_user') or  @userFolderSecurity.isOwnId(authentication,#subUserId)")

                //Directive
                .antMatchers("/files/users/{mainId}/directives/**")
                .access("hasAnyAuthority('ROLE_admin','ROLE_DirectivesFull','ROLE_DirectivesView') and @userFolderSecurity.isMainId(authentication,#mainId) or hasAnyAuthority('ROLE_super_admin','ROLE_demo_user') or  @userFolderSecurity.isOwnId(authentication,#subUserId)")

                //Project
                .antMatchers("/files/users/{mainId}/projects/**")
                .access("hasAnyAuthority('ROLE_admin','ROLE_ProjectsFull','ROLE_ProjectsView') and @userFolderSecurity.isMainId(authentication,#mainId) or hasAnyAuthority('ROLE_super_admin','ROLE_demo_user') or  @userFolderSecurity.isOwnId(authentication,#subUserId)")

                //LeaveRequest
                .antMatchers("/files/users/{mainId}/subUsers/{subUserId}/leaveRequests/**")
                .access("(hasAnyAuthority('ROLE_admin','ROLE_LeaveRequestFull') or @userFolderSecurity.isOwnId(authentication,#subUserId)) and @userFolderSecurity.isMainId(authentication,#mainId) or hasAnyAuthority('ROLE_super_admin','ROLE_demo_user') or  @userFolderSecurity.isOwnId(authentication,#subUserId)")

                //Contact
                .antMatchers("/files/users/{mainId}/contacts/**")
                .access("hasAnyAuthority('ROLE_admin','ROLE_ContactsFull','ROLE_ContractsView') and @userFolderSecurity.isMainId(authentication,#mainId) or hasAnyAuthority('ROLE_super_admin','ROLE_demo_user') or  @userFolderSecurity.isOwnId(authentication,#subUserId)")

                //TaskERP
                .antMatchers("/files/users/{mainId}/subUsers/{subUserId}/tasksERP/{taskERPId}/**")
                .access("hasAnyAuthority('ROLE_super_admin','ROLE_admin','ROLE_ERPTasksFull','ROLE_ERPTasksView','ROLE_demo_user') or @userFolderSecurity.isTaskERP(authentication,#taskERPId)")

                .antMatchers("/**").permitAll()
                .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and().logout().permitAll().logoutUrl("/logout")
                .and().logout().logoutSuccessUrl(apiUrl + "/auth/login")
                .and()
                .formLogin().disable()
                .httpBasic()
                .authenticationEntryPoint(new NoPopupBasicAuthenticationEntryPoint());

    }

    /**
     * Password encoder needed for inMemoryAuthentication (Spring Security requires the password to be encoded) .
     * Can be deleted after inMemoryAuthentication is no longer used (most likely)
     *
     * @return BCrypt encoder
     * @Deletable
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}