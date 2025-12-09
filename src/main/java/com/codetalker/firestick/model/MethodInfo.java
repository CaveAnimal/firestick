package com.codetalker.firestick.model;

public class MethodInfo {
    private String name;
    private String signature;
    private String modifiers;
    private String annotations;
    private String javaDoc;
    private int startLine;
    private int endLine;
    private java.util.List<String> calledMethods;
    // Optional: qualified calls like "OtherClass.someMethod" or "com.example.OtherClass.someMethod"
    private java.util.List<String> calledQualifiedMethods;
    // Optional: constructors invoked in this method, e.g., "Util" or "com.example.Util"
    private java.util.List<String> calledConstructors;

    private MethodInfo(Builder builder) {
        this.name = builder.name;
        this.signature = builder.signature;
        this.modifiers = builder.modifiers;
        this.annotations = builder.annotations;
        this.javaDoc = builder.javaDoc;
        this.startLine = builder.startLine;
        this.endLine = builder.endLine;
        this.calledMethods = builder.calledMethods;
        this.calledQualifiedMethods = builder.calledQualifiedMethods;
        this.calledConstructors = builder.calledConstructors;
    }

    public static class Builder {
        private String name;
        private String signature;
        private String modifiers;
        private String annotations;
        private String javaDoc;
        private int startLine;
        private int endLine;
        private java.util.List<String> calledMethods;
    private java.util.List<String> calledQualifiedMethods;
        private java.util.List<String> calledConstructors;

        public Builder name(String name) { this.name = name; return this; }
        public Builder signature(String signature) { this.signature = signature; return this; }
        public Builder modifiers(String modifiers) { this.modifiers = modifiers; return this; }
        public Builder annotations(String annotations) { this.annotations = annotations; return this; }
        public Builder javaDoc(String javaDoc) { this.javaDoc = javaDoc; return this; }
        public Builder startLine(int startLine) { this.startLine = startLine; return this; }
        public Builder endLine(int endLine) { this.endLine = endLine; return this; }
        public Builder calledMethods(java.util.List<String> calledMethods) { this.calledMethods = calledMethods; return this; }
        public Builder calledQualifiedMethods(java.util.List<String> calledQualifiedMethods) { this.calledQualifiedMethods = calledQualifiedMethods; return this; }
        public Builder calledConstructors(java.util.List<String> calledConstructors) { this.calledConstructors = calledConstructors; return this; }
        public MethodInfo build() { return new MethodInfo(this); }
    }

    public String getName() { return name; }
    public String getSignature() { return signature; }
    public String getModifiers() { return modifiers; }
    public String getAnnotations() { return annotations; }
    public String getJavaDoc() { return javaDoc; }
    public int getStartLine() { return startLine; }
    public int getEndLine() { return endLine; }
    public java.util.List<String> getCalledMethods() { return calledMethods; }
    public java.util.List<String> getCalledQualifiedMethods() { return calledQualifiedMethods; }
    public java.util.List<String> getCalledConstructors() { return calledConstructors; }

    @Override
    public String toString() {
        return "MethodInfo{" +
                "name='" + name + '\'' +
                ", signature='" + signature + '\'' +
                ", modifiers='" + modifiers + '\'' +
                ", annotations='" + annotations + '\'' +
                ", javaDoc='" + javaDoc + '\'' +
                ", startLine=" + startLine +
                ", endLine=" + endLine +
        ", calledMethods=" + calledMethods +
    ", calledQualifiedMethods=" + calledQualifiedMethods +
        ", calledConstructors=" + calledConstructors +
                '}';
    }
}
