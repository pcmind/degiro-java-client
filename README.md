# degiro-java-client

Unofficial DeGiro stock boker java API client.
**WORK IN PROGRESS**

The DeGiro java api client makes it easier to automate DeGiro stock broker actions. DeGiro java client provides a set of methods and objects that allow you to perform the same interactions as with the web trader. DeGiro could change their API in any moment. 

If you have any questions, please open an issue.

## Usage

### Obtain a DeGiro instance
Add {maven_publish_pending} artifact to your project. 

**Prepare a DCredentials** object with your credentials:

```Java
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
```
Get a **DeGiro** instance:
```java
DeGiro degiro = DeGiroFactory.newInstance(creds);
```
If you don't want to create a new DeGiro session on each execution of your code. Instantiate DeGiro object indicating a DPersistentSession, in this case DeGiro API will try to reuse previous session values (if session is expired a new one is obtained and stored):

```java
DeGiro degiro = DeGiroFactory.newInstance(creds, new DPersistentSession("/path/to/session.json"));
```
:warning: You are required to apply a security policy to your session file: A malicious user could take control of your DeGiro account.

