// BurdaparcamModels.swift
// burdaparcam.com API - iOS Swift Modelleri
// Swift 5.9+ · iOS 15+

import Foundation

// ─────────────────────────────────────────────
//  AUTH CONFIG
// ─────────────────────────────────────────────
enum BurdaparcamConfig {
    static let baseURL   = "https://www.burdaparcam.com"
    static let apiKey    = "9c02280d-c7eb-4318-ad23-2bb2b00d57d6"
    static let apiSecret = "26f005a2a9e96189be795039ff1aadcesZyjGBLXzEOy6h5/LQ=="
}

// ─────────────────────────────────────────────
//  GENEL YANIT
// ─────────────────────────────────────────────
struct ApiResponse<T: Decodable>: Decodable {
    let success: Bool
    let message: String?
    let data: T?
}

// ─────────────────────────────────────────────
//  ÜRÜN KAYDET  –  /api/v2/product/save
// ─────────────────────────────────────────────
struct ProductSaveRequest: Encodable {
    let products: [Product]
}

struct Product: Encodable {
    let code: String
    var name: String?            = nil
    var invoiceName: String?     = nil
    var status: Int?             = nil
    var seq: Int?                = nil
    var barcode: String?         = nil
    var mpn: String?             = nil
    var shortDescription: String? = nil
    var brand: String?           = nil
    var category1: String?       = nil
    var category2: String?       = nil
    var category3: String?       = nil
    var longDescription: String? = nil
    var seoTitle: String?        = nil
    var seoKeywords: String?     = nil
    var seoDescription: String?  = nil
    let taxRate: Int
    var shipmentPayment: Int?    = nil
    let currency: String
    var buyPrice: Double?        = nil
    var listPrice: Double?       = nil
    let salePrice: Double
    var fastSalePrice: Double?   = nil
    var dealerPrice1: Double?    = nil
    var dealerPrice2: Double?    = nil
    var dealerPrice3: Double?    = nil
    var dealerPrice4: Double?    = nil
    var unit: Int?               = nil
    let quantity: Int
    var desi: Int?               = nil
    var domestic: Int?           = nil
    var specialCode1: String?    = nil
    var specialCode2: String?    = nil
    var specialCode3: String?    = nil
    var variant1Name: String?    = nil
    var variant2Name: String?    = nil
    var variant3Name: String?    = nil
    var marketplacePrices: [MarketplacePrice]? = nil
    var variants: [ProductVariant]?            = nil
    var images: [ProductImage]?                = nil
    var attributes: [ProductAttribute]?        = nil
}

struct MarketplacePrice: Codable {
    let type: String
    let currency: String
    let price: Double
}

struct ProductVariant: Encodable {
    var value1: String?  = nil
    var value2: String?  = nil
    var value3: String?  = nil
    let barcode: String
    let quantity: Int
    let priceStatus: Int
    let price: Double
}

struct ProductImage: Encodable {
    let imageUrl: String
}

struct ProductAttribute: Encodable {
    let name: String
    let value: String
}

// ─────────────────────────────────────────────
//  FİYAT GÜNCELLE  –  /api/v2/product/updatePrices
// ─────────────────────────────────────────────
struct UpdatePricesRequest: Encodable {
    let products: [ProductPriceUpdate]
}

struct ProductPriceUpdate: Encodable {
    let code: String
    let currency: String
    var buyPrice: Double?      = nil
    var listPrice: Double?     = nil
    let salePrice: Double
    var fastSalePrice: Double? = nil
    var dealerPrice1: Double?  = nil
    var dealerPrice2: Double?  = nil
    var dealerPrice3: Double?  = nil
    var dealerPrice4: Double?  = nil
    var marketplacePrices: [MarketplacePrice]?   = nil
    var variants: [VariantPriceUpdate]?          = nil
}

struct VariantPriceUpdate: Encodable {
    let barcode: String
    let priceStatus: Int
    let price: Double
}

// ─────────────────────────────────────────────
//  STOK GÜNCELLE  –  /api/v2/product/updateStocks
// ─────────────────────────────────────────────
struct UpdateStocksRequest: Encodable {
    let stocks: [StockUpdate]
}

struct StockUpdate: Encodable {
    let code: String
    var quantity: Int?                       = nil  // varyantsız
    var variants: [VariantStockUpdate]?      = nil  // varyantlı
}

struct VariantStockUpdate: Encodable {
    let barcode: String
    let quantity: Int
}

// ─────────────────────────────────────────────
//  SİPARİŞ OLUŞTUR  –  /api/v2/order/create
// ─────────────────────────────────────────────
struct OrderCreateRequest: Encodable {
    let customer: OrderCustomer
    let order: OrderInfo
    let products: [OrderProduct]
}

struct OrderCustomer: Encodable {
    let name: String
    let lastname: String
    let email: String
    let phone: String
    let country: String
    let city: String
    let district: String
    let address: String
    var taxId: String?           = nil
    var taxBranch: String?       = nil
    var nationalId: String?      = nil
    var invoiceTitle: String?    = nil
    var invoiceCity: String?     = nil
    var invoiceDistrict: String? = nil
    var invoiceCountry: String?  = nil
    var invoiceAddress: String?  = nil
}

struct OrderInfo: Encodable {
    let paymentType: Int
    let status: Int
    var note: String?             = nil
    var shipmentBarcode: String?  = nil
    var shipmentCode: String?     = nil
    var shipmentFirmName: String? = nil
    var shipmentUrl: String?      = nil
    var shipmentPrice: Double?    = nil
    var liveSale: Int?            = nil
    var labelId: Int?             = nil
}

struct OrderProduct: Encodable {
    let code: String
    let price: Double
    let quantity: Int
    var variant1: String? = nil
    var variant2: String? = nil
    var variant3: String? = nil
}

// ─────────────────────────────────────────────
//  API HATA TİPLERİ
// ─────────────────────────────────────────────
enum BurdaparcamError: LocalizedError {
    case networkError(Error)
    case httpError(Int)
    case decodingError(Error)
    case apiError(String)

    var errorDescription: String? {
        switch self {
        case .networkError(let e):  return "Bağlantı hatası: \(e.localizedDescription)"
        case .httpError(let code):  return "Sunucu hatası: HTTP \(code)"
        case .decodingError(let e): return "Veri çözümleme hatası: \(e.localizedDescription)"
        case .apiError(let msg):    return "API hatası: \(msg)"
        }
    }
}
