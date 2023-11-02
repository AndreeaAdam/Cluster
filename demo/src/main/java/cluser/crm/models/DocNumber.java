package cluser.crm.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Only one instance (with an id of 1) of this should exist
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "docNumber")
public class DocNumber {
    @Id
    private String id;

    @Field("contractNumber")
    private int contractNumber;

    @Field("offerNumber")
    private int offerNumber;

    @Field("projectNumber")
    private int projectNumber;

    @Field("mainId")
    private String mainId;

}
