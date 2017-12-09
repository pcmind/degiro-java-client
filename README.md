# degiro-java-client

Unofficial DeGiro stock boker java API client.
**WORK IN PROGRESS**

The DeGiro java api client makes it easier to automate DeGiro stock broker actions. DeGiro java client provides a set of methods and objects that allow you to perform the same interactions as with the web trader. DeGiro could change their API in any moment. 

If you have any questions, please open an issue.

## Usage

### Obtain a DeGiro instance
Add {maven_publish_pending} artifact to your project and then use ```DeGiroFactory``` to obtain a ```DeGiro``` instance.

```java
DCredentials creds = new DCredentials() {

        @Override
        public String getUsername() {
          return "YOUR_USERNAME";
        }

        @Override
        public String getPassword() {
          return "YOUR_PASSWORD";
        }
    };

DeGiro degiro = DeGiroFactory.newInstance(creds);
```
If you don't want to create a new DeGiro session on each execution instantiate a DeGiro object with a DPersistentSession. DeGiro API will try to reuse session values (if previous session is expired a new one is obtained and stored).

```java
DeGiro degiro = DeGiroFactory.newInstance(creds, new DPersistentSession("/path/to/session.json"));
```

### Getting account data

```java
//Obtain current orders
DOrders orders = degiro.getOrders();

//Obtain current portfolio
DPortfolio portfolio = degiro.getPortfolio();

// Get cash funds
DCashFunds cashFunds = degiro.getCashFunds();

// Get last executed transactions
DLastTransactions lastTransactions = degiro.getLastTransactions();

// Get transactions between dates 
Calendar c = Calendar.getInstance();
Calendar c2 = Calendar.getInstance();
c.add(Calendar.MONTH, -1);
DTransactions transactions = degiro.getTransactions(c, c2);
```





