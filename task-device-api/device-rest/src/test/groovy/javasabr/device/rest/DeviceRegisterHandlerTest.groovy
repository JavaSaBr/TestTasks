package javasabr.device.rest

import javasabr.device.model.NetworkDeviceType
import javasabr.device.service.DeviceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

import static java.nio.charset.StandardCharsets.UTF_8
import static javasabr.device.rest.DeviceRestConstants.R_DEVICE

class DeviceRegisterHandlerTest extends RestSpecification {
  
  @Autowired
  DeviceService deviceService
  
  def "should register new device"(NetworkDeviceType deviceType, String macAddress) {
    when:
        def response = webClient.post()
          .uri("/$R_DEVICE/${deviceType.label()}/$macAddress")
          .exchange()
    then:
        response
          .expectStatus().isCreated()
          .expectHeader().location("/$R_DEVICE/${URLEncoder.encode(macAddress, UTF_8)}")
    when:
        response = webClient.get()
          .uri("/$R_DEVICE/$macAddress")
          .exchange()
    then:
        response
          .expectStatus().isOk()
          .expectBody()
          .jsonPath('$.macAddress').isEqualTo(macAddress)
          .jsonPath('$.type').isEqualTo(deviceType.label())
          .jsonPath('$.uplinkMacAddress').doesNotExist()
    where:
        deviceType << [
          NetworkDeviceType.ACCESS_POINT,
          NetworkDeviceType.GATEWAY,
          NetworkDeviceType.SWITCH
        ]
        macAddress << [
          "9c:b0:18:0a:c3:6f",
          "43:76:67:b5:00:71",
          "3e:75:bf:b7:7f:af"
        ]
  }
  
  def "should register new connected device"(
      NetworkDeviceType deviceType, String macAddress, NetworkDeviceType uplinkDeviceType, String uplinkMacAddress) {
    
    given:
        deviceService.register(uplinkDeviceType, uplinkMacAddress, null)
    when:
        def response = webClient.post()
          .uri("/$R_DEVICE/${deviceType.label()}/$macAddress/$uplinkMacAddress")
          .exchange()
    then:
        response
          .expectStatus().isCreated()
          .expectHeader().location("/$R_DEVICE/${URLEncoder.encode(macAddress, UTF_8)}")
    when:
        response = webClient.get()
          .uri("/$R_DEVICE/$macAddress")
          .exchange()
    then:
        response
          .expectStatus().isOk()
          .expectBody()
          .jsonPath('$.macAddress').isEqualTo(macAddress)
          .jsonPath('$.type').isEqualTo(deviceType.label())
          .jsonPath('$.uplinkMacAddress').isEqualTo(uplinkMacAddress)
    where:
        deviceType << [
          NetworkDeviceType.ACCESS_POINT,
          NetworkDeviceType.GATEWAY,
          NetworkDeviceType.SWITCH
        ]
        macAddress << [
          "30:8a:54:91:42:08",
          "3e:c6:bf:e6:33:70",
          "9e:23:4f:ce:10:a0"
        ]
        uplinkDeviceType << [
          NetworkDeviceType.SWITCH,
          NetworkDeviceType.GATEWAY,
          NetworkDeviceType.GATEWAY
        ]
        uplinkMacAddress << [
          "3c:4e:d7:eb:a0:f6",
          "35:0a:e6:7c:2b:7b",
          "e0:6d:46:51:48:87"
        ]
  }
  
  def "should handle error cases correctly during register device"(
      String deviceType, String macAddress, HttpStatus expectedStatus) {
    
    when:
        def response = webClient.post()
          .uri("/$R_DEVICE/${deviceType}/$macAddress")
          .exchange()
    then:
        response
          .expectStatus().isEqualTo(expectedStatus)
    where:
        deviceType << [
          NetworkDeviceType.GATEWAY.label(),
          NetworkDeviceType.SWITCH.label(),
          "invalid",
          NetworkDeviceType.ACCESS_POINT.label(),
          NetworkDeviceType.ACCESS_POINT.label(),
        ]
        macAddress << [
          "invalid",
          "3e:75:bf  :b7:  7f:af",
          "a0:1d:0a:43:5d:df",
          "a0:1d:0a:43:5d:df",
          "a0:1d:0a:43:5d:df"
        ]
        expectedStatus << [
          HttpStatus.BAD_REQUEST,
          HttpStatus.BAD_REQUEST,
          HttpStatus.BAD_REQUEST,
          HttpStatus.CREATED,
          HttpStatus.CONFLICT,
        ]
  }
  
  def "should handle error cases correctly during register connected device"(
      String deviceType, String macAddress, String uplinkMacAddress, HttpStatus expectedStatus) {
    
    when:
        def response = webClient.post()
          .uri("/$R_DEVICE/${deviceType}/$macAddress/$uplinkMacAddress")
          .exchange()
    then:
        response
          .expectStatus().isEqualTo(expectedStatus)
    where:
        deviceType << [
          NetworkDeviceType.SWITCH.label(),
          NetworkDeviceType.SWITCH.label(),
        ]
        macAddress << [
          "79:c5:89:f5:ba:c5",
          "79:c5:89:f5:ba:c5",
        ]
        uplinkMacAddress << [
          "6e:50:0c:89:8b:2a",
          "invalid"
        ]
        expectedStatus << [
          HttpStatus.BAD_REQUEST,
          HttpStatus.BAD_REQUEST,
        ]
  }
}
