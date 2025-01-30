package javasabr.revolut.interview

import javasabr.revoult.interview.RandomBasedShortURIFactory
import javasabr.revoult.interview.UrlShorter
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class UrlShorterCommonTest extends Specification {

  @Shared
  static RandomBasedShortURIFactory randomFactory = new RandomBasedShortURIFactory("http://mysite.io/short/")

  @Shared
  static UrlShorter shorter = new UrlShorter(randomFactory, 100)

  def "should generate and resolve short URLs"(URI original) {

    given:
        def shorter = new UrlShorter(randomFactory, 100)
    when:
        def shortVersion = shorter.generateShortVersion(original)
        def resolved = shorter.resolveOriginal(shortVersion)
        println("Short URL:$shortVersion")
        println("Resolved:$resolved")
    then:
        shortVersion != null && shortVersion != original
        resolved == original
    where:
        original << [
            URI.create("http://mysite.io/ewfwfewfwfewfewfwefwwefew/fwefwefewf/fewfwef/wfwefewfwef/wefwef/"),
            URI.create("http://mysite.io/wdqwfqwfqwf/qwfww/fewfwef/wfwefewfwef/")
        ]
  }

  def "should generate and resolve short URLs with shared shorter"(URI original) {
    when:
        def shortVersion = shorter.generateShortVersion(original)
        def resolved = shorter.resolveOriginal(shortVersion)
        println("Short URL:$shortVersion")
        println("Resolved:$resolved")
    then:
        shortVersion != null && shortVersion != original
        resolved == original
    where:
        original << [
            URI.create("http://mysite.io/ewfwfewfwfewfewfwefwwefew/fwefwefewf/fewfwef/wfwefewfwef/wefwef/"),
            URI.create("http://mysite.io/wdqwfqwfqwf/qwfww/fewfwef/wfwefewfwef/")
        ]
  }

  def "should not allow to generate short version when short is full"() {
    given:
        def shorter = new UrlShorter(randomFactory, 2)
        shorter.generateShortVersion(URI.create("http://mysite.io/full/1"))
        shorter.generateShortVersion(URI.create("http://mysite.io/full/2"))
    when:
        shorter.generateShortVersion(URI.create("http://mysite.io/full/3"))
    then:
        thrown IllegalStateException
  }

  def "should not resolve short url when it does not exist"() {
    given:
        def shorter = new UrlShorter(randomFactory, 2)
    when:
        shorter.resolveOriginal(URI.create("http://mysite.io/short/1"))
    then:
        thrown IllegalArgumentException
  }

  def "should generate and resolve short URLs in concurrent access correctly"(int urlsCount, int parallel) {
    given:
        def shorter = new UrlShorter(randomFactory, 100000)
        def executor = Executors.newFixedThreadPool(parallel)
        def originalPatter = "http://mysite.io/original/%s"
        def generated = new ConcurrentHashMap<URI, URI>()
    when:

        def pendingFeatures = new ArrayList<CompletableFuture<Void>>();

        urlsCount.times { Integer index
          pendingFeatures.add(CompletableFuture.runAsync({
            def original = URI.create(originalPatter.formatted(index + 1))
            def shortVersion = shorter.generateShortVersion(original)
            generated.put(shortVersion, original)
          }, executor))
        }

        for (final def feature in pendingFeatures) {
          feature.join()
        }

        def pendingResolving = new ArrayList<CompletableFuture<Boolean>>();

        for (final def entry in generated.entrySet()) {
          pendingResolving.add(CompletableFuture.supplyAsync({
            def resolved = shorter.resolveOriginal(entry.key)
            return resolved == entry.value
          }, executor))
        }

    then:
        for (def feature in pendingResolving) {
          feature.join()
        }

    where:
        urlsCount << [100]
        parallel << 10
  }
}
