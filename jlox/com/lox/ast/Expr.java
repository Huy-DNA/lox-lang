package com.lox.ast;

import java.util.List;

public abstract class Expr extends SyntaxNode {
  public static class Binary extends Expr {
    public final Expr left;
    public final Token op;
    public final Expr right;

    public Binary(Expr left, Token op, Expr right) {
      this.left = left;
      this.op = op;
      this.right = right;
    }
  }

  public static class Unary extends Expr {
    public final Token op;
    public final Expr inner;

    public Unary(Token op, Expr inner) {
      this.op = op;
      this.inner = inner;
    }
  }

  public static class Literal extends Expr {
    public final Token value;

    public Literal(Token value) {
      this.value = value;
    }
  }

  public static class Variable extends Expr {
    public final Token var;

    public Variable(Token var) {
      this.var = var;
    }
  }

  public static class Grouping extends Expr {
    public final Expr inner;

    public Grouping(Expr inner) {
      this.inner = inner;
    }
  }

  public static class Call extends Expr {
    public final Expr callee;
    public final List<Expr> params;

    public Call(Expr callee, List<Expr> params) {
      this.callee = callee;
      this.params = params;
    }
  }

  public static class Get extends Expr {
    public final Expr object;
    public final Token property;

    public Get(Expr object, Token property) {
      this.object = object;
      this.property = property;
    }
  }

  public static class Set extends Expr {
    public final Expr object;
    public final Token property;
    public final Expr value;

    public Set(Expr object, Token property, Expr value) {
      this.object = object;
      this.property = property;
      this.value = value;
    }
  }

  public static class SuperCall extends Expr {
    public final List<Expr> params;

    public SuperCall(List<Expr> params) {
      this.params = params;
    }
  }

  public static class SuperGet extends Expr {
    public final Token member;

    public SuperGet(Token member) {
      this.member = member;
    }
  }

  public static class This extends Expr {
  }
}
