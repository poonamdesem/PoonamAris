Source code matching plays an important role in programming, and provides an intrinsic resource for learning and reusing. This project focus on extending Arís (Analogical Reasoning for reuse of Implementation & Specification) which is a tool that has been developed to measure code similarity and perform code retrieval on a large corpus of methods written in the C# programming language. We extend Arís to permit matching and retrievel of programs written in the Java programming language. Arís translates source code via the Abstract Syntax Tree into a conceptual graph. In order to effectively compare two source code files, Conceptual Graphs allow us to explore the semantic content of the code while also analyzing its structural properties using graph-based techniques.We present the Arís framework for detecting and locating functionally similar source code (Nearest-Neighbors (NN) data) from a repository of Java code, based on some presented target problem. To get Nearest-Neighbors (NN) data, a vector space model is used to select a subset of the entire corpus, with this subset we perform a graph-matching process using the VF2 algorithm to present isomorphic (exact matches) or homomorphic (non-identical) sub-graphs including their conceptual similarity. A vector space model is used to store the graph data such as the number of concepts and relations present for each method.
