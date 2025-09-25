package com.cjrequena.sample.infrastructure.adapter.in.grpc;

import com.cjrequena.sample.domain.exception.domain.CustomerNotFoundException;
import com.cjrequena.sample.domain.exception.domain.GrpcExceptionHandler;
import com.cjrequena.sample.domain.mapper.CustomerMapper;
import com.cjrequena.sample.domain.model.aggregate.Customer;
import com.cjrequena.sample.domain.model.vo.EmailVO;
import com.cjrequena.sample.domain.port.in.customer.CreateCustomerUseCase;
import com.cjrequena.sample.domain.port.in.customer.RetrieveCustomerUseCase;
import com.cjrequena.sample.grpc.customer.*;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Log4j2
@GrpcService
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerServiceGrpc extends com.cjrequena.sample.grpc.customer.CustomerServiceGrpc.CustomerServiceImplBase {

  private final CreateCustomerUseCase createCustomerUseCase;
  private final RetrieveCustomerUseCase retrieveCustomerUseCase;
  private final GrpcExceptionHandler grpcExceptionHandler;
  private final CustomerMapper customerMapper;

  @Override
  public void createCustomer(CreateCustomerRequest request, StreamObserver<CreateCustomerResponse> responseObserver) {
    CustomerGrpc customerGrpc = request.getCustomer();
    Customer customer = Customer
      .builder()
      .name(customerGrpc.getName())
      .email(EmailVO
        .builder()
        .email(customerGrpc.getEmail())
        .build())
      .build();
    createCustomerUseCase.create(customer);
    CreateCustomerResponse response = CreateCustomerResponse
      .newBuilder()
      .setSuccess(true)
      .setMessage("Customer created successfully")
      .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void retrieveCustomerById(RetrieveCustomerByIdRequest request, StreamObserver<RetrieveCustomerByIdResponse> responseObserver) {
    final Long customerId = request.getId();
    try {
      final Customer customer = this.retrieveCustomerUseCase.retrieveById(customerId);
      final CustomerGrpc customerGrpc = this.customerMapper.toCustomerGrpc(customer);
      RetrieveCustomerByIdResponse response = RetrieveCustomerByIdResponse
        .newBuilder()
        .setCustomer(customerGrpc)
        .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (CustomerNotFoundException ex) {
      String errorMessage = String.format("The customer :: %s :: was not found", customerId);
      final StatusRuntimeException err = this.grpcExceptionHandler.buildErrorResponse(new CustomerNotFoundException(errorMessage));
      responseObserver.onError(err);
    }
  }

  @Override
  public void retrieveCustomers(RetrieveCustomersRequest request, StreamObserver<RetrieveCustomersResponse> responseObserver) {
    final List<Customer> customerList = this.retrieveCustomerUseCase.retrieve();
    final List<CustomerGrpc> customerGrpcList = this.customerMapper.toCustomerGrpcList(customerList);
    RetrieveCustomersResponse response = RetrieveCustomersResponse
      .newBuilder()
      .addAllCustomers(customerGrpcList)
      .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void updateCustomer(UpdateCustomerRequest request, StreamObserver<UpdateCustomerResponse> responseObserver) {
    super.updateCustomer(request, responseObserver);
  }

  @Override
  public void deleteCustomer(DeleteCustomerRequest request, StreamObserver<DeleteCustomerResponse> responseObserver) {
    super.deleteCustomer(request, responseObserver);
  }
}
