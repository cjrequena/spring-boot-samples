package com.cjrequena.sample.model.dto;

import lombok.Data;

@Data
public class UserDTO {

  private Long id;
  private String userName;
  private String password;
  private String role; //Eg: ADMIN,USER
}
