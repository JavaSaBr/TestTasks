## Device API Test Task
### Dependencies
##### 1. JAVA 23+
### Build and verify
```bash
./gradlew test
```
### Run
```bash
./gradlew :device-rest:bootRun
```
### cURL request samples:
```bash
# register a gateway 
curl -X POST localhost:8080/device/Gateway/9c:b0:18:0a:c3:6f
```

```bash
# register a connected switch to the gateway
curl -X POST localhost:8080/device/Switch/ec:43:d4:2d:56:a4/9c:b0:18:0a:c3:6f
```

```bash
# register AP connected to the switch
curl -X POST localhost:8080/device/Access%20Point/69:aa:59:1d:b7:db/ec:43:d4:2d:56:a4
curl -X POST localhost:8080/device/Access%20Point/e5:f1:a7:2f:07:8a/ec:43:d4:2d:56:a4
```

```bash
# get device info
curl -X GET localhost:8080/device/9c:b0:18:0a:c3:6f
curl -X GET localhost:8080/device/ec:43:d4:2d:56:a4
curl -X GET localhost:8080/device/69:aa:59:1d:b7:db
curl -X GET localhost:8080/device/e5:f1:a7:2f:07:8a
```

```bash
# get all registered devices
curl -X GET localhost:8080/devices
```
