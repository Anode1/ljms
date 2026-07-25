/* ljms -- the worker loop, in C. Put your work in work().
 * Copyright (c) 2001, 2026 Vasili Gavrilov. MIT License; see ../LICENSE. */
#include "queue.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <time.h>
#include <unistd.h>

/* EDIT THESE, or read them from wherever your project keeps such things.
 * There is no configuration file here on purpose: every project has its own
 * way of holding credentials, and a template that insists on one leaves you a
 * layer to remove. */
#define LJMS_DSN  "LJMS"
#define LJMS_USER "ljms"
#define LJMS_PASS ""

#define LJMS_POLL_SECONDS     5
#define LJMS_LEASE_SECONDS 1800
#define LJMS_ERROR_BACKOFF   60
#define LJMS_MAX_ERRORS      10

/* Set by the signal handler. The loop finishes the task in hand and returns,
 * which is why nothing here kills a thread or longjmps out of the work. */
static volatile sig_atomic_t terminating = 0;

/* The task being worked on right now, so a shutdown can hand it back rather
 * than leaving it stranded for the rest of its lease. */
static long in_flight = 0;

static char owner[LJMS_OWNER_MAX + 1];
static char node[64];

static void on_signal(int sig)
{
    (void)sig;
    terminating = 1;
}

/* Your work goes here. Empty on purpose.
 *
 * Dispatch on t->type, read t->ref_id and t->payload, do the job. Return 0 if
 * it worked and -1 if it did not: the caller parks a failure in ERROR with one
 * log line and carries on, and nothing retries it.
 *
 * If a task can outlive LJMS_LEASE_SECONDS it has to say so as it goes, or
 * another worker will take it as well. */
static int work(const ljms_task *t)
{
    (void)t;
    return 0;
}

/* The lease token stamped on every row this worker takes: node, the moment it
 * started, and a random tail. The tail matters because two workers on one host
 * default to the same node name, and identical tokens would let a stalled
 * worker's outcome land on a live sibling's task. */
static void make_owner(void)
{
    time_t now = time(NULL);
    struct tm tm;
    char when[32];

    if (gethostname(node, sizeof node - 1) != 0) {
        (void)snprintf(node, sizeof node, "unknown");
    }
    node[sizeof node - 1] = '\0';

    (void)localtime_r(&now, &tm);
    (void)strftime(when, sizeof when, "%Y-%m-%d %H:%M:%S", &tm);
    (void)snprintf(owner, sizeof owner, "%s %s %lx",
                   node, when, (unsigned long)getpid());
}

/* Sleep in one-second slices, so a shutdown does not have to wait out the
 * whole interval. */
static void nap(int seconds)
{
    int i;
    for (i = 0; i < seconds && terminating == 0; i++) {
        (void)sleep(1u);
    }
}

/* Run one task and record what happened. Never leaves the row in IN_PROCESS
 * if it can help it, because a row left there waits out its lease. */
static void process(const ljms_task *t)
{
    in_flight = t->id;

    (void)printf("ljms: processing id=%ld type=%s ref_id=%ld attempt=%d\n",
                 t->id, t->type, t->ref_id, t->attempts);

    if (work(t) == 0) {
        if (ljms_done(t->id, owner) == 0) {
            (void)fprintf(stderr, "ljms: id=%ld finished, but the lease was already "
                                  "lost; another worker may have run it too\n", t->id);
        }
    } else {
        /* One failure, one line, one terminal row. Fix the cause and restart
         * the worker, which is the only thing that retries anything here. */
        (void)fprintf(stderr, "ljms: id=%ld failed, parked in %s\n", t->id, LJMS_ERROR);
        (void)ljms_error(t->id, owner, "work() returned an error");
    }

    in_flight = 0;
}

int main(void)
{
    int errors = 0;
    time_t last_sweep = 0;

    (void)signal(SIGTERM, on_signal);
    (void)signal(SIGINT, on_signal);

    make_owner();

    if (ljms_connect(LJMS_DSN, LJMS_USER, LJMS_PASS) != 0) {
        (void)fprintf(stderr, "ljms: cannot connect\n");
        return 1;
    }
    (void)printf("ljms: worker starting, owner=%s\n", owner);

    while (terminating == 0) {
        ljms_task t;
        int got;
        time_t now = time(NULL);

        /* Free anything a dead worker was holding, on a timer rather than
         * every cycle: a lease is measured in minutes, so sweeping per task
         * would add a statement to every task in order to find nothing. */
        if (now - last_sweep > (LJMS_LEASE_SECONDS / 2)) {
            int freed = ljms_recover_expired();
            last_sweep = now;
            if (freed > 0) {
                (void)fprintf(stderr, "ljms: recovered %d task(s) with an expired lease\n",
                              freed);
            }
        }

        got = ljms_take(owner, node, LJMS_LEASE_SECONDS, &t);

        if (got < 0) {
            /* The queue machinery itself failed, which is not one task's
             * problem. Back off, and give up rather than filling the log
             * forever; whatever this worker held is freed by its lease. */
            errors++;
            (void)fprintf(stderr, "ljms: queue cycle failed (%d in a row)\n", errors);
            if (errors >= LJMS_MAX_ERRORS) {
                (void)fprintf(stderr, "ljms: giving up after %d failures\n", errors);
                break;
            }
            nap(LJMS_ERROR_BACKOFF);
            continue;
        }

        errors = 0;

        if (got == 0) {
            nap(LJMS_POLL_SECONDS);
            continue;
        }

        process(&t);
    }

    /* Asked to stop while holding a task: hand it back, so another worker can
     * take it at once instead of after the rest of its lease. */
    if (in_flight != 0) {
        (void)ljms_abandon(in_flight, owner);
        (void)fprintf(stderr, "ljms: released id=%ld back to %s\n", in_flight, LJMS_NEW);
    }

    (void)printf("ljms: worker stopped, owner=%s\n", owner);
    ljms_disconnect();
    return 0;
}
