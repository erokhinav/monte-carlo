package org.example

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.PrintWriter
import java.util.Random

private val writer: PrintWriter = File("res-splay.csv").printWriter()
val random: Random = Random()

@InternalCoroutinesApi
fun splay(variance: Int, addPercentage: Int) {
    writer.println("threads,coroutines,permits,inside,outside,time")

    repeat(10) {
        monteCarloIteration(false)
    }

    repeat(20000) {
        monteCarloIteration(true)
    }

    if (variance < 0)
        for (i in 1..VARIANCE_MAX)
            run(true, i, addPercentage)

    if (addPercentage < 0)
        for (i in 1..PERCENTAGE_MAX)
            run(true, variance, i)

    writer.flush()
}

@InternalCoroutinesApi
private fun monteCarloIteration(f: Boolean) {
    val variance = kotlin.random.Random.nextInt(1, VARIANCE_MAX)
    val addPercentage = kotlin.random.Random.nextInt(1, PERCENTAGE_MAX)
    run(f, variance, addPercentage)
}

@InternalCoroutinesApi
private fun run(print: Boolean, variance: Int, addPercentage: Int) {
    val tree = SplayTree<Int>()
    val startTime = System.nanoTime()
    benchmark(tree, variance, addPercentage)
    val endTime = System.nanoTime()
    val time = endTime - startTime
    if (print) {
        writer.println("$variance,$addPercentage,$time")
    }
}

@InternalCoroutinesApi
private fun benchmark(tree: SplayTree<Int>, variance: Int, addPercentage: Int) = runBlocking {
    var ans = 0
    repeat(BATCH_SIZE) {
        val key = genGaussianDist(1000, variance)
        if(kotlin.random.Random.nextInt(0, 100) < addPercentage) {
            tree.add(key)
        } else {
            ans += tree.find(key)!!.key
        }
    }
}

private fun genGaussianDist(mean: Int, variance: Int): Int {
    return Math.round(mean + random.nextGaussian() * variance).toInt()
}

private const val BATCH_SIZE = 1000000
private const val VARIANCE_MAX = 5000
private const val PERCENTAGE_MAX = 100