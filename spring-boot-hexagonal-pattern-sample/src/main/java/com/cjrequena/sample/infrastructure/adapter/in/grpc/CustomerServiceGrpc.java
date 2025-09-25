package com.cjrequena.sample.infrastructure.adapter.in.grpc;

import com.cjrequena.sample.domain.mapper.CustomerMapper;
import com.cjrequena.sample.domain.model.vo.EmailVO;
import com.cjrequena.sample.domain.port.in.customer.CreateCustomerUseCase;
import com.cjrequena.sample.domain.port.in.customer.RetrieveCustomerUseCase;
import com.cjrequena.sample.grpc.customer.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
@GrpcService
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerServiceGrpc extends com.cjrequena.sample.grpc.customer.CustomerServiceGrpc.CustomerServiceImplBase {

  private final CreateCustomerUseCase createCustomerUseCase;
  private final RetrieveCustomerUseCase retrieveCustomerUseCase;
  private final CustomerMapper customerMapper;

  @Override
  public void createCustomer(CreateCustomerRequest request, StreamObserver<CreateCustomerResponse> responseObserver) {
    com.cjrequena.sample.grpc.customer.Customer customerGrpc = request.getCustomer();
    com.cjrequena.sample.domain.model.aggregate.Customer customer = com.cjrequena.sample.domain.model.aggregate.Customer
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
    super.retrieveCustomerById(request, responseObserver);
  }

  @Override
  public void retrieveCustomers(RetrieveCustomersRequest request, StreamObserver<RetrieveCustomersResponse> responseObserver) {
    super.retrieveCustomers(request, responseObserver);
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
