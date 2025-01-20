package javasabr.revolut.interview

import javasabr.revoult.interview.MainCheck
import spock.lang.Specification

class MainCheckTest extends Specification {

  def "should generate Hello"() {
    given:
        def obj = new MainCheck();
    when:
        def result = obj.generateHello()
    then:
        result == "Hello"
  }
}
