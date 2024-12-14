package javasabr.device.rest

import javasabr.device.model.AccessPointNetworkDevice
import javasabr.device.model.GatewayNetworkDevice
import javasabr.device.model.NetworkDeviceType
import javasabr.device.model.SwitchNetworkDevice
import javasabr.device.rest.dto.NetworkDeviceDto
import javasabr.device.service.DeviceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference

import static javasabr.device.rest.DeviceRestConstants.R_DEVICE
import static javasabr.device.rest.DeviceRestConstants.R_DEVICES

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
  
  def "should return all registered connected device"() {
    
    given:
        def devices = [
          new GatewayNetworkDevice("72:5b:ce:45:9d:96", null),
          new GatewayNetworkDevice("b4:cc:6d:e8:23:00", null),
          
          new SwitchNetworkDevice("56:f0:fb:8e:1a:bd", "72:5b:ce:45:9d:96"),
          new SwitchNetworkDevice("3c:93:9a:c4:16:cf", "b4:cc:6d:e8:23:00"),
          new SwitchNetworkDevice("3e:25:c6:4d:df:06", "b4:cc:6d:e8:23:00"),
          
          new AccessPointNetworkDevice("90:5c:8d:34:37:da", "3e:25:c6:4d:df:06"),
          new AccessPointNetworkDevice("90:71:9f:d5:7a:cd", null),
          new AccessPointNetworkDevice("69:c5:80:20:a5:90", null),
          new AccessPointNetworkDevice("66:58:ba:08:3a:75", "b4:cc:6d:e8:23:00")
        ]
        for (final def device in devices) {
          deviceService.register(device.type(), device.macAddress(), device.uplinkMacAddress())
        }
    
        def responseBodyType = new ParameterizedTypeReference<List<NetworkDeviceDto>>() {}
    
    when:
        def responseBody = webClient.get()
          .uri("/$R_DEVICES")
          .exchange()
          .expectBody(responseBodyType)
          .returnResult()
          .getResponseBody()
    
        def index1 = responseBody.indexOf(NetworkDeviceDto.from(devices[0]))
        def index2 = responseBody.indexOf(NetworkDeviceDto.from(devices[3]))
        def index3 = responseBody.indexOf(NetworkDeviceDto.from(devices[6]))
    
    then:
        index1 < index2
        index2 < index3
  }
}
