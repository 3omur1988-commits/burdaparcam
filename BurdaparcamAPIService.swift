// BurdaparcamAPIService.swift
// burdaparcam.com API – iOS Swift Servis Katmanı
// Swift 5.9+ · iOS 15+ · async/await

import Foundation

// ─────────────────────────────────────────────
//  ANA SERVİS
// ─────────────────────────────────────────────
final class BurdaparcamAPIService {

    // Singleton – isteğe göre DI ile de kullanılabilir
    static let shared = BurdaparcamAPIService()

    private let session: URLSession
    private let baseURL: String
    private let apiKey: String
    private let apiSecret: String
    private let encoder = JSONEncoder()

    init(
        baseURL: String   = BurdaparcamConfig.baseURL,
        apiKey: String    = BurdaparcamConfig.apiKey,
        apiSecret: String = BurdaparcamConfig.apiSecret
    ) {
        self.baseURL   = baseURL
        self.apiKey    = apiKey
        self.apiSecret = apiSecret

        let config = URLSessionConfiguration.default
        config.timeoutIntervalForRequest  = 15
        config.timeoutIntervalForResource = 30
        self.session = URLSession(configuration: config)

        encoder.keyEncodingStrategy = .useDefaultKeys
    }

    // ── Ortak istek oluşturucu ───────────────────────────────────────────
    private func makeRequest<B: Encodable>(endpoint: String, body: B) throws -> URLRequest {
        guard let url = URL(string: "\(baseURL)/\(endpoint)") else {
            throw BurdaparcamError.networkError(
                NSError(domain: "BurdaparcamAPI", code: -1,
                        userInfo: [NSLocalizedDescriptionKey: "Geçersiz URL"])
            )
        }
        var req = URLRequest(url: url)
        req.httpMethod        = "POST"
        req.httpBody          = try encoder.encode(body)
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        req.setValue(apiKey,             forHTTPHeaderField: "apikey")
        req.setValue(apiSecret,          forHTTPHeaderField: "apisecret")
        return req
    }

    // ── Ortak yanıt işleyici ─────────────────────────────────────────────
    private func perform<T: Decodable>(_ request: URLRequest) async throws -> ApiResponse<T> {
        let (data, response): (Data, URLResponse)
        do {
            (data, response) = try await session.data(for: request)
        } catch {
            throw BurdaparcamError.networkError(error)
        }

        if let http = response as? HTTPURLResponse, !(200...299).contains(http.statusCode) {
            throw BurdaparcamError.httpError(http.statusCode)
        }

        do {
            return try JSONDecoder().decode(ApiResponse<T>.self, from: data)
        } catch {
            // Boş data body için fallback
            if data.isEmpty { return ApiResponse(success: true, message: nil, data: nil) }
            throw BurdaparcamError.decodingError(error)
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  1) ÜRÜN KAYDET / GÜNCELLE
    //     Hızlı kullanım:
    //     try await apiService.saveProducts([Product(code:"X", taxRate:18,
    //                                                currency:"TL", salePrice:99, quantity:5)])
    // ──────────────────────────────────────────────────────────────────────
    func saveProducts(_ products: [Product]) async throws -> ApiResponse<AnyCodable> {
        let req = try makeRequest(endpoint: "api/v2/product/save",
                                  body: ProductSaveRequest(products: products))
        return try await perform(req)
    }

    // ──────────────────────────────────────────────────────────────────────
    //  2) FİYAT GÜNCELLE
    // ──────────────────────────────────────────────────────────────────────
    func updatePrices(_ products: [ProductPriceUpdate]) async throws -> ApiResponse<AnyCodable> {
        let req = try makeRequest(endpoint: "api/v2/product/updatePrices",
                                  body: UpdatePricesRequest(products: products))
        return try await perform(req)
    }

    // ──────────────────────────────────────────────────────────────────────
    //  3) STOK GÜNCELLE
    // ──────────────────────────────────────────────────────────────────────
    func updateStocks(_ stocks: [StockUpdate]) async throws -> ApiResponse<AnyCodable> {
        let req = try makeRequest(endpoint: "api/v2/product/updateStocks",
                                  body: UpdateStocksRequest(stocks: stocks))
        return try await perform(req)
    }

    // ──────────────────────────────────────────────────────────────────────
    //  4) SİPARİŞ OLUŞTUR
    // ──────────────────────────────────────────────────────────────────────
    func createOrder(
        customer: OrderCustomer,
        order: OrderInfo,
        products: [OrderProduct]
    ) async throws -> ApiResponse<AnyCodable> {
        let req = try makeRequest(
            endpoint: "api/v2/order/create",
            body: OrderCreateRequest(customer: customer, order: order, products: products)
        )
        return try await perform(req)
    }
}

// ─────────────────────────────────────────────
//  AnyCodable – generic JSON data alanları için
// ─────────────────────────────────────────────
struct AnyCodable: Decodable {
    let value: Any?
    init(from decoder: Decoder) throws {
        let c = try decoder.singleValueContainer()
        if      let v = try? c.decode([String: AnyCodable].self) { value = v }
        else if let v = try? c.decode([AnyCodable].self)         { value = v }
        else if let v = try? c.decode(String.self)               { value = v }
        else if let v = try? c.decode(Double.self)               { value = v }
        else if let v = try? c.decode(Bool.self)                 { value = v }
        else { value = nil }
    }
}

// ─────────────────────────────────────────────
//  ÖRNEK KULLANIM (SwiftUI ViewModel)
// ─────────────────────────────────────────────
/*
@MainActor
class StokViewModel: ObservableObject {

    let api = BurdaparcamAPIService.shared

    // Stok güncelle
    func stokGuncelle() async {
        do {
            let yanit = try await api.updateStocks([
                StockUpdate(code: "URUN001", quantity: 50),
                StockUpdate(code: "URUN002",
                            variants: [VariantStockUpdate(barcode: "6242005878213", quantity: 10)])
            ])
            print("Başarılı:", yanit.message ?? "")
        } catch {
            print("Hata:", error.localizedDescription)
        }
    }

    // Sipariş oluştur
    func siparisOlustur() async {
        let musteri = OrderCustomer(
            name: "Ahmet", lastname: "Yılmaz",
            email: "ahmet@test.com", phone: "+905001234567",
            country: "Türkiye", city: "Bursa",
            district: "Osmangazi", address: "Test sokak no:1"
        )
        let siparis = OrderInfo(paymentType: 92, status: 1, shipmentPrice: 0)
        let urunler = [OrderProduct(code: "URUN001", price: 199.90, quantity: 1)]

        do {
            let yanit = try await api.createOrder(customer: musteri,
                                                  order: siparis,
                                                  products: urunler)
            print("Sipariş oluşturuldu:", yanit.success)
        } catch {
            print("Hata:", error.localizedDescription)
        }
    }
}
*/
