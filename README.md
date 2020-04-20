# degiro-java-client

Unofficial DeGiro stock broker java API client.

This library implements all DeGiro primitive operations. It provides the same functionality of DeGiro web and makes it easier to automate your portfolio management. 

VERY IMPORTANT: DeGiro could change their API in any moment so use this library at your own risk.

If you have any questions, please open an issue.

## Usage

### Adding lib to your build

Maven artifact not yet release, in the meantime use [jitpack.io](https://jitpack.io/):

Add [jitpack.io](https://jitpack.io/) repository:
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add dependency:
```
<dependency>
    <groupId>com.github.pcmind</groupId>
    <artifactId>degiro-java-client</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```

### Obtain a DeGiro instance

Maven artifact not yet release but in the mean time use jitpack:

Add the artifact to your project and then use ```DeGiroFactory``` to obtain a ```DeGiro``` instance.

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
If you don't want to create a new DeGiro session on each execution instantiate a DeGiro object with a DPersistentSession. DeGiro API will try to reuse session values (expired session will be renewed automatically).

```java
DeGiro degiro = DeGiroFactory.newInstance(creds, new DPersistentSession("/path/to/session.json"));
```

### Get delta sync update 
For most operation Degiro web portal use a delta sync update polling mechanism to keep web view live data. 
Web portal usualy request delta sync for multiple sections/tables at once to minimize numbers of requests and lag. 
I recommend you do the same.

Deliberately library client class does not handle state management for the delta sync mechanist and only expose received delta sync operations.
This way one can implement its own method to save records, and react to changes.


````java

//use some kind of repository ...
final Map<String, DOrder> orders = new HashMap<>();
//use a repository for each of the available sections: portfolio, totalPortfolio, orders, historicalOrders, transactions, alerts, cashFunds;

//a location to keep track of last update token received (or processed)
List<DUpdateToken> tokens = new ArrayList<>(DUpdateToken.allSections());

//in some kind of loop do the following:

//fetch detla updates from tokens to current state 
final DUpdates dUpdates1 = deGiro.updateAll(tokens);

//handle received updates with eventual changes (create/update/delete) 
for (DUpdate<DOrder> update : dUpdates.getOrders().getUpdates()) {
    switch (update.getType()) {
        case DELETED:
            orders.remove(update.getId());
        case CREATED:
            orders.put(update.getId(), update.getNew());
        case UPDATED:
            //if in-place update can't be used, use DOrder#copy() first to apply updates on object copy
            update.update(orders.get(update.getId()));
    }
}
//handle other sections (portfolio, totalPortfolio, orders, historicalOrders, transactions, alerts, cashFunds) the same way

//record last used update tokens.
tokens = dUpdates1.getTokens();

````

### Search products

```java

// Search products by text, signature:
// DProductSearch searchProducts(String text, DProductType type, int limit, int offset);
DProductSearch ps = degiro.searchProducts("telepizza", DProductType.ALL, 10, 0);
for (DProduct product : ps.getProducts()) {
    System.out.println(product.getId() + " " + product.getName());
}

// Get product info by id, signature:
// DProducts getProducts(List<String> productIds);
List<String> productIds = new ArrayList<>();
//be aware that in some cases productId is a number and others a string. You can safly convert number to String for this request
productIds.add("1482366"); // productId obtained in (orders, portfolio, transactions, searchProducts....)
degiro.getProducts(productIds);
DProductDescriptions products = degiro.getProducts(productIds);

for (DProductDescription value : products.getData().values()) {
    System.out.println(value.getId() + " " + value.getName());
}

```

### Subscribe to product price updates

```java

// Register a price update listener (called on price update)
degiro.setPriceListener(new DPriceListener() {
    @Override
    public void priceChanged(DPrice price) {
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(price));
    }
});

// Create a vwdIssueId list. Note that vwdIssueId is NOT a productId (vwdIssueId is a DProduct field).
List<Long> vwdIssueIds = new ArrayList<>(1);
vwdIssueIds.add(280099308L); // Example product vwdIssueId
degiro.subscribeToPrice(vwdIssueIds); // Callable multiple times with different products. 

// You need some type of control loop, background thread, etc... to prevent JVM termination (out of this scope)
while (true) {
   Thread.sleep(1000);
}

```
By default, price updates are checked every 5 seconds. Polling frequency can be changed:

```java
degiro.setPricePollingInterval(1, TimeUnit.MINUTES);
```
Clear all subscriptions:

```java
degiro.clearPriceSubscriptions();
```

### Order management
Orders are placed in two steps: check order (to ensure order factibility) and confirmation. When DConfirmation status is 0 then the order is placed successfully.


```java
// Generate a new order. Signature:
// public DNewOrder(DOrderAction action, DOrderType orderType, DOrderTime timeType, long productId, long size, BigDecimal limitPrice, BigDecimal stopPrice)

DNewOrder order = new DNewOrder(DOrderAction.SELL, DOrderType.LIMITED, DOrderTime.DAY, 1482366, 20, new BigDecimal("4.5"), null);

DOrderConfirmation confirmation = degiro.checkOrder(order);

if (!Strings.isNullOrEmpty(confirmation.getConfirmationId())) {
    DPlacedOrder placed = degiro.confirmOrder(order, confirmation.getConfirmationId());
    if (place.getStatusId() != 0) {
        throw new RuntimeException("Order not placed: " + place.getStatusText());
    }
}
```
Order update example:

```java
// Update an order. Signature:
// DPlacedOrder updateOrder(DOrder order, BigDecimal limit, BigDecimal stop);
DPlacedOrder updated = degiro.updateOrder(order, new BigDecimal("0.04"), null); // obtained in getOrders()
if (updated.getStatusId() != 0) {
    throw new RuntimeException("Order not updated: " + updated.getStatusText());
}
```


Order delete example:

```java
DPlacedOrder deleted = degiro.deleteOrder(orderId); // orderId obtained in getOrders() 
if (deleted.getStatusId() != 0) {
    throw new RuntimeException("Order not deleted: " + deleted.getStatusText());
}
```
