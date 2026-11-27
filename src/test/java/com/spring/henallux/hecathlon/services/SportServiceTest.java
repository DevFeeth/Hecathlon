package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.TestCatalog;
import com.spring.henallux.hecathlon.dataAccess.dao.SportDataAccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SportServiceTest {
    @Mock
    private SportDataAccess sportDataAccess;

    @InjectMocks
    private SportService sportService;

    @Test
    void sportsComeFromTheDatabase() {
        when(sportDataAccess.findAll()).thenReturn(TestCatalog.sports());

        assertEquals(3, sportService.getSports().size());
        assertEquals("Running", sportService.getSports().getFirst().getName("en"));
        assertEquals("Course à pied", sportService.getSports().getFirst().getName("fr"));
    }

    @Test
    void unknownSportIsEmpty() {
        when(sportDataAccess.findById("curling")).thenReturn(null);

        assertTrue(sportService.findById("curling").isEmpty());
    }
}
