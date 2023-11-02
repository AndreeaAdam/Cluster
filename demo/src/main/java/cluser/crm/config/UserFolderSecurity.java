//package project.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//import ro.thecon.upsell.models.TaskERP;
//import ro.thecon.upsell.models.User;
//import ro.thecon.upsell.repositories.TaskERPRepository;
//import ro.thecon.upsell.repositories.UserRepository;
//
//import java.util.Optional;
//
//@Component("userFolderSecurity")
//public class UserFolderSecurity {
//
//    @Autowired
//    UserRepository userRepository;
//    @Autowired
//    private TaskERPRepository taskERPRepository;
//
//    //for mainId
//    public boolean isMainId(Authentication authentication, String mainId) {
//        if (userRepository.getByEmail(authentication.getName()) != null) {
//            User user = userRepository.getByEmail(authentication.getName());
//            return user.getMainId().equals(mainId);
//        } else return false;
//    }
//
//    //for ownId
//    public boolean isOwnId(Authentication authentication, String subUserId) {
//        if (userRepository.getByEmail(authentication.getName()) != null) {
//            User user = userRepository.getByEmail(authentication.getName());
//            return user.getId().equals(subUserId);
//        } else return false;
//    }
//
//    public boolean isTaskERP(Authentication authentication, String taskERPId) {
//        User loggedInUser = userRepository.getByEmail(authentication.getName());
//        Optional<TaskERP> taskERP = taskERPRepository.findById(taskERPId);
//        return taskERP.get().getSenderUserId().equals(loggedInUser.getId())
//                || taskERP.get().getReceiverUserId().equals(loggedInUser.getId());
//    }
//}
