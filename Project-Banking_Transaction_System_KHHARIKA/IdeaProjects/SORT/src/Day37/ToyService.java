package Day37;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DemoTest08Mockito {


    @Spy
    ToyService spyToyService;


    @Test
    void method1() {
        // When
        String name = spyToyService.getToyName(1);


        // Then
        assertEquals("Lego", name);


        // We can still verify interactions with the spy
        verify(spyToyService, times(1)).getToyName(1);
    }


    @Test
    void method2() {
        doReturn("Default toy").when(spyToyService).getFallbackName();


        // When
        String name = spyToyService.getToyName(3);


        // Then
        assertEquals("Default toy", name);


        // Verify that getFallbackTitle was called exactly once
        verify(spyToyService, times(1)).getFallbackName();
    }
}