# BeeClear Binding

Attention: development not finished yet. This is just an announcement of the start of the development of this binding.

This binding is intended to integrate the BeeClear Energy Manager into openHAB2. See for BeeClear information and ordering https://beeclear.nl/. It is a unit intended to be used with smart energy meter with P1 external port.
The majority of the more recently installed systems by public services in The Netherlands are equipped with such a port.    

## Supported Things

The basic thing is the "meter" thing, representing the energy meter attached to the BeeClear unit.

Although the BeeClear unit comes with a set of databases that collect real time telegram data from the smart 
energy meter, usually openHAB will be used for this purpose. 
The RRD4j Persistence addon will typically be used to collect and managedata over time and generate graphics 
in e.g. the Basic UI or HABPanel.     

## Discovery

Currently, no auto discovery is included. A manual configuration of the IP address is needed. The easiest way to configure
the binding is to use the Paper UI.

## Binding Configuration

Todo.

## Thing Configuration

At this moment, the IP-address will be the only configuration needed. Maybe there will be other configuration items in
the future.

## Channels

Currently, the following channels for the "meter" thing are available:

power - reports the current total power consumption in kW.

usedLow - reports the current electricity meter reading for low tariff in kWh.

usedHigh - reports the current electricity meter reading for high tariff in kWh.

engine - reports the "engine" version retrieved from the BeeClear.

## Item configuration Example

Items can be configured using an ".items" file in the conf/items directory. A typical example:

```
String BC_Engine    "BeeClear Engine version [%s]"  <energy> { channel = "beeclear:meter:unit:engine" }
Number BC_Power     "Consumption [%.3f kW]"         <energy> { channel = "beeclear:meter:unit:power" }
Number BC_Used_Low  "Reading low [%.1f kWh]"        <energy> { channel = "beeclear:meter:unit:usedLow" }
Number BC_Used_High "Reading high [%.1f kWh]"       <energy> { channel = "beeclear:meter:unit:usedHigh" }
```


## Full Example

Not available yet.

## Any custom content here!


