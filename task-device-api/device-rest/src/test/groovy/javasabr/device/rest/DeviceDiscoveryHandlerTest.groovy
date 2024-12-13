package javasabr.device.rest

import javasabr.device.model.NetworkDeviceType
import javasabr.device.service.DeviceService
import org.springframework.beans.factory.annotation.Autowired

import static javasabr.device.rest.DeviceRestConstants.R_DEVICE

class DeviceDiscoveryHandlerTest extends RestSpecification {
  
  @Autowired
  DeviceService deviceService
  
  def "should return registered device"(NetworkDeviceType deviceType, String macAddress) {
    given:
        deviceService.register(deviceType, macAddress, null)
    when:
        def response = webClient.get()
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
          "56:5e:1e:76:f5:da",
          "1d:3a:69:82:4f:6c",
          "63:19:06:02:e0:5b"
        ]
  }
  
  def "should return registered connected device"(
    NetworkDeviceType deviceType, String macAddress, NetworkDeviceType uplinkDeviceType, String uplinkMacAddress) {
    
    given:
        deviceService.register(uplinkDeviceType, uplinkMacAddress, null)
        deviceService.register(deviceType, macAddress, uplinkMacAddress)
    when:
        def response = webClient.get()
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
          "e7:b2:12:58:9f:48",
          "0c:48:4d:0f:03:e6",
          "10:d7:2f:b2:8b:f3"
        ]
        uplinkDeviceType << [
          NetworkDeviceType.SWITCH,
          NetworkDeviceType.GATEWAY,
          NetworkDeviceType.GATEWAY
        ]
        uplinkMacAddress << [
          "88:66:1f:c3:fa:ae",
          "ec:43:d4:2d:56:a4",
          "69:aa:59:1d:b7:db"
        ]
  }
}
