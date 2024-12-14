import javasabr.device.model.AccessPointNetworkDevice
import javasabr.device.model.GatewayNetworkDevice
import javasabr.device.model.NetworkDeviceType
import javasabr.device.model.SwitchNetworkDevice
import javasabr.device.service.exception.DeviceIsAlreadyRegisteredException
import javasabr.device.service.exception.UnsupportedUplinkException
import javasabr.device.service.exception.UplinkDeviceNotFoundException
import javasabr.device.service.impl.InMemoryDeviceService
import spock.lang.Shared
import spock.lang.Specification

class InMemoryDeviceServiceTest extends Specification {

  @Shared
  static def sharedDeviceService = new InMemoryDeviceService()

  def "should register new device"(NetworkDeviceType deviceType, String macAddress, String uplinkMacAddress) {
    when:
        def device = sharedDeviceService.register(deviceType, macAddress, uplinkMacAddress)
    then:
        device.macAddress() == macAddress
        device.uplinkMacAddress() == uplinkMacAddress
        device.type() == deviceType
    where:
        deviceType << [
            NetworkDeviceType.GATEWAY,
            NetworkDeviceType.SWITCH,
            NetworkDeviceType.ACCESS_POINT,
        ]
        macAddress << [
            "9c:b0:18:0a:c3:6f",
            "43:76:67:b5:00:71",
            "3e:75:bf:b7:7f:af"
        ]
        uplinkMacAddress << [
            null,
            "9c:b0:18:0a:c3:6f",
            "43:76:67:b5:00:71"
        ]
  }

  def "should throw expected error during registration"(
      NetworkDeviceType deviceType,
      String macAddress,
      String uplinkMacAddress,
      Class<Throwable> expectedError) {

    given:
        def deviceService = new InMemoryDeviceService()
        deviceService.register(NetworkDeviceType.GATEWAY, "9c:b0:18:0a:c3:6f", null)
        deviceService.register(NetworkDeviceType.ACCESS_POINT, "c4:56:32:d2:d6:40", null)
    when:
        deviceService.register(deviceType, macAddress, uplinkMacAddress)
    then:
        thrown expectedError
    where:
        deviceType << [
            NetworkDeviceType.GATEWAY,
            NetworkDeviceType.SWITCH,
            NetworkDeviceType.ACCESS_POINT,
        ]
        macAddress << [
            "9c:b0:18:0a:c3:6f",
            "43:76:67:b5:00:71",
            "43:76:67:b5:00:71",
        ]
        uplinkMacAddress << [
            null,
            "3e:75:bf:b7:7f:af",
            "c4:56:32:d2:d6:40"
        ]
        expectedError << [
            DeviceIsAlreadyRegisteredException,
            UplinkDeviceNotFoundException,
            UnsupportedUplinkException
        ]
  }

  def "should return registered device"(NetworkDeviceType deviceType, String macAddress, String uplinkMacAddress) {
    given:
        sharedDeviceService.register(deviceType, macAddress, uplinkMacAddress)
    when:
        def device = sharedDeviceService.getByMacAddress(macAddress)
    then:
        device.macAddress() == macAddress
        device.uplinkMacAddress() == uplinkMacAddress
        device.type() == deviceType
    where:
        deviceType << [
            NetworkDeviceType.GATEWAY,
            NetworkDeviceType.SWITCH,
            NetworkDeviceType.ACCESS_POINT,
        ]
        macAddress << [
            "18:a3:6a:7a:7b:d6",
            "b6:7c:4b:5b:fe:e2",
            "6e:9e:11:bc:c9:25"
        ]
        uplinkMacAddress << [
            null,
            "18:a3:6a:7a:7b:d6",
            "b6:7c:4b:5b:fe:e2"
        ]
  }

  def "should return null for unknown device"() {
    given:
        def deviceService = new InMemoryDeviceService()
    when:
        def device = deviceService.getByMacAddress("43:76:67:b5:00:71")
    then:
        device == null
  }

  def "should return empty list for getting all registered devices"() {
    given:
        def deviceService = new InMemoryDeviceService()
    when:
        def devices = deviceService.getAllDevices()
    then:
        devices.isEmpty()
  }

  def "should return correctly sorted all devices"() {
    given:
        def deviceService = new InMemoryDeviceService()
        def devices = [
            new AccessPointNetworkDevice("6b:cb:2c:89:d5:2d", null),
            new AccessPointNetworkDevice("9e:13:ae:a7:46:80", null),
            new AccessPointNetworkDevice("e1:a5:b8:2e:15:c3", null),
            new AccessPointNetworkDevice("f4:11:af:9f:2c:4c", null),
            new AccessPointNetworkDevice("23:f0:b4:fc:be:09", null),

            new GatewayNetworkDevice("b0:32:d2:60:89:8b", null),
            new GatewayNetworkDevice("b2:83:91:6b:19:6b", null),
            new GatewayNetworkDevice("c7:19:c4:52:fa:1e", null),

            new SwitchNetworkDevice("1b:fd:e1:03:fe:55", null),
            new SwitchNetworkDevice("49:a0:16:71:df:37", null),
            new SwitchNetworkDevice("a7:c8:63:3d:e1:22", null),
        ]
        devices.each { device ->
          deviceService.register(device.type(), device.macAddress(), device.uplinkMacAddress())
        }
    when:
        def allDevices = deviceService.getAllDevices()
    then:
        allDevices[0].type() == NetworkDeviceType.GATEWAY
        allDevices[1].type() == NetworkDeviceType.GATEWAY
        allDevices[2].type() == NetworkDeviceType.GATEWAY
        allDevices[3].type() == NetworkDeviceType.SWITCH
        allDevices[4].type() == NetworkDeviceType.SWITCH
        allDevices[5].type() == NetworkDeviceType.SWITCH
        allDevices[6].type() == NetworkDeviceType.ACCESS_POINT
        allDevices[7].type() == NetworkDeviceType.ACCESS_POINT
        allDevices[8].type() == NetworkDeviceType.ACCESS_POINT
        allDevices[9].type() == NetworkDeviceType.ACCESS_POINT
  }

  def "should build null topology for unknown device"() {
    given:
        def deviceService = new InMemoryDeviceService()
    when:
        def topology = deviceService.buildDeviceTopology("43:76:67:b5:00:71")
    then:
        topology == null
  }

  def "should build correct device topology"() {
    given:
        def deviceService = new InMemoryDeviceService()
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
        def topology = deviceService.buildDeviceTopology("95:ec:38:da:4b:3f")
        def rootChildren = topology.children()
    then:
        topology.device().macAddress() == devices[0].macAddress()
        rootChildren.size() == 1
        rootChildren[0].device().macAddress() == devices[1].macAddress()
        rootChildren[0].children().size() == 3
        rootChildren[0].children()[0].device().macAddress() == devices[7].macAddress()
        rootChildren[0].children()[0].children().isEmpty()
        rootChildren[0].children()[1].device().macAddress() == devices[2].macAddress()
        rootChildren[0].children()[1].children().size() == 1
        rootChildren[0].children()[2].device().macAddress() == devices[3].macAddress()
        rootChildren[0].children()[2].children().size() == 2
  }

  def "should build empty full topology"() {
    given:
        def deviceService = new InMemoryDeviceService()
    when:
        def topology = deviceService.buildFullTopology()
    then:
        topology.roots().isEmpty()
  }

  def "should build correct full topology"() {
    given:
        def deviceService = new InMemoryDeviceService()
        def devices = [
            new GatewayNetworkDevice("1d:e0:fc:68:2f:b0", null),
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
        def topology = deviceService.buildFullTopology()
        def roots = topology.roots()
        def secondRoot = roots[1]
        def secondRootChildren = secondRoot.children()
    then:
        roots.size() == 2
        roots[0].device().macAddress() == devices[0].macAddress()
        roots[1].device().macAddress() == devices[1].macAddress()
        secondRootChildren.size() == 1
        secondRootChildren[0].device().macAddress() == devices[2].macAddress()
        secondRootChildren[0].children().size() == 3
        secondRootChildren[0].children()[0].device().macAddress() == devices[8].macAddress()
        secondRootChildren[0].children()[0].children().isEmpty()
        secondRootChildren[0].children()[1].device().macAddress() == devices[3].macAddress()
        secondRootChildren[0].children()[1].children().size() == 1
        secondRootChildren[0].children()[2].device().macAddress() == devices[4].macAddress()
        secondRootChildren[0].children()[2].children().size() == 2
  }
}
