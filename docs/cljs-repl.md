### Running REPL

1. Start server with `$ boot dev` - assuming `boot-repl` task exists in `build.boot`

2. When all compiled, in another tab: `$ boot repl -c`, then

```
boot.user=> (start-repl)
```

This result means everything is fine:

```
<< started Weasel server on ws://127.0.0.1:55604 >>
<< waiting for client to connect ... Connection is ws://localhost:55604
Writing boot_cljs_repl.cljs...
 connected! >>
To quit, type: :cljs/quit
nil
```

3. Refresh browser with opened DevTools.

4. In REPL navigate to desired namespace...

 `cljs.user=> (in-ns 'nexus.db)`

 5. You're good to go
