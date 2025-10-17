package com.codetalker.firestick.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codetalker.firestick.model.CodeFile;
import com.codetalker.firestick.model.Symbol;

public interface SymbolRepository extends JpaRepository<Symbol, Long> {
    List<Symbol> findByFile(CodeFile file);
    List<Symbol> findByNameAndType(String name, String type);
}
