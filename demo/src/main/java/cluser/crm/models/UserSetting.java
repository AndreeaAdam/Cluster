package cluser.crm.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * @Description: User Settings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "userSettings")
public class UserSetting {

    @Id
    private String id;


    @Field("mainId")
    private String mainId;

    @Field("ticketCounter")
    private int ticketCounter = 0;

    @Field("disconnectMinutes")
    private int disconnectMinutes;
    @Field("date")
    private LocalDateTime date;
}
