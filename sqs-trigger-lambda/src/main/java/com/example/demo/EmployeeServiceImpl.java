package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;


@Service
public class EmployeeServiceImpl  {

	
    
    public ResponseDTO<EmployeeEntity> saveEmployee(EmployeeEntity employeeDTO) throws Exception {
    	
    	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

    	DynamoDBMapper mapper = new DynamoDBMapper(client);
        if (ObjectUtils.isEmpty(employeeDTO)) {
            throw new Exception("Employee details cannot be null");
        }
        mapper.save(employeeDTO);
        return new ResponseDTO<>("Employee created successfully", employeeDTO);
    }
    
}