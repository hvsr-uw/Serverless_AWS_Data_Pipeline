package com.example.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LambdaEventHandler implements RequestHandler<S3Event, Boolean> {

   
	//we need AmazonS3Client to read the contents from S3 object
    private static final AmazonS3 s3Client = AmazonS3Client.builder()
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .build();
    private  static final AmazonSQS amazonSQSClient = AmazonSQSClientBuilder.standard()
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .build();
    
   
    public Boolean handleRequest(S3Event s3event, Context context) {

        final LambdaLogger logger = context.getLogger();

        //logic to check if any records found
        if(s3event.getRecords().isEmpty()){
            logger.log("No records found");
            return false;
        }
        //process the records
        for(S3EventNotification.S3EventNotificationRecord record: s3event.getRecords()){
            String bucketName = record.getS3().getBucket().getName();
            String objectKey = record.getS3().getObject().getKey();

            S3Object s3Object = s3Client.getObject(bucketName, objectKey);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            
           
            try{
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                
                CSVParser csvParser = new CSVParser(br, CSVFormat.DEFAULT.withFirstRecordAsHeader());

                
                for (CSVRecord record1 : csvParser) {
                	Employee obj = new Employee();
                   obj.setEmail(record1.get("email"));
                   logger.log("email"+obj.getEmail());
                   obj.setName(record1.get("name"));
                   logger.log("name"+obj.getName());
                   obj.setPhone(record1.get("phone"));
                   obj.setAddress(record1.get("address"));
                    obj.setCountry(record1.get("country"));
                    
                    sendtoSQS(obj,context);
                }

               
                
                
            } catch (Exception e){
                logger.log("Error occurred while processing:" + e.getMessage());
                return false;
            }
        }
        return true;
    }
	private void sendtoSQS(Employee obj,Context context) {
		 final LambdaLogger logger = context.getLogger();
	        ObjectMapper objectMapper = new ObjectMapper();

		  logger.log("inside SQS method");
		 try {
			var result = amazonSQSClient.sendMessage("https://sqs.us-east-1.amazonaws.com/215472211497/springboot-lambda-queue", objectMapper.writeValueAsString(obj));
			logger.log("result"+result);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}