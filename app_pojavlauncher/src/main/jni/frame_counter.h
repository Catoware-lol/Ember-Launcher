#ifndef EMBER_FRAME_COUNTER_H
#define EMBER_FRAME_COUNTER_H
#include <stdatomic.h>
#include <stdint.h>
#include <stdbool.h>
static atomic_bool ember_frames_enabled = false;
static _Atomic(uint32_t) ember_frame_count = 0;
static inline void ember_record_frame(void) {
    if (atomic_load_explicit(&ember_frames_enabled, memory_order_relaxed))
        atomic_fetch_add_explicit(&ember_frame_count, 1, memory_order_relaxed);
}
#endif
