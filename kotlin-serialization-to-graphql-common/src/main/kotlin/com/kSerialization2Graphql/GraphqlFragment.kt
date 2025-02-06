package com.kSerialization2Graphql

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class GraphqlFragment(
  val defaultArguments: Array<String> = [],
  val unionType: String = ""
)