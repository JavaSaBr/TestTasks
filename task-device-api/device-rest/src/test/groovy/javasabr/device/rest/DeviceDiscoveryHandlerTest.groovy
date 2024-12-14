package javasabr.device.rest

import javasabr.device.model.AccessPointNetworkDevice
import javasabr.device.model.GatewayNetworkDevice
import javasabr.device.model.NetworkDeviceType
import javasabr.device.model.SwitchNetworkDevice
import javasabr.device.rest.dto.NetworkDeviceDto
import javasabr.device.rest.dto.TopologyNodeDto
import javasabr.device.service.DeviceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus

import static javasabr.device.rest.DeviceRestConstants.R_DEVICE
import static javasabr.device.rest.DeviceRestConstants.R_DEVICES
import static javasabr.device.rest.DeviceRestConstants.R_TOPOLOGY

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

  def "should handle error cases correctly during getting device info"(
      String macAddress, HttpStatus expectedStatus) {

    when:
        def response = webClient.get()
            .uri("/$R_DEVICE/$macAddress")
            .exchange()
    then:
        response
            .expectStatus().isEqualTo(expectedStatus)
    where:
        macAddress << [
            "invalid",
            "31:1a:b7:ab:b4:69"
        ]
        expectedStatus << [
            HttpStatus.BAD_REQUEST,
            HttpStatus.NOT_FOUND
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

        devices.each { device ->
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

  def "should return correct device topology"() {
    given:
        def devices = [
            new GatewayNetworkDevice("95:ec:38:da:4b:3f", null),
            new GatewayNetworkDevice("80:79:9c:0b:32:f5", "95:ec:38:da:4b:3f"),

            new SwitchNetworkDevice("0a:10:0b:b1:ce:0c", "80:79:9c:0b:32:f5"),
            new SwitchNetworkDevice("07:14:c3:7e:de:6e", "80:79:9c:0b:32:f5"),

            new AccessPointNetworkDevice("f0:4c:86:a9:d3:7d", "0a:10:0b:b1:ce:0c"),
            new AccessPointNetworkDevice("d0:e8:d6:14:64:89", "07:14:c3:7e:de:6e"),
            new AccessPointNetworkDevice("21:b7:77:51:cf:ad", "07:14:c3:7e:de:6e"),
            new AccessPointNetworkDevice("c3:48:60:95:36:ad", "80:79:9c:0b:32:f5")
        ]

        devices.each { device ->
          deviceService.register(device.type(), device.macAddress(), device.uplinkMacAddress())
        }

    when:

        def topology = webClient.get()
            .uri("/$R_TOPOLOGY/${devices[0].macAddress()}")
            .exchange()
            .expectBody(TopologyNodeDto)
            .returnResult()
            .getResponseBody()

        def rootChildren = topology.children()

    then:
        topology.macAddress() == devices[0].macAddress()
        rootChildren.size() == 1
        rootChildren[0].macAddress() == devices[1].macAddress()
        rootChildren[0].children().size() == 3
        rootChildren[0].children()[0].macAddress() == devices[7].macAddress()
        rootChildren[0].children()[0].children() == null
        rootChildren[0].children()[1].macAddress() == devices[2].macAddress()
        rootChildren[0].children()[1].children().size() == 1
        rootChildren[0].children()[2].macAddress() == devices[3].macAddress()
        rootChildren[0].children()[2].children().size() == 2
  }

  def "should handle error cases correctly during getting device topology"(
      String macAddress, HttpStatus expectedStatus) {
    when:
        def response = webClient.get()
            .uri("/$R_TOPOLOGY/$macAddress")
            .exchange()
    then:
        response
            .expectStatus().isEqualTo(expectedStatus)
    where:
        macAddress << [
            "invalid",
            "31:1a:b7:ab:b4:69"
        ]
        expectedStatus << [
            HttpStatus.BAD_REQUEST,
            HttpStatus.NOT_FOUND
        ]
  }

  def "should return correct full topology"() {
    given:
        def devices = [
            new GatewayNetworkDevice("b0:32:d2:60:89:8b", null),
            new GatewayNetworkDevice("b2:83:91:6b:19:6b", null),
            new GatewayNetworkDevice("c7:19:c4:52:fa:1e", "b0:32:d2:60:89:8b"),

            new SwitchNetworkDevice("1b:fd:e1:03:fe:55", "c7:19:c4:52:fa:1e"),
            new SwitchNetworkDevice("49:a0:16:71:df:37", "b2:83:91:6b:19:6b"),
            new SwitchNetworkDevice("a7:c8:63:3d:e1:22", "b2:83:91:6b:19:6b"),

            new AccessPointNetworkDevice("6b:cb:2c:89:d5:2d", "1b:fd:e1:03:fe:55"),
            new AccessPointNetworkDevice("9e:13:ae:a7:46:80", "1b:fd:e1:03:fe:55"),
            new AccessPointNetworkDevice("e1:a5:b8:2e:15:c3", "49:a0:16:71:df:37"),
            new AccessPointNetworkDevice("f4:11:af:9f:2c:4c", "a7:c8:63:3d:e1:22"),
            new AccessPointNetworkDevice("23:f0:b4:fc:be:09", "a7:c8:63:3d:e1:22")
        ]

        devices.each { device ->
          deviceService.register(device.type(), device.macAddress(), device.uplinkMacAddress())
        }

        def responseBodyType = new ParameterizedTypeReference<List<TopologyNodeDto>>() {}

    when:

        def topology = webClient.get()
            .uri("/$R_TOPOLOGY")
            .exchange()
            .expectBody(responseBodyType)
            .returnResult()
            .getResponseBody()

        def first = topology.find {
          it.macAddress() == devices[0].macAddress()
        }
        def second = topology.find {
          it.macAddress() == devices[1].macAddress()
        }

    then:
        first != null && second != null
        first.children().size() == 1
        first.children()[0].macAddress() == devices[2].macAddress()
        first.children()[0].children().size() == 1
        first.children()[0].children()[0].macAddress() == devices[3].macAddress()
        second.children().size() == 2
        second.children()[0].macAddress() == devices[4].macAddress()
        second.children()[0].children().size() == 1
        second.children()[1].macAddress() == devices[5].macAddress()
        second.children()[1].children().size() == 2
  }
}
