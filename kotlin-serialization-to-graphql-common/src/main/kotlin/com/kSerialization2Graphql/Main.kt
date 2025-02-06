package com.kSerialization2Graphql

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

fun main() {
  val kSerialization2Graphql = KSerialization2Graphql()
  val query = kSerialization2Graphql.createQuery<CategoryList>(
    "first" to 10,
    "offset" to 0
  )
  println(kSerialization2Graphql.body)
}

@Serializable
data class PageInfo(
  @SerialName("endCursor")
  val endCursor: String,
  @SerialName("hasNextPage")
  val hasNextPage: Boolean,
)

@Serializable
data class CategoryList(
  @SerialName("categories")
  val categories: List<Category>,
)

@Serializable
data class Category(
  @SerialName("id")
  val id: Long,
  @SerialName("name")
  val name: String,
  @SerialName("products")
  val products: List<Product>,
  @SerialName("pageInfo")
  val pageInfo: PageInfo
)

@Serializable
data class Product(
  @SerialName("id")
  val id: Long,
  @SerialName("name")
  val name: String,
  @SerialName("productDetail")
  @GraphqlFragment(unionType = "SHOE")
  val productDetail: ProductDetail,
  @SerialName("options")
  @GraphqlFragment(
    defaultArguments = ["first:10", "offset:5"]
  )
  val options: List<Option>
)

@Serializable
data class ProductDetail(
  @SerialName("id")
  val id: Long,
  @SerialName("name")
  val name: String,
)

@Serializable
data class Option(
  @SerialName("name")
  val name: String,
  @SerialName("id")
  val id: Long,
  @SerialName("pageInfo")
  val pageInfo: PageInfo
)