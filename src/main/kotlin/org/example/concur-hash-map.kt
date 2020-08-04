package org.example

import kotlinx.coroutines.InternalCoroutinesApi
import java.io.File
import java.io.PrintWriter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.HashMap

private var writerMonteCarlo: PrintWriter = File("res-map.csv").printWriter()
private var writerCorrect: PrintWriter = File("res-map.csv").printWriter()
private val random: Random = Random()

@InternalCoroutinesApi
fun map(_writerMonteCarlo: PrintWriter, _writerCorrect: PrintWriter, capacity: Int, loadFactor: Int, addPercentage: Int) {
    writerMonteCarlo = _writerMonteCarlo
    writerCorrect = _writerCorrect
    writerMonteCarlo.println("capacity,factor,percentage,time")
    writerCorrect.println("capacity,factor,percentage,time")

    repeat(10) {
        monteCarloIteration(false)
    }

    repeat(20000) {
        monteCarloIteration(true)
    }

    repeat(100) {
        if (capacity < 0)
            for (i in 1..CAPACITY_MAX)
                run(true, true, i, (loadFactor / 100.0).toFloat(), addPercentage)

        if (loadFactor < 0)
            for (i in 1..LOAD_FACTOR_MAX)
                run(true, true, capacity, (i / 100.0).toFloat(), addPercentage)

        if (addPercentage < 0)
            for (i in 1..PERCENTAGE_MAX)
                run(true, true, capacity, (loadFactor / 100.0).toFloat(), i)
    }

    writerMonteCarlo.flush()
    writerCorrect.flush()
}

@InternalCoroutinesApi
private fun monteCarloIteration(f: Boolean) {
    val capacity = kotlin.random.Random.nextInt(1, CAPACITY_MAX)
    val loadFactor = kotlin.random.Random.nextInt(1, LOAD_FACTOR_MAX)
    val addPercentage = kotlin.random.Random.nextInt(1, PERCENTAGE_MAX)
    run(f, false, capacity, (loadFactor / 100.0).toFloat(), addPercentage)
}

@InternalCoroutinesApi
private fun run(print: Boolean, correct: Boolean, capacity: Int, loadFactor: Float, addPercentage: Int) {
    val hm = HashMap<Int, Int>(capacity, loadFactor)
    val process = Process(hm)
    process.run()
//    println(process.totalTime.get())
    if (print) {
        if (correct)
            writerCorrect.println("$capacity,$loadFactor,$addPercentage,${process.totalTime.get()}")
        else
            writerMonteCarlo.println("$capacity,$loadFactor,$addPercentage,${process.totalTime.get()}")
    }
}

private class Process(private var mp: HashMap<Int, Int>) {
    private val executor = Executors.newFixedThreadPool(10)
    private var uninitializedThreads = AtomicInteger(10)
    private var startTime = AtomicLong(-1L)
    var totalTime = AtomicLong(0)
    private var yieldInvokedInOnStart = AtomicBoolean(false)
    private var spinningTimeBeforeYield = AtomicInteger(1000)

    fun run() {
        repeat(10) {
            executor.submit {
                onStart()
                repeat(10000) {
//                    print('!')
                    val key = random.nextInt(1000)
                    val value = random.nextInt(1000)
                    when (random.nextInt(2)) {
                        0 -> mp.put(key, value)
                        1 -> mp.get(key)
                    }
                }
                totalTime.addAndGet(System.nanoTime() - startTime.get())
//                println(totalTime.get())
            }
        }

        println(1)
        while (uninitializedThreads.get() > 0) {
            // wait
            if (yieldInvokedInOnStart.get()) {
                spinningTimeBeforeYield.set((spinningTimeBeforeYield.get() + 1) / 2)
                yieldInvokedInOnStart.set(false)
            } else {
                spinningTimeBeforeYield.set((spinningTimeBeforeYield.get() * 2)
                    .coerceAtMost(MAX_SPINNING_TIME_BEFORE_YIELD))
            }
        }
        startTime.set(System.nanoTime())

//        println("shut")
        executor.shutdown()
        println("start")
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)
        println("end")
//        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)
//        println("end")
    }

    fun onStart() {
//        println(-1)
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
//        println(0)
    }
}

private const val CAPACITY_MAX = 200
private const val LOAD_FACTOR_MAX = 100
private const val PERCENTAGE_MAX = 100
private const val MAX_SPINNING_TIME_BEFORE_YIELD = 2_000_000