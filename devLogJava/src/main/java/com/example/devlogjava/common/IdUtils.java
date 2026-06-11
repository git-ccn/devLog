package com.example.devlogjava.common;

public final class IdUtils {
    private static final long EPOCH_MS = 1704067200000L;
    private static final long WORKER_ID = 1L;
    private static final long DATACENTER_ID = 1L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = -1L ^ (-1L << DATACENTER_ID_BITS);
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);
    private static final Object LOCK = new Object();

    private static long lastTimestamp = -1L;
    private static long sequence = 0L;

    private IdUtils() {
    }

    public static String defaultId(String id, String prefix) {
        if (id != null && !id.isBlank()) {
            return id;
        }
        return prefix + nextSnowflakeId();
    }

    public static long nextSnowflakeId() {
        if (WORKER_ID > MAX_WORKER_ID || WORKER_ID < 0) {
            throw new IllegalStateException("workerId is invalid");
        }
        if (DATACENTER_ID > MAX_DATACENTER_ID || DATACENTER_ID < 0) {
            throw new IllegalStateException("datacenterId is invalid");
        }
        synchronized (LOCK) {
            long timestamp = currentTime();
            if (timestamp < lastTimestamp) {
                timestamp = lastTimestamp;
            }
            if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) & SEQUENCE_MASK;
                if (sequence == 0) {
                    timestamp = waitNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }
            lastTimestamp = timestamp;
            return ((timestamp - EPOCH_MS) << TIMESTAMP_LEFT_SHIFT)
                    | (DATACENTER_ID << DATACENTER_ID_SHIFT)
                    | (WORKER_ID << WORKER_ID_SHIFT)
                    | sequence;
        }
    }

    private static long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTime();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTime();
        }
        return timestamp;
    }

    private static long currentTime() {
        return System.currentTimeMillis();
    }
}

