package com.burdaparcam.api.models

import com.google.gson.annotations.SerializedName

// ─────────────────────────────────────────────
//  AUTH CONFIG
// ─────────────────────────────────────────────
object BurdaparcamConfig {
    const val BASE_URL    = "https://www.burdaparcam.com/"
    const val API_KEY     = "9c02280d-c7eb-4318-ad23-2bb2b00d57d6"
    const val API_SECRET  = "26f005a2a9e96189be795039ff1aadcesZyjGBLXzEOy6h5/LQ=="
}

// ─────────────────────────────────────────────
//  GENEL YANIT SARMALAYICI
// ─────────────────────────────────────────────
data class ApiResponse<T>(
    @SerializedName("success")  val success: Boolean,
    @SerializedName("message")  val message: String?,
    @SerializedName("data")     val data: T?
)

// ─────────────────────────────────────────────
//  ÜRÜN KAYDET  –  /api/v2/product/save
// ─────────────────────────────────────────────
data class ProductSaveRequest(
    @SerializedName("products") val products: List<Product>
)

data class Product(
    @SerializedName("code")             val code: String,
    @SerializedName("name")             val name: String?             = null,
    @SerializedName("invoiceName")      val invoiceName: String?      = null,
    @SerializedName("status")           val status: Int?              = null,
    @SerializedName("seq")              val seq: Int?                 = null,
    @SerializedName("barcode")          val barcode: String?          = null,
    @SerializedName("mpn")              val mpn: String?              = null,
    @SerializedName("shortDescription") val shortDescription: String? = null,
    @SerializedName("brand")            val brand: String?            = null,
    @SerializedName("category1")        val category1: String?        = null,
    @SerializedName("category2")        val category2: String?        = null,
    @SerializedName("category3")        val category3: String?        = null,
    @SerializedName("longDescription")  val longDescription: String?  = null,
    @SerializedName("seoTitle")         val seoTitle: String?         = null,
    @SerializedName("seoKeywords")      val seoKeywords: String?      = null,
    @SerializedName("seoDescription")   val seoDescription: String?   = null,
    @SerializedName("taxRate")          val taxRate: Int,
    @SerializedName("shipmentPayment")  val shipmentPayment: Int?     = null,
    @SerializedName("currency")         val currency: String,
    @SerializedName("buyPrice")         val buyPrice: Double?         = null,
    @SerializedName("listPrice")        val listPrice: Double?        = null,
    @SerializedName("salePrice")        val salePrice: Double,
    @SerializedName("fastSalePrice")    val fastSalePrice: Double?    = null,
    @SerializedName("dealerPrice1")     val dealerPrice1: Double?     = null,
    @SerializedName("dealerPrice2")     val dealerPrice2: Double?     = null,
    @SerializedName("dealerPrice3")     val dealerPrice3: Double?     = null,
    @SerializedName("dealerPrice4")     val dealerPrice4: Double?     = null,
    @SerializedName("unit")             val unit: Int?                = null,
    @SerializedName("quantity")         val quantity: Int,
    @SerializedName("desi")             val desi: Int?                = null,
    @SerializedName("domestic")         val domestic: Int?            = null,
    @SerializedName("specialCode1")     val specialCode1: String?     = null,
    @SerializedName("specialCode2")     val specialCode2: String?     = null,
    @SerializedName("specialCode3")     val specialCode3: String?     = null,
    @SerializedName("variant1Name")     val variant1Name: String?     = null,
    @SerializedName("variant2Name")     val variant2Name: String?     = null,
    @SerializedName("variant3Name")     val variant3Name: String?     = null,
    @SerializedName("marketplacePrices") val marketplacePrices: List<MarketplacePrice>? = null,
    @SerializedName("variants")         val variants: List<ProductVariant>? = null,
    @SerializedName("images")           val images: List<ProductImage>? = null,
    @SerializedName("attributes")       val attributes: List<ProductAttribute>? = null
)

data class MarketplacePrice(
    @SerializedName("type")     val type: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("price")    val price: Double
)

data class ProductVariant(
    @SerializedName("value1")      val value1: String?  = null,
    @SerializedName("value2")      val value2: String?  = null,
    @SerializedName("value3")      val value3: String?  = null,
    @SerializedName("barcode")     val barcode: String,
    @SerializedName("quantity")    val quantity: Int,
    @SerializedName("priceStatus") val priceStatus: Int,
    @SerializedName("price")       val price: Double
)

data class ProductImage(
    @SerializedName("imageUrl") val imageUrl: String
)

data class ProductAttribute(
    @SerializedName("name")  val name: String,
    @SerializedName("value") val value: String
)

// ─────────────────────────────────────────────
//  FİYAT GÜNCELLE  –  /api/v2/product/updatePrices
// ─────────────────────────────────────────────
data class UpdatePricesRequest(
    @SerializedName("products") val products: List<ProductPriceUpdate>
)

data class ProductPriceUpdate(
    @SerializedName("code")              val code: String,
    @SerializedName("currency")          val currency: String,
    @SerializedName("buyPrice")          val buyPrice: Double?      = null,
    @SerializedName("listPrice")         val listPrice: Double?     = null,
    @SerializedName("salePrice")         val salePrice: Double,
    @SerializedName("fastSalePrice")     val fastSalePrice: Double? = null,
    @SerializedName("dealerPrice1")      val dealerPrice1: Double?  = null,
    @SerializedName("dealerPrice2")      val dealerPrice2: Double?  = null,
    @SerializedName("dealerPrice3")      val dealerPrice3: Double?  = null,
    @SerializedName("dealerPrice4")      val dealerPrice4: Double?  = null,
    @SerializedName("marketplacePrices") val marketplacePrices: List<MarketplacePrice>? = null,
    @SerializedName("variants")          val variants: List<VariantPriceUpdate>? = null
)

data class VariantPriceUpdate(
    @SerializedName("barcode")     val barcode: String,
    @SerializedName("priceStatus") val priceStatus: Int,
    @SerializedName("price")       val price: Double
)

// ─────────────────────────────────────────────
//  STOK GÜNCELLE  –  /api/v2/product/updateStocks
// ─────────────────────────────────────────────
data class UpdateStocksRequest(
    @SerializedName("stocks") val stocks: List<StockUpdate>
)

data class StockUpdate(
    @SerializedName("code")     val code: String,
    @SerializedName("quantity") val quantity: Int?              = null,  // varyantsız ürün
    @SerializedName("variants") val variants: List<VariantStockUpdate>? = null  // varyantlı
)

data class VariantStockUpdate(
    @SerializedName("barcode")  val barcode: String,
    @SerializedName("quantity") val quantity: Int
)

// ─────────────────────────────────────────────
//  SİPARİŞ OLUŞTUR  –  /api/v2/order/create
// ─────────────────────────────────────────────
data class OrderCreateRequest(
    @SerializedName("customer") val customer: OrderCustomer,
    @SerializedName("order")    val order: OrderInfo,
    @SerializedName("products") val products: List<OrderProduct>
)

data class OrderCustomer(
    @SerializedName("name")            val name: String,
    @SerializedName("lastname")        val lastname: String,
    @SerializedName("email")           val email: String,
    @SerializedName("phone")           val phone: String,
    @SerializedName("country")         val country: String,
    @SerializedName("city")            val city: String,
    @SerializedName("district")        val district: String,
    @SerializedName("address")         val address: String,
    @SerializedName("taxId")           val taxId: String?      = null,
    @SerializedName("taxBranch")       val taxBranch: String?  = null,
    @SerializedName("nationalId")      val nationalId: String? = null,
    @SerializedName("invoiceTitle")    val invoiceTitle: String?   = null,
    @SerializedName("invoiceCity")     val invoiceCity: String?    = null,
    @SerializedName("invoiceDistrict") val invoiceDistrict: String? = null,
    @SerializedName("invoiceCountry")  val invoiceCountry: String? = null,
    @SerializedName("invoiceAddress")  val invoiceAddress: String? = null
)

data class OrderInfo(
    @SerializedName("paymentType")      val paymentType: Int,
    @SerializedName("status")           val status: Int,
    @SerializedName("note")             val note: String?             = null,
    @SerializedName("shipmentBarcode")  val shipmentBarcode: String?  = null,
    @SerializedName("shipmentCode")     val shipmentCode: String?     = null,
    @SerializedName("shipmentFirmName") val shipmentFirmName: String? = null,
    @SerializedName("shipmentUrl")      val shipmentUrl: String?      = null,
    @SerializedName("shipmentPrice")    val shipmentPrice: Double?    = null,
    @SerializedName("liveSale")         val liveSale: Int?            = null,
    @SerializedName("labelId")          val labelId: Int?             = null
)

data class OrderProduct(
    @SerializedName("code")     val code: String,
    @SerializedName("price")    val price: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("variant1") val variant1: String? = null,
    @SerializedName("variant2") val variant2: String? = null,
    @SerializedName("variant3") val variant3: String? = null
)
