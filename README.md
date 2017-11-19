# degiro-java-client
Unofficial Degiro trading platform java API client.

Work in progress...
Roadmap:

Get portfolio

Get/Set orders

Get RT prices

Implement new order types:

Peak profit: Automatic stoploss adjusting if a stock increases its value in a non usual way. (a stock gets +15%, this actiavates peak profit, when stock decreases 2% from its peak, then sell all stocks). (proposal mode, automatic mode, automatic-dryrun mode)

Index based futures: if a tendency is confirmed (1min, 5min, 15min, 30min, 60min...) make profit of that: sell/buy according to the tendency and cancel the operation with a fixed gain/loss (adjustable risk) (proposal mode, automatic mode, automatic-dryrun mode)
