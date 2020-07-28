package org.example

import kotlinx.coroutines.*
import kotlinx.coroutines.scheduling.ExperimentalCoroutineDispatcher
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File
import java.io.PrintWriter
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

private var writer: PrintWriter = File("res-semaphore.csv").printWriter()

@InternalCoroutinesApi
fun semaphore(_writer: PrintWriter, parallelism: Int, coroutines: Int, maxPermits: Int, workInside: Int, workOutside: Int) {
    writer = _writer
    writer.println("threads,coroutines,permits,inside,outside,time")

    repeat(10) {
        monteCarloIterationSemaphore(false)
    }

    repeat(20000) {
        monteCarloIterationSemaphore(true)
    }

    if (parallelism < 0)
        for (i in 1..PARALLELISM_MAX)
            runSemaphore(true, i, coroutines, maxPermits, workInside, workOutside)

    if (coroutines < 0)
        for (i in 1..COROUTINES_MAX)
            runSemaphore(true, parallelism, i, maxPermits, workInside, workOutside)

    if (maxPermits < 0)
        for (i in 1..PERMITS_MAX)
            runSemaphore(true, parallelism, coroutines, i, workInside, workOutside)

    if (workInside < 0)
        for (i in 1..WORK_MAX)
            runSemaphore(true, parallelism, coroutines, maxPermits, i, workOutside)

    if (workOutside < 0)
        for (i in 1..WORK_MAX)
            runSemaphore(true, parallelism, coroutines, maxPermits, workInside, i)

    writer.flush()
}

@InternalCoroutinesApi
fun doGeomDistrWork(work: Int) {
    val p = 1.0 / work
    val r = ThreadLocalRandom.current()
    while (true) {
        if (r.nextDouble() < p) break
    }
}

@InternalCoroutinesApi
fun monteCarloIterationSemaphore(f: Boolean) {
    val parallelism = Random.nextInt(1, PARALLELISM_MAX)
    val coroutines = Random.nextInt(1, COROUTINES_MAX)
    val maxPermits = Random.nextInt(1, PERMITS_MAX)
    val workInside = Random.nextInt(1, WORK_MAX)
    val workOutside = Random.nextInt(1, WORK_MAX)
    runSemaphore(f, parallelism, coroutines, maxPermits, workInside, workOutside)
}

@InternalCoroutinesApi
fun runSemaphore(
    print: Boolean,
    parallelism: Int,
    coroutines: Int,
    maxPermits: Int,
    workInside: Int,
    workOutside: Int
) {
    val dispatcher = ExperimentalCoroutineDispatcher(corePoolSize = parallelism, maxPoolSize = parallelism)
    val startTime = System.nanoTime()
    semaphoreInternal(dispatcher, coroutines, maxPermits, workInside, workOutside)
    val endTime = System.nanoTime()
    val time = endTime - startTime
    if (print) {
        writer.println("$parallelism,$coroutines,$maxPermits,$workInside,$workOutside,$time")
    }
    dispatcher.close()
}

@InternalCoroutinesApi
fun semaphoreInternal(
    dispatcher: CoroutineDispatcher,
    coroutines: Int,
    maxPermits: Int,
    workInside: Int,
    workOutside: Int
) = runBlocking {
    val n = BATCH_SIZE / coroutines
    val semaphore = Semaphore(maxPermits)
    val jobs = ArrayList<Job>(coroutines)
    repeat(coroutines) {
        jobs += GlobalScope.launch(dispatcher) {
            repeat(n) {
                semaphore.withPermit {
                    doGeomDistrWork(workInside)
                }
                doGeomDistrWork(workOutside)
            }
        }
    }
    jobs.forEach { it.join() }
}

private const val WORK_INSIDE = 80
private const val WORK_OUTSIDE = 40
private const val BATCH_SIZE = 1000000
private const val PARALLELISM_MAX = 32
private const val COROUTINES_MAX = 64
private const val PERMITS_MAX = 40
private const val WORK_MAX = 100