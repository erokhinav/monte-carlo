package org.example

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.PrintWriter
import java.util.Random

private var writerMonteCarlo: PrintWriter = File("res-splay.csv").printWriter()
private var writerCorrect: PrintWriter = File("res-splay.csv").printWriter()
val random: Random = Random()

@InternalCoroutinesApi
fun splay(_writerMonteCarlo: PrintWriter, _writerCorrect: PrintWriter, variance: Int, addPercentage: Int) {
    writerMonteCarlo = _writerMonteCarlo
    writerCorrect = _writerCorrect
    writerMonteCarlo.println("variance,percentage,time")
    writerCorrect.println("variance,percentage,time")

    repeat(10) {
        monteCarloIteration(false)
    }

    repeat(20000) {
        monteCarloIteration(true)
    }

    repeat(100) {
        if (variance < 0)
            for (i in 1..VARIANCE_MAX)
                run(true, true, i, addPercentage)

        if (addPercentage < 0)
            for (i in 1..PERCENTAGE_MAX)
                run(true, true, variance, i)
    }

    writerMonteCarlo.flush()
    writerCorrect.flush()
}

@InternalCoroutinesApi
private fun monteCarloIteration(f: Boolean) {
    val variance = kotlin.random.Random.nextInt(1, VARIANCE_MAX)
    val addPercentage = kotlin.random.Random.nextInt(1, PERCENTAGE_MAX)
    run(f, false, variance, addPercentage)
}

@InternalCoroutinesApi
private fun run(print: Boolean, correct: Boolean, variance: Int, addPercentage: Int) {
    val tree = SplayTree<Int>()
    val startTime = System.nanoTime()
    benchmark(tree, variance, addPercentage)
    val endTime = System.nanoTime()
    val time = endTime - startTime
    if (print) {
        if (correct)
            writerCorrect.println("$variance,$addPercentage,$time")
        else
            writerMonteCarlo.println("$variance,$addPercentage,$time")
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