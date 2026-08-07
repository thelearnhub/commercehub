rootProject.name = "commercehub"

include(
    // platform
    "platform:eureka-server",
    "platform:config-server",
    "platform:api-gateway",
    "platform:scheduler",

    // libs
    "libs:common-dto",
    "libs:common-exceptions",
    "libs:common-tracing",
    "libs:common-kafka",
    "libs:common-security",
    "libs:common-testing",

    // services
    "services:auth-service",
    "services:user-service",
    "services:product-service",
    "services:cart-service",
    "services:order-service",
    "services:payment-service",
    "services:inventory-service",
    "services:shipping-service",
    "services:notification-service",
    "services:review-service",
    "services:search-service",
    "services:recommendation-service",
    "services:analytics-service",
    "services:fraud-service"
)
