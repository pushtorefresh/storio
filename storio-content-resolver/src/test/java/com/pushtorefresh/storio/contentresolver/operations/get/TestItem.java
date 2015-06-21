package com.pushtorefresh.storio.contentresolver.operations.get;

import java.util.concurrent.atomic.AtomicLong;

class TestItem {

    private static final AtomicLong COUNTER = new AtomicLong(0);

    private Long id = COUNTER.incrementAndGet();

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestItem testItem = (TestItem) o;

        return !(id != null ? !id.equals(testItem.id) : testItem.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
