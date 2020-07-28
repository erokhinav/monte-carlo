package org.example

class Node<T>(var key: T,
              var parent: Node<T>? = null,
              var left: Node<T>? = null,
              var right: Node<T>? = null)