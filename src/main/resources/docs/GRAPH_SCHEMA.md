# Dependency Graph Schema

## Node Types
- **ClassNode**: fullyQualifiedName, packageName, modifiers, superClassName, interfaces[]
- **MethodNode**: className, methodName, signature, returnType, parameters[], modifiers
- **PackageNode**: packageName, classCount

## Edge Types
- **EXTENDS**: source=subclass, target=superclass
- **IMPLEMENTS**: source=class, target=interface
- **CALLS**: source=caller method, target=callee method
- **IMPORTS**: source=class, target=imported class/package

## Graph Type
DirectedGraph<GraphNode, DependencyEdge> using JGraphT

## Call resolution: metrics and fallback hierarchy

This project extracts CALLS edges by walking the Java AST and applying a set of heuristics to resolve the callee method node. We record detailed build-time metrics to make resolution observable and debuggable.

### Metrics (graph metadata)
- callsAttempted: total method/constructor calls examined
- callsResolved: total calls that were resolved to a concrete method node
- callEdgesAdded: CALLS edges actually added to the graph
- callsIntraClassResolved: unqualified calls resolved within the same class
- callsStaticResolved: calls resolved via exact static imports
- callsStaticWildcardResolved: calls resolved via wildcard static imports (Class.*)
- callsQualifiedResolved: qualified calls resolved via imports/same-package (e.g., OtherClass.m())
- callsQualifiedChainedResolved: qualified calls with a simple chained qualifier normalized (e.g., new T().m() → T().m)
- callsQualifiedFqnResolved: qualified calls whose qualifier is a fully qualified class name
- callsQualifiedUniqueSimpleResolved: qualified calls resolved via the unique simple-name fallback (see below)
- callsConstructorResolved: constructor invocations linked to their constructor method node
- callsUnresolvedStaticNoClass: unqualified static-like calls where no imported class matched
- callsUnresolvedQualifiedNoClass: qualified calls where the qualifier class could not be resolved
- callsUnresolvedQualifiedNoMethod: qualified calls where the qualifier class resolved but the method did not
- callsUnresolvedCtorNoClass: constructor calls where the target class could not be resolved
- callsUnresolvedCtorNoMethod: constructor calls where the class resolved but no matching constructor method was found

All metrics are stored in the graph metadata map returned by DependencyGraphService.getMetadata(). Names above match keys exactly.

### Fallback hierarchy for resolving a call
Given a call site, the resolver attempts the following, short-circuiting on first success:
1) Intra-class by name
	- Unqualified calls are first matched against methods declared in the same class.
	- this.method() is treated as intra-class.
2) this./super. qualified
	- this.method() → current class
	- super.method() → resolve superclass FQN and search its methods
3) Constructor calls
	- Link constructor invocations to the constructor method node on the target class.
	- Simple chained qualifiers are normalized (e.g., new T().m() yields T as the qualifier for m).
4) Static imports
	- Exact static imports first; if not found, wildcard static imports (Class.*).
5) Qualified class FQN
	- If the qualifier contains a '.', treat it as an FQN and resolve directly.
6) Qualified via imports/same-package
	- Resolve simple-class qualifiers using the current class imports or same package.
7) Unique simple-name fallback (observability only when safe)
	- If no class found yet and exactly one known class has the qualifier’s simple name, use that class.
	- This is tracked via callsQualifiedUniqueSimpleResolved to make the heuristic visible.

If none of the above resolve the target, the appropriate callsUnresolved* counter is incremented with the closest reason (no class vs no method), and no CALLS edge is added.