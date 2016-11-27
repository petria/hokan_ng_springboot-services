package org.freakz.hokan_ng_springboot.bot.services.service.scripting;

/**
 * Created by Petri Airio on 5.4.2016.
 * -
 */

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ExecJavascript {
    private static final int TIMEOUT_SEC = 5;

    public static void main(final String... args) throws Exception {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        final String script =
                "var out = java.lang.System.out;\n" + "out.println( 'JS: Before infinite loop...' );\n"
                        + "while( true ) {print('a')}\n" + "out.println( 'JS: After infinite loop...' );\n";
        execWithThread(engine, script);
//    execWithFuture(engine, script);

    /*		if (args.length == 0) {
            execWithThread(engine, script);
		} else {
			execWithFuture(engine, script);
		}*/
    }

    private static void execWithThread(final ScriptEngine engine, final String script) {
        final Runnable r = new Runnable() {
            public void run() {
                try {
                    engine.eval(script);
                } catch (ScriptException e) {
                    System.out.println("Java: Caught exception from eval(): " + e.getMessage());
                }
            }
        };
        System.out.println("Java: Starting thread...");
        final Thread t = new Thread(r);
        t.start();
        System.out.println("Java: ...thread started");
        try {
            Thread.currentThread().sleep(TIMEOUT_SEC * 1000);
            if (t.isAlive()) {
                System.out.println("Java: Thread alive after timeout, stopping...");
                t.stop();
                System.out.println("Java: ...thread stopped");
            } else {
                System.out.println("Java: Thread not alive after timeout.");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted while waiting for timeout to elapse.");
        }
    }

    private static void execWithFuture(final ScriptEngine engine, final String script) throws Exception {
        final Callable<Object> c = new Callable<Object>() {
            public Object call() throws Exception {
                return engine.eval(script);
            }
        };
        ExecutorService service = Executors.newCachedThreadPool();
        System.out.println("Java: Submitting script eval to thread pool...");
        final Future<Object> f = service.submit(c);
        System.out.println("Java: ...submitted.");
        try {
            final Object result = f.get(TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Java: Interrupted while waiting for script...");
        } catch (ExecutionException e) {
            System.out.println("Java: Script threw exception: " + e.getMessage());
        } catch (TimeoutException e) {
            System.out.println("Java: Timeout! trying to future.cancel()...");
            f.cancel(true);
            System.out.println("Java: ...future.cancel() executed");
            service.shutdown();
        }
    }
}