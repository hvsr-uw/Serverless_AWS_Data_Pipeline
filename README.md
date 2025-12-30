# AWS Serverless Services using Spring boot

In this Project, we will implement a full serverless solution using AWS Services and Java Spring boot as the framework for the processing.
We will implement the below solution : 

User Invoking API Gateway Rest API and upload file on S3.

S3 event notification triggers Lambda.

Lambda processes the records in the file and pushes them to SQS.

Another Lambda function consumes the records from SQS and saves it into Dynamo DB.

Here is the architecture diagram for the same :

![diagram-export-1-27-2025-1_59_22-PM](https://github.com/user-attachments/assets/1fed67ee-fd26-4aa2-aaf0-52646273c290)

## Step 1 : Create a S3 bucket.

Navigate to AWS Console and search for S3. Click Create Bucket.

<img width="1648" alt="Screenshot 2025-01-27 at 2 02 29 PM" src="https://github.com/user-attachments/assets/0f93d258-c868-404a-89af-16624d884dcc" />

Give name to S3 bucket and uncheck block all public access. Keep all other settings as default .

<img width="1655" alt="Screenshot 2025-01-27 at 2 03 32 PM" src="https://github.com/user-attachments/assets/29c08ee5-632b-4c78-9701-2de392edc890" />

This will create our S3 bucket.

## Step 2 : Create API Gateway Rest API

In the AWS Console , search for API Gateway and Select Rest API and Build.

<img width="1694" alt="Screenshot 2025-01-27 at 2 06 28 PM" src="https://github.com/user-attachments/assets/ffc11ee7-90a8-4d47-8a41-eb3b4dff1956" />

Give the name and description and Click Create API

<img width="1701" alt="Screenshot 2025-01-27 at 2 07 10 PM" src="https://github.com/user-attachments/assets/258ecb2c-e806-4e3e-89ed-0f8cdaf8bbcb" />

Once API is created, Click Create Resource :

Resource path should be left as it is and Resource-name should be {bucket}. This means we will pass our bucket name as path parameter.

Click Create Resource again and enter resource-name as {filename}

<img width="285" alt="Screenshot 2025-01-27 at 2 08 13 PM" src="https://github.com/user-attachments/assets/2bf7ef7e-a785-4f87-aee0-922fef8e1632" />


Lets now create API Gateway role to access s3

Navigate to API Gateway and select Create Role and select AWS Service as API Gateway

<img width="1469" alt="Screenshot 2025-01-27 at 2 15 53 PM" src="https://github.com/user-attachments/assets/0fd6e466-56cf-4898-b9aa-b28d4186714a" />

Clickk, next, next and Enter role name and hit submit.

Navigate to the newly created role and attach permissions tab . click Add permissions and add the below json :

{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor0",
            "Effect": "Allow",
            "Action": "s3:*",
            "Resource": "*"
        }
    ]
}

Now, Let us go back to API Gateway and Click Create Method.

Select method as PUT and AWS Service as S3 as shown in the screenshot. Rest all details should be default.

<img width="1645" alt="Screenshot 2025-01-27 at 2 22 26 PM" src="https://github.com/user-attachments/assets/79f5f870-081a-4ccd-8f78-f67ff4f0eb23" />

Now go to integration request add edit path variables.

<img width="1645" alt="Screenshot 2025-01-27 at 2 22 26 PM" src="https://github.com/user-attachments/assets/fafd62ad-de7b-4299-b4cb-648f9159bbb6" />

One last thing, Go to settings , we need to accept binary file types and enter */*

<img width="1462" alt="Screenshot 2025-01-27 at 2 26 26 PM" src="https://github.com/user-attachments/assets/ea4e66ee-ee56-42ad-8786-f88e30e2ca2f" />

Now click on deploy and enter stage name as dev.

Once it is deployed successdfully, we have not integrated API Gateway and S3.

## Step 3 : Create Lambda Function which will trigger when file gets uploaded to S3 and send the data to Amazon SQS.

Lets create a Lambda function using spring boot. Navigate to https://start.spring.io and Download skeleton project.

Add the below dependencies in pom.xml
 <dependency>
   <groupId>com.amazonaws</groupId>
   <artifactId>aws-lambda-java-core</artifactId>
   <version>1.2.3</version>
  </dependency>
  <dependency>
   <groupId>com.amazonaws</groupId>
   <artifactId>aws-lambda-java-events</artifactId>
   <version>3.11.5</version>
  </dependency>
  <dependency>
   <groupId>com.amazonaws</groupId>
   <artifactId>aws-java-sdk-s3</artifactId>
   <version>1.12.705</version>
  </dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-csv</artifactId>
    <version>1.13.0</version>
</dependency>
<dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-java-sdk-sqs</artifactId>
            <version>1.12.705</version>
        </dependency>

Also, we need to add a plugin to build jar for Lambda .

<build>
  <plugins>
   <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <configuration>
     <createDependencyReducedPom>false</createDependencyReducedPom>
    </configuration>
    <executions>
     <execution>
      <phase>package</phase>
      <goals>
       <goal>shade</goal>
      </goals>
     </execution>
    </executions>
   </plugin>
  </plugins>
 </build>


Next, Create a LambdaHandler Class and add the code as shown below :

<img width="866" alt="Screenshot 2025-01-27 at 2 57 02 PM" src="https://github.com/user-attachments/assets/3aeaedd0-4738-41dd-8502-b2b718adf3b4" />

<img width="1082" alt="Screenshot 2025-01-27 at 2 57 39 PM" src="https://github.com/user-attachments/assets/9a932f80-871e-4a00-aafc-9346799c7757" />

This shows we receive file from S3 using S3Event Object, process the file and save it as Java Object by parsing it using Open CSV parser.
Once the java object is formed, it is being sent to SQS queue.

In AWS console, Navigate to Lambda and Click Create Function. Select runtime as Java 17 and enter function name and click create.

<img width="1685" alt="Screenshot 2025-01-27 at 2 59 31 PM" src="https://github.com/user-attachments/assets/4bb4f1bf-c9e8-4381-a85f-6a6bd2eeaaa0" />

Now, lets upload the jar file, Click on Code tab and upload .zip or jar file.

<img width="1712" alt="Screenshot 2025-01-27 at 3 00 35 PM" src="https://github.com/user-attachments/assets/3dae2209-2c6d-4147-a4dd-d980cd67ce02" />

Upload our jar file and click save.

one last thing, we need to edit runtime settings and enter fully qualified name of our handler class and method name as shown in the screenshot :

<img width="1657" alt="Screenshot 2025-01-27 at 3 02 44 PM" src="https://github.com/user-attachments/assets/1a112bc9-fa73-477e-bb66-6768fc906fb0" />

Now, Lets create Trigger for S3  . We can do this both ways, Create Event Notification in S3 or trigger in Lambda.

Lets create in Lambda itself. Click Add trigger. Select service as S3. 

<img width="1709" alt="Screenshot 2025-01-27 at 3 04 55 PM" src="https://github.com/user-attachments/assets/8b122251-7a7f-4195-b230-76cf8cd9d4e8" />

We can verify that it is also now reflected in S3 event notification :

<img width="1489" alt="Screenshot 2025-01-27 at 3 06 29 PM" src="https://github.com/user-attachments/assets/811eec6d-7b14-47b6-9b7d-f4e6d1a4cedc" />

Let us now create SQS queue where Lambda can push messages.

Click Create Queue. Enter Queue name and it should be standard Queue. Leave Everything default.

<img width="1362" alt="Screenshot 2025-01-27 at 5 05 53 PM" src="https://github.com/user-attachments/assets/e948dcb2-d604-48e2-8b1b-9a2c4764d95d" />

With this Step 2 is completed and We should be able to now invoke API gateway and upload file in S3. This in turn should trigger Lambda function which should push messages to SQS.

## Step 3 : Lambda to consume Message from SQS and saving into Dynamo dB.

We need to create Lambda function which will pick messages from SQS. Lets create a role for Lambda which gives permissions for SQS execution.

click Create Role and Add permissions AWSLambdaSQSQueueExecutionRole policy .

Also, as this lambda function will also write to dynamo db, we need to add one customer managed custom policy as shown below :

<img width="1484" alt="Screenshot 2025-01-27 at 5 11 30 PM" src="https://github.com/user-attachments/assets/50954c3a-af71-436e-abd6-cbba14b8c89e" />

Here is the custom policy :

{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "DynamoDBIndexAndStreamAccess",
            "Effect": "Allow",
            "Action": [
                "dynamodb:GetShardIterator",
                "dynamodb:Scan",
                "dynamodb:Query",
                "dynamodb:DescribeStream",
                "dynamodb:GetRecords",
                "dynamodb:ListStreams"
            ],
            "Resource": [
                "arn:aws:dynamodb:us-east-1:215472211497:table/Books/index/*",
                "arn:aws:dynamodb:us-east-1:215472211497:table/Books/stream/*"
            ]
        },
        {
            "Sid": "DynamoDBTableAccess",
            "Effect": "Allow",
            "Action": [
                "dynamodb:BatchGetItem",
                "dynamodb:BatchWriteItem",
                "dynamodb:ConditionCheckItem",
                "dynamodb:PutItem",
                "dynamodb:DescribeTable",
                "dynamodb:DeleteItem",
                "dynamodb:GetItem",
                "dynamodb:Scan",
                "dynamodb:Query",
                "dynamodb:UpdateItem"
            ],
            "Resource": "arn:aws:dynamodb:us-east-1:215472211497:table/*"
        },
        {
            "Sid": "DynamoDBDescribeLimitsAccess",
            "Effect": "Allow",
            "Action": "dynamodb:DescribeLimits",
            "Resource": [
                "arn:aws:dynamodb:*:123456789012:table/*",
                "arn:aws:dynamodb:*:123456789012:table/*/index/*"
            ]
        }
    ]
}


Now, Lets create a Dynamo DB table. Enter table name and parititon Key as "id". Rest everything we can keep as default.

<img width="1707" alt="Screenshot 2025-01-27 at 5 18 32 PM" src="https://github.com/user-attachments/assets/76967d89-7acc-419b-a898-b06ebf617c23" />

Lets now Create a lambda function using Spring boot. Navigate to https://start.spring.io and download skeleton project.

Add the below dependencies :

<dependency>
   <groupId>com.amazonaws</groupId>
   <artifactId>aws-lambda-java-core</artifactId>
   <version>1.2.3</version>
  </dependency>
  <dependency>
   <groupId>com.amazonaws</groupId>
   <artifactId>aws-lambda-java-events</artifactId>
   <version>3.11.5</version>
  </dependency>
  <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-java-sdk-sqs</artifactId>
            <version>1.12.705</version>
        </dependency>
        
        <dependency>
   <groupId>com.amazonaws</groupId>
   <artifactId>aws-java-sdk-dynamodb</artifactId>
   <version>1.12.705</version>

We will add the lambda event handler to process the message as shown below. Also, we have created dynamo db mapper using the endpoint and region details :

<img width="1077" alt="Screenshot 2025-01-27 at 5 21 12 PM" src="https://github.com/user-attachments/assets/18ca60f8-d53a-4cd7-b621-aaf24d9f5594" />

We have used spring boot JPA methods to save the details. 
Employee Entity class is created and annotated with Dynamo DB table annotations.

<img width="652" alt="Screenshot 2025-01-27 at 5 22 39 PM" src="https://github.com/user-attachments/assets/430d4c81-4419-47e5-90af-ddfcace878c8" />

We can build the code.
Next, Go to Lambda and create a new function and upload the jar file.
Next, update the runtime settings as explained in the earlier lambda method.

We can now deploy the lambda function.

It's time to test the end to end functionality :

Lets execute the API gateway using Postman. (In real world, this will be called by front end application) and upload the csv file.

<img width="1389" alt="Screenshot 2025-01-27 at 5 26 38 PM" src="https://github.com/user-attachments/assets/7c4f9eff-3e9f-48a4-a993-70320f64c654" />

Now, Lets examine the records in Dynamo DB table :

<img width="1162" alt="Screenshot 2025-01-27 at 5 28 13 PM" src="https://github.com/user-attachments/assets/0f735d0e-ade2-4c03-a77a-7cfd19f9acac" />

### Congratulations ! we have now successfully built a scalable full serverless asynchronous event based system using AWS services.

Thank you

Rahul Ahuja
