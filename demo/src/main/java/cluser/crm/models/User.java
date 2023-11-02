package cluser.crm.models;

import cluser.crm.config.UserRoleEnum;
import cluser.crm.dtos.CompanyDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @Description: Model class that stores each user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    /**
     * BASIC INFO
     */
    @Field("firstName")
    private String firstName;

    @Field("lastName")
    private String lastName;

    @Field("email")
    private String email;

    @Field("password")
    private String password;

    @Field("personalPhone")
    private String personalPhone;

    @Field("department")
    private String department;
    @Field("title")
    private String title;

    @Field("roles")
    private ArrayList<UserRoleEnum> roles;

    @Field("hiringDate")
    private LocalDate hiringDate;

    @Field("employeeStatus")
    private String employeeStatus;

    @Field("employeeType")
    private String employeeType;
    @Field("companyDetails")
    private CompanyDetailDTO companyDetails;

    /**
     * Personal details
     */
    @Field("address")
    private String address;

    @Field("city")
    private String city;

    @Field("country")
    private String country;

    @Field("county")
    private String county;

    @Field("dateOfBirth")
    private LocalDate dateOfBirth;

    @Field("gender")
    private String gender;

    @Field("clientType")
    private String clientType;

    @Field("isAdmin")
    private boolean isAdmin;

    @Field("isActive")
    private boolean isActive;

    @Field("isSubUser")
    private boolean isSubUser;

    @Field("mainId")
    private String mainId;

    @Field("profileImagePath")
    private String profileImagePath;

    /**
     * Date when the user was registered
     */
    @Field("registerDate")
    private LocalDateTime registerDate;


    /**
     * @Description: Used for tickets
     * todo : check if this is added whenever a user is created (and not removed at updated nd such)
     */
    @Field("userTicketCounter")
    private int userTicketCounter = 0;
}
