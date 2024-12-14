## Device API Test Task

***

### Description
Network deployment might consist of several devices.  
Networking device might be of following types:  
 - Gateway - serves as access point to another network  
 - Switch - connects devices on a computer network  
 - Access Point - connects devices on a computer network via Wi-Fi  

Typically, these devices are connected to one another and collectively form a
network deployment. Every device on a computer network can be identified by MAC address.
If device is attached to another device in same network, it is represented via
uplink reference.

### Task
Define and implement Device API, which should support following features:   
 - Registering a device to a network deployment 
   - input: deviceType, macAddress, uplinkMacAddress
 - Retrieving all registered devices, sorted by device type
   - output: sorted list of devices, where each entry has deviceType and macAddress (sorting order: Gateway > Switch > Access Point)
 - Retrieving network deployment device by MAC address
   - input: macAddress
   - output: Device entry, which consists of deviceType and macAddress
 - Retrieving all registered network device topology
   - output: Device topology as tree structure, node should be
represented as macAddress
 - Retrieving network device topology starting from a specific device
   - input: macAddress
   - output: Device topology where root node is device with matching macAddress
Additional notes:
- Device may or may not be connected to uplink device

***

### Dependencies
1. JDK 21+

### Build and verify
```bash
./gradlew test
```
### Build aggregated code coverage report
```bash
./gradlew testCodeCoverageReport
```
### Run
```bash
./gradlew :device-rest:bootRun
```
### cURL request samples:
```bash
# register gateways 
curl -X POST localhost:8080/device/Gateway/9c:b0:18:0a:c3:6f
curl -X POST localhost:8080/device/Gateway/b7:8e:d0:76:c9:6b
```
```bash
# register switches
curl -X POST localhost:8080/device/Switch/53:3f:8d:6b:44:8f
curl -X POST localhost:8080/device/Switch/8e:8c:18:e9:22:3c
```
```bash
# register APs
curl -X POST localhost:8080/device/Access%20Point/24:5c:78:13:56:53
curl -X POST localhost:8080/device/Access%20Point/ae:57:00:d0:14:7c
```

```bash
# register a connected gateway to gateway #b7:8e:d0:76:c9:6b
curl -X POST localhost:8080/device/Gateway/cf:92:f1:49:4d:15/b7:8e:d0:76:c9:6b
```

```bash
# register connected switches
curl -X POST localhost:8080/device/Switch/ec:43:d4:2d:56:a4/9c:b0:18:0a:c3:6f
curl -X POST localhost:8080/device/Switch/20:7a:28:99:a0:24/9c:b0:18:0a:c3:6f
curl -X POST localhost:8080/device/Switch/9b:b7:cc:f2:1a:0f/cf:92:f1:49:4d:15
curl -X POST localhost:8080/device/Switch/4d:b7:cd:68:cc:96/9b:b7:cc:f2:1a:0f
```

```bash
# register connected APs
curl -X POST localhost:8080/device/Access%20Point/69:aa:59:1d:b7:db/ec:43:d4:2d:56:a4
curl -X POST localhost:8080/device/Access%20Point/e5:f1:a7:2f:07:8a/ec:43:d4:2d:56:a4
curl -X POST localhost:8080/device/Access%20Point/f4:b5:3a:d0:58:47/4d:b7:cd:68:cc:96
curl -X POST localhost:8080/device/Access%20Point/d4:f3:30:2f:49:3a/4d:b7:cd:68:cc:96
```

```bash
# get device info
curl -X GET localhost:8080/device/9c:b0:18:0a:c3:6f
curl -X GET localhost:8080/device/ec:43:d4:2d:56:a4
curl -X GET localhost:8080/device/cf:92:f1:49:4d:15
curl -X GET localhost:8080/device/e5:f1:a7:2f:07:8a
```

```bash
# get all registered devices
curl -X GET localhost:8080/devices
```

```bash
# get device network topology
curl -X GET localhost:8080/topology/9c:b0:18:0a:c3:6f
curl -X GET localhost:8080/topology/69:aa:59:1d:b7:db
```

```bash
# get full network topology
curl -X GET localhost:8080/topology
```
