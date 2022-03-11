# UberRender Graphics Library

UberRender, or URender in short, is a graphics API abstraction library, rendering engine and scenegraph package. The primary goal of URender is to achieve performance-competitive multi-layer blended transparency rendering using purely deferred shading techniques, however, it allows for using forward shading as well as combining the two methods freely.

## URender API

The URender API package is a Java-oriented graphics API abstraction built upon OpenGL 4.0, but designed with other potential versions or backends in mind. The rendering engine uses this API exclusively.

## URender Engine

The URender rendering engine is, by itself, not a complete rendering engine, rather a set of base classes and backend calls that reduce the work needed to build a basic renderer on top of it to a mere bunch of glue code. An implementation of the engine can be found in the demo.

## URender G3DIO

G3DIO contains classes that manage conversion of common engine formats (Wavefront, ImageIO) to URender's optimized resource format (dubbed simply UGfx). The demo's material editor uses these very classes for model data conversion.

## URender SceneGraph

URender also contains a basic scenegraph system, with a render tree, node hierarchy, relative transforms and render queue calculator. It is mainly targeted at simpler research project, but is built to be easily extensible.