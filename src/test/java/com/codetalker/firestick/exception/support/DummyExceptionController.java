package com.codetalker.firestick.exception.support;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codetalker.firestick.exception.CodeParsingException;
import com.codetalker.firestick.exception.EmbeddingException;
import com.codetalker.firestick.exception.FileDiscoveryException;
import com.codetalker.firestick.exception.IndexingException;

@RestController
@RequestMapping("/api/test")
public class DummyExceptionController {
    @GetMapping("/file-discovery")
    public String fileDiscovery() { throw new FileDiscoveryException("boom"); }

    @GetMapping("/code-parse")
    public String codeParse() { throw new CodeParsingException("bad code", "/tmp/Foo.java"); }

    @GetMapping("/indexing")
    public String indexing() { throw new IndexingException("index failed"); }

    @GetMapping("/embedding")
    public String embedding() { throw new EmbeddingException("embed failed"); }

    @GetMapping("/generic")
    public String generic() { throw new IllegalStateException("oops"); }

    @GetMapping("/validation-required")
    public String validationRequired(@RequestParam(name = "q") String q) {
        return q;
    }
}
