package com.codetalker.firestick.model;

import java.util.List;

public class ClassInfo {
    private String name;
    private String signature;
    private String modifiers;
    private String annotations;
    private String javaDoc;
    private int startLine;
    private int endLine;
    private List<MethodInfo> methods;
    private String superClass;
    private List<String> interfaces;

    private ClassInfo(Builder builder) {
        this.name = builder.name;
        this.signature = builder.signature;
        this.modifiers = builder.modifiers;
        this.annotations = builder.annotations;
        this.javaDoc = builder.javaDoc;
        this.startLine = builder.startLine;
        this.endLine = builder.endLine;
        this.methods = builder.methods;
        this.superClass = builder.superClass;
        this.interfaces = builder.interfaces;
    }

    public static class Builder {
        private String name;
        private String signature;
        private String modifiers;
        private String annotations;
        private String javaDoc;
        private int startLine;
        private int endLine;
        private List<MethodInfo> methods;
        private String superClass;
        private List<String> interfaces;

        public Builder name(String name) { this.name = name; return this; }
        public Builder signature(String signature) { this.signature = signature; return this; }
        public Builder modifiers(String modifiers) { this.modifiers = modifiers; return this; }
        public Builder annotations(String annotations) { this.annotations = annotations; return this; }
        public Builder javaDoc(String javaDoc) { this.javaDoc = javaDoc; return this; }
        public Builder startLine(int startLine) { this.startLine = startLine; return this; }
        public Builder endLine(int endLine) { this.endLine = endLine; return this; }
        public Builder methods(List<MethodInfo> methods) { this.methods = methods; return this; }
        public Builder superClass(String superClass) { this.superClass = superClass; return this; }
        public Builder interfaces(List<String> interfaces) { this.interfaces = interfaces; return this; }
        public ClassInfo build() { return new ClassInfo(this); }
    }

    public String getName() { return name; }
    public String getSignature() { return signature; }
    public String getModifiers() { return modifiers; }
    public String getAnnotations() { return annotations; }
    public String getJavaDoc() { return javaDoc; }
    public int getStartLine() { return startLine; }
    public int getEndLine() { return endLine; }
    public List<MethodInfo> getMethods() { return methods; }
    public String getSuperClass() { return superClass; }
    public List<String> getInterfaces() { return interfaces; }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "name='" + name + '\'' +
                ", signature='" + signature + '\'' +
                ", modifiers='" + modifiers + '\'' +
                ", annotations='" + annotations + '\'' +
                ", javaDoc='" + javaDoc + '\'' +
                ", startLine=" + startLine +
                ", endLine=" + endLine +
                ", methods=" + methods +
                ", superClass='" + superClass + '\'' +
                ", interfaces=" + interfaces +
                '}';
    }
}
