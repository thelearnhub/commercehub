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
| **Notification Service** | 8089 | `/notifications/**` | [http://localhost:8089/v3/api-docs](http://localhost:8089/v3/api-docs) | [http://localhost:8089/swagger-ui.html](http://localhost:8089/swagger-ui.html) |

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
| `/payments/charge` | `POST` | Bearer | `{ "orderId": "...", "amount": 99.99, "paymentProvider": "STRIPE" }` (Header `Idempotency-Key: <uuid>`) | ⬜ Unconsumed | Checkout Payment Step (`/checkout`) |
| `/payments/refund` | `POST` | Bearer | `{ "paymentId": "...", "refundAmount": 99.99, "reason": "..." }` | ⬜ Unconsumed | Order Refund Action |
| `/payments/{id}` | `GET` | Bearer | Path `id` | ⬜ Unconsumed | Payment Receipt Page |
| `/payments/order/{orderId}` | `GET` | Bearer | Path `orderId` | ⬜ Unconsumed | Order Details Payment Status |
| `/payments/{id}/audit-logs` | `GET` | ADMIN | Path `id` | ⬜ Unconsumed | Admin Payment Audit Dashboard |

---

### 📦 Order Service (`http://localhost:8080/orders`)
| Endpoint | Method | Auth | Body / Params | Frontend Status | UI View / Page |
|---|---|---|---|---|---|
| `/orders/checkout` | `POST` | Bearer | `{ "paymentMethod": "STRIPE", "shippingAddressId": "..." }` | ⬜ Unconsumed | Checkout Page (`/checkout`) |
| `/orders/me` | `GET` | Bearer | Header `Authorization: Bearer <token>` | ⬜ Unconsumed | Order History (`/orders`) |
| `/orders/{id}` | `GET` | Bearer | Path `id` | ⬜ Unconsumed | Order Details Page (`/orders/:id`) |
| `/orders/{id}/cancel` | `POST` | Bearer | `{ "reason": "..." }` | ⬜ Unconsumed | Cancel Order Action |
| `/orders/{id}/status` | `PUT` | ADMIN | `{ "status": "SHIPPED" }` | ⬜ Unconsumed | Admin Order Management |

---

### 🔔 Notification Service (`http://localhost:8080/notifications`)
| Endpoint | Method | Auth | Body / Params | Frontend Status | UI View / Page |
|---|---|---|---|---|---|
| `/notifications/send` | `POST` | Bearer | `{ "channel": "EMAIL", "recipient": "...", "subject": "...", "content": "..." }` | ⬜ Unconsumed | Internal Dispatch / Contact Form |
| `/notifications/history`| `GET` | Bearer | `?recipient=<email>` | ⬜ Unconsumed | User Notifications Bell |
| `/notifications/logs` | `GET` | ADMIN | — | ⬜ Unconsumed | Admin Notification Audit |

---

### 🚚 Shipping Service (`http://localhost:8080/shipping`)
| Endpoint | Method | Auth | Body / Params | Frontend Status | UI View / Page |
|---|---|---|---|---|---|
| `/shipping/shipments` | `POST` | Bearer | `{ "orderId": "...", "carrier": "FEDEX", "shippingAddress": "..." }` | ⬜ Unconsumed | Checkout Shipping Step |
| `/shipping/shipments/order/{orderId}`| `GET` | Bearer | Path `orderId` | ⬜ Unconsumed | Order Details Delivery Tracker |
| `/shipping/track/{trackingNumber}` | `GET` | Public | Path `trackingNumber` | ⬜ Unconsumed | Public Tracking Portal (`/track`) |
| `/shipping/shipments/{id}/status` | `PUT` | ADMIN/CARRIER | `{ "status": "IN_TRANSIT" }` | ⬜ Unconsumed | Carrier Management Portal |

---

## 3. Integration Checklist & Progress Track

- [x] **Auth Flow:** Login & Register forms $\rightarrow$ Store `accessToken` in localStorage/memory $\rightarrow$ Attach to fetch headers
- [x] **Product Catalog:** Fetch `/products` and `/products/categories` $\rightarrow$ Render catalog grid & PDP
- [x] **Guest Session Cart:** Generate guest UUID $\rightarrow$ Pass `X-Guest-ID` $\rightarrow$ Add to cart $\rightarrow$ View cart drawer
- [x] **Guest $\rightarrow$ User Cart Merge:** Call `/cart/merge` immediately after successful `/auth/login`
- [x] **User Profile & Address Book:** Render `/users/me` profile and `/users/me/addresses` CRUD
- [x] **Dynamic Price Breakdown:** Call `/products/{id}/price` to show base price, discount, promo code, tax calculation
- [x] **Admin & Seller Operations:** Create/edit products, stock replenishment, stock release/deduction compensation, & audit log history trail
