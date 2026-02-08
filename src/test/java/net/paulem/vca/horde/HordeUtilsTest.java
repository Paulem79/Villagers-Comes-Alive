package net.paulem.vca.horde;

import net.paulem.vca.utils.HordeUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HordeUtilsTest {
    @Test
    void testExtractJson() {
        assertEquals("{\"a\": 1}", HordeUtils.extractJson("{\"a\": 1}"));
        assertEquals("{\"a\": 1}", HordeUtils.extractJson("Some text {\"a\": 1} more text"));
        assertEquals("{\"a\": 1}", HordeUtils.extractJson("5. {\"a\": 1}"));
        assertEquals("{\"a\": {\"b\": 2}}", HordeUtils.extractJson("{\"a\": {\"b\": 2}} extra"));
        assertEquals("{\"a\": 1", HordeUtils.extractJson("{\"a\": 1")); // Cut off
        assertEquals("{}", HordeUtils.extractJson(""));
        assertEquals("{}", HordeUtils.extractJson(null));
        assertEquals("{}", HordeUtils.extractJson("no json"));
        assertEquals("{}", HordeUtils.extractJson("5."));
        assertEquals("{\"thought_process\": \"test\"}", HordeUtils.extractJson("Here is the JSON: {\"thought_process\": \"test\"}"));
        assertEquals("{\"a\": \"}\"}", HordeUtils.extractJson("{\"a\": \"}\"} extra"));
        assertEquals("{\"a\": \"\\\"}\"}", HordeUtils.extractJson("{\"a\": \"\\\"}\"} extra"));
    }
}
