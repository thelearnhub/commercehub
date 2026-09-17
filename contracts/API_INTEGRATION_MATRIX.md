# CommerceHub — API Integration Matrix & Contract Checklist

This document tracks all available backend microservice API endpoints, their HTTP request/response contracts, and their integration status with the frontend application (`commercehub-web`).

---

## 1. Central API Gateway Routing (`http://localhost:8080`)

All frontend requests MUST route through the **API Gateway** on port `8080`.

| Target Microservice | Backend Port | Gateway Route Prefix | OpenAPI Spec | Swagger UI |
|---|---|---|---|---|
| **Auth Service** | 8081 | `/auth/**` | [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs) | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) |
| **User Service** | 8082 | `/users/**` | [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs) | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) |
| **Product Service** | 8083 | `/products/**` | [http://localhost:8083/v3/api-docs](http://localhost:8083/v3/api-docs) | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) |
| **Cart Service** | 8084 | `/cart/**` | [http://localhost:8084/v3/api-docs](http://localhost:8084/v3/api-docs) | [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html) |
| **Inventory Service** | 8085 | `/inventory/**` | [http://localhost:8085/v3/api-docs](http://localhost:8085/v3/api-docs) | [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html) |
| **Payment Service** | 8086 | `/payments/**` | [http://localhost:8086/v3/api-docs](http://localhost:8086/v3/api-docs) | [http://localhost:8086/swagger-ui.html](http://localhost:8086/swagger-ui.html) |
| **Order Service** | 8087 | `/orders/**` | [http://localhost:8087/v3/api-docs](http://localhost:8087/v3/api-docs) | [http://localhost:8087/swagger-ui.html](http://localhost:8087/swagger-ui.html) |
| **Shipping Service** | 8088 | `/shipping/**` | [http://localhost:8088/v3/api-docs](http://localhost:8088/v3/api-docs) | [http://localhost:8088/swagger-ui.html](http://localhost:8088/swagger-ui.html) |
| **Notification Service**| 8089 | `/notifications/**` | [http://localhost:8089/v3/api-docs](http://localhost:8089/v3/api-docs) | [http://localhost:8089/swagger-ui.html](http://localhost:8089/swagger-ui.html) |

---

## 2. API Contract & Frontend Consumption Matrix

### 🔐 Auth Service (`http://localhost:8080/auth`)
| Endpoint | Method | Auth | Body / Params | Frontend Status | UI View / Page |
|---|---|---|---|---|---|
| `/auth/register` | `POST` | Public | `{ "email": "...", "password": "..." }` | ✅ Consumed | `/signup` Page & `authApi.ts` |
| `/auth/login` | `POST` | Public | `{ "email": "...", "password": "..." }` | ✅ Consumed | `/login` Page & `authApi.ts` |
| `/auth/refresh` | `POST` | Public | `{ "refreshToken": "..." }` | ✅ Consumed | `apiClient.ts` (Auto Token Refresh) |
| `/auth/google` | `POST` | Public | `{ "idToken": "..." }` | ✅ Consumed | Google Sign-In Button |

---

### 👤 User Service (`http://localhost:8080/users`)
| Endpoint | Method | Auth | Body / Params | Frontend Status | UI View / Page |
|---|---|---|---|---|---|
| `/users/profile` | `POST` | Bearer | `{ "firstName": "...", "lastName": "...", "phone": "..." }` | ✅ Consumed | Initial Profile Onboarding |
| `/users/me` | `GET` | Bearer | Header `Authorization: Bearer <token>` | ✅ Consumed | Header Profile & `/profile` Page |
| `/users/me` | `PUT` | Bearer | `{ "firstName": "...", "lastName": "...", "phone": "..." }` | ✅ Consumed | Profile Settings Edit |
| `/users/me/addresses` | `GET` | Bearer | Header `Authorization: Bearer <token>` | ✅ Consumed | Address Book `/profile` |
| `/users/me/addresses` | `POST` | Bearer | `{ "label": "...", "street": "...", "city": "...", "isDefault": true }` | ✅ Consumed | Add Address Modal |
| `/users/me/addresses/{id}`| `PUT` | Bearer | `{ "label": "...", "street": "...", "city": "...", "isDefault": true }` | ✅ Consumed | Edit Address Modal |
| `/users/me/addresses/{id}`| `DELETE`| Bearer | Header `Authorization: Bearer <token>` | ✅ Consumed | Delete Address Action |

---

### 📦 Product Service (`http://localhost:8080/products`)
| Endpoint | Method | Auth | Body / Params | Frontend Status | UI View / Page |
|---|---|---|---|---|---|
| `/products` | `GET` | Public | `?categoryId=<uuid>` (optional) | ✅ Consumed | Product Catalog Grid (`/catalog`) |
| `/products/{id}` | `GET` | Public | Path `id` | ✅ Consumed | Product Detail Page (`/products/:id`) |
| `/products/categories` | `GET` | Public | — | ✅ Consumed | Catalog Sidebar Navigation |
| `/products/{id}/price` | `GET` | Public | `?discountPercentage=10&promoCode=SAVE50&taxPercentage=10` | ✅ Consumed | Dynamic Decorator Price Calculator |
| `/products` | `POST` | ADMIN/SELLER | `{ "name": "...", "sku": "...", "basePrice": 99.99, "stockQuantity": 100, "categoryId": "..." }` | ✅ Consumed | Seller Portal (`/admin`) |
| `/products/{id}` | `PUT` | ADMIN/SELLER | `{ "name": "...", "basePrice": 99.99, "stockQuantity": 100, "categoryId": "..." }` | ✅ Consumed | Seller Portal (`/admin`) |

---

### 🛒 Cart Service (`http://localhost:8080/cart`)
| Endpoint | Method | Auth | Body / Params | Frontend Status | UI View / Page |
|---|---|---|---|---|---|
| `/cart` | `GET` | Optional | Header `X-Guest-ID: <uuid>` or `Authorization: Bearer <token>` | ✅ Consumed | Cart Drawer & Cart Page (`/cart`) |
| `/cart/items` | `POST` | Optional | `{ "sku": "...", "name": "...", "unitPrice": 19.99, "quantity": 1 }` | ✅ Consumed | Add to Cart Buttons |
| `/cart/items/{sku}` | `PUT` | Optional | `{ "quantity": 2 }` | ✅ Consumed | Quantity Increment/Decrement |
| `/cart/items/{sku}` | `DELETE`| Optional | — | ✅ Consumed | Remove Cart Item Button |
| `/cart` | `DELETE`| Optional | — | ✅ Consumed | Clear Cart Button |
| `/cart/merge` | `POST` | Bearer | `{ "guestCartId": "<uuid>" }` | ✅ Consumed | Login Post-Action (Guest Cart Merge) |

---

### 🏭 Inventory Service (`http://localhost:8080/inventory`)
| Endpoint | Method | Auth | Body / Params | Frontend Status | UI View / Page |
|---|---|---|---|---|---|
| `/inventory/{sku}` | `GET` | Public | Path `sku` | ✅ Consumed | PDP Stock Audit Badge |
| `/inventory/reserve` | `POST` | Bearer | `{ "sku": "...", "quantity": 1, "referenceId": "..." }` | ✅ Consumed | Checkout Initiation Test |
| `/inventory/release` | `POST` | Bearer | `{ "sku": "...", "quantity": 1, "referenceId": "..." }` | ✅ Consumed | Seller Portal (`/admin`) |
| `/inventory/deduct` | `POST` | Bearer | `{ "sku": "...", "quantity": 1, "referenceId": "..." }` | ✅ Consumed | Seller Portal (`/admin`) |
| `/inventory/replenish` | `POST` | ADMIN/SELLER | `{ "sku": "...", "quantity": 50 }` | ✅ Consumed | Seller Portal (`/admin`) |
| `/inventory/{sku}/audit-logs` | `GET` | ADMIN/SELLER | Path `sku` | ✅ Consumed | Seller Portal (`/admin`) |

---

### 💳 Payment Service (`http://localhost:8080/payments`)
| Endpoint | Method | Auth | Body / Params | Frontend Status | UI View / Page |
|---|---|---|---|---|---|
| `/payments/charge` | `POST` | Bearer | `{ "orderId": "...", "amount": 99.99, "currency": "USD" }` | ✅ Consumed | Seller Portal & Checkout (`/admin`) |
| `/payments/refund` | `POST` | Bearer | `{ "paymentId": "...", "amount": 99.99, "reason": "..." }` | ✅ Consumed | Seller Portal (`/admin`) |
| `/payments/{id}` | `GET` | Bearer | Path `id` | ✅ Consumed | Payment Transaction View |
| `/payments/order/{orderId}` | `GET` | Bearer | Path `orderId` | ✅ Consumed | Payment Order Lookup |
| `/payments/{id}/audit-logs` | `GET` | Bearer | Path `id` | ✅ Consumed | Payment Audit Logs |

---

### 🛍️ Order Service (`http://localhost:8080/orders`)
| Endpoint | Method | Auth | Body / Params | Frontend Status | UI View / Page |
|---|---|---|---|---|---|
| `/orders/checkout` | `POST` | Bearer | `{ "shippingAddress": "...", "items": [...] }` | ✅ Consumed | Cart Checkout Facade CTA |
| `/orders/me` | `GET` | Bearer | Header `Authorization: Bearer <token>` | ✅ Consumed | My Orders Page (`/orders`) |
| `/orders/{id}` | `GET` | Bearer | Path `id` | ✅ Consumed | Order Details View |
| `/orders/{id}/cancel` | `POST` | Bearer | Path `id` | ✅ Consumed | State Machine Cancel Order Action |
| `/orders/{id}/status` | `PUT` | ADMIN | `{ "status": "SHIPPED" }` | ✅ Consumed | Admin Status Update |

---

### 🚚 Shipping Service (`http://localhost:8080/shipping`)
| Endpoint | Method | Auth | Body / Params | Frontend Status | UI View / Page |
|---|---|---|---|---|---|
| `/shipping/shipments` | `POST` | Bearer | `{ "orderId": "...", "recipientName": "...", "streetAddress": "..." }` | ✅ Consumed | Label Creation |
| `/shipping/shipments/order/{orderId}` | `GET` | Public | Path `orderId` | ✅ Consumed | Shipment Lookup by Order |
| `/shipping/track/{trackingNumber}` | `GET` | Public | Path `trackingNumber` | ✅ Consumed | Live Package Tracker (`/track`) |
| `/shipping/shipments/{id}/status` | `PUT` | ADMIN/CARRIER | `{ "status": "IN_TRANSIT" }` | ✅ Consumed | Operations Portal (`/admin`) |

---

### 🔔 Notification Service (`http://localhost:8080/notifications`)
| Endpoint | Method | Auth | Body / Params | Frontend Status | UI View / Page |
|---|---|---|---|---|---|
| `/notifications/send` | `POST` | Bearer | `{ "channel": "EMAIL", "recipient": "...", "content": "..." }` | ✅ Consumed | Notification Center Dispatcher |
| `/notifications/history` | `GET` | Bearer | `?recipient=...` | ✅ Consumed | Notification Center Popover |
| `/notifications/logs` | `GET` | ADMIN | — | ✅ Consumed | Admin Notification Audit Logs (`/admin`) |

---

## 3. Integration Checklist & Progress Track

- [x] **Auth Flow:** Login & Register forms $\rightarrow$ Store `accessToken` in localStorage/memory $\rightarrow$ Attach to fetch headers
- [x] **Product Catalog:** Fetch `/products` and `/products/categories` $\rightarrow$ Render catalog grid & PDP
- [x] **Guest Session Cart:** Generate guest UUID $\rightarrow$ Pass `X-Guest-ID` $\rightarrow$ Add to cart $\rightarrow$ View cart drawer
- [x] **Guest $\rightarrow$ User Cart Merge:** Call `/cart/merge` immediately after successful `/auth/login`
- [x] **User Profile & Address Book:** Render `/users/me` profile and `/users/me/addresses` CRUD
- [x] **Dynamic Price Breakdown:** Call `/products/{id}/price` to show base price, discount, promo code, tax calculation
- [x] **Admin & Seller Operations:** Create/edit products, stock replenishment, stock release/deduction compensation, & audit log history trail
- [x] **Order Checkout Facade & Lifecycle:** Initiate checkout, view order history (`/orders`), & State Machine cancellation
- [x] **Payment Charge & Refund:** Process charges with `Idempotency-Key` headers & issue refunds
- [x] **Shipment Tracking:** Track shipments by tracking number (`/track`) & update carrier status transitions
- [x] **Multi-Channel Notifications:** Send & audit `EMAIL`, `SMS`, and `PUSH` notifications in Notification Center

---

## 4. Pre-Seeded Seed Accounts & Sample Catalog Data

### 🔑 Pre-Configured Test Accounts (Password: `Password123!`)
| Role | Email | Name | Default Address |
|---|---|---|---|
| **ADMIN** | `admin@commercehub.com` | Alex Administrator | 100 Commerce Way, Suite 500, San Francisco, CA |
| **SELLER** | `seller@commercehub.com` | Sam Seller (TechHub) | 200 Merchant Blvd, Austin, TX |
| **CUSTOMER** | `customer@commercehub.com` | Charlie Customer | 742 Evergreen Terrace, Springfield, OR |
| **CUSTOMER** | `john.doe@example.com` | John Doe | 123 Main Street, Apt 4B, New York, NY |
| **CUSTOMER** | `jane.smith@example.com` | Jane Smith | 456 Oak Avenue, Seattle, WA |

---

### 🛍️ Pre-Configured Sample Products Catalog
| SKU | Product Name | Category | Base Price | Stock |
|---|---|---|---|---|
| `LAP-MBP-16-M3` | MacBook Pro 16" M3 Max | Electronics | $2,499.99 | 50 |
| `AUD-SONY-XM5-BLK` | Sony WH-1000XM5 Wireless Headphones | Electronics | $399.99 | 120 |
| `MON-34-UW-144HZ` | Ultra-Wide Curved Gaming Monitor 34" | Electronics | $649.50 | 35 |
| `SHOE-NIKE-AF1-WHT-10` | Nike Air Force 1 '07 Sneaker | Fashion | $115.00 | 200 |
| `APP-HD-ORG-BLK-L` | Organic Cotton Oversized Hoodie | Fashion | $78.00 | 85 |
| `FUR-CHR-ERG-GRY` | Ergonomic Mesh Office Chair | Home & Living | $299.00 | 40 |
| `KIT-ESP-SMART-SS` | Smart WiFi Espresso Machine | Home & Living | $499.00 | 60 |
| `BK-DDIA-KLIPP-PB` | Designing Data-Intensive Applications | Books & Media | $45.99 | 150 |
