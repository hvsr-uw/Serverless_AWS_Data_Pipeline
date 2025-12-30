package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LambdaEventHandler implements RequestHandler<SQSEvent, Void> {

   
	//we need AmazonSQSClient to read the contents from S3 object
   
    private  static final AmazonSQS amazonSQSClient = AmazonSQSClientBuilder.standard()
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .build();
    
  
    
    private  static final AmazonDynamoDB dynamodb = AmazonDynamoDBClientBuilder.standard().
            withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("https://dynamodb.us-east-1.amazonaws.com", "us-east-1")).
            withCredentials(new DefaultAWSCredentialsProviderChain()).
            build();
  
    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(dynamodb);
    }
    
	@Override
	public Void handleRequest(SQSEvent input, Context context) {
		 for (SQSMessage msg : input.getRecords()) {
	            try {
					processMessage(msg, context);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        context.getLogger().log("done");
			return null;
	}
	
	 private void processMessage(SQSMessage msg, Context context) throws Exception {
		 EmployeeServiceImpl empService = new EmployeeServiceImpl();
	        try {
	            context.getLogger().log("Processed message " + msg.getBody());

	      String jsonbody = msg.getBody();
	      Employee emp = new ObjectMapper().readValue(jsonbody, Employee.class);  
	      context.getLogger().log("Converted into java object employee"+emp.getName());
	      EmployeeEntity entity = new EmployeeEntity();
	      entity.setAddress(emp.getAddress());
	      entity.setCountry(emp.getCountry());
	      entity.setEmail(emp.getEmail());
	      entity.setName(emp.getName());
	      entity.setPhone(emp.getPhone());
	      empService.saveEmployee(entity);
	        } catch (Exception e) {
	            context.getLogger().log("An error occurred");
	            throw e;
	        }

	    }
	
}