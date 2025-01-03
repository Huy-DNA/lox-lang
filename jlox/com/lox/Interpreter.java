package com.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lox.ast.Expr;
import com.lox.ast.Stmt;
import com.lox.ast.TokenType;
import com.lox.ast.Expr.Variable;
import com.lox.object.LoxBoolean;
import com.lox.object.LoxNil;
import com.lox.object.LoxNumber;
import com.lox.object.LoxObject;
import com.lox.object.LoxString;

public class Interpreter {
  private Environment env = new Environment();

  public Interpreter() {}

  public void evaluate(List<Stmt> stmts) throws InterpreterException {
    for (Stmt stmt: stmts) {
      this.evaluateStmt(stmt);
    }
  }

  public LoxObject evaluateStmt(Stmt stmt) throws InterpreterException {
    return switch(stmt) {
      case Stmt.PrintStmt p -> {
        System.out.println(StringifyUtils.stringify(this.evaluateExpr(p.expr)));
        yield new LoxNil();
      }
      case Stmt.ExprStmt e -> this.evaluateExpr(e.expr);
      case Stmt.DeclStmt d -> {
        this.env.define(d.id.lexeme, d.expr == null ? new LoxNil() : this.evaluateExpr(d.expr));
        yield new LoxNil();
      }
      case Stmt.IfStmt i -> {
        final LoxObject condValue = this.evaluateExpr(i.cond);
        if (ValuecheckUtils.isTruthy(condValue)) {
          yield this.evaluateStmt(i.thenBranch);
        } else {
          yield i.elseBranch != null ? this.evaluateStmt(i.elseBranch) : new LoxNil();
        }
      }
      case Stmt.WhileStmt w -> {
        while (ValuecheckUtils.isTruthy(this.evaluateExpr(w.cond))) {
          this.evaluateStmt(w.body);
        }
        yield new LoxNil();
      }
      case Stmt.ForStmt f -> {
        this.env = new Environment(this.env);
        this.evaluateStmt(f.init);
        while (ValuecheckUtils.isTruthy(this.evaluateStmt(f.cond))) {
          this.evaluateStmt(f.body);
          this.evaluateExpr(f.post);
        }
        this.env = this.env.parent;
        yield new LoxNil();
      }
      case Stmt.BlockStmt b -> {
        this.env = new Environment(this.env);
        LoxObject lastValue = new LoxNil();
        for (Stmt s: b.stmts) {
          lastValue = this.evaluateStmt(s);
        }
        this.env = this.env.parent;
        yield lastValue;
      }
      default -> throw new Error("Non-exhaustive check");
    };
  }

  public LoxObject evaluateExpr(Expr expr) throws InterpreterException {
    return switch(expr) {
      case Expr.Binary b -> this.evaluateBinary(b);
      case Expr.Unary u -> this.evaluateUnary(u);
      case Expr.Grouping g -> this.evaluateGrouping(g);
      case Expr.Variable v -> this.evaluateVariable(v);
      case Expr.Literal l -> this.evaluateLiteral(l);
      default -> throw new Error("Non-exhaustive check");
    };
  }

  private LoxObject evaluateBinary(Expr.Binary bin) throws InterpreterException {
    if (bin.op.type == TokenType.EQUAL) {
      final LoxObject right = this.evaluateExpr(bin.right);
      this.env.assign(((Variable)bin.left).var.lexeme, right);
      return right;
    }
    if (bin.op.type == TokenType.OR) {
      final LoxObject left = this.evaluateExpr(bin.left);
      if (ValuecheckUtils.isTruthy(left)) {
        return left;
      }
      final LoxObject right = this.evaluateExpr(bin.right);
      return right;
    }
    if (bin.op.type == TokenType.AND) {
      final LoxObject left = this.evaluateExpr(bin.left);
      if (ValuecheckUtils.isFalsy(left)) {
        return left;
      }
      final LoxObject right = this.evaluateExpr(bin.right);
      return right;
    }
    final LoxObject left = this.evaluateExpr(bin.left);
    final LoxObject right = this.evaluateExpr(bin.right);
    return switch (bin.op.type) { 
      case TokenType.PLUS -> {
        if (!TypecheckUtils.isNumber(left) || !TypecheckUtils.isNumber(right)) {
          throw new InterpreterException(String.format("Unsupported operator '+' on %s and %s", TypecheckUtils.typenameOf(left), TypecheckUtils.typenameOf(right)));
        }
        yield new LoxNumber(((LoxNumber)left).value + ((LoxNumber)right).value);
      }
      case TokenType.MINUS -> {
        if (!TypecheckUtils.isNumber(left) || !TypecheckUtils.isNumber(right)) {
          throw new InterpreterException(String.format("Unsupported operator '-' on %s and %s", TypecheckUtils.typenameOf(left), TypecheckUtils.typenameOf(right)));
        }
        yield new LoxNumber(((LoxNumber)left).value - ((LoxNumber)right).value);
      }
      case TokenType.STAR -> {
        if (!TypecheckUtils.isNumber(left) || !TypecheckUtils.isNumber(right)) {
          throw new InterpreterException(String.format("Unsupported operator '*' on %s and %s", TypecheckUtils.typenameOf(left), TypecheckUtils.typenameOf(right)));
        }
        yield new LoxNumber(((LoxNumber)left).value * ((LoxNumber)right).value);
      }
      case TokenType.SLASH -> {
        if (!TypecheckUtils.isNumber(left) || !TypecheckUtils.isNumber(right)) {
          throw new InterpreterException(String.format("Unsupported operator '/' on %s and %s", TypecheckUtils.typenameOf(left), TypecheckUtils.typenameOf(right)));
        }
        yield new LoxNumber(((LoxNumber)left).value / ((LoxNumber)right).value);
      }
      case TokenType.EQUAL_EQUAL -> {
        if (!TypecheckUtils.isSameType(left, right)) {
          yield new LoxBoolean(false);
        }
        yield new LoxBoolean(left.value().equals(right.value()));
      }
      case TokenType.BANG_EQUAL -> {
        if (!TypecheckUtils.isSameType(left, right)) {
          yield new LoxBoolean(true);
        }
        yield new LoxBoolean(!left.value().equals(right.value()));
      }
      case TokenType.LESS -> {
        if (!TypecheckUtils.isNumber(left) || !TypecheckUtils.isNumber(right)) {
          throw new InterpreterException(String.format("Unsupported operator '<' on %s and %s", TypecheckUtils.typenameOf(left), TypecheckUtils.typenameOf(right)));
        }
        yield new LoxBoolean(((LoxNumber)left).value < ((LoxNumber)right).value);
      }
      case TokenType.LESS_EQUAL -> {
        if (!TypecheckUtils.isNumber(left) || !TypecheckUtils.isNumber(right)) {
          throw new InterpreterException(String.format("Unsupported operator '<=' on %s and %s", TypecheckUtils.typenameOf(left), TypecheckUtils.typenameOf(right)));
        }
        yield new LoxBoolean(((LoxNumber)left).value <= ((LoxNumber)right).value);
      }
      case TokenType.GREATER -> {
        if (!TypecheckUtils.isNumber(left) || !TypecheckUtils.isNumber(right)) {
          throw new InterpreterException(String.format("Unsupported operator '>' on %s and %s", TypecheckUtils.typenameOf(left), TypecheckUtils.typenameOf(right)));
        }
        yield new LoxBoolean(((LoxNumber)left).value > ((LoxNumber)right).value);
      }
      case TokenType.GREATER_EQUAL -> {
        if (!TypecheckUtils.isNumber(left) || !TypecheckUtils.isNumber(right)) {
          throw new InterpreterException(String.format("Unsupported operator '>=' on %s and %s", TypecheckUtils.typenameOf(left), TypecheckUtils.typenameOf(right)));
        }
        yield new LoxBoolean(((LoxNumber)left).value >= ((LoxNumber)right).value);
      } 
      default -> throw new Error(String.format("Unreachable: Unexpected binary operator '%s'", bin.op.lexeme));
    };
  }

  private LoxObject evaluateUnary(Expr.Unary un) throws InterpreterException {
    final LoxObject inner = this.evaluateExpr(un.inner);
    return switch(un.op.type) {
      case TokenType.BANG -> {
        yield new LoxBoolean(ValuecheckUtils.isFalsy(inner));
      }
      case TokenType.MINUS -> {
        if (!TypecheckUtils.isNumber(inner)) {
          throw new InterpreterException(String.format("Unsupported operator '-' on %s", TypecheckUtils.typenameOf(inner)));
        }
        yield new LoxNumber(-((LoxNumber)inner).value);
      }
      default -> throw new Error(String.format("Unreachable: Unexpected unary operator '%s'", un.op.lexeme));
    };
  }

  private LoxObject evaluateGrouping(Expr.Grouping gr) throws InterpreterException {
    return this.evaluateExpr(gr.inner);
  }

  private LoxObject evaluateLiteral(Expr.Literal lit) {
    if (lit.value.literal == null) {
      return new LoxNil();
    }
    return switch (lit.value.literal) {
      case Double d -> new LoxNumber(d);
      case String s -> new LoxString(s);
      case Boolean b -> new LoxBoolean(b);
      default -> throw new Error(String.format("Unreachable: Unexpected literal type"));
    };
  }

  private LoxObject evaluateVariable(Expr.Variable var) throws InterpreterException {
    return this.env.get(var.var.lexeme);
  }
}

class Environment {
  public final Environment parent;
  private final Map<String, LoxObject> values = new HashMap<>();

  Environment() {
    this.parent = null;
  }

  Environment(Environment parent) {
    this.parent = parent;
  }

  public void define(String name, LoxObject value) throws InterpreterException {
    if (values.containsKey(name)) {
      throw new InterpreterException("Redeclared variable '" + name + "'");
    }
    this.values.put(name, value);
  }

  public void assign(String name, LoxObject value) throws InterpreterException {
    for (Environment env = this; env != null; env = env.parent) {
      if (env.values.containsKey(name)) {
        env.values.put(name, value);
        return;
      }
    }
    throw new InterpreterException("Undefined variable '" + name + "'");
  }

  public LoxObject get(String name) throws InterpreterException {
    for (Environment env = this; env != null; env = env.parent) {
      if (env.values.containsKey(name)) {
        return env.values.get(name);
      }
    }
    throw new InterpreterException("Undefined variable '" + name + "'");
  }
}

class TypecheckUtils {
  public static boolean isNumber(LoxObject obj) {
    return obj instanceof LoxNumber;
  }

  public static boolean isString(LoxObject obj) {
    return obj instanceof LoxString;
  }

  public static boolean isBoolean(LoxObject obj) {
    return obj instanceof LoxBoolean;
  }

  public static boolean isNil(LoxObject obj) {
    return obj instanceof LoxNil;
  }

  public static boolean isSameType(LoxObject obj1, LoxObject obj2) {
    return typenameOf(obj1) == typenameOf(obj2);
  }

  public static String typenameOf(LoxObject obj) {
    return switch(obj) {
      case LoxNumber n -> "number";
      case LoxString s -> "string";
      case LoxBoolean b -> "boolean";
      case LoxNil nil -> "nil";
      default -> "object";
    };
  }
}

class ValuecheckUtils {
  public static boolean isFalsy(LoxObject obj) {
    if (TypecheckUtils.isBoolean(obj)) {
      return !((LoxBoolean)obj).value;
    }
    return TypecheckUtils.isNil(obj);
  }
  
  public static boolean isTruthy(LoxObject obj) {
    return !ValuecheckUtils.isFalsy(obj);
  }
}

class StringifyUtils {
  public static String stringify(LoxObject obj) {
    return switch(obj) {
      case LoxNumber n -> String.valueOf(n.value);
      case LoxString s -> s.value;
      case LoxBoolean b -> String.valueOf(b.value);
      case LoxNil nil -> "nil";
      default -> String.valueOf(obj);
    };
  }
}
