package com.lox.object;

import java.util.List;

import com.lox.Interpreter;
import com.lox.InterpreterException;

public abstract class LoxCallable extends LoxObject {
  public Object value() {
    return this;
  }
  public abstract LoxObject call(Interpreter interpreter, List<LoxObject> arguments) throws InterpreterException;
  public abstract int arity();
  public abstract String toString();
}
