package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.TestCatalog;
import com.spring.henallux.hecathlon.dataAccess.dao.ProductDataAccess;
import com.spring.henallux.hecathlon.model.Product;
import com.spring.henallux.hecathlon.model.ProductSort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductDataAccess productDataAccess;

    @InjectMocks
    private ProductService productService;

    @Test
    void searchUsesPageSizeAndChosenSort() {
        when(productDataAccess.search(eq("running"), eq("shoes"), eq(true), eq(new BigDecimal("100")), any()))
                .thenAnswer(invocation -> new PageImpl<>(List.of(TestCatalog.pace500()), invocation.getArgument(4), 1));

        Page<Product> page = productService.findBySport("running", "shoes", true, new BigDecimal("100"), ProductSort.PRICE_DESC, 0);

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(productDataAccess).search(eq("running"), eq("shoes"), eq(true), eq(new BigDecimal("100")), pageable.capture());
        assertEquals(ProductService.PAGE_SIZE, pageable.getValue().getPageSize());
        assertEquals(Sort.Direction.DESC, pageable.getValue().getSort().getOrderFor("price").getDirection());
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void pageAfterTheLastOneShowsTheLastPage() {
        when(productDataAccess.search(eq("running"), eq(null), eq(false), eq(null), any()))
                .thenAnswer(invocation -> {
                    Pageable asked = invocation.getArgument(4);
                    List<Product> content = asked.getPageNumber() == 0 ? List.of(TestCatalog.pace500()) : List.of();
                    return new PageImpl<>(content, asked, 1);
                });

        Page<Product> page = productService.findBySport("running", null, false, null, ProductSort.RELEVANCE, 42);

        assertEquals(0, page.getNumber());
        assertEquals(1, page.getContent().size());
        verify(productDataAccess, times(2)).search(eq("running"), eq(null), eq(false), eq(null), any());
    }

    @Test
    void negativePageIsTheFirstPage() {
        when(productDataAccess.search(any(), any(), eq(false), any(), any()))
                .thenAnswer(invocation -> new PageImpl<>(List.of(), invocation.getArgument(4), 0));

        productService.findBySport("running", null, false, null, ProductSort.RELEVANCE, -3);

        verify(productDataAccess).search(eq("running"), eq(null), eq(false), eq(null),
                eq(PageRequest.of(0, ProductService.PAGE_SIZE, ProductSort.RELEVANCE.getSort())));
    }

    @Test
    void relatedProductsExcludeTheProductItself() {
        Product pace = TestCatalog.pace500();
        when(productDataAccess.findOthersOfSport("running", List.of("pace-500"), 4)).thenReturn(List.of(TestCatalog.air100()));

        List<Product> related = productService.findRelated(pace, 4);

        assertEquals("air-100", related.getFirst().getId());
    }

    @Test
    void unknownProductIsEmpty() {
        when(productDataAccess.findById("inconnu")).thenReturn(null);

        assertTrue(productService.findById("inconnu").isEmpty());
    }
}
