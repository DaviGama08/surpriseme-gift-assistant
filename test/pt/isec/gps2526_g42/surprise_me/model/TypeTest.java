package pt.isec.gps2526_g42.surprise_me.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Type Tests")
class TypeTest {

    @Test
    @DisplayName("Test all Type enum values exist")
    void testAllTypeValues() {
        Type[] values = Type.values();
        assertNotNull(values);
        assertTrue(values.length >= 5); // ANY, PHYSICAL, EXPERIENCE, DIGITAL, DO_IT_YOURSELF
    }

    @Test
    @DisplayName("Test Type.ANY")
    void testAny() {
        assertEquals("Any type", Type.ANY.getType());
    }

    @Test
    @DisplayName("Test Type.PHYSICAL")
    void testPhysical() {
        assertEquals("Physical gift", Type.PHYSICAL.getType());
    }

    @Test
    @DisplayName("Test Type.EXPERIENCE")
    void testExperience() {
        assertEquals("Experience", Type.EXPERIENCE.getType());
    }

    @Test
    @DisplayName("Test Type.DIGITAL")
    void testDigital() {
        assertEquals("Digital gift", Type.DIGITAL.getType());
    }

    @Test
    @DisplayName("Test Type.DO_IT_YOURSELF")
    void testDoItYourself() {
        assertEquals("Do it yourself", Type.DO_IT_YOURSELF.getType());
    }

    @Test
    @DisplayName("Test convertFromString with valid values")
    void testConvertFromStringValid() {
        assertEquals(Type.ANY, Type.convertFromString("Any type"));
        assertEquals(Type.PHYSICAL, Type.convertFromString("Physical gift"));
        assertEquals(Type.EXPERIENCE, Type.convertFromString("Experience"));
        assertEquals(Type.DIGITAL, Type.convertFromString("Digital gift"));
        assertEquals(Type.DO_IT_YOURSELF, Type.convertFromString("Do it yourself"));
    }

    @Test
    @DisplayName("Test convertFromString case insensitive")
    void testConvertFromStringCaseInsensitive() {
        assertEquals(Type.PHYSICAL, Type.convertFromString("physical gift"));
        assertEquals(Type.EXPERIENCE, Type.convertFromString("EXPERIENCE"));
        assertEquals(Type.DIGITAL, Type.convertFromString("DiGiTaL gIfT"));
    }

    @Test
    @DisplayName("Test convertFromString with null throws exception")
    void testConvertFromStringNull() {
        assertThrows(NullPointerException.class, () -> Type.convertFromString(null));
    }

    @Test
    @DisplayName("Test convertFromString with invalid string returns ANY")
    void testConvertFromStringInvalid() {
        assertEquals(Type.ANY, Type.convertFromString("invalid"));
        assertEquals(Type.ANY, Type.convertFromString(""));
    }

    @Test
    @DisplayName("Test getAllTypes returns all type strings")
    void testGetAllTypes() {
        ArrayList<String> allTypes = Type.getAllTypes();
        assertNotNull(allTypes);
        assertTrue(allTypes.size() >= 5);

        boolean hasAny = false;
        boolean hasPhysical = false;
        boolean hasExperience = false;
        boolean hasDigital = false;
        boolean hasDoItYourself = false;

        for (String type : allTypes) {
            if ("Any type".equals(type)) hasAny = true;
            if ("Physical gift".equals(type)) hasPhysical = true;
            if ("Experience".equals(type)) hasExperience = true;
            if ("Digital gift".equals(type)) hasDigital = true;
            if ("Do it yourself".equals(type)) hasDoItYourself = true;
        }

        assertTrue(hasAny);
        assertTrue(hasPhysical);
        assertTrue(hasExperience);
        assertTrue(hasDigital);
        assertTrue(hasDoItYourself);
    }

    @Test
    @DisplayName("Test getType returns correct string")
    void testGetType() {
        assertEquals("Any type", Type.ANY.getType());
        assertEquals("Physical gift", Type.PHYSICAL.getType());
        assertEquals("Experience", Type.EXPERIENCE.getType());
        assertEquals("Digital gift", Type.DIGITAL.getType());
        assertEquals("Do it yourself", Type.DO_IT_YOURSELF.getType());
    }

    @Test
    @DisplayName("Test valueOf works correctly")
    void testValueOf() {
        assertEquals(Type.ANY, Type.valueOf("ANY"));
        assertEquals(Type.PHYSICAL, Type.valueOf("PHYSICAL"));
        assertEquals(Type.EXPERIENCE, Type.valueOf("EXPERIENCE"));
        assertEquals(Type.DIGITAL, Type.valueOf("DIGITAL"));
        assertEquals(Type.DO_IT_YOURSELF, Type.valueOf("DO_IT_YOURSELF"));
    }

    @Test
    @DisplayName("Test convertFromString with trimmed whitespace")
    void testConvertFromStringWithWhitespace() {
        assertEquals(Type.PHYSICAL, Type.convertFromString("  Physical gift  "));
        assertEquals(Type.EXPERIENCE, Type.convertFromString(" Experience "));
    }

    @Test
    @DisplayName("Test Type enum order")
    void testTypeEnumOrder() {
        Type[] values = Type.values();
        assertEquals(Type.ANY, values[0]);
        assertEquals(Type.PHYSICAL, values[1]);
        assertEquals(Type.EXPERIENCE, values[2]);
        assertEquals(Type.DIGITAL, values[3]);
        assertEquals(Type.DO_IT_YOURSELF, values[4]);
    }

    @Test
    @DisplayName("Test all types can be converted back and forth")
    void testConvertBackAndForth() {
        for (Type type : Type.values()) {
            String typeString = type.getType();
            Type converted = Type.convertFromString(typeString);
            assertEquals(type, converted);
        }
    }
}

