package org.example

import kotlinx.coroutines.InternalCoroutinesApi
import java.io.File
import java.io.PrintWriter
import java.util.*

private var writer: PrintWriter = File("res-map.csv").printWriter()
//private var writerCorrect: PrintWriter = File("res-splay.csv").printWriter()
private val random: Random = Random()

@InternalCoroutinesApi
fun rbtree(_writer: PrintWriter, variance: Int, addPercentage: Int, all: Boolean) {
    writer = _writer
    writer.println("variance,percentage,time")

    repeat(10) {
        monteCarloIteration(false)
    }

    if (all)
        repeat(20000) {
            monteCarloIteration(true)
        }
    else
        repeat(5) {
            if (variance < 0)
                for (i in 1..VARIANCE_MAX)
                    run(true, true, i, addPercentage)

            if (addPercentage < 0)
                for (i in 1..PERCENTAGE_MAX)
                    run(true, true, variance, i)
        }

    writer.flush()
}

@InternalCoroutinesApi
private fun monteCarloIteration(f: Boolean) {
    val variance = kotlin.random.Random.nextInt(1, VARIANCE_MAX)
    val addPercentage = kotlin.random.Random.nextInt(1, PERCENTAGE_MAX)
    run(f, false, variance, addPercentage)
}

@InternalCoroutinesApi
private fun run(print: Boolean, correct: Boolean, variance: Int, addPercentage: Int) {
//    val tree = SplayTree(Node(4))
    val tree = TreeMap<Int, Int>()
    val startTime = System.nanoTime()
    benchmark(tree, variance, addPercentage)
    val endTime = System.nanoTime()
    val time = endTime - startTime
    if (print) {
        if (correct)
            writer.println("$variance,$addPercentage,$time")
        else
            writer.println("$variance,$addPercentage,$time")
    }
}

@InternalCoroutinesApi
private fun benchmark(tree: TreeMap<Int, Int>, variance: Int, addPercentage: Int) {
    var ans = 0
    repeat(BATCH_SIZE) {
        val key = genGaussianDist(1000, variance)
        if(kotlin.random.Random.nextInt(0, 100) < addPercentage) {
            tree.put(key, key)
        } else {
            ans += if (tree.containsKey(key)) tree.get(key)!! else 0
        }
    }
}

private fun genGaussianDist(mean: Int, variance: Int): Int {
    return Math.round(mean + random.nextGaussian() * variance).toInt()
}

private const val BATCH_SIZE = 10000
private const val VARIANCE_MAX = 5000
private const val PERCENTAGE_MAX = 100