//package project.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.stereotype.Component;
//import ro.thecon.upsell.models.User;
//import ro.thecon.upsell.models.dto.GoogleLoginDTO;
//import ro.thecon.upsell.models.dto.RegisterUserDTO;
//import ro.thecon.upsell.repositories.UserRepository;
//import ro.thecon.upsell.services.UserService;
//import ro.thecon.upsell.util.google.GoogleService;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.ArrayList;
//import java.util.List;
//@Component
//public class CustomAuthenticationProvider extends DaoAuthenticationProvider {
//
//
//    private final UserService userService;
//
//    @Autowired
//    private SecurityConfig securityConfig;
//
//    @Autowired
//    public CustomAuthenticationProvider(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    GoogleService googleService;
//
//    /**
//     * @Description: Used to authenticate the user
//     * @Details: If the user has no roles , the role 'user' is added to the user
//     * #Suggestions Instead of auto-wiring the whole securityConfig , try adding the bean here directly*
//     * @param auth-
//     * @throws AuthenticationException -
//     * @throws NullPointerException -
//     * @return Authentication
//     */
//    public Authentication authenticate(Authentication auth) throws AuthenticationException, NullPointerException {
//        User user = userRepository.getByEmailIgnoreCase(auth.getName());
//        List<GrantedAuthority> authorities = new ArrayList<>();
//
//        //Checks authentication's password compared to the user's password (in the database)
//        if (securityConfig.passwordEncoder().matches(auth.getCredentials().toString(), user.getPassword())) {
//            if (user.getRoles() != null) {
//                for (UserRoleEnum role : user.getRoles()) {
//                    //Here we add authorities from the user's "user_roles"
//                    authorities.add(new SimpleGrantedAuthority(role.toString()));
//                }
//            } else {
//                authorities.add(new SimpleGrantedAuthority("ROLE_user"));
//            }
//            return new UsernamePasswordAuthenticationToken(user.getEmail(),
//                    auth.getCredentials(), authorities);
//        }
//
//        //In case authentication fails
//        throw new BadCredentialsException("Bad Credentials");
//    }
//
//
//    //todo : Mostly finished (but not completely)
//    public Authentication authenticateGoogle(Authentication auth, GoogleLoginDTO googleLoginDTO) throws AuthenticationException, NullPointerException, GeneralSecurityException, IOException {
//        List<GrantedAuthority> authorities = new ArrayList<>();
//
//        String email = googleService.getInfo(googleLoginDTO.getGoogleToken());
//
//        if (email.equals("false")) {
//            throw new BadCredentialsException("Bad Credentials");
//        } else {
//            if (email.equals(googleLoginDTO.getEmail())) {
//                User user = userRepository.getByEmail(email);
//                if (user == null) {
//                    RegisterUserDTO userX = new RegisterUserDTO();
//                    userX.setEmail(email);
//                    userX.setFirstName("");
//                    userX.setLastName("");
//                    userX.setPassword(null);
//                    userX.setPersonalPhone("");
//
//                    userService.registerUserWithGoogle(userX);
//                    return new UsernamePasswordAuthenticationToken(email, auth.getCredentials(), authorities);
//                } else {
//                    return new UsernamePasswordAuthenticationToken(googleLoginDTO.getEmail(), auth.getCredentials(),
//                            authorities);
//                }
//            } else {
//                throw new BadCredentialsException("Bad Credentials");
//            }
//        }
//    }
//
//    @PostConstruct
//    public void postConstruct() {
//        setUserDetailsService(this.userService);
//    }
//
//}
