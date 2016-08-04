package org.freakz.hokan_ng_springboot.bot.service.scripting;

/**
 * Created by Petri Airio on 4.4.2016.
 * -
 */
import java.util.List;
//ww  w .ja  v  a 2s  .co  m
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Main {
  public static void main(String[] args) throws ScriptException {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("JavaScript");
    engine.eval("var x = 10; for (var i = 10; i !=11; i--) print(x)");
  }
}