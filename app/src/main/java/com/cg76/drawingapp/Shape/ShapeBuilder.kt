package com.cg76.drawingapp.Shape

interface ShapeBuilder {
    fun build(
        startPoint: FloatArray,
        endPoint: FloatArray,
        color: FloatArray,
        size: Float): Shape
}