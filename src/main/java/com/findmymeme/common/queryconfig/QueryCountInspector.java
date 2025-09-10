package com.findmymeme.common.queryconfig;

import lombok.Getter;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

@Component
public class QueryCountInspector implements StatementInspector {
    private final ThreadLocal<Counter> queryCount = new ThreadLocal<>();

    public void startCounter() {
        queryCount.set(new Counter(0L, System.currentTimeMillis()));
    }

    public Counter getQueryCount() {
        return queryCount.get();
    }

    public void clearCounter() {
        queryCount.remove();
    }
    @Override
    public String inspect(String s) {
        Counter counter = queryCount.get();
        if (counter != null) {
            counter.increaseCount();
        }
        return s;
    }

    @Getter
    class Counter {
        private Long count;
        private Long time;

        public Counter(Long count, Long time) {
            this.count = count;
            this.time = time;
        }

        public void increaseCount() {
            count++;
        }
    }
}
