package cluser.crm.services;

import cluser.crm.config.ResponseEnum;
import cluser.crm.config.SecurityConfig;
import cluser.crm.config.UserRoleEnum;
import cluser.crm.dtos.PasswordResetDTO;
import cluser.crm.dtos.RegisterClientDTO;
import cluser.crm.dtos.RegisterUserDTO;
import cluser.crm.models.DocNumber;
import cluser.crm.models.PasswordResetCode;
import cluser.crm.models.RegActivationCode;
import cluser.crm.models.User;
import cluser.crm.repositories.DocNumberRepository;
import cluser.crm.repositories.PasswordResetCodeRepository;
import cluser.crm.repositories.RegActivationCodeRepository;
import cluser.crm.repositories.UserRepository;
import cluser.crm.util.RandomToken;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SecurityConfig securityConfig;
    @Autowired
    private MailService mailService;
    @Autowired
    private RegActivationCodeRepository regActivationCodeRepository;
    @Autowired
    private UserSettingService userSettingService;
    @Autowired
    private RandomToken randomToken;
    @Autowired
    private FileService fileService;
    @Autowired
    private PasswordResetCodeRepository passwordResetCodeRepository;
    @Autowired
    private DocNumberRepository docNumberRepository;

    @Value("${apiUrl}")
    private String apiUrl;
    @Value("${apiUrlPort}")
    private String apiUrlPort;
    @Value("${server.servlet.contextPath}")
    private String contextPath;

    /**
     * @param user - the actual user being registered
     * @return String
     * @Description: Register user
     */
    public String registerUser(RegisterUserDTO user) {
        if (userRepository.getByEmailIgnoreCase(user.getEmail()) != null) {
            return "emailExists";
        }
        User user1 = new User();
        user1.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));

        user1.setSubUser(false);
        setUserInfo(user, user1);
        if (isValidEmailAddress(user1.getEmail())) {
            return "emailNotValid";
        }

        ArrayList<UserRoleEnum> userAuthorities = new ArrayList<>();
        userAuthorities.add(UserRoleEnum.ROLE_admin);
        user1.setRoles(userAuthorities);

        userRepository.save(user1);
        user1.setMainId(user1.getId());
        DocNumber docNumber = new DocNumber();
        docNumber.setId(null);
        docNumber.setContractNumber(1);
        docNumber.setOfferNumber(1);
        docNumber.setProjectNumber(1);
        docNumber.setMainId(user1.getMainId());
        docNumberRepository.save(docNumber);
        userRepository.save(user1);
        userSettingService.createDefaultUserSettings(user1.getId());

//        categoryService.createFirstCategory(user1.getId());
        fileService.createFolder("users", user1.getId());
        return user1.getId();
    }
    /**
     * @param user    - the actual subUser being registered
     * @param request - used to find the current user
     * @return String
     * @Description: Register subUser for currentUser
     */
    public String registerSubUser(RegisterUserDTO user, HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            Optional<User> currentUser = userRepository.findById((request.getUserPrincipal().getName()));
            if (currentUser.isEmpty() || currentUser.get().isSubUser()) {
                return "accessDenied";
            }
            if (userRepository.getByEmailIgnoreCase(user.getEmail()) != null) {
                return "emailExists";
            }
            User user1 = new User();
            user1.setCompanyDetails(currentUser.get().getCompanyDetails());
            user1.setMainId(currentUser.get().getId());
            setUserInfo(user, user1);
            if (isValidEmailAddress(user1.getEmail())) {
                return "emailNotValid";
            }
            user1.setSubUser(true);

            ArrayList<UserRoleEnum> userAuthorities = new ArrayList<>();
            userAuthorities.add(UserRoleEnum.ROLE_user);

            user1.setRoles(userAuthorities);
            userRepository.save(user1);
//            notificationService.createNotification(request, "message", user1.getId(), user1.getId(), "user", "user");
            fileService.createFolder("users", user1.getId());
//            historyService.addHistory(request, "create", LocalDateTime.now(), "user", "registerSubUser", "source_userId created a new user", "user", user1.getId());

            return user1.getId();
        } else {
            return ResponseEnum.notLoggedIn.toString();
        }
    }
    /**
     * @param user    - the actual client being registered
     * @return String
     * @Description: Register client for currentUser
     */
    public String registerClient(RegisterClientDTO user) {
        User client1 = new User();
        if (userRepository.getByEmailIgnoreCase(user.getEmail()) != null) {
            return "emailExists";
        }
        client1.setEmail(user.getEmail());
        client1.setCity(user.getCity());
        client1.setCompanyDetails(user.getCompanyDetails());
        client1.setCountry(user.getCountry());
        client1.setCounty(user.getCounty());
        client1.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
        client1.setFirstName(user.getFirstName());
        client1.setLastName(user.getLastName());
        client1.setPersonalPhone(user.getPersonalPhone());
        client1.setAdmin(false);
        client1.setActive(false);
        client1.setSubUser(false);

        client1.setProfileImagePath("defaultProfileImage.png");
        if (isValidEmailAddress(client1.getEmail())) {
            return "emailNotValid";
        }
        client1.setRegisterDate(LocalDateTime.now());
        ArrayList<UserRoleEnum> userAuthorities = new ArrayList<>();
        userAuthorities.add(UserRoleEnum.ROLE_client);
        client1.setRoles(userAuthorities);

        userRepository.save(client1);
        client1.setMainId(client1.getId());
        userRepository.save(client1);
        userSettingService.createDefaultUserSettings(client1.getId());
        fileService.createFolder("users",client1.getId());
        return client1.getId();
    }
    public void setUserInfo(RegisterUserDTO dto, User user){
        user.setEmail(dto.getEmail());
        user.setCity(dto.getCity());
        user.setCountry(dto.getCountry());
        user.setCounty(dto.getCounty());
        user.setPassword(securityConfig.passwordEncoder().encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPersonalPhone(dto.getPersonalPhone());
        user.setAdmin(false);
        user.setActive(false);
        user.setProfileImagePath("defaultProfileImage.png");
        user.setRegisterDate(LocalDateTime.now());
    }
    public boolean sendRegistrationEmail(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {

            try {
                String registrationCode = randomToken.generateRandomCode();
                String url = apiUrlPort + contextPath + "/activateUser?registrationCode=" + registrationCode;
                sendActivateAccountMessage(user.get(), url, registrationCode, user.get().getFirstName());
            } catch (Exception e) {
                e.printStackTrace();
                userRepository.deleteById(user.get().getId());
                return false;
            }
            return true;
        }
        return false;
    }
    public void sendActivateAccountMessage(User user, String url, String registrationCode, String name) {

        String message = "<tbody><tr><td valign=\"top\" id=\"m_-9194262119173991429body_content\" style=\"background-color:#ffffff\">" +
                "<table border=\"0\" cellpadding=\"20\" cellspacing=\"0\" width=\"100%\"><tbody><tr><td valign=\"top\" style=\"padding:48px 48px 32px\">" +
                "<div id=\"m_-9194262119173991429body_content_inner\" style=\"color:#636363;font-family:&quot;Helvetica Neue&quot;,Helvetica,Roboto,Arial,sans-serif;font-size:14px;line-height:150%;text-align:left\">" +
                "<p style=\"margin:0 0 16px\"> Bună, " + name + "</p>" +
                "<p style=\"margin:0 0 16px\">Ai primit acest email deoarece adresa ta de email a fost folosită  pentru a te înregistra pe platforma <span class=\"il\">Cluster CRM</span>. Te rugăm să accesezi următorul link pentru a confirma intenția ta: <br><br><a href= " + url + " target=\"_blank\"  style = \" border: none; background-color: #0076FF; cursor:pointer; color: white;margin: 6px 2px; border-radius: 12px;padding: 10px\"> Activează cont </a><br><br> Dacă nu accesezi acest link în 7 zile, codul va expira." +
                "<p style=\"margin:0 0 16px\">Pentru orice întrebare, nu ezita să ne contactezi - suntem mereu bucuroși să ajutăm.</p>" +
                "<p style=\"margin:0 0 16px\">Mulțumim,</p>" +
                "<p style=\"margin:0 0 16px\">Echipa Cluster CRM!</p></div></td></tr></tbody><tbody><tr>" +
                "<td valign=\"top\" style=\"padding:48px 48px 32px\">" +
                "<div id=\"m_-9194262119173991429body_content_inner\" style=\"color:#636363;font-family:&quot;Helvetica Neue&quot;,Helvetica,Roboto,Arial,sans-serif;font-size:14px;line-height:150%;text-align:left\">" +
                "<p style=\"margin:0 0 16px\"> Hi, " + name + "</p>" +
                "<p style=\"margin:0 0 16px\">You're receiving this email because your email address was used to register on <span class=\"il\">Cluster CRM</span>.Please press this button to confirm your intention: <br><br><a href= " + url + " target=\"_blank\" style = \" border: none;background-color: #0076FF; cursor:pointer; color: white;margin: 6px 2px; border-radius: 12px;padding: 10px\"> Activate account</a> <br><br> If you don’t use this link within 7 days, it will expire." +
                "<p style=\"margin:0 0 16px\">For any questions, please email us – we’re always happy to help.</p>" +
                "<p style=\"margin:0 0 16px\"> Kind Regards,</p>" +
                "<p style=\"margin:0 0 16px\">Cluster CRM Team!</p></div></td></tr></tbody></table></td></tr></tbody></table></td></tr>" +
                "</tbody>";
        createRegistrationCode(user.getEmail(), user.getId(), registrationCode);
        mailService.sendMailSMTPGeneric(user.getEmail(), "Registration mail!", message);
    }

    /**
     * @param userEmail        - string used to find the userEmail
     * @param userId           - id used to find the user
     * @param registrationCode - string used to find the registrationCode
     * @Description: Creates a registration code for the user
     * @Details: The code has an expiry date set at current date + 1 week
     */
    public void createRegistrationCode(String userEmail, String userId, String registrationCode) {
        RegActivationCode code = new RegActivationCode();
        code.setEmail(userEmail);
        code.setUserId(userId);
        code.setRegistrationCode(registrationCode);
        code.setCreationDate(LocalDateTime.now());
        code.setExpiryDate(LocalDateTime.now().plusDays(7));
        regActivationCodeRepository.save(code);
    }
    public boolean isValidEmailAddress(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

    public boolean activateUser(String registrationCode) {
        return activateAccount(registrationCode);
    }
    public boolean activateAccount(String registrationCode) {
        RegActivationCode code = regActivationCodeRepository.getByRegistrationCode(registrationCode);
        if (code != null && code.getExpiryDate().isAfter(LocalDateTime.now())) {
            User user = userRepository.getByEmailIgnoreCase(code.getEmail());
            user.setActive(true);
            userRepository.save(user);
            regActivationCodeRepository.deleteById(code.getId());
            return true;
        }
        return false;
    }
    /**
     * @param email - used to find the user
     * @return string
     * @Description: Resend a new registration code if currentUser is not active and first registration code is expired
     * @Description: Resend a new registration code if currentUser is not active and first registration code is expired
     */
    public String resendRegistrationCode(String email) {
        User currentUser = userRepository.getByEmailIgnoreCase(email);
        if (currentUser == null) {
            return "invalidEmail";
        }

        if (currentUser.isActive()) {
            return "alreadyActive";
        }
        List<RegActivationCode> codes = regActivationCodeRepository.getAllByUserId(currentUser.getId());

        /**
         * Deletes the expired codes
         * @Improvable
         */
        ArrayList<RegActivationCode> codesToBeRemoved = new ArrayList<>();
        codes.forEach(regActivationCode -> {
            if (regActivationCode.getExpiryDate().isBefore(LocalDateTime.now())) {
                codesToBeRemoved.add(regActivationCode);
            }
        });
        codesToBeRemoved.forEach(regActivationCode -> regActivationCodeRepository.delete(regActivationCode));

        String registrationCode = randomToken.generateRandomCode();
        if (codes.size() < 2) {
            try {
                String url = apiUrl +"" + contextPath + "/activateUser?registrationCode=" + registrationCode;
                String message = "<tbody><tr><td valign=\"top\" id=\"m_-9194262119173991429body_content\" style=\"background-color:#ffffff\">" +
                        "<table border=\"0\" cellpadding=\"20\" cellspacing=\"0\" width=\"100%\"><tbody><tr><td valign=\"top\" style=\"padding:48px 48px 32px\">" +
                        "<div id=\"m_-9194262119173991429body_content_inner\" style=\"color:#636363;font-family:&quot;Helvetica Neue&quot;,Helvetica,Roboto,Arial,sans-serif;font-size:14px;line-height:150%;text-align:left\">" +
                        "<p style=\"margin:0 0 16px\"> Am auzit că ți-ai pierdut codul de înregistrare. Ne pare rău!</p>" +
                        "<p style=\"margin:0 0 16px\">Dar nu-ți face griji! Poți accesa următorul link pentru a primi unul nou: <br><br><a href= " + url + " target=\"_blank\" style = \" border: none; background-color: #0076FF; cursor:pointer; color: white;margin: 4px 2px; border-radius: 12px;padding: 10px\"> Activează cont </a><br><br> Dacă nu accesezi acest link în 7 zile, codul va expira." +
                        "<p style=\"margin:0 0 16px\">Mulțumim,</p>" +
                        "<p style=\"margin:0 0 16px\">Echipa Cluster CRM!</p></div></td></tr></tbody><tbody><tr>" +
                        "<td valign=\"top\" style=\"padding:48px 48px 32px\">" +
                        "<div id=\"m_-9194262119173991429body_content_inner\" style=\"color:#636363;font-family:&quot;Helvetica Neue&quot;,Helvetica,Roboto,Arial,sans-serif;font-size:14px;line-height:150%;text-align:left\">" +
                        "<p style=\"margin:0 0 16px\"> We heard that you lost your registration code. Sorry about that!</p>" +
                        "<p style=\"margin:0 0 16px\">But don’t worry! You can use the following link to get a new one: <br><br><a href= " + url + " target=\"_blank\" style = \" border: none;background-color: #0076FF; cursor:pointer; color: white;margin: 4px 2px; border-radius: 12px;padding: 10px\"> Activate account </a> <br><br>If you don’t use this link within 7 days, it will expire." +
                        "<p style=\"margin:0 0 16px\"> Kind Regards,</p>" +
                        "<p style=\"margin:0 0 16px\">Cluster CRM Team!</p></div></td></tr></tbody></table></td></tr></tbody></table></td></tr>" +
                        "</tbody>";
                createRegistrationCode(email, currentUser.getId(), registrationCode);
                mailService.sendMailSMTPGeneric(email, "Registration mail!", message);
                return ResponseEnum.success.toString();
            } catch (Exception e) {
                regActivationCodeRepository.delete(regActivationCodeRepository.getByRegistrationCode(registrationCode));
                e.printStackTrace();
                return "mailFail";
            }
        }
        return "moreThan2ActiveCodes";
    }
    /**
     * @param email - the email used to send the resetCode
     * @return String
     * @Description: Sends a password reset code to a user based on the email
     */
    public String sendResetUserPassword(String email) {
        String resetCode = randomToken.generateRandomCode();
        User user = userRepository.getByEmailIgnoreCase(email);

        if (user == null) {
            return "emailNotFound";
        }
        PasswordResetCode code = new PasswordResetCode();
        code.setUserId(user.getId());
        code.setEmail(email);
        code.setPasswordResetCode(resetCode);
        code.setCreationDate(LocalDateTime.now());
        code.setExpiryDate(LocalDateTime.now().plusDays(7));
        code.setActive(true);
        boolean result;
        try {
            String message = "<tbody><tr><td valign=\"top\" id=\"m_-9194262119173991429body_content\" style=\"background-color:#ffffff\">" +
                    "<table border=\"0\" cellpadding=\"20\" cellspacing=\"0\" width=\"100%\"><tbody><tr><td valign=\"top\" style=\"padding:48px 48px 32px\">" +
                    "<div id=\"m_-9194262119173991429body_content_inner\" style=\"color:#636363;font-family:&quot;Helvetica Neue&quot;,Helvetica,Roboto,Arial,sans-serif;font-size:14px;line-height:150%;text-align:left\">" +
                    "<p style=\"margin:0 0 16px\">Am primit o cerere de resetare a parolei pentru contul cu adresa de e-mail: </p>" + email +
                    "<p style=\"margin:0 0 16px\">Poți folosi următorul cod pentru a reseta parola:  <br><strong>" + resetCode + "</strong>" +
                    "<p style=\"margin:0 0 16px\">Dacă nu vrei sa resetezi parola poți ignora acest mesaj.</p>" +
                    "<td valign=\"top\" style=\"padding:48px 48px 32px\">" +
                    "<div id=\"m_-9194262119173991429body_content_inner\" style=\"color:#636363;font-family:&quot;Helvetica Neue&quot;,Helvetica,Roboto,Arial,sans-serif;font-size:14px;line-height:150%;text-align:left\">" +
                    "<p style=\"margin:0 0 16px\"> We received a password reset request for the email account</p>" +
                    "<p style=\"margin:0 0 16px\"> You can use this code to reset your password:  <br><strong>" + resetCode + "</strong>" +
                    "<p style=\"margin:0 0 16px\"> If you do not want to reset your password, you can ignore this message.</p></div></td></tr></tbody></table></td></tr></tbody></table></td></tr>" +
                    "</tbody>";
            result = mailService.sendMailSMTPGeneric(email, "Codul de resetare al parolei ", message);
        } catch (Exception e) {
            return "sendEmailFail";
        }
        if (result) {
            passwordResetCodeRepository.save(code);
            return ResponseEnum.success.toString();
        } else
            return "fail";
    }


    /**
     * @param request     - used to find out the current user
     * @param oldPassword -
     * @param newPassword - the new password being changed
     * @return String
     * @Description: Changes a user's password ( requires old password)
     */
    public String changePassword(HttpServletRequest request, String oldPassword, String newPassword) {
        if (getCurrentUser(request).equals(ResponseEnum.notLoggedIn.toString())) {
            return ResponseEnum.notLoggedIn.toString();
        }
        User user = (User) getCurrentUser(request);
        if (securityConfig.passwordEncoder().matches(oldPassword, user.getPassword())) {
            user.setPassword(securityConfig.passwordEncoder().encode(newPassword));
        } else {
            return "invalidOldPassword";
        }
        userRepository.save(user);
        return ResponseEnum.success.toString();
    }
    /**
     * @param passwordReset - the actual password being rested
     * @return boolean
     * @Description: Changes the password of a user based on the password reset code
     */
    public String newUserPassword(PasswordResetDTO passwordReset) {
        User user = userRepository.getByEmailIgnoreCase(passwordReset.getUserEmail());
        if (user == null) return ResponseEnum.notExists.toString();
        Optional<PasswordResetCode> code = passwordResetCodeRepository.getByPasswordResetCode(passwordReset.getUserResetCode());
        if (code.isPresent() && code.get().isActive()) {
            user.setPassword(securityConfig.passwordEncoder().encode(passwordReset.getUserPassword()));
            userRepository.save(user);
            passwordResetCodeRepository.delete(code.get());
            return ResponseEnum.success.toString();
        }
        return "invalidCode";
    }


    /**
     * @param request - used to find the current user
     * @return Optional<User>
     * @Description: Returns current user info
     */
    public Object getCurrentUser(HttpServletRequest request) {
        if (request.getUserPrincipal() == null) {
            return ResponseEnum.notLoggedIn.toString();
        } else return userRepository.getByEmailIgnoreCase(request.getUserPrincipal().getName());
    }
    /**
     * @param userId - used to find the user
     * @return Optional<User>
     * @Description: Returns a user by his id
     */
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }


    /**
     * @param request - used to find the currentUser
     * @param userId  - used to find the id
     * @param active  - boolean
     * @return boolean
     * @Description: Changes isActive for an user. Also change this for  all subUsers belonging to the user (also changes the status of previously deactivate users)
     */
    public String changeUserStatus(HttpServletRequest request, String userId, boolean active) {
        if (getCurrentUser(request).equals(ResponseEnum.notLoggedIn.toString())) {
            return ResponseEnum.notLoggedIn.toString();
        }
        User currentUser = (User) getCurrentUser(request);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) return ResponseEnum.invalidId.toString();
        if (user.get().isSubUser() && (!currentUser.getId().equals(user.get().getMainId()) && !currentUser.getRoles().contains(UserRoleEnum.ROLE_super_admin))){
            return ResponseEnum.notAllowed.toString();
        }
        if (!user.get().isSubUser() && !currentUser.getRoles().contains(UserRoleEnum.ROLE_super_admin)) return ResponseEnum.notAllowed.toString();
        if (!user.get().isSubUser()) {
            userRepository.getByMainId(user.get().getMainId()).forEach(user2 -> {
                user2.setActive(active);
                userRepository.save(user2);
            });
        }
        user.get().setActive(active);
        userRepository.save(user.get());
        return ResponseEnum.success.toString();
    }
    /**
     * @param subUserId - used to find the subUser
     * @param password  - new password
     * @param request   - used to find the currentUser
     * @return boolean
     * @Description: Allows a main user to change a subUser's password
     */
    public String changeSubUserPassword(String subUserId, String password, HttpServletRequest request) {
        if (getCurrentUser(request).equals(ResponseEnum.notLoggedIn.toString())) {
            return ResponseEnum.notLoggedIn.toString();
        }
        User currentUser = (User) getCurrentUser(request);
        Optional<User> subUser = userRepository.findById(subUserId);
        if (subUser.isEmpty()) return ResponseEnum.invalidId.toString();

        if (currentUser.getId().equals(subUser.get().getMainId()) || currentUser.getRoles().contains(UserRoleEnum.ROLE_super_admin)) {
            subUser.get().setPassword(securityConfig.passwordEncoder().encode(password));
            userRepository.save(subUser.get());
            return ResponseEnum.success.toString();
        } else return ResponseEnum.notAllowed.toString();
    }
    /**
     * @param request   - used to find the current user
     * @param subUserId - used to find the subUser
     * @param status    - the actual status being changed
     * @return boolean
     * @Description: Allows a main user to change a subUser's employee status
     */
    public String changeSubUserEmployeeStatus(HttpServletRequest request, String subUserId, String status) {
        if (getCurrentUser(request).equals(ResponseEnum.notLoggedIn.toString())) {
            return ResponseEnum.notLoggedIn.toString();
        }
        Optional<User> subUser = userRepository.findById(subUserId);
        if (subUser.isEmpty()) return ResponseEnum.invalidId.toString();
        User currentUser = (User) getCurrentUser(request);
        if (subUser.get().getMainId().equals(currentUser.getId()) || currentUser.getRoles().contains(UserRoleEnum.ROLE_super_admin)) {
            subUser.get().setEmployeeStatus(status);
            userRepository.save(subUser.get());
            return ResponseEnum.success.toString();
        }
        return ResponseEnum.notAllowed.toString();
    }
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
}
