package javasabr.test.support

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import spock.lang.Specification

@ContextConfiguration
@WebFluxTest(properties = "spring.main.allow-bean-definition-overriding=true")
class WebFluxSpecification extends Specification {
    
    @Autowired
    protected WebTestClient webClient
}
