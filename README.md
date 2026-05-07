# burdaparcam.com API – Android & iOS Entegrasyon Kılavuzu

## Dosya Yapısı

```
android/
  BurdaparcamModels.kt        ← Tüm data class'lar
  BurdaparcamApiService.kt    ← Retrofit servisi + AuthInterceptor

ios/
  BurdaparcamModels.swift     ← Codable modeller + hata tipleri
  BurdaparcamAPIService.swift ← URLSession async/await servisi
```

---

## Android Kurulumu

### 1. build.gradle (app) bağımlılıkları ekle

```kotlin
dependencies {
    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
```

### 2. AndroidManifest.xml – internet izni

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 3. API Key'leri gir

`BurdaparcamModels.kt` içindeki `BurdaparcamConfig` objesini düzenle:

```kotlin
object BurdaparcamConfig {
    const val BASE_URL   = "https://www.burdaparcam.com/"
    const val API_KEY    = "9c02280d-c7eb-4318-ad23-2bb2b00d57d6"
    const val API_SECRET = "26f005a2a9e96189be795039ff1aadcesZyjGBLXzEOy6h5/LQ=="
}
```

### 4. Kullanım (ViewModel)

```kotlin
val apiService = BurdaparcamApiService(enableLogging = BuildConfig.DEBUG)

viewModelScope.launch {
    // Stok güncelle
    val sonuc = apiService.updateStocks(
        listOf(StockUpdate(code = "URUN001", quantity = 50))
    )
    when (sonuc) {
        is ApiResult.Success   -> { /* sonuc.data */ }
        is ApiResult.Error     -> { /* sonuc.message */ }
        ApiResult.NetworkError -> { /* bağlantı yok */ }
    }
}
```

---

## iOS Kurulumu

### 1. Swift Package Manager veya Manuel

Ek kütüphane gerekmez. URLSession + async/await kullanılıyor.

Swift 5.9+ · iOS 15+ · Xcode 15+

### 2. API Key'leri gir

`BurdaparcamModels.swift` içindeki `BurdaparcamConfig` enum'unu düzenle:

```swift
enum BurdaparcamConfig {
    static let baseURL   = "https://www.burdaparcam.com"
    static let apiKey    = "9c02280d-c7eb-4318-ad23-2bb2b00d57d6"
    static let apiSecret = "26f005a2a9e96189be795039ff1aadcesZyjGBLXzEOy6h5/LQ=="
}
```

### 3. Kullanım (SwiftUI ViewModel)

```swift
let api = BurdaparcamAPIService.shared

// Task içinde çağır
Task {
    do {
        let yanit = try await api.updateStocks([
            StockUpdate(code: "URUN001", quantity: 50)
        ])
        print(yanit.success) // true
    } catch {
        print(error.localizedDescription)
    }
}
```

---

## API Endpoint Özeti

| Endpoint | Metod | Açıklama |
|---|---|---|
| `/api/v2/product/save` | POST | Ürün oluştur / güncelle |
| `/api/v2/product/updatePrices` | POST | Fiyat güncelle |
| `/api/v2/product/updateStocks` | POST | Stok güncelle |
| `/api/v2/order/create` | POST | Sipariş oluştur |

## Auth Header

Her istekte aşağıdaki header'lar otomatik eklenir:

```
Content-Type: application/json
apikey: <API_KEY>
apisecret: <API_SECRET>
```
