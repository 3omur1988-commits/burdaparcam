package com.burdaparcam.api

import com.burdaparcam.api.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// ─────────────────────────────────────────────
//  RETROFIT INTERFACE
// ─────────────────────────────────────────────
interface BurdaparcamRetrofitApi {

    @POST("api/v2/product/save")
    suspend fun saveProducts(
        @Body body: ProductSaveRequest
    ): Response<ApiResponse<Any>>

    @POST("api/v2/product/updatePrices")
    suspend fun updatePrices(
        @Body body: UpdatePricesRequest
    ): Response<ApiResponse<Any>>

    @POST("api/v2/product/updateStocks")
    suspend fun updateStocks(
        @Body body: UpdateStocksRequest
    ): Response<ApiResponse<Any>>

    @POST("api/v2/order/create")
    suspend fun createOrder(
        @Body body: OrderCreateRequest
    ): Response<ApiResponse<Any>>
}

// ─────────────────────────────────────────────
//  OkHttp AUTH INTERCEPTOR
// ─────────────────────────────────────────────
class AuthInterceptor(
    private val apiKey: String,
    private val apiSecret: String
) : okhttp3.Interceptor {

    override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("apikey", apiKey)
            .addHeader("apisecret", apiSecret)
            .build()
        return chain.proceed(request)
    }
}

// ─────────────────────────────────────────────
//  SONUÇ SARMALAYICI  (sealed class)
// ─────────────────────────────────────────────
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int, val message: String) : ApiResult<Nothing>()
    object NetworkError : ApiResult<Nothing>()
}

// ─────────────────────────────────────────────
//  ANA SERVİS SINIFI
// ─────────────────────────────────────────────
class BurdaparcamApiService(
    private val apiKey: String    = BurdaparcamConfig.API_KEY,
    private val apiSecret: String = BurdaparcamConfig.API_SECRET,
    baseUrl: String               = BurdaparcamConfig.BASE_URL,
    enableLogging: Boolean        = false       // prod'da false bırakın
) {

    private val api: BurdaparcamRetrofitApi

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = if (enableLogging)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(apiKey, apiSecret))
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BurdaparcamRetrofitApi::class.java)
    }

    // ── Yardımcı: HTTP yanıtını ApiResult'a çevir ──────────────────────────
    private fun <T> Response<ApiResponse<T>>.toResult(): ApiResult<ApiResponse<T>> {
        return try {
            if (isSuccessful && body() != null) {
                ApiResult.Success(body()!!)
            } else {
                ApiResult.Error(code(), errorBody()?.string() ?: "Bilinmeyen hata")
            }
        } catch (e: Exception) {
            ApiResult.Error(-1, e.message ?: "Parse hatası")
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  1) ÜRÜN KAYDET / GÜNCELLE
    //     Hızlı kullanım (sadece zorunlu alanlar):
    //     saveProducts(listOf(Product(code="ABC", taxRate=18, currency="TL",
    //                                salePrice=99.9, quantity=10)))
    // ──────────────────────────────────────────────────────────────────────
    suspend fun saveProducts(
        products: List<Product>
    ): ApiResult<ApiResponse<Any>> = try {
        api.saveProducts(ProductSaveRequest(products)).toResult()
    } catch (e: Exception) {
        ApiResult.NetworkError
    }

    // ──────────────────────────────────────────────────────────────────────
    //  2) FİYAT GÜNCELLE
    // ──────────────────────────────────────────────────────────────────────
    suspend fun updatePrices(
        products: List<ProductPriceUpdate>
    ): ApiResult<ApiResponse<Any>> = try {
        api.updatePrices(UpdatePricesRequest(products)).toResult()
    } catch (e: Exception) {
        ApiResult.NetworkError
    }

    // ──────────────────────────────────────────────────────────────────────
    //  3) STOK GÜNCELLE
    // ──────────────────────────────────────────────────────────────────────
    suspend fun updateStocks(
        stocks: List<StockUpdate>
    ): ApiResult<ApiResponse<Any>> = try {
        api.updateStocks(UpdateStocksRequest(stocks)).toResult()
    } catch (e: Exception) {
        ApiResult.NetworkError
    }

    // ──────────────────────────────────────────────────────────────────────
    //  4) SİPARİŞ OLUŞTUR
    // ──────────────────────────────────────────────────────────────────────
    suspend fun createOrder(
        customer: OrderCustomer,
        order: OrderInfo,
        products: List<OrderProduct>
    ): ApiResult<ApiResponse<Any>> = try {
        api.createOrder(OrderCreateRequest(customer, order, products)).toResult()
    } catch (e: Exception) {
        ApiResult.NetworkError
    }
}

// ─────────────────────────────────────────────
//  ÖRNEK KULLANIM (ViewModel / Repository)
// ─────────────────────────────────────────────
/*
class StokViewModel : ViewModel() {

    private val apiService = BurdaparcamApiService(enableLogging = BuildConfig.DEBUG)

    // Stok güncelle
    fun stokGuncelle() {
        viewModelScope.launch {
            val sonuc = apiService.updateStocks(
                listOf(
                    StockUpdate(code = "URUN001", quantity = 50),
                    StockUpdate(
                        code = "URUN002",
                        variants = listOf(
                            VariantStockUpdate(barcode = "6242005878213", quantity = 10),
                            VariantStockUpdate(barcode = "6242005414718", quantity = 4)
                        )
                    )
                )
            )
            when (sonuc) {
                is ApiResult.Success  -> { /* başarılı */ }
                is ApiResult.Error    -> { /* sonuc.message */ }
                ApiResult.NetworkError -> { /* internet yok */ }
            }
        }
    }

    // Sipariş oluştur
    fun siparisOlustur() {
        viewModelScope.launch {
            val musteri = OrderCustomer(
                name = "Ahmet", lastname = "Yılmaz",
                email = "ahmet@test.com", phone = "+905001234567",
                country = "Türkiye", city = "Bursa",
                district = "Osmangazi", address = "Test sokak no:1"
            )
            val siparis = OrderInfo(paymentType = 92, status = 1, shipmentPrice = 0.0)
            val urunler = listOf(
                OrderProduct(code = "URUN001", price = 199.90, quantity = 1)
            )
            apiService.createOrder(musteri, siparis, urunler)
        }
    }
}
*/
