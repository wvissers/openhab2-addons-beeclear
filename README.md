# BeeClear Binding

Attention: development not finished yet. This is just an announcement of the start of the development of this binding.

This binding is intended to integrate the BeeClear Energy Manager into openHAB2. See for BeeClear information and ordering https://beeclear.nl/. It is a unit intended to be used with smart energy meter with P1 external port.
The majority of the more recently installed systems by public services in The Netherlands are equipped with such a port.    

## Supported Things

The BeeClear unit comes with a set of databases that collect real time telegram data from the smart energy meter. In general,
openHAB2 will be used to collect the real time data. The RRD4j Persistence addon will typically be used to collect and manage
data over time and generate graphics in e.g. the Basic UI or HABPanel.     

## Discovery

Currently, no auto discovery is included. A manual configuration of the IP address is needed. The easiest way to configure
the binding is to use the Paper UI.

## Binding Configuration

Todo.

## Thing Configuration

At this moment, the IP-address will be the only configuration needed. Maybe there will be other configuration items in
the future.

## Channels

To be planned.

## Full Example

Not available yet.

## Any custom content here!


