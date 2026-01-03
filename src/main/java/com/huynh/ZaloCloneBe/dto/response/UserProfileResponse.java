package com.huynh.ZaloCloneBe.dto.response;

import com.huynh.ZaloCloneBe.entity.RelationshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String firstname;
    private Integer gender;
    private String avatarUrl;
    private String coverUrl;
    private String lastname;
    private Boolean online;
    private Date lastOnline;
    private RelationshipStatus relationshipStatus;
}
