# data-ingest-api

Java Spring Boot app that provides an API to support POST method to upload data file and insert records into database.

Has one endpoint “upload”.
The endpoint takes a .txt file as input.
The text file is split into records and stored in a database. 
The records contain:
•       Company Name
•       Company Number 
•       EventType 
•       EventDate 
•       UniqueIdentifier 

# command to run 
mvn spring-boot:run

# API endpoint
 http://localhost:8080/upload accepting POST

# Example POST 

## Curl
curl --form file='@datafile.txt' http://localhost:8080/upload --header "Accept:text/plain"

## Postman
Using local install of postman
https://www.postman.com/




