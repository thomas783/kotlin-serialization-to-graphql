package com.kSerialization2Graphql

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class KSerialization2Graphql(
  private val indentSize: Int = 2,
  private val initialArguments: MutableMap<String, Any?> = mutableMapOf()
) {
  var body: String = ""

  fun toGraphqlQuery(): String {
    return """
      {"query": "query $body"}
    """.trimIndent()
  }

  inline fun <reified T : Any> createQuery(
    vararg arguments: Pair<String, Any?> = emptyArray()
  ): String {
    val formattedArguments = arguments.associate { (key, value) ->
      key to value
    }.toMutableMap()

    body = toGraphqlObject(T::class, formattedArguments)

    return toGraphqlQuery()
  }

  fun toGraphqlObject(
    kClass: KClass<*>,
    arguments: MutableMap<String, Any?> = mutableMapOf(),
    depth: Int = 1
  ): String = kClass.memberProperties.joinToString(
    separator = "",
    prefix = "{\n",
    postfix = "${getIndent(depth - 1)}}\n",
    transform = { prop ->
      val serialName = prop.findAnnotation<SerialName>()?.value ?: prop.name
      val kClassifier = prop.returnType.arguments.firstOrNull()?.type?.classifier as? KClass<*>
        ?: prop.returnType.classifier as KClass<*>
      val isSerializable = kClassifier.findAnnotation<Serializable>() != null

      val fragment = prop.findAnnotation<GraphqlFragment>()
      val defaultArguments = fragment?.defaultArguments?.map {
        val (key, value) = it.split(":")
        key to value
      }?.toTypedArray() ?: emptyArray()
      arguments.putAll(defaultArguments)

      val unionType = fragment?.unionType

      getIndent(depth) + when {
        !isSerializable -> "$serialName\n"
        !unionType.isNullOrBlank() -> {
          "$serialName${parseArguments(arguments)} ${
            wrapUnionType(unionType, depth, toGraphqlObject(kClassifier, mutableMapOf(), depth + 2))
          }"
        }

        else -> {
          "$serialName${parseArguments(arguments)} ${
            toGraphqlObject(
              kClassifier,
              mutableMapOf(),
              depth + 1
            )
          }"
        }
      }
    }
  )

  private fun wrapUnionType(unionType: String, depth: Int, block: String): String {
    return "{\n ${getIndent(depth)}...on $unionType $block${getIndent(depth)}}\n"
  }

  private fun getIndent(depth: Int): String = " ".repeat(indentSize).repeat(depth)

  companion object {
    private fun parseArguments(arguments: MutableMap<String, Any?>): String {
      if (arguments.isEmpty()) return ""
      return arguments.entries.joinToString(
        prefix = "(",
        postfix = ")",
        transform = { (key, value) ->
          "$key: $value"
        }
      )
    }
  }
}