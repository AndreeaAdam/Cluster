package cluser.crm.services;

import cluser.crm.models.UserSetting;
import cluser.crm.repositories.UserRepository;
import cluser.crm.repositories.UserSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Description: Settings for the mainId
 */
@Service
public class UserSettingService {
    
    @Autowired private UserSettingRepository userSettingRepository;
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    
    /**
     * @Description: Creates the default user settings
     * @Details: Should be used at startup
     * @param mainId - used to find the main user
     */
    public void createDefaultUserSettings(String mainId) {
        if (userSettingRepository.getByMainId(mainId) == null) {
            UserSetting settings = new UserSetting();
            settings.setMainId(mainId);
            settings.setDate(LocalDateTime.now());
            userSettingRepository.save(settings);
        }
    }



    /**
     * @Description: Updates current user settings
     * @param request - used to find the current user
     * @param settings - the actual settings being changed
     * @return boolean
     */
//    public boolean updateCurrentUserUserSettings(HttpServletRequest request, UserSetting settings) {
//        Optional<User> currentUser = userRepository.findById(userService.getUserIdByEmail(request.getUserPrincipal().getName()));
//        UserSettings userSettings = userSettingsRepository.getByMainId(currentUser.get().getId());
//        if (currentUser.get().isSubUser()) {
//            return false;
//        }
//        if (userSettings.getMainId().equals(currentUser.get().getId())) {
//            userSettings.setMainId(currentUser.get().getId());
//            userSettings.setUserTitle(settings.getUserTitle());
//            userSettings.setProcedureCategories(settings.getProcedureCategories());
//            userSettings.setUserCompanyName(settings.getUserCompanyName());
//            userSettings.setProductTags(settings.getProductTags());
//            userSettings.setCustomRoleGroups(settings.getCustomRoleGroups());
//            userSettings.setPublicHolidayDTOList(settings.getPublicHolidayDTOList());
//            userSettings.setUserDepartments(settings.getUserDepartments());
//            userSettings.setUserTeamName(settings.getUserTeamName());
//            userSettings.setDisconnectMinutes(settings.getDisconnectMinutes());
//            userSettings.setEvents(settings.getEvents());
//             ArrayList<LocalDate> localDates = new ArrayList<>();
//            userSettings.getPublicHolidayDTOList().forEach(holidayDTO -> {
//                Stream<LocalDate> dates = holidayDTO.getStartDate().datesUntil(holidayDTO.getEndDate().plusDays(1));
//                localDates.addAll(dates.collect(Collectors.toList()));
//            });
//            userSettings.setPublicHolidayList(localDates);
//            userSettingsRepository.save(userSettings);
//            return true;
//        } else return false;
//    }
//

}
