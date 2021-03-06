package org.jooby.assets;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.ConfigFactory;

public class JsHintMultiThreadTest {

  /** The logging system. */
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Test
  public void uglify() throws Exception {
    long s = System.currentTimeMillis();
    char start = 'a';
    char stop = 'z';
    int size = stop - start;
    ExecutorService executor = Executors.newFixedThreadPool(size + 1);
    CountDownLatch latch = new CountDownLatch(size);
    for (char i = 0; i <= size; i++) {
      executor.execute(run(latch, "var " + (char) (i + start) + "=1;"));
    }
    latch.await();
    long e = System.currentTimeMillis();
    log.info(" {} took {}ms each", size, ((e - s) / size));
  }

  private Runnable run(final CountDownLatch latch, final String statement) {
    return () -> {
      try {
        assertEquals(statement, new Jshint().process("/x.js", statement, ConfigFactory.empty()));
      } catch (Exception ex) {
        throw new IllegalStateException(ex);
      } finally {
        latch.countDown();
      }
    };
  }

}
