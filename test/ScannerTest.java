package com.lox;

import java.util.List;
import com.lox.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ScannerTest {
  @Test
  public void testIdentifiers() {
    String source = """
          andy formless fo _ _123 _abc ab123
          abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_
        """;

    Scanner scanner = new Scanner(source);
    Pair<List<Token>, List<ScannerException>> res = scanner.tokenize();

    List<Token> tokens = res.first;
    List<ScannerException> errors = res.second;

    // Test errors
    assertEquals(errors.size(), 0);

    // Test tokens
    assertEquals(tokens.size(), 9); // 8 identifiers + 1 EOF

    assertEquals(tokens.get(0).type, TokenType.IDENTIFIER);
    assertEquals(tokens.get(0).lexeme, "andy");
    assertEquals(tokens.get(0).literal, null);

    assertEquals(tokens.get(1).type, TokenType.IDENTIFIER);
    assertEquals(tokens.get(1).lexeme, "formless");
    assertEquals(tokens.get(1).literal, null);

    assertEquals(tokens.get(2).type, TokenType.IDENTIFIER);
    assertEquals(tokens.get(2).lexeme, "fo");
    assertEquals(tokens.get(2).literal, null);

    assertEquals(tokens.get(3).type, TokenType.IDENTIFIER);
    assertEquals(tokens.get(3).lexeme, "_");
    assertEquals(tokens.get(3).literal, null);

    assertEquals(tokens.get(4).type, TokenType.IDENTIFIER);
    assertEquals(tokens.get(4).lexeme, "_123");
    assertEquals(tokens.get(4).literal, null);

    assertEquals(tokens.get(5).type, TokenType.IDENTIFIER);
    assertEquals(tokens.get(5).lexeme, "_abc");
    assertEquals(tokens.get(5).literal, null);

    assertEquals(tokens.get(6).type, TokenType.IDENTIFIER);
    assertEquals(tokens.get(6).lexeme, "ab123");
    assertEquals(tokens.get(6).literal, null);

    assertEquals(tokens.get(7).type, TokenType.IDENTIFIER);
    assertEquals(tokens.get(7).lexeme, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_");
    assertEquals(tokens.get(7).literal, null);

    assertEquals(tokens.get(8).type, TokenType.EOF);
    assertEquals(tokens.get(8).literal, null);
  }

  @Test
  public void testKeywords() {
    String source = """
          and class else false for fun if nil or return super this true var while
        """;

    Scanner scanner = new Scanner(source);
    Pair<List<Token>, List<ScannerException>> res = scanner.tokenize();

    List<Token> tokens = res.first;
    List<ScannerException> errors = res.second;

    // Test errors
    assertEquals(errors.size(), 0);

    // Test tokens
    assertEquals(tokens.size(), 16); // 15 keywords + 1 EOF

    assertEquals(tokens.get(0).type, TokenType.AND);
    assertEquals(tokens.get(0).lexeme, "and");
    assertEquals(tokens.get(0).literal, null);

    assertEquals(tokens.get(1).type, TokenType.CLASS);
    assertEquals(tokens.get(1).lexeme, "class");
    assertEquals(tokens.get(1).literal, null);

    assertEquals(tokens.get(2).type, TokenType.ELSE);
    assertEquals(tokens.get(2).lexeme, "else");
    assertEquals(tokens.get(2).literal, null);

    assertEquals(tokens.get(3).type, TokenType.FALSE);
    assertEquals(tokens.get(3).lexeme, "false");
    assertEquals(tokens.get(3).literal, null);

    assertEquals(tokens.get(4).type, TokenType.FOR);
    assertEquals(tokens.get(4).lexeme, "for");
    assertEquals(tokens.get(4).literal, null);

    assertEquals(tokens.get(5).type, TokenType.FUN);
    assertEquals(tokens.get(5).lexeme, "fun");
    assertEquals(tokens.get(5).literal, null);

    assertEquals(tokens.get(6).type, TokenType.IF);
    assertEquals(tokens.get(6).lexeme, "if");
    assertEquals(tokens.get(6).literal, null);

    assertEquals(tokens.get(7).type, TokenType.NIL);
    assertEquals(tokens.get(7).lexeme, "nil");
    assertEquals(tokens.get(7).literal, null);

    assertEquals(tokens.get(8).type, TokenType.OR);
    assertEquals(tokens.get(8).lexeme, "or");
    assertEquals(tokens.get(8).literal, null);

    assertEquals(tokens.get(9).type, TokenType.RETURN);
    assertEquals(tokens.get(9).lexeme, "return");
    assertEquals(tokens.get(9).literal, null);

    assertEquals(tokens.get(10).type, TokenType.SUPER);
    assertEquals(tokens.get(10).lexeme, "super");
    assertEquals(tokens.get(10).literal, null);

    assertEquals(tokens.get(11).type, TokenType.THIS);
    assertEquals(tokens.get(11).lexeme, "this");
    assertEquals(tokens.get(11).literal, null);

    assertEquals(tokens.get(12).type, TokenType.TRUE);
    assertEquals(tokens.get(12).lexeme, "true");
    assertEquals(tokens.get(12).literal, null);

    assertEquals(tokens.get(13).type, TokenType.VAR);
    assertEquals(tokens.get(13).lexeme, "var");
    assertEquals(tokens.get(13).literal, null);

    assertEquals(tokens.get(14).type, TokenType.WHILE);
    assertEquals(tokens.get(14).lexeme, "while");
    assertEquals(tokens.get(14).literal, null);

    assertEquals(tokens.get(15).type, TokenType.EOF);
    assertEquals(tokens.get(15).literal, null);
  }
}