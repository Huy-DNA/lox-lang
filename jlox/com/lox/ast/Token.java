package com.lox.ast;

public class Token {
  public final TokenType type;
  public final String lexeme;
  public final Object literal; // value of `lexeme` interpreted as `type` in the program

  public final int startOffset;
  public final int endOffset;

  public Token(TokenType type, String lexeme, Object literal, int startOffset, int endOffset) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.startOffset = startOffset;
    this.endOffset = endOffset;
  }
}
