package cluser.crm.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDetailDTO {
    private String name;
    private String description;
    private String cif;
    private String activityDomain;
    private String email;
    private String phone;
    private String address;
    private String county;
    private String city;
}