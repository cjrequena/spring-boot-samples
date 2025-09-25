package com.cjrequena.sample.domain.mapper;

import com.cjrequena.sample.domain.model.aggregate.Customer;
import com.cjrequena.sample.domain.model.vo.EmailVO;
import com.cjrequena.sample.infrastructure.adapter.in.controller.dto.CustomerDTO;
import com.cjrequena.sample.infrastructure.adapter.out.persistence.jpa.entity.CustomerEntity;
import org.mapstruct.*;

import java.time.*;
import java.util.List;

/**
 * MapStruct mapper for Customer domain conversions.
 * Handles conversions between Customer aggregate, CustomerDTO, and CustomerEntity.
 */
@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CustomerMapper {

  // ========================================
  // Customer Domain <-> CustomerDTO Mappings
  // ========================================

  /**
   * Maps Customer domain aggregate to CustomerDTO.
   *
   * @param customer the domain Customer
   * @return CustomerDTO for API responses
   */
  @Mapping(target = "email", source = "email", qualifiedByName = "emailVOToString")
  CustomerDTO toDTO(Customer customer);

  /**
   * Maps CustomerDTO to Customer domain aggregate.
   *
   * @param customerDTO the DTO from API requests
   * @return Customer domain aggregate
   */
  @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmailVO")
  Customer toCustomerDomain(CustomerDTO customerDTO);

  /**
   * Maps list of Customer domain aggregates to CustomerDTO list.
   *
   * @param customers list of domain Customers
   * @return list of CustomerDTOs
   */
  List<CustomerDTO> toDTOList(List<Customer> customers);

  /**
   * Maps list of CustomerDTOs to Customer domain aggregate list.
   *
   * @param customerDTOs list of DTOs
   * @return list of domain Customers
   */
  List<Customer> toCustomerDomainList(List<CustomerDTO> customerDTOs);

  // ========================================
  // Customer Domain <-> CustomerEntity Mappings
  // ========================================

  /**
   * Maps Customer domain aggregate to CustomerEntity.
   *
   * @param customer the domain Customer
   * @return CustomerEntity for persistence
   */
  @Mapping(target = "email", source = "email", qualifiedByName = "emailVOToString")
  CustomerEntity toEntity(Customer customer);

  /**
   * Maps CustomerEntity to Customer domain aggregate.
   *
   * @param customerEntity the entity from persistence
   * @return Customer domain aggregate
   */
  @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmailVO")
  Customer toCustomerDomain(CustomerEntity customerEntity);

  /**
   * Maps list of Customer domain aggregates to CustomerEntity list.
   *
   * @param customers list of domain Customers
   * @return list of CustomerEntities
   */
  List<CustomerEntity> toEntityList(List<Customer> customers);

  /**
   * Maps list of CustomerEntities to Customer domain aggregate list.
   *
   * @param customerEntities list of entities
   * @return list of domain Customers
   */
  List<Customer> toCustomerDomainFromEntityList(List<CustomerEntity> customerEntities);

  // ========================================
  // CustomerDTO <-> CustomerEntity Mappings (if needed)
  // ========================================

  /**
   * Maps CustomerDTO directly to CustomerEntity.
   * Useful for cases where domain layer is bypassed.
   *
   * @param customerDTO the DTO
   * @return CustomerEntity for persistence
   */
  CustomerEntity toEntity(CustomerDTO customerDTO);

  /**
   * Maps CustomerEntity directly to CustomerDTO.
   * Useful for cases where domain layer is bypassed.
   *
   * @param customerEntity the entity
   * @return CustomerDTO for API responses
   */
  CustomerDTO toDTO(CustomerEntity customerEntity);

  // ========================================
  // Grpc::Proto <-> Domain Mappings
  // ========================================
  @Mapping(source = "email", target = "email", qualifiedByName = "stringToEmailVO")
  com.cjrequena.sample.domain.model.aggregate.Customer toCustomerDomain(com.cjrequena.sample.grpc.customer.CustomerGrpc customerGrpc);

  @Mapping(source = "email", target = "email", qualifiedByName = "emailVOToString")
  com.cjrequena.sample.grpc.customer.CustomerGrpc toCustomerGrpc(com.cjrequena.sample.domain.model.aggregate.Customer customer);

  List<com.cjrequena.sample.grpc.customer.CustomerGrpc> toCustomerGrpcList(List<com.cjrequena.sample.domain.model.aggregate.Customer> customers);

  // ========================================
  // Update Mappings
  // ========================================

  /**
   * Updates existing CustomerEntity with data from Customer domain aggregate.
   *
   * @param customer source Customer with updates
   * @param customerEntity target entity to update
   */
  @Mapping(target = "email", source = "email", qualifiedByName = "emailVOToString")
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromCustomer(Customer customer, @MappingTarget CustomerEntity customerEntity);

  /**
   * Updates existing CustomerEntity with data from CustomerDTO.
   *
   * @param customerDTO source DTO with updates
   * @param customerEntity target entity to update
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDTO(CustomerDTO customerDTO, @MappingTarget CustomerEntity customerEntity);

  // ========================================
  // Custom Mapping Methods for EmailVO
  // ========================================

  /**
   * Converts EmailVO to String for DTO/Entity mapping.
   *
   * @param emailVO the EmailVO value object
   * @return email string or null
   */
  @Named("emailVOToString")
  default String emailVOToString(EmailVO emailVO) {
    return emailVO != null ? emailVO.email() : null;
  }

  /**
   * Converts String to EmailVO for domain mapping.
   * Returns null for invalid emails instead of throwing exception
   * to allow for graceful handling in the application layer.
   *
   * @param email the email string
   * @return EmailVO value object or null
   */
  @Named("stringToEmailVO")
  default EmailVO stringToEmailVO(String email) {
    if (email == null || email.trim().isEmpty()) {
      return null;
    }
    try {
      return EmailVO.builder().email(email.trim()).build();
    } catch (Exception e) {
      // Log the error and return null to allow the application
      // to handle validation errors appropriately
      // In a real application, you might want to log this
      return null;
    }
  }

  // ========================================
  // Builder Support Methods (for records)
  // ========================================

  /**
   * Creates a Customer builder from CustomerDTO.
   * Useful when you need more control over the building process.
   *
   * @param customerDTO the source DTO
   * @return Customer.CustomerBuilder
   */
  default Customer.CustomerBuilder toCustomerBuilder(CustomerDTO customerDTO) {
    if (customerDTO == null) {
      return null;
    }

    return Customer.builder()
      .id(customerDTO.getId())
      .name(customerDTO.getName())
      .email(stringToEmailVO(customerDTO.getEmail()));
  }

  /**
   * Creates a CustomerDTO builder from Customer.
   * Useful when you need more control over the building process.
   *
   * @param customer the source Customer
   * @return CustomerDTO.CustomerDTOBuilder
   */
  default CustomerDTO.CustomerDTOBuilder toCustomerDTOBuilder(Customer customer) {
    if (customer == null) {
      return null;
    }

    return CustomerDTO.builder()
      .id(customer.getId())
      .name(customer.getName())
      .email(emailVOToString(customer.getEmail()));
  }

  // ========================================
  // Custom Mappings
  // ========================================

  default LocalDate mapToLocalDate(long epochMillis) {
    return Instant.ofEpochMilli(epochMillis)
      .atZone(ZoneId.systemDefault())
      .toLocalDate();
  }

  default long mapFromLocalDate(LocalDate date) {
    return date.atStartOfDay(ZoneId.systemDefault())
      .toInstant()
      .toEpochMilli();
  }

  default OffsetDateTime mapToOffsetDateTime(long value) {
    return Instant.ofEpochMilli(value).atOffset(ZoneOffset.UTC);
  }

  default long mapFromOffsetDateTime(OffsetDateTime value) {
    return value.toInstant().toEpochMilli();
  }
}
