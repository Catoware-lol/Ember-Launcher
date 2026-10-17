#include <assert.h>
#include <pthread.h>
#include <stdio.h>
#include "frame_counter.h"
static void *render(void *unused) {
    for (int i = 0; i < 10000; i++) ember_record_frame();
    return NULL;
}
int main(void) {
    ember_record_frame();
    assert(atomic_load(&ember_frame_count) == 0);
    atomic_store(&ember_frames_enabled, true);
    pthread_t workers[4];
    for (int i = 0; i < 4; i++) assert(!pthread_create(&workers[i], NULL, render, NULL));
    for (int i = 0; i < 4; i++) pthread_join(workers[i], NULL);
    assert(atomic_load(&ember_frame_count) == 40000);
    atomic_store(&ember_frames_enabled, false);
    ember_record_frame();
    assert(atomic_load(&ember_frame_count) == 40000);
    atomic_store(&ember_frame_count, UINT32_MAX);
    atomic_store(&ember_frames_enabled, true);
    ember_record_frame();
    assert(atomic_load(&ember_frame_count) == 0);
    puts("Native frame counter tests passed");
}
