package com.thelearnhub.commercehub.order.facade;

import com.thelearnhub.commercehub.order.client.CartClient;
import com.thelearnhub.commercehub.order.client.InventoryClient;
import com.thelearnhub.commercehub.order.client.PaymentClient;
import com.thelearnhub.commercehub.order.domain.Order;
import com.thelearnhub.commercehub.order.domain.OrderState;
import com.thelearnhub.commercehub.order.dto.CheckoutRequest;
import com.thelearnhub.commercehub.order.dto.OrderResponse;
import com.thelearnhub.commercehub.order.exception.CheckoutException;
import com.thelearnhub.commercehub.order.pattern.OrderStateMachine;
import com.thelearnhub.commercehub.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutFacadeTest {

    @Mock
    private CartClient cartClient;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private OrderRepository orderRepository;

    @Spy
    private OrderStateMachine orderStateMachine;

    @InjectMocks
    private CheckoutFacade checkoutFacade;

    private String userEmail;
    private CheckoutRequest checkoutRequest;
    private CartClient.CartDto cartDto;

    @BeforeEach
    void setUp() {
        userEmail = "user@example.com";
        checkoutRequest = new CheckoutRequest("456 Market St", "CREDIT_CARD");

        CartClient.CartItemDto item = new CartClient.CartItemDto("SKU-1", "Laptop", new BigDecimal("1000.00"), 1);
        cartDto = new CartClient.CartDto(userEmail, List.of(item), new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("Should process checkout successfully end-to-end")
    void testCheckoutSuccess() {
        when(cartClient.getCart(userEmail)).thenReturn(cartDto);
        when(inventoryClient.reserveStock(any())).thenReturn(true);
        when(paymentClient.chargePayment(eq(userEmail), any(), eq("CREDIT_CARD"))).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            if (o.getId() == null) {
                o.setId(UUID.randomUUID());
            }
            return o;
        });

        OrderResponse response = checkoutFacade.checkout(userEmail, checkoutRequest);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(OrderState.PAID);
        assertThat(response.userEmail()).isEqualTo(userEmail);
        assertThat(response.totalAmount()).isEqualByComparingTo("1000.00");

        verify(cartClient).clearCart(userEmail);
        verify(inventoryClient).reserveStock(any());
        verify(paymentClient).chargePayment(eq(userEmail), any(), eq("CREDIT_CARD"));
    }

    @Test
    @DisplayName("Should throw CheckoutException when cart is empty")
    void testCheckoutEmptyCart() {
        when(cartClient.getCart(userEmail)).thenReturn(new CartClient.CartDto(userEmail, List.of(), BigDecimal.ZERO));

        assertThatThrownBy(() -> checkoutFacade.checkout(userEmail, checkoutRequest))
                .isInstanceOf(CheckoutException.class)
                .hasMessageContaining("Shopping cart is empty");
    }

    @Test
    @DisplayName("Should throw CheckoutException when stock reservation fails")
    void testCheckoutStockReservationFailed() {
        when(cartClient.getCart(userEmail)).thenReturn(cartDto);
        when(inventoryClient.reserveStock(any())).thenReturn(false);

        assertThatThrownBy(() -> checkoutFacade.checkout(userEmail, checkoutRequest))
                .isInstanceOf(CheckoutException.class)
                .hasMessageContaining("Stock reservation failed");
    }

    @Test
    @DisplayName("Should cancel order and release stock when payment fails")
    void testCheckoutPaymentFailed() {
        when(cartClient.getCart(userEmail)).thenReturn(cartDto);
        when(inventoryClient.reserveStock(any())).thenReturn(true);
        when(paymentClient.chargePayment(any(), any(), any())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> checkoutFacade.checkout(userEmail, checkoutRequest))
                .isInstanceOf(CheckoutException.class)
                .hasMessageContaining("Payment failed");

        verify(inventoryClient).releaseStock(any());
    }
}
