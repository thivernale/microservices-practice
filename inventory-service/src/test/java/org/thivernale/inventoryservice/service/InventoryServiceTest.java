package org.thivernale.inventoryservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thivernale.inventoryservice.dto.InventoryResponse;
import org.thivernale.inventoryservice.model.Inventory;
import org.thivernale.inventoryservice.repository.InventoryRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.thivernale.inventoryservice.service.TestDataUtil.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;
    @Captor
    private ArgumentCaptor<Collection<String>> skuCodeListCaptor;
    @Captor
    private ArgumentCaptor<Iterable<Inventory>> inventoryListCaptor;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(inventoryRepository);
    }

    @Test
    void whenAvailableAndReserve_thenUpdateQuantity() {
        Map<String, Double> inventoryRequestMap = getInventoryRequestMap();
        Collection<String> skuCodeList = getSkuCodeList();
        List<Inventory> inventoryList = getInventoryList();
        List<Inventory> inventoryListAfter = inventoryList.stream()
            .map(i -> new Inventory(i.getId(), i.getSkuCode(),
                i.getQuantity() - inventoryRequestMap.get(i.getSkuCode())))
            .collect(Collectors.toList());
        List<InventoryResponse> expectedResponse = inventoryList.stream()
            .map(i -> new InventoryResponse(i.getSkuCode(), i.getQuantity(), true))
            .toList();

        when(inventoryRepository.findBySkuCodeIn(any()))
            .thenReturn(inventoryList);

        List<InventoryResponse> inventoryResponseList = inventoryService.getInventory(inventoryRequestMap, true);

        verify(inventoryRepository, times(1))
            .findBySkuCodeIn(skuCodeListCaptor.capture());

        assertThat(skuCodeListCaptor.getValue())
            .containsExactlyInAnyOrderElementsOf(skuCodeList);

        verify(inventoryRepository, times(1)).saveAll(inventoryListCaptor.capture());

        assertThat(inventoryListCaptor.getValue())
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(inventoryListAfter);

        verifyNoMoreInteractions(inventoryRepository);

        assertThat(inventoryResponseList).containsExactlyInAnyOrderElementsOf(expectedResponse);
    }

    @Test
    void whenAvailableAndNoReserve_thenDoNotUpdateQuantity() {
        Map<String, Double> inventoryRequestMap = getInventoryRequestMap();
        Collection<String> skuCodeList = getSkuCodeList();
        List<Inventory> inventoryList = getInventoryList();

        when(inventoryRepository.findBySkuCodeIn(any()))
            .thenReturn(inventoryList);

        inventoryService.getInventory(inventoryRequestMap, false);

        verify(inventoryRepository, times(1))
            .findBySkuCodeIn(skuCodeListCaptor.capture());

        assertThat(skuCodeListCaptor.getValue())
            .containsExactlyInAnyOrderElementsOf(skuCodeList);

        verify(inventoryRepository, never()).saveAll(inventoryListCaptor.capture());

        verifyNoMoreInteractions(inventoryRepository);
    }

    @Test
    public void whenNotAvailableAndReserve_thenDoNotUpdateQuantity() {
        Map<String, Double> inventoryRequestMap = getInventoryRequestMap();
        Collection<String> skuCodeList = getSkuCodeList();
        List<Inventory> inventoryList = getInventoryList();
        inventoryList.getFirst()
            .setQuantity(0);
        List<InventoryResponse> expectedResponse = inventoryList.stream()
            .map(i -> new InventoryResponse(i.getSkuCode(), i.getQuantity(), i.getQuantity() > 0))
            .toList();

        when(inventoryRepository.findBySkuCodeIn(any()))
            .thenReturn(inventoryList);

        List<InventoryResponse> inventoryResponseList = inventoryService.getInventory(inventoryRequestMap, true);

        verify(inventoryRepository, times(1))
            .findBySkuCodeIn(skuCodeListCaptor.capture());

        assertThat(skuCodeListCaptor.getValue())
            .containsExactlyInAnyOrderElementsOf(skuCodeList);

        verify(inventoryRepository, never()).saveAll(inventoryListCaptor.capture());

        verifyNoMoreInteractions(inventoryRepository);

        assertThat(inventoryResponseList).containsExactlyInAnyOrderElementsOf(expectedResponse);
    }
}
