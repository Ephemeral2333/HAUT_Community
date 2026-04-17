package com.liyh.common.utils;

import java.util.Base64;

public class DatatypeConverter {
     public static String printBase64Binary(byte[] input) {
         return Base64.getEncoder().encodeToString(input);
     }

     public static byte[] parseBase64Binary(String input) {
         return Base64.getDecoder().decode(input);
     }
}
