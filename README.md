# Loan Processing Microservice:

# How to run

Download the binaries from ./bin folder
In ./bin folder there are 2 jars and 2 bat files. 

### You need java >= 1.8 to run the application

## Run the microservice application:
$> loan-server.bat
or 
$> java -jar loanservice-server.jar

## Run the sample client application:
$> client.bar
or 
$> java -jar loanclient.jar

### Running sample client application will generate a file in client-output.txt in the same directory with output.

### You can also test using any HTTP client like CURL, POSTMAN or so.


## End points

### 1.	Create new loan:
•	url: http://localhost:8080/loan 
•	Method: POST
•	Request Body: 
{
    "amount": "1000",
    "interest": "20",
    "startDate": "2020-06-23"
}
•	ResponseBody:
{
    "accountId": "3fc4ea2e-d5ad-40ad-a491-5a1e0f3f35d1",
    "loanAmount": "1000",
    "interestRate": "20",
    "startDate": "2020-06-23"
}

Validation: Valid loanAmount ( positive real numbers), interest amount (positive real number). Date format strictly: java.time.LocalDate

### 2.	Add new payment:
•	url: http://localhost:8080/payment/{{account_id}}
•	Method: POST
•	Request Body: 
{
    "amount": 1000,
    "transactionDate": "2021-06-29"
}
•	Response Body:
{
    "transactionId": "3fc4ea2e-d5ad-40ad-a491-5a1e0f3f35d1"
}

Throw error if adding the payment, exceeds the total principal balance. Applicable to even in case of back dated payment.
 
### 3.	Check balance:
•	url: http://localhost:8080/balance/{{account_id}}?date=2025-06-23 
•	Method: GET
•	Date field is optional, if not passed it will return the principal balance of the last payment date.

### 4.	Get audit logs:
•	url: http://localhost:8080/audit 
•	Method: GET
•	Response Body: String
