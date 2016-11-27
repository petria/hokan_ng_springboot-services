package org.freakz.hokan_ng_springboot.bot.services.service.scripting;

/**
 * Created by Petri Airio on 4.4.2016.
 * -
 */

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

//ww  w .ja  v  a 2s  .co  m

public class Main {
    public static void main(String[] args) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval("var x = 10; for (var i = 10; i !=11; i--) print(x)");
    }
}