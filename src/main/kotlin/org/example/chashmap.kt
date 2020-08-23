package org.example

import kotlinx.coroutines.InternalCoroutinesApi
import java.io.File
import java.io.PrintWriter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

private var writer: PrintWriter = File("res-map.csv").printWriter()
//private var writerCorrect: PrintWriter = File("res-map.csv").printWriter()
private val random: Random = Random()

@InternalCoroutinesApi
fun map(_writer: PrintWriter, /* capacity: Int,*/ threads: Int, loadFactor: Int, addPercentage: Int, all: Boolean) {
    writer = _writer
    writer.println("threads,factor,percentage,time")

    repeat(10) {
        monteCarloIteration(false)
    }

    if (all) {
        repeat(10000) {
            monteCarloIteration(true)
        }
    } else {
        repeat(5) {
            if (threads < 0)
                for (i in 1..THREADS_MAX)
                    run(true, true, i, (loadFactor / 100.0).toFloat(), addPercentage)

            if (loadFactor < 0)
                for (i in 1..LOAD_FACTOR_MAX)
                    run(true, true, threads, (i / 100.0).toFloat(), addPercentage)

            if (addPercentage < 0)
                for (i in 1..PERCENTAGE_MAX)
                    run(true, true, threads, (loadFactor / 100.0).toFloat(), i)
        }
    }

    writer.flush()
}

@InternalCoroutinesApi
private fun monteCarloIteration(f: Boolean) {
    val threads = kotlin.random.Random.nextInt(1, THREADS_MAX)
    val loadFactor = kotlin.random.Random.nextInt(1, LOAD_FACTOR_MAX)
    val addPercentage = kotlin.random.Random.nextInt(1, PERCENTAGE_MAX)
    run(f, false, threads, (loadFactor / 100.0).toFloat(), addPercentage)
}

@InternalCoroutinesApi
private fun run(print: Boolean, correct: Boolean, threads: Int, loadFactor: Float, addPercentage: Int) {
    val hm = ConcurrentHashMap<Int, Int>(16, loadFactor)
    val process = Process(hm, threads)
    process.run()
    if (print) {
        if (correct)
            writer.println("$threads,$loadFactor,$addPercentage,${process.totalTime.get()}")
        else
            writer.println("$threads,$loadFactor,$addPercentage,${process.totalTime.get()}")
    }
}

private class Process(private var mp: ConcurrentHashMap<Int, Int>, private var threads: Int) {
    private val executor = Executors.newFixedThreadPool(threads)
    private var uninitializedThreads = AtomicInteger(threads)
    private var startTime = AtomicLong(-1L)
    var totalTime = AtomicLong(0)
    private var yieldInvokedInOnStart = AtomicBoolean(false)
    private var spinningTimeBeforeYield = AtomicInteger(1000)

    fun run() {
        repeat(32) {
            executor.submit {
                onStart()
                repeat(50000) {
                    val key = random.nextInt(1000)
                    val value = random.nextInt(1000)
                    when (random.nextInt(2)) {
                        0 -> mp.put(key, value)
                        1 -> mp.get(key)
                    }
                }
                totalTime.addAndGet(System.nanoTime() - startTime.get())
            }
        }

        println(1)
        while (uninitializedThreads.get() > 0) {
            if (yieldInvokedInOnStart.get()) {
                spinningTimeBeforeYield.set((spinningTimeBeforeYield.get() + 1) / 2)
                yieldInvokedInOnStart.set(false)
            } else {
                spinningTimeBeforeYield.set((spinningTimeBeforeYield.get() * 2)
                    .coerceAtMost(MAX_SPINNING_TIME_BEFORE_YIELD))
            }
        }
        startTime.set(System.nanoTime())

        println("start")
        executor.shutdown()
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
        }
        println("end")
    }

    fun onStart() {
        uninitializedThreads.decrementAndGet() // this thread has finished initialization
        // wait for other threads to start
        var i = 1
        while (startTime.get() == -1L) {
            if (i % spinningTimeBeforeYield.get() == 0) {
                yieldInvokedInOnStart.set(true)
                Thread.yield()
            }
            i++
        }
    }
}

private const val THREADS_MAX = 32
private const val LOAD_FACTOR_MAX = 100
private const val PERCENTAGE_MAX = 100
private const val MAX_SPINNING_TIME_BEFORE_YIELD = 2_000_000